package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.mgd.ClientTypeMgd;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Silver extends ClientType {
    public Silver(UUID id, Double discount, Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }

    public Silver(ClientTypeMgd clientTypeMgd) {
        super(clientTypeMgd.getId(), clientTypeMgd.getDiscount(), clientTypeMgd.getMaxVehicles());
    }
}
