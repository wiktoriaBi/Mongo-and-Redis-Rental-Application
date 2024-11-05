package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.Bicycle;
import org.example.model.Car;
import org.example.repositories.interfaces.IBicycleRepository;

import java.util.UUID;


public class BicycleRepository extends VehicleRepository<Bicycle> implements IBicycleRepository {

    public BicycleRepository(Class<Bicycle> entityClass) {
        super(entityClass);
    }

    @Override
    public Bicycle createBicycle(String plateNumber, Double basePrice, Integer pedal_num) {
        Bicycle bicycle = new Bicycle(
                UUID.randomUUID(),
                plateNumber,
                basePrice,
                pedal_num
        );
        save(bicycle);
        return bicycle;

    }
}
