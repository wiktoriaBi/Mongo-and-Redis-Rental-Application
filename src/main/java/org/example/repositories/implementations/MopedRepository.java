package org.example.repositories.implementations;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import org.example.mgd.MopedMgd;
import org.example.model.Moped;
import org.example.repositories.interfaces.IMopedRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

public class MopedRepository extends VehicleRepository<Moped, MopedMgd> implements IMopedRepository {

    public MopedRepository(java.util.function.Function<MopedMgd, Moped> toModelMapper,
                           java.util.function.Function<Moped, MopedMgd> toMgdMapper,
                           Class<MopedMgd> mgdClass) {
        super(toModelMapper, toMgdMapper, mgdClass);
    }

    public Moped create(String plateNumber, Double basePrice, Integer engine_displacement) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();
            MopedMgd mopedMgd = new MopedMgd(
                    UUID.randomUUID(),
                    plateNumber,
                    basePrice,
                    false,
                    0,
                    engine_displacement
            );
            MongoCollection<MopedMgd> vehicleCollection = super.getRentACarDB()
                    .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, DatabaseConstants.MOPED_COLLECTION_TYPE);
            vehicleCollection.insertOne(mopedMgd);
            return new Moped(mopedMgd);
        } catch (MongoWriteException exception) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("MopedRepository: Moped with provided plate number already exists!");
        }
    }

}
