package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.MotorVehicle;
import org.example.repositories.interfaces.IMotorVehicleRepository;

public class MotorVehicleRepository extends VehicleRepository<MotorVehicle> implements IMotorVehicleRepository {

    public MotorVehicleRepository(Class<MotorVehicle> entityClass) {
        super(entityClass);
    }
}
