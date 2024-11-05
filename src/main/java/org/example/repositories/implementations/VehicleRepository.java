package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import org.example.model.AbstractEntity;
import org.example.repositories.implementations.ObjectRepository;
import org.example.repositories.interfaces.IVehicleRepository;


public class VehicleRepository<T extends AbstractEntity> extends ObjectRepository<T> implements IVehicleRepository<T> {

    public VehicleRepository(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public T findByPlateNumber(String plateNumber) {

        //todo implement
        return null;
    }
}
