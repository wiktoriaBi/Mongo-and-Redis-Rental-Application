package org.example.model;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.example.mgd.BicycleMgd;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@BsonDiscriminator(value = DatabaseConstants.BICYCLE_DISCRIMINATOR)
@Setter @Getter
public class Bicycle extends Vehicle {

    private Integer pedalsNumber;

    public Bicycle(UUID id, String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(id,plateNumber, basePrice);
        this.pedalsNumber = pedalsNumber;
    }

    public Bicycle(BicycleMgd bicycleMgd) {
        super(
                bicycleMgd.getEntityId(),
                bicycleMgd.getPlateNumber(),
                bicycleMgd.getBasePrice()
        );
        this.pedalsNumber = bicycleMgd.getPedalsNumber();
    }

}
