package org.example.model;


import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.example.utils.consts.DatabaseConstants;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter

public class Bicycle extends Vehicle {

    public Bicycle(UUID id, String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(id,plateNumber, basePrice);
        this.pedalsNumber = pedalsNumber;
    }

    private Integer pedalsNumber;

}
