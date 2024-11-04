package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.ClientType;

import java.util.UUID;

@NoArgsConstructor
@Setter @Getter
public class Silver extends ClientType {

    public Silver(UUID id, Double discount, Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }
}
