package org.example.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@Entity
@Table (name = DatabaseConstants.BICYCLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
@PrimaryKeyJoinColumn(
        name = DatabaseConstants.PK,
        referencedColumnName = DatabaseConstants.PK,
        foreignKey = @ForeignKey (name = DatabaseConstants.BICYCLE_VEHICLE_ID_FK)
)
public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(plateNumber, basePrice);
        this.pedalsNumber = pedalsNumber;
    }

    @Column(name = DatabaseConstants.BICYCLE_PEDAL_NUMBER)
    private Integer pedalsNumber;

}
