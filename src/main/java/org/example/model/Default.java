package org.example.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.ClientType;

import java.util.UUID;

@Getter @Setter
public class Default extends ClientType {

    public Default(UUID id, Double discount, Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }
}
