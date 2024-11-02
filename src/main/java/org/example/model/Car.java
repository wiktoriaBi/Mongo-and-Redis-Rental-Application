package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@Getter @Setter
@NoArgsConstructor
public class Car extends MotorVehicle{

    public Car(String plateNumber, Double basePrice, Integer engine_displacement) {
        super(plateNumber, basePrice, engine_displacement);
    }

}
