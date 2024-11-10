package org.example.repositories.implementations;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.example.mgd.BicycleMgd;
import org.example.mgd.CarMgd;
import org.example.mgd.MopedMgd;
import org.example.model.Vehicle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class VehicleRepository<T extends Vehicle, M> extends ObjectRepository<T, M> implements IVehicleRepository<T> {


    private String discriminatorValue;

    private final java.util.function.Function<T, M> toMgdMapper;

    public VehicleRepository(java.util.function.Function<M, T> toModelMapper,
                             java.util.function.Function<T, M> toMgdMapper,
                             Class<M> mgdClass) {
        super(toModelMapper, toMgdMapper, mgdClass);

        this.toMgdMapper = toMgdMapper;
        if (mgdClass.equals(CarMgd.class)) {
            this.discriminatorValue = DatabaseConstants.CAR_DISCRIMINATOR;
        }
        else if (mgdClass.equals(BicycleMgd.class)) {
            this.discriminatorValue = DatabaseConstants.BICYCLE_DISCRIMINATOR;
        }
        else if (mgdClass.equals(MopedMgd.class)) {
            this.discriminatorValue = DatabaseConstants.MOPED_DISCRIMINATOR;
        }

        initDatabaseCon(DatabaseConstants.DATABASE_NAME);
        boolean collectionExist = getRentACarDB().listCollectionNames()
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
            super.getRentACarDB().createCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, createCollectionOptions);
            Bson plateNumberIndex = new BasicDBObject(DatabaseConstants.VEHICLE_PLATE_NUMBER, 1);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME)
                    .createIndex(plateNumberIndex, indexOptions);
        }

    }

    @Override
    public T findById(UUID id) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<M> vehicleCollection = super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    getMgdClass());
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminatorValue);
            Bson idFilter = Filters.eq(DatabaseConstants.ID, id);
            Bson bothFilters = Filters.and(discriminatorFilter, idFilter);
            M foundCar = vehicleCollection.find(bothFilters).first();

            if (foundCar == null) {
                throw  new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n Vehicle with provided UUID was not found!!!");
            }
            return getToModelMapper().apply(foundCar);

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }

    @Override
    public T findByPlateNumber(String plateNumber) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<M> vehicleCollection = super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    getMgdClass());
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminatorValue);
            Bson plateNumberFilter = Filters.eq(DatabaseConstants.VEHICLE_PLATE_NUMBER, plateNumber);
            Bson bothFilters = Filters.and(discriminatorFilter, plateNumberFilter);
            M foundCar = vehicleCollection.find(bothFilters).first();

            if (foundCar == null) {
                throw  new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n Car with provided plate number was not found!!!");
            }
            return getToModelMapper().apply(foundCar);

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }

    // Read methods
    @Override
    public List<T> findAll() {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<M> vehicleCollection = super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    super.getMgdClass());
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminatorValue);
            List<M> foundCarsMgd = new ArrayList<>();
            vehicleCollection.find(discriminatorFilter).into(foundCarsMgd);
            return foundCarsMgd.stream().map(getToModelMapper()).collect(Collectors.toList());
        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n, findAll: MongoCommandException!");
        }
    }

    // Update methods

    private static void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
    }

    @Override
    public void update(T modifiedVehicle) {
        ClientSession clientSession = getMongoClient().startSession();
        try{
            clientSession.startTransaction();
            MongoCollection<M> vehicleCollection = super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    getMgdClass());
            Bson filter = Filters.eq(DatabaseConstants.ID, modifiedVehicle.getId());
            M modifiedCarDoc = this.toMgdMapper.apply(modifiedVehicle);
            List<Bson> updates = new ArrayList<>();

            List<Field> fieldList = new ArrayList<>();
            getAllFields(fieldList, modifiedCarDoc.getClass());

            for (Field field : fieldList) {
                field.setAccessible(true);
                Object value = field.get(modifiedCarDoc);
                if (value != null) {
                    updates.add(Updates.set(field.getAnnotation(BsonProperty.class).value(), value));
                }
                field.setAccessible(false);
            }
            Bson combinedUpdates = Updates.combine(updates);
            vehicleCollection.updateOne( filter, combinedUpdates);

        } catch (IllegalAccessException | MongoWriteException e) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("Dupa", e);
        }
    }


    // Delete methods
    @Override
    public void deleteById(UUID id) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<M> vehicleCollection = super.getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    super.getMgdClass());
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, discriminatorValue);
            Bson idFilter = Filters.eq(DatabaseConstants.ID, id);
            Bson bothFilters = Filters.and(discriminatorFilter, idFilter);
            long deletedCount = vehicleCollection.deleteOne(bothFilters).getDeletedCount();

            if (deletedCount == 0) {
                throw new RuntimeException("VehicleRepository:\n Class: " + getMgdClass().getSimpleName() + "\n deleteById: Vehicle with provided UUID not found!!!");
            }

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }


}
