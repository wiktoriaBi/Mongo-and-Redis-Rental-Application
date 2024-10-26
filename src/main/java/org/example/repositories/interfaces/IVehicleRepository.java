package org.example.repositories.interfaces;

import org.example.repositories.interfaces.IObjectRepository;

public interface IVehicleRepository<T> extends IObjectRepository<T> {

    T findByPlateNumber(String plateNumber);
}
