package org.example.repositories.redis.implementations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.CarMgd;
import org.example.model.Car;
import org.example.redis.RedisConnectionManager;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.*;
import org.openjdk.jmh.annotations.*;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


// https://jenkov.com/tutorials/java-performance/jmh.html#jmh-benchmark-modes
@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Benchmark)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1)
@Measurement(iterations = 1)
public class BenchmarkTest {

    public VehicleRepositoryDecorator redisRepo;
    public VehicleRepository mongoRepo;
    public static MongoClient client;

    public static void connect() {


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

    private UUID idMongo;
    private UUID idRedis;

    private String plateNumberMongo = "HJ56G7";
    private String plateNumberRedis = "HJ56G8";

    @Setup(Level.Invocation)
    public void setUp() {
        connect();
//        client = MongoClients.create(settings);
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        redisRepo = new VehicleRepositoryDecorator(client);
        mongoRepo = new VehicleRepository(client);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        redisRepo.clearCache();
        idMongo = UUID.randomUUID();
        Car car = new Car(idMongo, plateNumberMongo, 100.0, 30, Car.TransmissionType.AUTOMATIC);
        mongoRepo.save(new CarMgd(car));
        idRedis = UUID.randomUUID();
        Car car2 = new Car(idRedis, plateNumberRedis, 100.0, 30, Car.TransmissionType.AUTOMATIC);

        redisRepo.save(new CarMgd(car2));
    }

    @TearDown(Level.Invocation)
    public  void tearDown() {
       // RedisConnectionManager.close();
    }

    @Benchmark
    public void saveVehicleRedis() {
        Car car = new Car(UUID.randomUUID(), "HJ56G7", 100.0, 30, Car.TransmissionType.AUTOMATIC);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + car.getId();
        redisRepo.saveToCache(redisKey, new CarMgd(car));
    }

    @Benchmark
    public void saveVehicleMongo() {
        Car car = new Car(UUID.randomUUID(), "HJ56G7", 100.0, 30, Car.TransmissionType.AUTOMATIC);
        mongoRepo.save(new CarMgd(car));
    }

    @Benchmark
    public void findAllMongo() {
        mongoRepo.findAll();
    }

    @Benchmark
    public void findAllRedis() {
        redisRepo.getAllFromCache();
    }

    @Benchmark
    public void findAllCache() {
        redisRepo.findAll();
    }

    @Benchmark
    public void findByIdNotInCache() {
        redisRepo.clearCache();
        redisRepo.findById(idMongo);
    }

    @Benchmark
    public void findByIdInCache() {
        redisRepo.findById(idRedis);
    }

    @Benchmark
    public void findByPlateNumberNotInCache() {
        redisRepo.clearCache();
        redisRepo.findByPlateNumber(plateNumberMongo);
    }

    @Benchmark
    public void findByPlateNumberInCache() {
        redisRepo.findByPlateNumber(plateNumberRedis);
    }

    @Benchmark
    public void findByPlateNumberMongo() {
        mongoRepo.findByPlateNumber(plateNumberMongo);
    }

    @Benchmark
    public void findByPlateNumberRedis() {
        redisRepo.getFromCacheByPlateNumber(plateNumberRedis);
    }
}
