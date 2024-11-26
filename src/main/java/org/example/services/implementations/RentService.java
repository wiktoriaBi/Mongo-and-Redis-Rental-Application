package org.example.services.implementations;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import lombok.Getter;
import org.example.commons.dto.create.RentCreateDTO;
import org.example.mgd.ClientMgd;
import org.example.mgd.ClientTypeMgd;
import org.example.mgd.RentMgd;
import org.example.mgd.VehicleMgd;
import org.example.model.Client;
import org.example.model.ClientType;
import org.example.model.Rent;
import org.example.model.Vehicle;
import org.example.repositories.mongo.implementations.ClientRepository;
import org.example.repositories.mongo.implementations.ClientTypeRepository;
import org.example.repositories.mongo.implementations.RentRepository;
import org.example.repositories.mongo.implementations.VehicleRepository;
import org.example.repositories.mongo.interfaces.IClientRepository;
import org.example.repositories.mongo.interfaces.IClientTypeRepository;
import org.example.repositories.mongo.interfaces.IRentRepository;
import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.services.interfaces.IRentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class RentService extends ObjectService implements IRentService {

    private final IClientRepository clientRepository;
    private final IRentRepository rentRepository;
    private final IVehicleRepository vehicleRepository;
    private final IClientTypeRepository clientTypeRepository;


    public RentService() {
        super();
        this.rentRepository = new RentRepository(super.getClient(), RentMgd.class);
        this.vehicleRepository = new VehicleRepository(super.getClient());
        this.clientRepository = new ClientRepository(super.getClient(), ClientMgd.class);
        this.clientTypeRepository = new ClientTypeRepository(super.getClient(), ClientTypeMgd.class);
    }


    @Override
    public Rent createRent(RentCreateDTO createRentDTO) {
        ClientSession clientSession  = super.getClient().startSession();
        try {
            clientSession.startTransaction();
            ClientMgd foundClient = clientRepository.findById(createRentDTO.clientId());
            VehicleMgd foundVehicle = vehicleRepository.findAnyVehicle(createRentDTO.vehicleId());

            if (foundClient == null && foundVehicle == null) {
                throw new RuntimeException("RentRepository: Client or Vehicle not found");
            }

            if (createRentDTO.endTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("RentRepository: Invalid end time ");

            }

            ClientMgd clientMgd = clientRepository.findById(createRentDTO.clientId());
            ClientTypeMgd clientTypeMgd = clientTypeRepository.findAnyClientType(clientMgd.getClientType());

            if (Objects.equals(clientMgd.getActiveRents(), clientTypeMgd.getMaxVehicles())) {
                throw new RuntimeException("RentRepository: Client has max vehicles");
            }

            foundClient = clientRepository.increaseActiveRents(createRentDTO.clientId(), 1);


            foundVehicle = vehicleRepository.changeRentedStatus(foundVehicle.getId(), true);

            Rent rent = new Rent(
                    UUID.randomUUID(),
                    createRentDTO.endTime(),
                    new Client(foundClient, new ClientType(clientTypeMgd)),
                    new Vehicle(foundVehicle)
            );
            RentMgd rentMgd = new RentMgd(rent, foundClient, foundVehicle);
            rentRepository.save(rentMgd);
            return rent;
        }
        catch (MongoWriteException e) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("RentRepository: Vehicle already rented!");
        }
        catch (RuntimeException e) {
            clientSession.abortTransaction();
            clientSession.close();
            throw e;
        }
    }

    @Override
    public Rent findRentById(UUID id) {
        RentMgd rentMgd = rentRepository.findById(id);
        VehicleMgd vehicleMgd = vehicleRepository.findAnyVehicle(rentMgd.getVehicle().getId());
        ClientMgd clientMgd = clientRepository.findById(rentMgd.getClient().getId());
        ClientTypeMgd clientTypeMgd = clientTypeRepository.findAnyClientType(clientMgd.getClientType());
        ClientType clientType = new ClientType(clientTypeMgd);

        return new Rent(rentMgd, new Client(clientMgd, clientType), new Vehicle(vehicleMgd));

    }

    @Override
    public List<Rent> findAllActiveByClientID(UUID clientId) {
        return rentRepository.findAllActiveByClientId(clientId).stream().map(Rent::new).toList();
    }

    @Override
    public List<Rent> findAllArchivedByClientID(UUID clientId) {
        return rentRepository.findAllArchivedByClientId(clientId).stream().map(Rent::new).toList();
    }

    @Override
    public List<Rent> findAllActiveByVehicleID(UUID vehicleId) {
        return rentRepository.findAllActiveByVehicleId(vehicleId).stream().map(Rent::new).toList();
    }

    @Override
    public List<Rent> findAllArchivedByVehicleID(UUID vehicleId) {
        return rentRepository.findAllArchivedByVehicleId(vehicleId).stream().map(Rent::new).toList();
    }

    @Override
    public Rent updateRent(UUID id, LocalDateTime endTime) {

        RentMgd rentMgd = rentRepository.findActiveById(id);
        VehicleMgd vehicleMgd = vehicleRepository.findById(rentMgd.getVehicle().getId());
        ClientMgd clientMgd = clientRepository.findById(rentMgd.getClient().getId());

        Rent rent = findRentById(id);

        try {
            if (!endTime.isAfter(rentMgd.getEndTime()) ) {
                throw new RuntimeException("RentRepository: New Rent end time cannot be before current rent end time");
            }
            rent.setEndTime(endTime);
            rent.recalculateRentCost();
            rentRepository.save(new RentMgd(rent, clientMgd, vehicleMgd));
            return rent;

        } catch (RuntimeException e) {
            throw new RuntimeException("RentRepository: New Rent end time cannot be before current rent end time");
        }

    }

    @Override
    public void endRent(UUID id) {
        ClientSession clientSession = rentRepository.getClient().startSession();
        try {
            clientSession.startTransaction();
            RentMgd rent = rentRepository.findActiveById(id);
            vehicleRepository.changeRentedStatus(rent.getVehicle().getId(), false);
            clientRepository.increaseActiveRents(rent.getClient().getId(), -1);
            rentRepository.moveRentToArchived(id);
            clientSession.commitTransaction();
        } catch (RuntimeException e) {
            clientSession.abortTransaction();
            clientSession.close();
            throw e;
        }
    }


}
