package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.Car;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
@BsonDiscriminator(value = DatabaseConstants.CAR_DISCRIMINATOR)
public class CarMgd extends VehicleMgd {

    @BsonProperty(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT)
    private Integer engine_displacement;

    @BsonCreator
    public CarMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.VEHICLE_PLATE_NUMBER) String plateNumber,
            @BsonProperty(DatabaseConstants.VEHICLE_BASE_PRICE) Double basePrice,
            @BsonProperty(DatabaseConstants.VEHICLE_ARCHIVE) boolean archive,
            @BsonProperty(DatabaseConstants.VEHICLE_RENTED) int rented,
            @BsonProperty(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT) Integer engine_displacement,
            @BsonProperty(DatabaseConstants.CAR_TRANSMISSION_TYPE) Car.TransmissionType transmissionType) {

        super(entityId, plateNumber, basePrice, archive, rented);
        this.engine_displacement = engine_displacement;
        this.transmissionType = transmissionType;

    }

    @BsonProperty(DatabaseConstants.CAR_TRANSMISSION_TYPE)
    private Car.TransmissionType transmissionType;


    public CarMgd(Car car) {

        super(car.getId(), car.getPlateNumber(), car.getBasePrice(), car.isArchive(), car.isRented() == true ? 1 : 0);
        this.engine_displacement = car.getEngine_displacement();
        this.transmissionType = car.getTransmissionType();

    }


}
