package org.example.repositories.redis.implementations;

import com.mongodb.client.MongoClient;
import lombok.Getter;

import org.example.mgd.BicycleMgd;
import org.example.mgd.CarMgd;
import org.example.mgd.MopedMgd;
import org.example.mgd.VehicleMgd;
import org.example.redis.RedisConnectionManager;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import org.example.utils.serializers.CustomGsonObjectMapper;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.json.DefaultGsonObjectMapper;
import redis.clients.jedis.json.JsonObjectMapper;
import redis.clients.jedis.search.*;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class VehicleRepositoryDecorator implements IVehicleRepository {

    private final IVehicleRepository vehicleRepository;
    private final JsonObjectMapper objectRedisMapper = new CustomGsonObjectMapper();

    public VehicleRepositoryDecorator(MongoClient mongoClient) {
        RedisConnectionManager.connect();
        this.vehicleRepository = new VehicleRepository(mongoClient);
        createSchemaAndIndex();
    }

    private void createSchemaAndIndex() {
        JedisPooled pool = RedisConnectionManager.getConnection();
        Schema schema = new Schema()
                .addTextField("$.plateNumber", 1.0)
                .addNumericField("$.basePrice")
                .addTagField("$.archive")
                .addNumericField("$.rented")
                .addTextField("$._clazz", 1.0);
        IndexDefinition indexDefinition = new IndexDefinition(IndexDefinition.Type.JSON)
                .setPrefixes(DatabaseConstants.VEHICLE_PREFIX);
        try {
            pool.ftDropIndex(DatabaseConstants.VEHICLE_INDEX);
        }
        catch (JedisException ignored) {}
        pool.ftCreate(DatabaseConstants.VEHICLE_INDEX, IndexOptions.defaultOptions().setDefinition(indexDefinition),
                schema);
    }

    // Metody do Redisa - Zapis danych do cache'a, pobieranie danych z cache'a, czyszczenie cache'a.
    public void saveToCache(String key, VehicleMgd vehicle) {
        try {
            JedisPooled pool = RedisConnectionManager.getConnection();
            String jsonData = objectRedisMapper.toJson(vehicle);
            pool.jsonSet(key, jsonData);
        }
        catch (Exception e) {
            throw new RuntimeException("Błąd podczas zapisywania pojazdu do Redisa: " + e.getMessage(), e);
        }
    }

    public VehicleMgd getFromCache(String key) {
        try {
            JedisPooled pool = RedisConnectionManager.getConnection();
            Object foundObject = pool.jsonGet(key);
            if (foundObject == null) {
                return null;
            }
            String jsonData = foundObject.toString();
            return objectRedisMapper.fromJson(jsonData, VehicleMgd.class);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public List<VehicleMgd> getAllFromCache() {
        JedisPooled pool = RedisConnectionManager.getConnection();
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX,
                new Query().limit(0, 1000));
        if(searchResult.getDocuments().isEmpty()) return new ArrayList<>();
        return searchResult.getDocuments().stream().map(doc ->
                objectRedisMapper.fromJson( (String)doc.get("$"), VehicleMgd.class)).toList();
    }

    public VehicleMgd getFromCacheByPlateNumber(String plateNumber) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        String queryString = String.format("@\\$\\.plateNumber:%s", plateNumber);
        Query query = new Query(queryString);
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX, query);
        if (searchResult.getDocuments().isEmpty()) {
            return null;
        }
        return objectRedisMapper.fromJson(searchResult.getDocuments().getFirst().get("$").toString(), VehicleMgd.class);
    }

    public List<VehicleMgd> getAllFromCacheByDiscriminator(String discriminator) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        String queryString = String.format("@\\$\\._clazz:%s", discriminator);
        Query query = new Query(queryString);
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX, query);
        if (searchResult.getDocuments().isEmpty()) {
            return new ArrayList<>();
        }
        return searchResult.getDocuments().stream().map( doc ->
                objectRedisMapper.fromJson((String) doc.get("$"), VehicleMgd.class)).toList();
    }

    public void deleteFromCache(String key) {
        try {
            JedisPooled pool = RedisConnectionManager.getConnection();
            pool.jsonDel(key);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas usuwania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public void clearCache() {
            JedisPooled pool = RedisConnectionManager.getConnection();
            pool.flushDB();
            createSchemaAndIndex();
    }

    // Obsługa utraty połączenia z bazą danych Redis.
    @Override
    public VehicleMgd findByPlateNumber(String plateNumber) {
        VehicleMgd vehicleMgd = getFromCacheByPlateNumber(plateNumber);
        if (vehicleMgd != null) {
            return vehicleMgd;
        }
        vehicleMgd = vehicleRepository.findByPlateNumber(plateNumber);
        saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd);
        return vehicleMgd;
    }

    @Override
    public VehicleMgd findByIdAndDiscriminator(UUID id, String discriminator) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;

        VehicleMgd vehicleMgd = getFromCache(redisKey);
        if (vehicleMgd != null) {
            return vehicleMgd;
        }
        return vehicleRepository.findByIdAndDiscriminator(id, discriminator);
    }

    @Override
    public List<VehicleMgd> findAllByDiscriminator(String discriminator) {
        List<VehicleMgd> vehicleMgds = getAllFromCacheByDiscriminator(discriminator);
        if (vehicleMgds.isEmpty()) {
            vehicleMgds = vehicleRepository.findAllByDiscriminator(discriminator);
            vehicleMgds.forEach( vehicleMgd ->
                    saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd));
        }
        return vehicleMgds;
    }

    @Override
    public VehicleMgd findAnyVehicle(UUID vehicleId) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + vehicleId;

        VehicleMgd vehicleMgd = getFromCache(redisKey);
        if (vehicleMgd != null) {
            return vehicleMgd;
        }
        // jeśli nie ma Redis to może ma Mongo
        vehicleMgd = vehicleRepository.findAnyVehicle(vehicleId);
        saveToCache(redisKey, vehicleMgd);
        return vehicleMgd;
    }

    @Override
    public VehicleMgd changeRentedStatus(UUID id, Boolean status) {
        // wrzuć do mongo
        VehicleMgd updatedVehicle = vehicleRepository.changeRentedStatus(id, status);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
        // wrzuć do cache
        saveToCache(redisKey, updatedVehicle);
        return updatedVehicle;
    }

    @Override
    public VehicleMgd findById(UUID id) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;

        VehicleMgd vehicleMgd = getFromCache(redisKey);
        if (vehicleMgd != null) {
            return vehicleMgd;
        }
        vehicleMgd = vehicleRepository.findById(id);
        saveToCache(redisKey, vehicleMgd);
        return  vehicleMgd;
    }

    @Override
    public VehicleMgd findByIdOrNull(UUID id) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;

        VehicleMgd vehicleMgd = getFromCache(redisKey);
        if (vehicleMgd != null) {
            return vehicleMgd;
        }
        vehicleMgd = vehicleRepository.findByIdOrNull(id);
        saveToCache(redisKey, vehicleMgd);
        return  vehicleMgd;
    }

    @Override
    public List<VehicleMgd> findAll() {
        List<VehicleMgd> foundVehicles = getAllFromCache();

        if (foundVehicles.isEmpty()) {
            foundVehicles = vehicleRepository.findAll();
            foundVehicles.forEach(vehicleMgd -> {
                saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd);
            });
        }
        return foundVehicles;
    }

    @Override
    public VehicleMgd save(VehicleMgd doc) {
        VehicleMgd savedVehicle = vehicleRepository.save(doc);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + savedVehicle.getId();
        saveToCache(redisKey, savedVehicle);

        return savedVehicle;
    }

    @Override
    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
        deleteFromCache(redisKey);
    }

    @Override
    public MongoClient getClient() {
        return vehicleRepository.getClient();
    }
}
