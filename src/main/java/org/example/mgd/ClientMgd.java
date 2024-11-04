package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@ToString
@Getter @Setter
public class ClientMgd extends AbstractEntityMgd {

    @BsonProperty(DatabaseConstants.CLIENT_FIRST_NAME)
    private String firstName;

    @BsonProperty(DatabaseConstants.CLIENT_LAST_NAME)
    private String lastName;

    @BsonProperty(DatabaseConstants.CLIENT_EMAIL)
    private String email;

    @BsonProperty(DatabaseConstants.CLIENT_CLIENT_TYPE_ID)
    private UUID clientType;

    // liczba aktywnych wypozyczen, aby nie mozna bylo przekroczyc limitu dla typu klienta

    @BsonProperty(DatabaseConstants.CLIENT_ACTIVE_RENTS)
    private Integer activeRents;

    //@BsonProperty(DatabaseConstants.CLIENT_ADDRESS)
    //private Address address;

    @BsonCreator
    public ClientMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.CLIENT_FIRST_NAME) String firstName,
            @BsonProperty(DatabaseConstants.CLIENT_LAST_NAME) String lastName,
            @BsonProperty(DatabaseConstants.CLIENT_EMAIL) String email,
            @BsonProperty(DatabaseConstants.CLIENT_CLIENT_TYPE_ID) UUID type_id,
            @BsonProperty(DatabaseConstants.CLIENT_ACTIVE_RENTS) Integer activeRents ) {
        super(entityId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        clientType = type_id;
        this.activeRents = activeRents;
    }
}
