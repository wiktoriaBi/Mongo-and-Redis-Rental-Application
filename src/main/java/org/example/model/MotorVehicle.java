package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Setter @Getter
public class MotorVehicle extends Vehicle {

    private Integer engine_displacement;

    public MotorVehicle(UUID uuid, String plateNumber, Double basePrice, Integer engine_displacement) {
        super(uuid, plateNumber, basePrice);
        this.engine_displacement = engine_displacement;
    }
}
