package org.example.repositories.mongo.interfaces;

import org.example.mgd.vehicle.VehicleMgd;

import java.util.List;
import java.util.UUID;

public interface IVehicleRepository extends IObjectRepository<VehicleMgd> {

    VehicleMgd findByPlateNumber(String plateNumber);

    VehicleMgd findByIdAndDiscriminator(UUID id, String discriminator);

    List<VehicleMgd> findAllByDiscriminator(String discriminator);

    VehicleMgd findAnyVehicle(UUID vehicleId);

    VehicleMgd changeRentedStatus(UUID id, Boolean status);

}
