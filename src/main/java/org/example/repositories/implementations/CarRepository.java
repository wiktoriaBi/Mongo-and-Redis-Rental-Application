package org.example.repositories.implementations;

import com.mongodb.MongoWriteException;
import com.mongodb.client.*;

import org.example.mgd.CarMgd;
import org.example.model.Car;

import org.example.repositories.interfaces.ICarRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.*;


public class CarRepository extends VehicleRepository<Car, CarMgd> implements ICarRepository {

    public CarRepository(java.util.function.Function<CarMgd, Car> toModelMapper,
                         java.util.function.Function<Car, CarMgd> toMgdMapper,
                         Class<CarMgd> mgdClass) {
        super(toModelMapper, toMgdMapper, mgdClass);
    }


    // Create methods
    @Override
    public Car create(String plateNumber, Double basePrice, Integer engine_displacement, Car.TransmissionType transmissionType) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();
            CarMgd carMgd = new CarMgd(
                    UUID.randomUUID(),
                    plateNumber,
                    basePrice,
                    false,
                    0,
                    engine_displacement,
                    transmissionType
            );
        MongoCollection<CarMgd> vehicleCollection = super.getRentACarDB()
                .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, DatabaseConstants.CAR_COLLECTION_TYPE);
            vehicleCollection.insertOne(carMgd);
            return new Car(carMgd);
        } catch (MongoWriteException exception) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("CarRepository: Vehicle with provided plate number already exists!");
        }
    }

}
