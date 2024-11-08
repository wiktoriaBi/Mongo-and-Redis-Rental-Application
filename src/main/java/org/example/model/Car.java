package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.mgd.CarMgd;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Car extends MotorVehicle {

    public enum TransmissionType {
        MANUAL,
        AUTOMATIC,
    }

    private TransmissionType transmissionType;

    public Car(UUID id, String plateNumber, Double basePrice, Integer engine_displacement, TransmissionType type) {
        super(id, plateNumber, basePrice, engine_displacement);
        this.transmissionType = type;
    }

    public Car (CarMgd carMgd) {
        super(
            carMgd.getEntityId(),
            carMgd.getPlateNumber(),
            carMgd.getBasePrice(),
            carMgd.getEngine_displacement()
        );
        this.transmissionType = carMgd.getTransmissionType();
    }






}
