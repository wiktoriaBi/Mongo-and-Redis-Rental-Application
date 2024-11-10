package org.example.repositories.interfaces;

import org.example.model.Car;


public interface ICarRepository extends IVehicleRepository<Car> {

    // Create methods
    Car create(String plateNumber, Double basePrice, Integer engine_displacement, Car.TransmissionType transmissionType);



}
