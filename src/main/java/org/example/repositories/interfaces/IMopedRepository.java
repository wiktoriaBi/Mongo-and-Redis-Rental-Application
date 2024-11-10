package org.example.repositories.interfaces;

import org.example.model.Moped;

public interface IMopedRepository extends IVehicleRepository<Moped> {
    Moped create(String plateNumber, Double basePrice, Integer engine_displacement);
}
