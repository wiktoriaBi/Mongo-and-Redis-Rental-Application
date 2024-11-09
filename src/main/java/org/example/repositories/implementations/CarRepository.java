package org.example.repositories.implementations;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.example.mgd.CarMgd;
import org.example.model.Car;

import org.example.repositories.interfaces.ICarRepository;
import org.example.utils.consts.DatabaseConstants;

import java.lang.reflect.Field;
import java.util.*;


public class CarRepository extends VehicleRepository<Car> implements ICarRepository {

    public CarRepository() {
        super(Car.class);
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
            getRentACarDB().createCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, createCollectionOptions);
            Bson plateNumberIndex = new BasicDBObject(DatabaseConstants.VEHICLE_PLATE_NUMBER, 1);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE).createIndex(plateNumberIndex, indexOptions);


        }
    }


    // Create methods
    public Car createCar(String plateNumber, Double basePrice, Integer engine_displacement, Car.TransmissionType transmissionType) {
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
        MongoCollection<CarMgd> vehicleCollection = getRentACarDB()
                .getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME, DatabaseConstants.CAR_COLLECTION_TYPE);
            vehicleCollection.insertOne(carMgd);
            return new Car(carMgd);
        } catch (MongoWriteException exception) {
            clientSession.abortTransaction();
            clientSession.close();
            throw new RuntimeException("RentRepository: Vehicle with provided plate number already exists!");
        }


    }

    // Read methods
    public Car findById(UUID id) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE);
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, DatabaseConstants.CAR_DISCRIMINATOR);
            Bson idFilter = Filters.eq(DatabaseConstants.ID, id);
            Bson bothFilters = Filters.and(discriminatorFilter, idFilter);
            CarMgd foundCar = vehicleCollection.find(bothFilters).first();

            if (foundCar == null) {
                throw  new RuntimeException("CarRepository: Car with provided UUID was not found!!!");
            }
            return new Car(foundCar);

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }

    public Car findByPlateNumber(String plateNumber) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE);
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, DatabaseConstants.CAR_DISCRIMINATOR);
            Bson plateNumberFilter = Filters.eq(DatabaseConstants.VEHICLE_PLATE_NUMBER, plateNumber);
            Bson bothFilters = Filters.and(discriminatorFilter, plateNumberFilter);
            CarMgd foundCar = vehicleCollection.find(bothFilters).first();

            if (foundCar == null) {
                throw  new RuntimeException("CarRepository: Car with provided plate number was not found!!!");
            }
            return new Car(foundCar);

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }


    public List<Car> findAll() {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE);
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, DatabaseConstants.CAR_DISCRIMINATOR);
            List<CarMgd> foundCarsMgd = new LinkedList<>();
            vehicleCollection.find(discriminatorFilter).into(foundCarsMgd);
            return foundCarsMgd.stream().map(Car::new).toList();
        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("CarRepository, findAll: MongoCommandException!");
        }
    }

    public static void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
    }

    // Update methods
    public void update(Car modifiedCar) {
        ClientSession clientSession = getMongoClient().startSession();
        try{
            clientSession.startTransaction();
            MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE);
            Bson filter = Filters.eq(DatabaseConstants.ID, modifiedCar.getId());
            CarMgd modifiedCarDoc = new CarMgd(modifiedCar);
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

    public void deleteById(UUID id) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                    DatabaseConstants.CAR_COLLECTION_TYPE);
            Bson discriminatorFilter = Filters.eq(DatabaseConstants.BSON_DISCRIMINATOR_KEY, DatabaseConstants.CAR_DISCRIMINATOR);
            Bson idFilter = Filters.eq(DatabaseConstants.ID, id);
            Bson bothFilters = Filters.and(discriminatorFilter, idFilter);
            long deletedCount = vehicleCollection.deleteOne(bothFilters).getDeletedCount();

            if (deletedCount == 0) {
                throw  new RuntimeException("CarRepository, deleteById: Car with provided UUID not found!!!");
            }

        } catch (MongoCommandException e) {
            clientSession.close();
            throw new RuntimeException("MongoCommandException!");
        }
    }

}
