package org.example.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.ClientType;

@Entity
@DiscriminatorValue(value = "default")
@NoArgsConstructor
@Getter @Setter
public class Default extends ClientType {

    public Default(Double discount, Integer maxVehicles) {
        super(discount, maxVehicles);
    }
}
