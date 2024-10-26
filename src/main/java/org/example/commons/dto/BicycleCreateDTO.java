package org.example.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BicycleCreateDTO extends VehicleCreateDTO {

    public BicycleCreateDTO(String plateNumber, Double basePrice, Integer pedalNumber) {
        super(plateNumber, basePrice);
        this.pedalNumber = pedalNumber;
    }

    Integer pedalNumber;


}
