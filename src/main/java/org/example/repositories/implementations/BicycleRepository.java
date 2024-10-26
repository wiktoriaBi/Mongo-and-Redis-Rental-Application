package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.Bicycle;
import org.example.repositories.interfaces.IBicycleRepository;


public class BicycleRepository extends VehicleRepository<Bicycle> implements IBicycleRepository {

    public BicycleRepository(EntityManager em, Class<Bicycle> entityClass) {
        super(em, entityClass);
    }
}
