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
        repo = new VehicleRepositoryDecorator(client);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        repo.clearCache();
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
    void getFromCacheByPlateNumber() {
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
        Assertions.assertThrows(RuntimeException.class, () -> repo.getFromCache(redisKey));
    }

    @Test
    void saveToGetFromCache() {
        Car car = new Car(UUID.randomUUID(), "HJ56G7", 100.0, 30, Car.TransmissionType.AUTOMATIC);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        repo.saveToCache(redisKey, new CarMgd(car));
        Assertions.assertEquals(car.getId(), repo.getFromCache(redisKey).getId());
    }

    @Test
    void getAllFromCache() {
        Car car = new Car(UUID.randomUUID(), "FA6G78", 300.0, 200, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car));
        Moped moped = new Moped(UUID.randomUUID(), "JL6739", 700.0, 5000);
        repo.save(new MopedMgd(moped));
        Assertions.assertEquals(2, repo.getAllFromCache().size());
        Assertions.assertEquals(car.getId(), repo.getAllFromCache().get(0).getId());
        Assertions.assertEquals(moped.getId(), repo.getAllFromCache().get(1).getId());
    }

    @Test
    void getAllFromCacheByDiscriminator() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), "EL78G6", 20.0, 2);
        repo.save(new BicycleMgd(bicycle));
        Car car = new Car(UUID.randomUUID(), "SK76G6", 300.0, 200, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car));
        Assertions.assertEquals(1, repo.getAllFromCacheByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR).size());
        Assertions.assertEquals(car.getId(), repo.getAllFromCacheByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR).getFirst().getId());

        Assertions.assertEquals(1, repo.getAllFromCacheByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR).size());
        Assertions.assertEquals(bicycle.getId(), repo.getAllFromCacheByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR).getFirst().getId());

        Assertions.assertEquals(0, repo.getAllFromCacheByDiscriminator(DatabaseConstants.MOPED_DISCRIMINATOR).size());
    }

    @Test
    void deleteFromCache(){
        Moped moped = new Moped(UUID.randomUUID(), "NW898J", 100.0, 1000);
        repo.save(new MopedMgd(moped));
        Assertions.assertNotNull(repo.findById(moped.getId()));
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + moped.getId();
        repo.deleteFromCache(redisKey);
        Assertions.assertThrows(RuntimeException.class, () -> repo.getFromCache(redisKey));
        Assertions.assertEquals(0, repo.getAllFromCache().size());
    }

    @Test
    void findByPlateNumber() {
        Car car = new Car(UUID.randomUUID(), "KU67NA", 150.0, 2000, Car.TransmissionType.MANUAL);
        repo.save(new CarMgd(car));
        Assertions.assertEquals(car.getId(), repo.findByPlateNumber(car.getPlateNumber()).getId());
    }

    @Test
    void findByIdAndDiscriminator() {
        Car car = new Car(UUID.randomUUID(), "NH2567", 250.0, 2000, Car.TransmissionType.MANUAL);
        repo.save(new CarMgd(car));
        Assertions.assertEquals(car.getId(), repo.findByIdAndDiscriminator(car.getId(), DatabaseConstants.CAR_DISCRIMINATOR).getId());
    }

    @Test
    void findAllByDiscriminator() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), "DF5678", 20.0, 2);
        repo.save(new BicycleMgd(bicycle));
        Car car = new Car(UUID.randomUUID(), "KL5666", 300.0, 200, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car));
        Assertions.assertEquals(1, repo.findAllByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR).size());
        Assertions.assertEquals(car.getId(), repo.findAllByDiscriminator(DatabaseConstants.CAR_DISCRIMINATOR).getFirst().getId());

        Assertions.assertEquals(1, repo.findAllByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR).size());
        Assertions.assertEquals(bicycle.getId(), repo.findAllByDiscriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR).getFirst().getId());

        Assertions.assertEquals(0, repo.findAllByDiscriminator(DatabaseConstants.MOPED_DISCRIMINATOR).size());
    }

    @Test
    void findAnyVehicle() {
        Moped moped = new Moped(UUID.randomUUID(), "WK9023", 200.0, 1000);
        repo.save(new MopedMgd(moped));
        Assertions.assertEquals(moped.getId(), repo.findAnyVehicle(moped.getId()).getId());
    }

    @Test
    void changeRentedStatus() {
        Car car = new Car(UUID.randomUUID(), "DJ67F5", 350.0, 2000, Car.TransmissionType.AUTOMATIC);
        repo.save(new CarMgd(car));
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        Assertions.assertEquals(0, repo.getFromCache(redisKey).getRented());
        repo.changeRentedStatus(car.getId(), true);
        Assertions.assertEquals(1, repo.getFromCache(redisKey).getRented());
    }

    @Test
    void findByIdOrNull_DeleteById() {
        Moped moped = new Moped(UUID.randomUUID(), "WH667G", 100.0, 1000);
        repo.save(new MopedMgd(moped));
        Assertions.assertEquals(moped.getId(), repo.findByIdOrNull(moped.getId()).getId());
        repo.deleteById(moped.getId());
        Assertions.assertNull(repo.findByIdOrNull(moped.getId()));
    }

}
