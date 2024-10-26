package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.ClientType;

@Entity
@DiscriminatorValue(value = "silver")
@NoArgsConstructor
@Setter @Getter
public class Silver extends ClientType {

    public Silver(Double discount, Integer maxVehicles) {
        super(discount, maxVehicles);
    }
}
