package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.Car;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.ICarRepository;



public class CarRepository extends VehicleRepository<Car> implements ICarRepository {

    public CarRepository(EntityManager em, Class<Car> entityClass) {
        super(em, entityClass);
    }

}
