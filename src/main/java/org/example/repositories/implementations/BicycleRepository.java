package org.example.repositories.implementations;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import org.example.mgd.BicycleMgd;
import org.example.model.Bicycle;
import org.example.repositories.interfaces.IBicycleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;


public class BicycleRepository extends VehicleRepository<Bicycle, BicycleMgd> implements IBicycleRepository {

    public BicycleRepository(java.util.function.Function<BicycleMgd, Bicycle> toModelMapper,
                             java.util.function.Function<Bicycle, BicycleMgd> toMgdMapper,
                             Class<BicycleMgd> mgdClass) {
        super(toModelMapper, toMgdMapper, mgdClass);
    }

    @Override
    public Bicycle create(String plateNumber, Double basePrice, Integer pedal_num) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();
            BicycleMgd bicycleMgd = new BicycleMgd(
                    UUID.randomUUID(),
                    plateNumber,
                    basePrice,
                    false,
                    0,
                    pedal_num
            );
            MongoCollection<BicycleMgd> vehicleCollection = super.getRentACarDB()
                    .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, DatabaseConstants.BICYCLE_COLLECTION_TYPE);
            vehicleCollection.insertOne(bicycleMgd);
            return new Bicycle(bicycleMgd);
        } catch (MongoWriteException exception) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("BicycleRepository: Bicycle with provided plate number already exists!");
        }
    }
}
