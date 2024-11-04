package org.example.mgd;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@BsonDiscriminator(value = DatabaseConstants.MOPED_DISCRIMINATOR)
public class MopedMgd extends MotorVehicleMgd {
    @BsonCreator
    public MopedMgd (
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) boolean rented,
            @BsonProperty(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT) Integer engine_displacement) {
        super(entityId, plateNumber, basePrice, archive, rented, engine_displacement);
    }
}
