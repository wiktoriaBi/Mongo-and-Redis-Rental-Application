package org.example.mgd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class BicycleMgd extends VehicleMgd {

    @BsonCreator
    public BicycleMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) boolean rented,
            @BsonProperty(DatabaseConstants.BICYCLE_PEDAL_NUMBER) Integer pedalsNumber) {
        super(entityId, plateNumber, basePrice, archive, rented);
        this.pedalsNumber = pedalsNumber;
    }

    @BsonProperty(DatabaseConstants.BICYCLE_PEDAL_NUMBER)
    private Integer pedalsNumber;



}
