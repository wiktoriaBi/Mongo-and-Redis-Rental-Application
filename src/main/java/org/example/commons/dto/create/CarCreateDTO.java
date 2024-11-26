package org.example.commons.dto.create;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Car;
import org.example.model.MotorVehicle;

@Getter @Setter
public class CarCreateDTO extends MopedCreateDTO {

    private final String transmissionType;

    public CarCreateDTO(String plateNumber,
                        Double basePrice,
                        Integer engineDisplacement,
                        String transmissionType) {
        super(plateNumber, basePrice, engineDisplacement);
        this.transmissionType = transmissionType;
    }
}
