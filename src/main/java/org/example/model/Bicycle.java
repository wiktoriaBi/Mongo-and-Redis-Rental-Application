package org.example.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter

public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(plateNumber, basePrice);
        this.pedalsNumber = pedalsNumber;
    }

    @Column(name = DatabaseConstants.BICYCLE_PEDAL_NUMBER)
    private Integer pedalsNumber;

}
