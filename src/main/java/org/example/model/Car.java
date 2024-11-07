package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.mgd.CarMgd;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class Car extends MotorVehicle {

    public Car(UUID id, String plateNumber, Double basePrice, Integer engine_displacement) {
        super(id, plateNumber, basePrice, engine_displacement);
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
