package org.example.model;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.mgd.MopedMgd;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Moped extends MotorVehicle {

    public Moped(UUID uuid, String plateNumber, Double basePrice, Integer engine_displacement) {
        super(uuid, plateNumber, basePrice, engine_displacement);
    }

    public Moped (MopedMgd mopedMgd) {
        super(
            mopedMgd.getEntityId(),
            mopedMgd.getPlateNumber(),
            mopedMgd.getBasePrice(),
            mopedMgd.getEngine_displacement()
        );
    }
}
