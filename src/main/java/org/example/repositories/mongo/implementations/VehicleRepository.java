package org.example.repositories.mongo.implementations;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.mgd.BicycleMgd;
import org.example.mgd.CarMgd;
import org.example.mgd.MopedMgd;
import org.example.mgd.VehicleMgd;

import org.example.repositories.mongo.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.*;


public class VehicleRepository extends ObjectRepository<VehicleMgd> implements IVehicleRepository {

    public VehicleRepository(MongoClient client) {
        super(client, VehicleMgd.class);

        boolean collectionExist = super.getDatabase().listCollectionNames()
                .into(new ArrayList<>()).contains(DatabaseConstants.VEHICLE_COLLECTION_NAME);

        if (!collectionExist) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse(
                            """
                                    {
                                        $jsonSchema: {
                                            "bsonType": "object",
                                            "required": ["_id", "rented"],
                                            "properties": {
                                                "rented" : {
                                                    "bsonType" : "int",
                                                    "minimum" : 0,
                                                    "maximum" : 1,
                                                    "description" : "must be between 0 and 1"
                                                }
                                            }
                                        }
                                    }
                                    """));

            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            super.getDatabase().createCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, createCollectionOptions);
            Bson plateNumberIndex = new BasicDBObject(DatabaseConstants.VEHICLE_PLATE_NUMBER, 1);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            super.getDatabase().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME)
                    .createIndex(plateNumberIndex, indexOptions);
        }

    }


    public String getDiscriminatorForClass(Class<?> mgdClass) {
        if (mgdClass.equals(CarMgd.class)) {
            return DatabaseConstants.CAR_DISCRIMINATOR;
        }
        else if (mgdClass.equals(BicycleMgd.class)) {
            return DatabaseConstants.BICYCLE_DISCRIMINATOR;
        }
        else if (mgdClass.equals(MopedMgd.class)) {
            return DatabaseConstants.MOPED_DISCRIMINATOR;
        }
        return DatabaseConstants.VEHICLE;
    }

    @Override
    public VehicleMgd findByPlateNumber(String plateNumber) {
        ClientSession clientSession = this.getClient().startSession();
        try {
            MongoCollection<VehicleMgd> vehicleCollection = super.getDatabase().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    getMgdClass());;
            Bson plateNumberFilter = Filters.eq(DatabaseConstants.VEHICLE_PLATE_NUMBER, plateNumber);
            VehicleMgd foundCar = vehicleCollection.find(plateNumberFilter).first();

            if (foundCar == null) {
                throw  new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n Car with provided plate number was not found!!!");
            }
            return foundCar;

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }

    @Override
    public VehicleMgd findByIdAndDiscriminator(UUID id, String discriminator) {
        try {
            MongoCollection<Document> vehicleCollection = this.getDatabase()
                    .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME);
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminator);
            Document vehicleDoc = vehicleCollection.find(discriminatorFilter).first();

            if (vehicleDoc == null) {
                throw new RuntimeException("Vehicle not found");
            }
            Class<?> mgdClass = getDiscriminatorForString(discriminator);

            if (mgdClass.equals(CarMgd.class)) {
                return new CarMgd(vehicleDoc);
            }
            else if (mgdClass.equals(BicycleMgd.class)) {
                return new BicycleMgd(vehicleDoc);
            }
            else if (mgdClass.equals(MopedMgd.class)) {
                return new MopedMgd(vehicleDoc);
            }
            else {
                throw new IllegalArgumentException("Unknown vehicle type: " + discriminator);
            }
        } catch (MongoCommandException e) {
            throw new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n, findAll: MongoCommandException!");
        }
    }


    @Override
    public List<VehicleMgd> findAll() {
        try {
            MongoCollection<VehicleMgd> vehicleCollection = this.getDatabase()
                    .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, getMgdClass());
            return vehicleCollection.find().into(new ArrayList<>());
        } catch (MongoCommandException e) {
            throw new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n, findAll: MongoCommandException!");
        }
    }

    @Override
    public List<VehicleMgd> findAllByDiscriminator(String discriminator) {
        try {
            MongoCollection<VehicleMgd> vehicleCollection = this.getDatabase()
                    .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, getMgdClass());
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminator);

            return vehicleCollection.find(discriminatorFilter).into(new ArrayList<>());
        } catch (MongoCommandException e) {
            throw new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n, findAll: MongoCommandException!");
        }
    }


    public static Class<?> getDiscriminatorForString(String discriminator) {
        return switch (discriminator) {
            case DatabaseConstants.CAR_DISCRIMINATOR -> CarMgd.class;
            case DatabaseConstants.MOPED_DISCRIMINATOR -> MopedMgd.class;
            case DatabaseConstants.BICYCLE_DISCRIMINATOR -> BicycleMgd.class;
            case null, default ->
                    throw new IllegalArgumentException("Unknown vehicle type: " + discriminator);
        };
    }

    @Override
    public VehicleMgd findAnyVehicle(UUID id) {
        MongoCollection<Document> vehicleMgdMongoCollection = super.getDatabase()
                .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME);
        Bson filter = Filters.eq(DatabaseConstants.ID, id);
        Document vehicleDoc = vehicleMgdMongoCollection.find(filter).first();
        if (vehicleDoc == null) {
            throw new RuntimeException("Vehicle not found");
        }
        String discriminatorValue = vehicleDoc.getString(DatabaseConstants.BSON_DISCRIMINATOR_KEY);

        Class<?> mgdClass = getDiscriminatorForString(discriminatorValue);

        if (mgdClass.equals(CarMgd.class)) {
            return new CarMgd(vehicleDoc);
        }
        else if (mgdClass.equals(BicycleMgd.class)) {
            return new BicycleMgd(vehicleDoc);
        }
        else if (mgdClass.equals(MopedMgd.class)) {
            return new MopedMgd(vehicleDoc);
        }
        else {
            throw new IllegalArgumentException("Unknown vehicle type: " + discriminatorValue);
        }

    }

    @Override
    public VehicleMgd changeRentedStatus(UUID id, Boolean status) {

        MongoCollection<VehicleMgd> vehicleCollection = super.getDatabase().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                getMgdClass());
        VehicleMgd vehicleMgd = findAnyVehicle(id);
        Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, getDiscriminatorForClass(vehicleMgd.getClass()));
        Bson idFilter = Filters.eq(DatabaseConstants.ID, id);
        Bson bothFilters = Filters.and(discriminatorFilter, idFilter);
        VehicleMgd foundCar = vehicleCollection.find(bothFilters).first();
        if (foundCar == null) {
            throw new RuntimeException("Vehicle with provided id was not found!!!");
        }
        Bson updateOperation;
        if (status) {
            updateOperation = Updates.inc(DatabaseConstants.VEHICLE_RENTED, 1);
        }
        else {
            updateOperation = Updates.inc(DatabaseConstants.VEHICLE_RENTED, -1);
        }
        vehicleCollection.updateOne(bothFilters, updateOperation);
        return vehicleCollection.find(bothFilters).first();
    }
}
