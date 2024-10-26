package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.Moped;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IMopedRepository;

public class MopedRepository extends VehicleRepository<Moped> implements IMopedRepository {

    public MopedRepository(EntityManager em, Class<Moped> entityClass) {
        super(em, entityClass);
    }
}
