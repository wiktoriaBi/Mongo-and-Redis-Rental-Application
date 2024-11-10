package org.example.repositories.implementations;

import com.mongodb.*;
import com.mongodb.client.*;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.BicycleMgd;
import org.example.repositories.interfaces.IObjectRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;


@Getter
public abstract class ObjectRepository<T, M> implements IObjectRepository<T, M> {


    private final Function<M, T> toModelMapper;
    private final Function<T, M> toMgdMapper;

    private final Class<M> mgdClass;

    public ObjectRepository(Function<M, T> toModelMapper, java.util.function.Function<T, M> toMgdMapper, Class<M> mgdClass) {
        this.toModelMapper = toModelMapper;
        this.toMgdMapper = toMgdMapper;
        this.mgdClass = mgdClass;
    }

    ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

    private MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    private CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

    protected MongoDatabase rentACarDB;

    protected MongoClient mongoClient;

    public void initDatabaseCon (String databaseName) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(
                        CodecRegistries.fromRegistries(
                                MongoClientSettings.getDefaultCodecRegistry(),
                                pojoCodecRegistry
                        ))
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();

        mongoClient = MongoClients.create(settings);
        rentACarDB = mongoClient.getDatabase(databaseName);
    }

    protected final String vehicleCollectionName = "vehicles";

    protected final Class<BicycleMgd> bicycleMgdClass = BicycleMgd.class;

    // dla obiektu potrzebne dwie informacje: nazwa kolekcji i klasa Mgd

    //todo save dla kazdego repo

    @Override
    public T findById(UUID id){
        //todo implement
        return null;
    }



}
