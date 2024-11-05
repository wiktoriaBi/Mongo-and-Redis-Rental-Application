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



public class CarRepository extends VehicleRepository<Car> implements ICarRepository {

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

    public CarRepository(Class<Car> entityClass) {
        super(entityClass);
    }

}
