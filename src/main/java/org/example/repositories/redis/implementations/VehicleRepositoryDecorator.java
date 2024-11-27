package org.example.repositories.redis.implementations;

import com.mongodb.client.MongoClient;
import lombok.Getter;

import org.example.mgd.vehicle.VehicleMgd;
import org.example.redis.RedisConnectionManager;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import org.example.utils.serializers.CustomGsonObjectMapper;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;
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
        JedisPooled pool = RedisConnectionManager.getConnection();
        this.vehicleRepository = new VehicleRepository(mongoClient);
        if(pool != null){
            createSchemaAndIndex(pool);
        }
    }

    private void createSchemaAndIndex(JedisPooled jedisPooled) {
        Schema schema = new Schema()
                .addTextField("$.plateNumber", 1.0)
                .addNumericField("$.basePrice")
                .addTagField("$.archive")
                .addNumericField("$.rented")
                .addTextField("$._clazz", 1.0);
        IndexDefinition indexDefinition = new IndexDefinition(IndexDefinition.Type.JSON)
                .setPrefixes(DatabaseConstants.VEHICLE_PREFIX);
        try {
            jedisPooled.ftDropIndex(DatabaseConstants.VEHICLE_INDEX);
        }
        catch (JedisException ignored) {}
        jedisPooled.ftCreate(DatabaseConstants.VEHICLE_INDEX, IndexOptions.defaultOptions().setDefinition(indexDefinition),
                schema);
    }

    // Metody do Redisa - Zapis danych do cache'a, pobieranie danych z cache'a, czyszczenie cache'a.
    public void saveToCache(String key, VehicleMgd vehicle, JedisPooled pool) {
        try {
            String jsonData = objectRedisMapper.toJson(vehicle);
            pool.jsonSet(key, jsonData);
            pool.expire(key, 20);
        }
        catch (Exception e) {
            throw new RuntimeException("Błąd podczas zapisywania pojazdu do Redisa: " + e.getMessage(), e);
        }
    }

    public VehicleMgd getFromCache(String key, JedisPooled pool) {
        try {
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

    public List<VehicleMgd> getAllFromCache(JedisPooled pool) {
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX,
                new Query().limit(0, 1000));
        if(searchResult.getDocuments().isEmpty()) return new ArrayList<>();
        return searchResult.getDocuments().stream().map(doc ->
                objectRedisMapper.fromJson( (String)doc.get("$"), VehicleMgd.class)).toList();
    }

    public VehicleMgd getFromCacheByPlateNumber(String plateNumber, JedisPooled pool) {
        String queryString = String.format("@\\$\\.plateNumber:%s", plateNumber);
        Query query = new Query(queryString);
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX, query);
        if (searchResult.getDocuments().isEmpty()) {
            return null;
        }
        return objectRedisMapper.fromJson(searchResult.getDocuments().getFirst().get("$").toString(), VehicleMgd.class);
    }

    public List<VehicleMgd> getAllFromCacheByDiscriminator(String discriminator, JedisPooled pool) {
        String queryString = String.format("@\\$\\._clazz:%s", discriminator);
        Query query = new Query(queryString);
        SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX, query);
        if (searchResult.getDocuments().isEmpty()) {
            return new ArrayList<>();
        }
        return searchResult.getDocuments().stream().map( doc ->
                objectRedisMapper.fromJson((String) doc.get("$"), VehicleMgd.class)).toList();
    }

    public void deleteFromCache(String key, JedisPooled pool) {
        try {
            pool.jsonDel(key);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas usuwania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public void clearCache(JedisPooled pool) {
        if(pool == null) return;
        int size = this.getAllFromCache(pool).size();
        if (size > 0) {
            pool.flushDB(); // Wyczyść bazę danych, jeśli są klucze
            createSchemaAndIndex(pool);
        }
    }

    // Obsługa utraty połączenia z bazą danych Redis.
    @Override
    public VehicleMgd findByPlateNumber(String plateNumber) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        VehicleMgd vehicleMgd;
        if (pool != null) {
            vehicleMgd = getFromCacheByPlateNumber(plateNumber, pool);
            if (vehicleMgd != null) {
                return vehicleMgd;
            }
            vehicleMgd = vehicleRepository.findByPlateNumber(plateNumber);
            saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd, pool);
        }
        return vehicleRepository.findByPlateNumber(plateNumber);
    }

    @Override
    public VehicleMgd findByIdAndDiscriminator(UUID id, String discriminator) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            VehicleMgd vehicleMgd = getFromCache(redisKey, pool);
            if (vehicleMgd != null) {
                return vehicleMgd;
            }
            vehicleMgd = vehicleRepository.findByIdAndDiscriminator(id, discriminator);
            saveToCache(redisKey, vehicleMgd, pool);
        }
        return vehicleRepository.findByIdAndDiscriminator(id, discriminator);
    }

    @Override
    public List<VehicleMgd> findAllByDiscriminator(String discriminator) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            List<VehicleMgd> vehicleMgds = getAllFromCacheByDiscriminator(discriminator, pool);
            if (vehicleMgds.isEmpty()) {
                vehicleMgds = vehicleRepository.findAllByDiscriminator(discriminator);
                vehicleMgds.forEach( vehicleMgd ->
                        saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd, pool));
            }
        }
        return vehicleRepository.findAllByDiscriminator(discriminator);
    }

    @Override
    public VehicleMgd findAnyVehicle(UUID vehicleId) {
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + vehicleId;
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            VehicleMgd vehicleMgd = getFromCache(redisKey, pool);
            if (vehicleMgd != null) {
                return vehicleMgd;
            }
            vehicleMgd = vehicleRepository.findAnyVehicle(vehicleId);
            saveToCache(redisKey, vehicleMgd, pool);
        }
        return vehicleRepository.findAnyVehicle(vehicleId);
    }

    @Override
    public VehicleMgd changeRentedStatus(UUID id, Boolean status) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        VehicleMgd updatedVehicle;
        updatedVehicle = vehicleRepository.changeRentedStatus(id, status);
        if (pool != null) {
            String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
            //wyczyść nieaktualne dane
            clearCache(pool);
            // wrzuć do cache
            saveToCache(redisKey, updatedVehicle, pool);
        }
        return updatedVehicle;
    }

    @Override
    public VehicleMgd findById(UUID id) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
            VehicleMgd vehicleMgd = getFromCache(redisKey, pool);
            if (vehicleMgd != null) {
                return vehicleMgd;
            }
            vehicleMgd = vehicleRepository.findById(id);
            saveToCache(redisKey, vehicleMgd, pool);
        }
        return vehicleRepository.findById(id);
    }

    @Override
    public VehicleMgd findByIdOrNull(UUID id) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
            VehicleMgd vehicleMgd = getFromCache(redisKey, pool);
            if (vehicleMgd != null) {
                return vehicleMgd;
            }
            vehicleMgd = vehicleRepository.findByIdOrNull(id);
            saveToCache(redisKey, vehicleMgd, pool);
        }
        return vehicleRepository.findByIdOrNull(id);
    }

    @Override
    public List<VehicleMgd> findAll() {
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            List<VehicleMgd> foundVehicles = getAllFromCache(pool);
            if (foundVehicles.isEmpty()) {
                foundVehicles = vehicleRepository.findAll();
                foundVehicles.forEach(vehicleMgd -> saveToCache(DatabaseConstants.VEHICLE_PREFIX + vehicleMgd.getId(), vehicleMgd, pool));
            }
        }
        return vehicleRepository.findAll();
    }

    @Override
    public VehicleMgd save(VehicleMgd doc) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        if (pool != null) {
            VehicleMgd savedVehicle = vehicleRepository.save(doc);
            String redisKey = DatabaseConstants.VEHICLE_PREFIX + savedVehicle.getId();
            saveToCache(redisKey, savedVehicle, pool);
        }
        return vehicleRepository.save(doc);
    }

    @Override
    public void deleteById(UUID id) {
        JedisPooled pool = RedisConnectionManager.getConnection();
        vehicleRepository.deleteById(id);
        if (pool != null) {
            String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
            deleteFromCache(redisKey, pool);
        }

    }

    @Override
    public MongoClient getClient() {
        return vehicleRepository.getClient();
    }
}
