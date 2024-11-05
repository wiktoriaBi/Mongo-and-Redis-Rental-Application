package org.example.repositories.implementations;

import org.example.model.Car;
import org.example.model.Moped;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IMopedRepository;

import java.util.UUID;

public class MopedRepository extends VehicleRepository<Moped> implements IMopedRepository {

    public MopedRepository(Class<Moped> entityClass) {
        super(entityClass);
    }

    public Moped createMoped(String plateNumber, Double basePrice, Integer engine_displacement) {
        Moped moped = new Moped(
                UUID.randomUUID(),
                plateNumber,
                basePrice,
                engine_displacement
        );
        save(moped);
        return moped;
    }

}
