package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.Bicycle;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
@BsonDiscriminator(key = DatabaseConstants.BSON_DISCRIMINATOR_KEY, value = DatabaseConstants.BICYCLE_DISCRIMINATOR)
public class BicycleMgd extends VehicleMgd {

    @BsonCreator
    public BicycleMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) int rented,
            @BsonProperty(DatabaseConstants.BICYCLE_PEDAL_NUMBER) Integer pedalsNumber) {
        super(entityId, plateNumber, basePrice, archive, rented);
        this.pedalsNumber = pedalsNumber;
    }

    public BicycleMgd(Bicycle bicycle) {
        super(
                bicycle.getId(),
                bicycle.getPlateNumber(),
                bicycle.getBasePrice(),
                bicycle.isArchive(),
                bicycle.isRented() ? 1 : 0
        );
        this.pedalsNumber = bicycle.getPedalsNumber();
    }

    @BsonProperty(DatabaseConstants.BICYCLE_PEDAL_NUMBER)
    private Integer pedalsNumber;



}
