package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Rent;
import org.example.repositories.interfaces.IRentRepository;

import java.util.List;
import java.util.UUID;

public class RentRepository extends ObjectRepository<Rent> implements IRentRepository {


    public RentRepository(EntityManager em, Class<Rent> entityClass) {
        super(em, entityClass);
    }


    @Override
    public List<Rent> findAllActiveByClientId(UUID clientId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.client.id = :clientId AND r.active = true", Rent.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public List<Rent> findAllByClientId(UUID clientId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.client.id = :clientId", Rent.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public List<Rent> findAllArchivedByClientId(UUID clientId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.client.id = :clientId AND r.active = false ", Rent.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public List<Rent> findAllArchivedByVehicleId(UUID vehicleId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.vehicle.id = :vehicleId AND r.active = false", Rent.class);
        query.setParameter("vehicleId", vehicleId);
        return query.getResultList();
    }

    @Override
    public List<Rent> findAllActiveByVehicleId(UUID vehicleId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.vehicle.id = :vehicleId AND r.active = true ", Rent.class);
        query.setParameter("vehicleId", vehicleId);
        return query.getResultList();
    }

    @Override
    public List<Rent> findAllByVehicleId(UUID vehicleId) {
        TypedQuery<Rent> query = getEm().createQuery("SELECT r FROM Rent r WHERE r.vehicle.id = :vehicleId AND r.active = true ", Rent.class);
        query.setParameter("vehicleId", vehicleId);
        return query.getResultList();
    }

}
