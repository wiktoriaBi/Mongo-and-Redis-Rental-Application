package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public abstract class MotorVehicleMgd extends VehicleMgd {

    @BsonCreator
    public MotorVehicleMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) boolean rented,
            @BsonProperty(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT) Integer engine_displacement) {
        super(entityId, plateNumber, basePrice, archive, rented);
        this.engine_displacement = engine_displacement;
    }

    @BsonProperty(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT)
    private Integer engine_displacement;






}
