package org.example.repositories.implementations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.model.Car;

import org.example.repositories.interfaces.ICarRepository;
import org.example.utils.consts.DatabaseConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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
                                            "_id" : {
                                                   "bsonType": "binData",
                                                   "description": "Unique identifier,I am using it instead of objectId for portibility",
                                                   "pattern": "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"
                                            },
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

        }
    }

    ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

    MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

    MongoDatabase rentACarDB;

    MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
            .applyConnectionString(connectionString)
            .codecRegistry(
                    CodecRegistries.fromRegistries(
                            CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                            MongoClientSettings.getDefaultCodecRegistry(),
                            pojoCodecRegistry
                    ))
            .build();

    MongoClient mongoClient = MongoClients.create(settings);

    public Car createCar(String plateNumber, Double basePrice, Integer engine_displacement, Car.TransmissionType transmissionType) {
        CarMgd carMgd = new CarMgd(
                UUID.randomUUID(),
                plateNumber,
                basePrice,
                false,
                0,
                engine_displacement,
                transmissionType
        );
        MongoCollection<CarMgd> vehicleCollection = getRentACarDB().getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME,
                DatabaseConstants.CAR_COLLECTION_TYPE);
        vehicleCollection.insertOne(carMgd);
        return new Car(carMgd);
    }


}
