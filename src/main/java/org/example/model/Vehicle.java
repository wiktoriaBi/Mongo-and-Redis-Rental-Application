package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class Vehicle extends AbstractEntity {

    @Column(name = DatabaseConstants.VEHICLE_PLATE_NUMBER, unique = true, nullable = false)
    private String plateNumber;

    @Column(name = DatabaseConstants.VEHICLE_BASE_PRICE, nullable = false)
    private Double basePrice;

    @Column(name = DatabaseConstants.VEHICLE_ARCHIVE, nullable = false)
    private boolean archive;

    @Column(name = DatabaseConstants.VEHICLE_RENTED , nullable = false)
    private boolean rented;

    public Vehicle(String plateNumber, Double basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = false;
        this.rented = false;
    }


}
