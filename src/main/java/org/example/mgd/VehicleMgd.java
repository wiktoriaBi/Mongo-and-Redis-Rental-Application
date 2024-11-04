package org.example.mgd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@ToString
@Getter @Setter
@BsonDiscriminator(key = "_clazz")
public abstract class VehicleMgd extends AbstractEntityMgd {

    @BsonCreator
    public VehicleMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) boolean rented) {
        super(entityId);
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = archive;
        this.rented = rented;
    }

    @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER)
    private String plateNumber;

    @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE)
    private Double basePrice;

    @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE)
    private boolean archive;

    @BsonProperty(DatabaseConstants.VEHICLE_RENTED)
    private boolean rented;
}
