package org.example.mgd;


import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;


@Getter @Setter
public class AccountMgd extends AbstractEntityMgd {

    @BsonProperty(DatabaseConstants.ACCOUNT_USERNAME)
    private String username;

    @BsonProperty(DatabaseConstants.ACCOUNT_PASSWORD)
    private String password;

    @BsonCreator
    public AccountMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.ACCOUNT_USERNAME) String username,
            @BsonProperty(DatabaseConstants.ACCOUNT_PASSWORD) String password) {
        super(entityId);
        this.username = username;
        this.password = password;
    }
}
