package org.example.repositories.interfaces;

import com.mongodb.client.MongoClient;

import java.util.List;
import java.util.UUID;

public interface IVehicleRepository<T> {

    // Read methods
    List<T> findAll();

    T findById(UUID id);

    T findByPlateNumber(String plateNumber);

    // Update methods
    void update(T modifiedVehicle);

    // Delete methods
    void deleteById(UUID id);

    // Other
    MongoClient getMongoClient();

}
