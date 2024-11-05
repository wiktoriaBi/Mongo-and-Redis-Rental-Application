package org.example.repositories.interfaces;

import org.example.model.Bicycle;


public interface IBicycleRepository extends IVehicleRepository<Bicycle> {

    Bicycle createBicycle(String plateNumber, Double basePrice, Integer pedal_num);




}
