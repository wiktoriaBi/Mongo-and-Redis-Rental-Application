package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class ClientEmbeddedMgd extends AbstractEntityMgd {

    @BsonProperty(DatabaseConstants.CLIENT_EMBEDDED_CLIENT)
    private ClientMgd clientMgd;

    @BsonProperty(DatabaseConstants.CLIENT_EMBEDDED_ACCOUNT)
    private AccountMgd accountMgd;

    @BsonCreator
    public ClientEmbeddedMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.CLIENT_EMBEDDED_CLIENT) ClientMgd clientMgd,
            @BsonProperty(DatabaseConstants.CLIENT_EMBEDDED_ACCOUNT) AccountMgd accountMgd) {
        super(entityId);
        this.clientMgd = clientMgd;
        this.accountMgd = accountMgd;
    }
}
