package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@Entity
@Table(name = DatabaseConstants.CLIENT_TYPE_TABLE,
        uniqueConstraints = @UniqueConstraint(columnNames = DatabaseConstants.CLIENT_TYPE_TYPE))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = DatabaseConstants.CLIENT_TYPE_TYPE, discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ClientType extends AbstractEntity {

    @Column(name =  DatabaseConstants.CLIENT_TYPE_DISCOUNT)
    private Double discount;

    @Column(name = DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES )
    private Integer maxVehicles;
}
