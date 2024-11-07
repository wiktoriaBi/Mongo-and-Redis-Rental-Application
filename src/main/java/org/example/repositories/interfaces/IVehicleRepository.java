package org.example.repositories.interfaces;

import org.example.repositories.interfaces.IObjectRepository;

public interface IVehicleRepository<T> {

    T findByPlateNumber(String plateNumber);
}
