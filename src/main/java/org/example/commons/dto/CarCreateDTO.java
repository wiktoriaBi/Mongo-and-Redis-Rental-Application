package org.example.commons.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Car;
import org.example.model.MotorVehicle;

@Getter @Setter
public class CarCreateDTO extends MopedCreateDTO {

    private final Car.TransmissionType transmissionType;

    public CarCreateDTO(String plateNumber,
                        Double basePrice,
                        Integer engineDisplacement,
                        Car.TransmissionType transmissionType) {
        super(plateNumber, basePrice, engineDisplacement);
         this.transmissionType = transmissionType;
    }
}
