package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class ClientTypeMgd extends AbstractEntityMgd {

    @BsonCreator
    public ClientTypeMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT) Double discount,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES) Integer maxVehicles) {
        super(entityId);
        this.discount = discount;
        this.maxVehicles = maxVehicles;
    }

    @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT)
    private Double discount;

    @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
    private Integer maxVehicles;

}
