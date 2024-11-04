package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class ClientType extends AbstractEntity {

    @BsonCreator
    public ClientType(
            @BsonProperty(DatabaseConstants.ID) UUID id,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT) Double discount,
            @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES) Integer maxVehicles) {
        super(id);
        this.discount = discount;
        this.maxVehicles = maxVehicles;
    }

    @BsonProperty(DatabaseConstants.CLIENT_TYPE_DISCOUNT)
    private Double discount;

    @BsonProperty(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
    private Integer maxVehicles;
}
