package org.example.repositories.implementations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.BicycleMgd;
import org.example.model.AbstractEntity;
import org.example.model.Bicycle;
import org.example.repositories.interfaces.IObjectRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Getter
public abstract class ObjectRepository<T extends AbstractEntity> implements IObjectRepository<T> {

    private final Class<T> entityClass;

    ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

    private MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    private CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

    protected MongoDatabase rentACarDB;

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

    protected final String vehicleCollectionName = "vehicles";

    protected final Class<BicycleMgd> bicycleMgdClass = BicycleMgd.class;

    // dla obiektu potrzebne dwie informacje: nazwa kolekcji i klasa Mgd

    //todo save dla kazdego repo

    @Override
    public T findById(UUID id){
        //todo implement
        return null;
    }

    @Override
    public void remove(T object) {
        //todo implement
    }
}
