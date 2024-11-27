package org.example.repositories.redis.implementations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.vehicle.BicycleMgd;
import org.example.mgd.vehicle.CarMgd;
import org.example.mgd.vehicle.MopedMgd;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.redis.RedisConnectionManager;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import redis.clients.jedis.JedisPooled;


import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RepositoryRedisCacheTest {

    static VehicleRepositoryDecorator repo;
    static JedisPooled pool;
    private static MongoClient client;

    static void connect() {
        ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
                .automatic(true)
                .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(
                        CodecRegistries.fromRegistries(
                                MongoClientSettings.getDefaultCodecRegistry(),
                                pojoCodecRegistry
                        ))
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();

        client = MongoClients.create(settings);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
    }

    @BeforeAll
    static void setUp() {
        connect();
        repo = new VehicleRepositoryDecorator(client);
        pool = RedisConnectionManager.getConnection();
        repo.clearCache(pool);
    }

    @AfterEach
    void clear() {
        repo.clearCache(pool);
    }

    @AfterAll
    static void tearDown() {
        RedisConnectionManager.close();
    }

    @Test
    void clearCache() {
        Moped moped = new Moped(UUID.randomUUID(), "KJF5TY", 200.0, 3000);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + moped.getId();
        repo.saveToCache(redisKey, new MopedMgd(moped), pool);
        Assertions.assertNotNull(repo.getFromCache(redisKey, pool));
        repo.clearCache(pool);
        Assertions.assertNull(repo.getFromCache(redisKey, pool));
    }

    @Test
    void getFromCacheByPlateNumber() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), "DE1298", 20.0, 2);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + bicycle.getId();
        repo.saveToCache(redisKey, new BicycleMgd(bicycle), pool);
        Assertions.assertEquals(bicycle.getId(), repo.getFromCacheByPlateNumber(bicycle.getPlateNumber(), pool).getId());
        Assertions.assertEquals(1, repo.getAllFromCache(pool).size());
    }

    @Test
    void saveToGetFromCache() {
        Car car = new Car(UUID.randomUUID(), "HJ56G7", 100.0, 30, Car.TransmissionType.AUTOMATIC);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        repo.saveToCache(redisKey, new CarMgd(car), pool);
        Assertions.assertEquals(car.getId(), repo.getFromCache(redisKey, pool).getId());
    }

    @Test
    void deleteFromCache(){
        Moped moped = new Moped(UUID.randomUUID(), "NW898J", 100.0, 1000);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + moped.getId();
        repo.saveToCache(redisKey, new MopedMgd(moped), pool);
        Assertions.assertNotNull(repo.findById(moped.getId()));
        repo.deleteFromCache(redisKey, pool);
        Assertions.assertNull(repo.getFromCache(redisKey, pool));
        Assertions.assertEquals(0, repo.getAllFromCache(pool).size());
    }

    @Test
    void getAllFromCacheByDiscriminator() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), "EL78G6", 20.0, 2);
        String redisKeyB = DatabaseConstants.VEHICLE_PREFIX + bicycle.getId();
        repo.saveToCache(redisKeyB, new BicycleMgd(bicycle), pool);
        Car car = new Car(UUID.randomUUID(), "SK76G6", 300.0, 200, Car.TransmissionType.AUTOMATIC);
        String redisKeyC = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        repo.saveToCache(redisKeyC, new CarMgd(car), pool);
        Assertions.assertEquals(1, repo.getAllFromCacheByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR, pool).size());
        Assertions.assertEquals(car.getId(), repo.getAllFromCacheByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR, pool).getFirst().getId());

        Assertions.assertEquals(1, repo.getAllFromCacheByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR, pool).size());
        Assertions.assertEquals(bicycle.getId(), repo.getAllFromCacheByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR, pool).getFirst().getId());

        Assertions.assertEquals(0, repo.getAllFromCacheByDiscriminator(DatabaseConstants.MOPED_DISCRIMINATOR, pool).size());
    }

    @Test
    void expireKey() throws InterruptedException {
        Car car = new Car(UUID.randomUUID(), "DN6R56", 250.0, 2000, Car.TransmissionType.AUTOMATIC);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        repo.saveToCache(redisKey, new CarMgd(car), pool);
        Assertions.assertEquals(car.getId(), repo.getFromCache(redisKey, pool).getId());
        TimeUnit.SECONDS.sleep(21);
        Assertions.assertNull(repo.getFromCache(redisKey, pool));
    }

    @Test
    void getAllFromCache() {
        Car car = new Car(UUID.randomUUID(), "FA6G78", 300.0, 200, Car.TransmissionType.AUTOMATIC);
        String redisKeyC = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        repo.saveToCache(redisKeyC, new CarMgd(car), pool);
        Moped moped = new Moped(UUID.randomUUID(), "JL6739", 700.0, 5000);
        String redisKeyM = DatabaseConstants.VEHICLE_PREFIX + moped.getId();
        repo.saveToCache(redisKeyM, new MopedMgd(moped), pool);
        Assertions.assertEquals(2, repo.getAllFromCache(pool).size());
        Assertions.assertEquals(car.getId(), repo.getAllFromCache(pool).get(0).getId());
        Assertions.assertEquals(moped.getId(), repo.getAllFromCache(pool).get(1).getId());

    }

}
