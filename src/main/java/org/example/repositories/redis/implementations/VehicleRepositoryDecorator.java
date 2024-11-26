package org.example.repositories.redis.implementations;

import com.mongodb.client.MongoClient;
import lombok.Getter;

import org.example.mgd.VehicleMgd;
import org.example.redis.RedisConnectionManager;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.json.DefaultGsonObjectMapper;
import redis.clients.jedis.json.JsonObjectMapper;
import redis.clients.jedis.search.*;


import java.util.List;
import java.util.UUID;

@Getter
public class VehicleRepositoryDecorator implements IVehicleRepository {

    private final IVehicleRepository vehicleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonObjectMapper objectRedisMapper = new DefaultGsonObjectMapper();

    public VehicleRepositoryDecorator(MongoClient mongoClient) {
        RedisConnectionManager.connect();
        this.vehicleRepository = new VehicleRepository(mongoClient);

        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            Schema schema = new Schema()
                    .addTextField("plateNumber", 1.0)
                    .addNumericField("basePrice")
                    .addTagField("archive")
                    .addNumericField("rented")
                    .addTextField("_clazz", 1.0);

            IndexDefinition indexDefinition = new IndexDefinition(IndexDefinition.Type.JSON)
                    .setPrefixes(DatabaseConstants.VEHICLE_PREFIX);

            try {
                pool.ftDropIndex(DatabaseConstants.VEHICLE_INDEX);
            } catch (JedisException ignored) {}
            pool.ftCreate(DatabaseConstants.VEHICLE_INDEX, IndexOptions.defaultOptions().setDefinition(indexDefinition),
                    schema);
        }
    }

    // Metody do Redisa - Zapis danych do cache'a, pobieranie danych z cache'a, czyszczenie cache'a.
    public void saveToCache(String key, VehicleMgd vehicle) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            String jsonData = objectMapper.writeValueAsString(vehicle);
            pool.jsonSet(key, jsonData);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas zapisywania pojazdu do Redisa: " + e.getMessage(), e);
        }
    }

    public void saveListToCache(String key, List<VehicleMgd> vehicleList) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            String jsonData = objectMapper.writeValueAsString(vehicleList);
            pool.jsonSet(key, jsonData);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas zapisywania pojazdu do Redisa: " + e.getMessage(), e);
        }
    }

    public VehicleMgd getFromCache(String key) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            String jsonData = pool.get(key);
            if (jsonData == null) {
                return  null;
            }
            return objectRedisMapper.fromJson(jsonData, VehicleMgd.class);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public List<VehicleMgd> getAllFromCache(String key) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            String jsonData = pool.get(key);
            if (jsonData == null) {
                return  null;
            }
            return objectMapper.readValue(jsonData, new TypeReference<List<VehicleMgd>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public List<VehicleMgd> getAllFromCache() {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX,
                    new Query().limit(0, 1000));
            return searchResult.getDocuments().stream().map(doc ->
                    objectRedisMapper.fromJson( (String)doc.get("$"), VehicleMgd.class)).toList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania pojazdów z Redisa: " + e.getMessage(), e);
        }
    }

    public VehicleMgd getFromCacheByPlateNumber(String plateNumber) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            // Construct the query to search by the specified plate_number
            String queryString = String.format("@plate_number:%s", plateNumber);
            Query query = new Query(queryString).limit(0, 1000);
            // Execute the searchF
            SearchResult searchResult = pool.ftSearch(DatabaseConstants.VEHICLE_INDEX,
                    query);

            return objectRedisMapper.fromJson(searchResult.getDocuments().getFirst().toString(), VehicleMgd.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching vehicles from Redis: " + e.getMessage(), e);
        }
    }

    public void deleteFromCache(String key) {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            pool.jsonDel(key);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas usuwania pojazdu z Redisa: " + e.getMessage(), e);
        }
    }

    public void clearCache() {
        try (JedisPooled pool = RedisConnectionManager.getConnection()) {
            String allKeys = DatabaseConstants.VEHICLE_PREFIX + "*";
            for (String key : pool.keys(allKeys)) {
                pool.jsonDel(key);
            }
        }
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
        String redisKey = DatabaseConstants.VEHICLE_PREFIX + discriminator;
        List<VehicleMgd> vehicleMgds = getAllFromCache(redisKey);
        if (vehicleMgds == null || vehicleMgds.isEmpty()) {
            vehicleMgds = vehicleRepository.findAllByDiscriminator(discriminator);
            saveListToCache(redisKey, vehicleMgds);
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

        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + "all");
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.CAR_DISCRIMINATOR);
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.BICYCLE_DISCRIMINATOR);
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.MOPED_DISCRIMINATOR);

        return savedVehicle;
    }

    @Override
    public void deleteById(UUID id) {

        vehicleRepository.deleteById(id);

        String redisKey = DatabaseConstants.VEHICLE_PREFIX + id;
        deleteFromCache(redisKey);

        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + "all");
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.CAR_DISCRIMINATOR);
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.BICYCLE_DISCRIMINATOR);
        deleteFromCache(DatabaseConstants.VEHICLE_PREFIX + DatabaseConstants.MOPED_DISCRIMINATOR);

    }

    @Override
    public MongoClient getClient() {
        return vehicleRepository.getClient();
    }


    //public void checkConnection() {
    //    jedis.connect();
    //    jedis.ping();
    //    if (jedis.getConnection().isConnected()) {
    //        System.out.println("Connected to Redis");
    //    }
    //    else {
    //        System.out.println("Not connected to Redis");
    //    }
    //    jedis.close();
    //}
    //
    //public List<CarMgd> findAll() {
    //    List<CarMgd> carMgdList = new ArrayList<>();
    //    if (jedis.getConnection().isConnected()) {
    //        //pobierz z redisa, jesli są jakies dane
    //        //JsonWriter jsonWriter = new JsonWriter(jedis);
    //        //jedis.set("vehicles", ve)
    //        //String key = "vehicles";
    //        //ScanParams scanParams = new ScanParams().count(100);
    //        //String cur = redis.clients.jedis.ScanParams.SCAN_POINTER_START;
    //        //boolean cycleIsFinished = false;
    //        //while (!cycleIsFinished) {
    //        //    ScanResult<Map.Entry<String, String>> scanResult =
    //        //            jedis.hscan(key, cur, scanParams);
    //        //    List<Map.Entry<String, String>> result = scanResult.getResult();
    //        //
    //        //    //do whatever with the key-value pairs in result
    //        //
    //        //    cur = scanResult.getStringCursor();
    //        //    if (cur.equals("0")) {
    //        //        cycleIsFinished = true;
    //        //    }
    //        //
    //        //}
    //    }
    //    else {
    //        // pobierz z mongo
    //        carMgdList = carRepository.findAll();
    //        //zapisz do cache
    //        for (CarMgd carMgd : carMgdList) {
    //            jedis.set(hashPrefix + carMgd.getId(), jsonObjectMapper.toJson(carMgd));
    //        }
    //
    //    }
    //    return carMgdList;
    //}
    //
    //




}
