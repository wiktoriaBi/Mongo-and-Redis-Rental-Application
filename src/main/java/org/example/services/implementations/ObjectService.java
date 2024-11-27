package org.example.services.implementations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.mgd.clientType.ClientTypeMgd;
import org.example.mgd.clientType.DefaultMgd;
import org.example.mgd.clientType.GoldMgd;
import org.example.mgd.clientType.SilverMgd;
import org.example.mgd.vehicle.BicycleMgd;
import org.example.mgd.vehicle.CarMgd;
import org.example.mgd.vehicle.MopedMgd;
import org.example.mgd.vehicle.VehicleMgd;
import org.example.services.interfaces.IObjectService;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;

@Getter
public abstract class ObjectService implements IObjectService {

    private MongoClient client;

    public ObjectService() {
        initDatabaseConnection();
    }

    @Override
    public void initDatabaseConnection() {
        ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
                .automatic(true)
                .register(VehicleMgd.class)
                .register(CarMgd.class)
                .register(MopedMgd.class)
                .register(BicycleMgd.class)
                .register(ClientTypeMgd.class)
                .register(DefaultMgd.class)
                .register(SilverMgd.class)
                .register(GoldMgd.class)
                .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

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
        client = MongoClients.create(settings);
    }
}