package org.example.mgd.clientType;

import lombok.EqualsAndHashCode;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.clientType.Gold;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@EqualsAndHashCode(callSuper=true)
@BsonDiscriminator(key = DatabaseConstants.BSON_DISCRIMINATOR_KEY, value = DatabaseConstants.GOLD_DISCRIMINATOR)
public class GoldMgd extends ClientTypeMgd {

    @BsonCreator
    public GoldMgd (
            @BsonProperty(DatabaseConstants.ID) UUID id,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT) Double discount,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES) Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }

    public GoldMgd(Gold gold) {
        super(gold);
    }

    public GoldMgd(Document document) {
        super(
            document.get(DatabaseConstants.ID, UUID.class),
            document.getDouble(DatabaseConstants.CLIENT_TYPE_DISCOUNT),
            document.getInteger(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
        );
    }
}
