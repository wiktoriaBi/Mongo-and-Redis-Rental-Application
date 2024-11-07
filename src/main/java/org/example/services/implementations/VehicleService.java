package org.example.services.implementations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.commons.dto.BicycleCreateDTO;
import org.example.commons.dto.MopedCreateDTO;
import org.example.model.*;
import org.example.repositories.implementations.BicycleRepository;
import org.example.repositories.implementations.CarRepository;
import org.example.repositories.implementations.MopedRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IBicycleRepository;
import org.example.repositories.interfaces.ICarRepository;
import org.example.repositories.interfaces.IMopedRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class VehicleService {

    private final EntityManager em;
    private final IRentRepository rentRepository;
    private final IVehicleRepository<Vehicle> vehicleRepository;

    public Bicycle addBicycle(BicycleCreateDTO bicycleCreateDTO) {
        IBicycleRepository bicycleRepository = new BicycleRepository(Bicycle.class);
        return bicycleRepository.createBicycle(bicycleCreateDTO.getPlateNumber(),
                                               bicycleCreateDTO.getBasePrice(),
                                               bicycleCreateDTO.getPedalNumber()
        );
    }

    public Car addCar(MopedCreateDTO mopedCreateDTO) {
        //ICarRepository carRepository = new CarRepository(Car.class);
        //return carRepository.createCar(mopedCreateDTO.getPlateNumber(),
        //                               mopedCreateDTO.getBasePrice(),
        //                               mopedCreateDTO.getEngine_displacement(),
        // );
        return null;
    }

    public Moped addMoped(MopedCreateDTO mopedCreateDTO) {
        //IMopedRepository mopedRepository = new MopedRepository(Moped.class);
        //return mopedRepository.createMoped(mopedCreateDTO.getPlateNumber(),
        //                                   mopedCreateDTO.getBasePrice(),
        //                                   mopedCreateDTO.getEngine_displacement()
        // );
        return null;
    }

    public void updatePlateNumber(UUID vehicleId, String plateNumber) {
        //Vehicle vehicle = vehicleRepository.findById(vehicleId);
        //vehicle.setPlateNumber(plateNumber);

    }

    public void updateBasePrice(UUID vehicleId, Double basePrice) {
        //Vehicle vehicle = vehicleRepository.findById(vehicleId);
        //vehicle.setBasePrice(basePrice);

    }

    public void updateEngineDisplacement(UUID vehicleId, Integer displacement) {
        //IVehicleRepository<MotorVehicle> vehicleRepository = new VehicleRepository<>(MotorVehicle.class);
        //MotorVehicle motorVehicle = vehicleRepository.findById(vehicleId);
        //motorVehicle.setEngine_displacement(displacement);
    }

    public void updatePedalsNumber(UUID vehicleId, Integer pedalsNumber) {
        //IVehicleRepository<Bicycle> vehicleRepository = new VehicleRepository<>(Bicycle.class);
        //Bicycle bicycle = vehicleRepository.findById(vehicleId);
        //bicycle.setPedalsNumber(pedalsNumber);
    }

    public void updateVehicleArchive(UUID vehicleId, boolean archive) {
        //Vehicle vehicle = vehicleRepository.findById(vehicleId);
        //if(archive && !vehicle.isRented()){
        //    vehicle.setArchive(true);
        //}
        //else{
        //    em.getTransaction().rollback();
        //    throw new RuntimeException("The vehicle cannot be archived!");
        //}
    }

    public void removeVehicle(UUID vehicleId) {
        //Vehicle vehicle = vehicleRepository.findById(vehicleId);
        //List<Rent> allVehicleRents = rentRepository.findAllByVehicleId(vehicleId);
        //if(allVehicleRents.isEmpty() && !vehicle.isRented()) {
        //    vehicleRepository.remove(vehicle);
        //}
        //else{
        //    em.getTransaction().rollback();
        //    throw new RuntimeException("The vehicle cannot be deleted!");
        //}
    }

}
