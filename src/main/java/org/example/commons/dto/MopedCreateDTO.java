package org.example.commons.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.MotorVehicle;


@Getter @Setter
public class MopedCreateDTO extends VehicleCreateDTO {

    private final Integer engineDisplacement;

    public MopedCreateDTO(String plateNumber, Double basePrice, Integer engineDisplacement) {
        super(plateNumber, basePrice);
        this.engineDisplacement = engineDisplacement;
    }
}
