package org.example.repositories.redis.implementations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.BicycleMgd;
import org.example.mgd.CarMgd;
import org.example.mgd.MopedMgd;
import org.example.mgd.VehicleMgd;
import org.example.model.Bicycle;
import org.example.model.Car;
import org.example.model.Moped;
import org.example.redis.RedisConnectionManager;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.*;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.DefaultGsonObjectMapper;
import redis.clients.jedis.json.JsonObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;


class VehicleRepositoryDecoratorTest {

    VehicleRepositoryDecorator repo;

    private static MongoClient client;

    @BeforeAll
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

    @BeforeEach
    void setUp() {
        RedisConnectionManager.connect();
        repo = new VehicleRepositoryDecorator(client);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();

    }

    @AfterEach
    void tearDown() {
        RedisConnectionManager.close();
    }

    @Test
    void saveVehicle() {
        Car car = new Car(UUID.randomUUID(),"SD67G6", 100.0,3, Car.TransmissionType.MANUAL);
        repo.save(new CarMgd(car));
        Assertions.assertEquals(car.getId(), repo.findById(car.getId()).getId());
        Car car2 = new Car(UUID.randomUUID(), "JDF73N", 1000.0,6, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car2));
        Assertions.assertEquals(car2.getId(), repo.findById(car2.getId()).getId());
        Assertions.assertEquals(2, repo.findAll().size());


    }

    @Test
    void findAll() {
        Car car = new Car(UUID.randomUUID(),"D98PY6", 100.0,3, Car.TransmissionType.MANUAL);
        repo.save(new CarMgd(car));
        Car car2 = new Car(UUID.randomUUID(), "SE2738", 1000.0,6, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car2));
        List<VehicleMgd> all = repo.findAll();
        Assertions.assertEquals(2, all.size());
    }


    @Test
    void isConnected() {
        //repo.clearCache();

        JedisPooled pool = RedisConnectionManager.getConnection();

        Assertions.assertEquals(1, repo.findAll().size());
    }

    @Test
    void getFromCacheByPlateNumber() {
        //repo.clearCache();
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), "DE1298", 20.0, 2);
        repo.save(new BicycleMgd(bicycle));
        Assertions.assertEquals(bicycle.getId(), repo.getFromCacheByPlateNumber(bicycle.getPlateNumber()).getId());
        Assertions.assertEquals(1, repo.findAll().size());
    }

    @Test
    void clearCache() {
        Moped moped = new Moped(UUID.randomUUID(), "KJF5TY", 200.0, 3000);
        repo.save(new MopedMgd(moped));
        Assertions.assertNotNull(repo.findById(moped.getId()));

        repo.clearCache();

        String redisKey = DatabaseConstants.VEHICLE_PREFIX + moped.getId();
        Assertions.assertNull(repo.getFromCache(redisKey));
    }

    @Test
    void saveToCache() {

    }
}
