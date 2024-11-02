package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ClientType extends AbstractEntity {

    @Column(name =  DatabaseConstants.CLIENT_TYPE_DISCOUNT)
    private Double discount;

    @Column(name = DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES )
    private Integer maxVehicles;
}
