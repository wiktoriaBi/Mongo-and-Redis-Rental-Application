package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = "gold")
@NoArgsConstructor
@Setter @Getter
public class Gold extends ClientType {
    public Gold(Double discount, Integer maxVehicles) {
        super(discount, maxVehicles);
    }
}
