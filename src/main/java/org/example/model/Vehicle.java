package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
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
