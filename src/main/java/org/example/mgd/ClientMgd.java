package org.example.mgd;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.Address;
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

    //@BsonProperty(DatabaseConstants.CLIENT_ADDRESS)
    //private Address address;

    @BsonCreator
    public ClientMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.CLIENT_FIRST_NAME) String firstName,
            @BsonProperty(DatabaseConstants.CLIENT_LAST_NAME) String lastName,
            @BsonProperty(DatabaseConstants.CLIENT_EMAIL) String email) {
           // @BsonProperty(DatabaseConstants.CLIENT_ADDRESS) Address address) {
        super(entityId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
