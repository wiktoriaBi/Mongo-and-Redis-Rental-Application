package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import org.example.model.AbstractEntity;
import org.example.repositories.implementations.ObjectRepository;
import org.example.repositories.interfaces.IVehicleRepository;


public class VehicleRepository<T extends AbstractEntity> extends ObjectRepository<T> implements IVehicleRepository<T> {

    public VehicleRepository(EntityManager em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public T findByPlateNumber(String plateNumber) {

        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        From<T,T> from = query.from(getEntityClass());
        query.select(from).where(cb.equal(from.get("plateNumber"), plateNumber));

        return getEm().createQuery(query).getSingleResult();
    }
}
