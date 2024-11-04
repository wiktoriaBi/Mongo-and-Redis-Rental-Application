package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class Vehicle extends AbstractEntity {

    private String plateNumber;

    private Double basePrice;

    private boolean archive;

    private boolean rented;

    public Vehicle(UUID id, String plateNumber, Double basePrice) {
        super(id);
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = false;
        this.rented = false;
    }


}
