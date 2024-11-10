package repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.example.mgd.ClientEmbeddedMgd;
import org.example.mgd.ClientMgd;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ObjectRepositoryTest {


    @BeforeEach
    void setUp() {

    }

    //@Test
    //void connectionTest() {
    //    ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);
    //
    //    MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());
    //
    //    CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true)
    //            .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());
    //    MongoClient mongoClient;
    //
    //    MongoDatabase rentACarDB;
    //
    //    MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
    //            .applyConnectionString(connectionString)
    //            .codecRegistry(
    //                    CodecRegistries.fromRegistries(
    //                            CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
    //                            MongoClientSettings.getDefaultCodecRegistry(),
    //                            pojoCodecRegistry
    //                    ))
    //            .build();
    //
    //    mongoClient = MongoClients.create(settings);
    //
    //
    //    UUID uuid = UUID.randomUUID();
    //    System.out.println("UUID = " + uuid);
    //
    //    ClientMgd clientMgd = new ClientMgd(uuid, "Jan", "Kowalski", "jk@gmail.com", UUID.randomUUID(), 0);
    //    AccountMgd accountMgd = new AccountMgd(uuid, "JKow", "password!");
    //
    //    ClientEmbeddedMgd clientEmbeddedMgd = new ClientEmbeddedMgd(uuid, clientMgd, accountMgd);
    //
    //    rentACarDB = mongoClient.getDatabase("rentacar");
    //    ClientSession clientSession = mongoClient.startSession();
    //
    //    try {
    //
    //        MongoCollection<ClientEmbeddedMgd> clientCollection = rentACarDB
    //                .getCollection("clientsembedded", ClientEmbeddedMgd.class);
    //
    //        clientCollection.insertOne(clientEmbeddedMgd);
    //    } catch (MongoCommandException e) {
    //        clientSession.abortTransaction();
    //    }
    //    finally {
    //        clientSession.close();
    //    }
    //
    //    ArrayList<ClientMgd> retrievedClients = findAllClients(rentACarDB);
    //
    //    for (ClientMgd clientMgd1: retrievedClients) {
    //        System.out.println(clientMgd1.toString());
    //    }
    //
    //}


    private  ArrayList<ClientMgd> findAllClients(MongoDatabase database) {
        MongoCollection<ClientEmbeddedMgd> clients = database
                .getCollection("clientsembedded", ClientEmbeddedMgd.class);

        return clients.aggregate(List.of(
                Aggregates.replaceRoot("$client")), ClientMgd.class).into(new ArrayList<>());
    }

    private void deleteById(UUID clientId, MongoDatabase database ) {

        Bson filter = Filters.eq(DatabaseConstants.ID, clientId);
        ClientMgd deletedClient = database
                .getCollection("clientsembedded", ClientMgd.class).findOneAndDelete(filter);

    }

  
}