package org.example.mgd;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

public class GoldMgd extends ClientTypeMgd {
    @BsonCreator
    public GoldMgd (
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT) Double discount,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES) Integer maxVehicles) {
        super(entityId, discount, maxVehicles);
    }
}
