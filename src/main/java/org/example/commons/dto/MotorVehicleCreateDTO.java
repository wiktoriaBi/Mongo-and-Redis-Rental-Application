package org.example.commons.dto;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class MotorVehicleCreateDTO extends VehicleCreateDTO {

    public MotorVehicleCreateDTO(String plateNumber, Double basePrice, Integer engine_displacement) {
        super(plateNumber, basePrice);
        this.engine_displacement = engine_displacement;
    }

    Integer engine_displacement;
}
