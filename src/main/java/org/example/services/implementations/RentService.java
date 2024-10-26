package org.example.services.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.example.commons.dto.RentCreateDTO;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.Vehicle;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IRentService;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class RentService implements IRentService {

    private final EntityManager em;

    private final IClientRepository clientRepository;
    private final IRentRepository rentRepository;
    private final IVehicleRepository<Vehicle> vehicleRepository;

    public Rent createRent(RentCreateDTO createRentDTO) {

        try {
            Client foundCLient = clientRepository.findById(createRentDTO.getClientId());
            Vehicle foundVehicle = vehicleRepository.findById(createRentDTO.getVehicleId());

            if (foundCLient == null && foundVehicle == null) {
                throw new RuntimeException("RentRepository: Client or Vehicle not found");
            }

            if (foundVehicle.isRented()) {
                throw new RuntimeException("RentRepository: Vehicle is rented");
            }

            if (createRentDTO.getEndTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("RentRepository: Invalid end time ");

            }

            if (foundCLient.getClientType().getMaxVehicles() == rentRepository.findAllActiveByClientId(createRentDTO.getClientId()).size() ) {
                throw new RuntimeException("RentRepository: Client has max vehicles");

            }

            if (foundVehicle.isRented()) {
                throw new RuntimeException("RentRepository: Vehicle already rented");
            }

            Rent rent = new Rent(
                    LocalDateTime.now(),
                    createRentDTO.getEndTime(),
                    foundCLient,
                    foundVehicle
            );

            em.lock(foundCLient, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            clientRepository.save(foundCLient);
            rentRepository.save(rent);

            foundVehicle.setRented(true);
            vehicleRepository.save(foundVehicle);

            return rent;

        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public Rent findRentById(UUID id) {
        return rentRepository.findById(id);
    }

    public Rent updateRentEndTime(UUID id, LocalDateTime endTime) {

        Rent rent = rentRepository.findById(id);

        try {
            if (!endTime.isAfter(rent.getEndTime()) ) {
                throw new RuntimeException("RentRepository: New Rent end time cannot be before current rent end time");
            }

            rent.setEndTime(endTime);
            rent.recalculateRentCost();
            rentRepository.save(rent);
            return rent;

        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }

    }

    public void removeRent(UUID id) {

        Rent rent = rentRepository.findById(id);

        if (rent.isActive()) {
            em.getTransaction().rollback();
            throw new RuntimeException("RentRepository: Active rent cannot be removed!");
        }
        rentRepository.remove(rent);
    }


    public void endRent(UUID id) {
        Rent rent = rentRepository.findById(id);

        try {
            if (rent.isActive()) {
                rent.setActive(false);
                rentRepository.save(rent);

                rent.getVehicle().setRented(false);

                vehicleRepository.save(rent.getVehicle());
            }
            else {
                throw new RuntimeException("RentRepository: Rent is not active");
            }
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }


    }

}
