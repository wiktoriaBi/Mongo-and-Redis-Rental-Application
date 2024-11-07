package org.example.model;


import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@BsonDiscriminator(value = DatabaseConstants.BICYCLE_DISCRIMINATOR)
@Setter @Getter
public class Bicycle extends Vehicle {

    private Integer pedalsNumber;

    public Bicycle(UUID id, String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(id,plateNumber, basePrice);
        this.pedalsNumber = pedalsNumber;
    }

}
