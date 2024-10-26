package org.example.services.implementations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.commons.dto.BicycleCreateDTO;
import org.example.commons.dto.MotorVehicleCreateDTO;
import org.example.model.*;
import org.example.repositories.implementations.BicycleRepository;
import org.example.repositories.implementations.CarRepository;
import org.example.repositories.implementations.MopedRepository;
import org.example.repositories.implementations.VehicleRepository;
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

        Bicycle bicycle = new Bicycle(
                bicycleCreateDTO.getPlateNumber(),
                bicycleCreateDTO.getBasePrice(),
                bicycleCreateDTO.getPedalNumber()
        );
        IVehicleRepository<Bicycle> bicycleRepository = new BicycleRepository(em, Bicycle.class);
        return bicycleRepository.save(bicycle);
    }

    public Car addCar(MotorVehicleCreateDTO motorVehicleCreateDTO) {

        Car car = new Car(
                motorVehicleCreateDTO.getPlateNumber(),
                motorVehicleCreateDTO.getBasePrice(),
                motorVehicleCreateDTO.getEngine_displacement()
        );
        IVehicleRepository<Car> carRepository = new CarRepository(em, Car.class);
        return carRepository.save(car);
    }

    public Moped addMoped(MotorVehicleCreateDTO motorVehicleCreateDTO) {

        Moped moped = new Moped(
                motorVehicleCreateDTO.getPlateNumber(),
                motorVehicleCreateDTO.getBasePrice(),
                motorVehicleCreateDTO.getEngine_displacement()
        );
        IVehicleRepository<Moped> mopedRepository = new MopedRepository(em, Moped.class);
        return mopedRepository.save(moped);
    }

    public void updatePlateNumber(UUID vehicleId, String plateNumber) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId);
        vehicle.setPlateNumber(plateNumber);
    }

    public void updateBasePrice(UUID vehicleId, Double basePrice) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId);
        vehicle.setBasePrice(basePrice);
    }

    public void updateEngineDisplacement(UUID vehicleId, Integer displacement) {
        IVehicleRepository<MotorVehicle> vehicleRepository = new VehicleRepository<>(em, MotorVehicle.class);
        MotorVehicle motorVehicle = vehicleRepository.findById(vehicleId);
        motorVehicle.setEngine_displacement(displacement);
    }

    public void updatePedalsNumber(UUID vehicleId, Integer pedalsNumber) {
        IVehicleRepository<Bicycle> vehicleRepository = new VehicleRepository<>(em, Bicycle.class);
        Bicycle bicycle = vehicleRepository.findById(vehicleId);
        bicycle.setPedalsNumber(pedalsNumber);
    }

    public void updateVehicleArchive(UUID vehicleId, boolean archive) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId);
        if(archive && !vehicle.isRented()){
            vehicle.setArchive(true);
        }
        else{
            em.getTransaction().rollback();
            throw new RuntimeException("The vehicle cannot be archived!");
        }
    }

    public void removeVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId);
        List<Rent> allVehicleRents = rentRepository.findAllByVehicleId(vehicleId);
        if(allVehicleRents.isEmpty() && !vehicle.isRented()) {
            vehicleRepository.remove(vehicle);
        }
        else{
            em.getTransaction().rollback();
            throw new RuntimeException("The vehicle cannot be deleted!");
        }
    }

}
