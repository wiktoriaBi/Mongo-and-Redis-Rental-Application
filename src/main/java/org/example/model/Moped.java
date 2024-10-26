package org.example.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.MotorVehicle;
import org.example.utils.consts.DatabaseConstants;

@Entity
@Table(name = DatabaseConstants.MOPED_TABLE)
@Getter @Setter
@NoArgsConstructor

@PrimaryKeyJoinColumn(
        name = DatabaseConstants.PK,
        referencedColumnName = DatabaseConstants.PK,
        foreignKey = @ForeignKey(name = DatabaseConstants.MOPED_MOTOR_VEHICLE_ID_FK)
)
public class Moped extends MotorVehicle {

    public Moped(String plateNumber, Double basePrice, Integer engine_displacement) {
        super(plateNumber, basePrice, engine_displacement);
    }
}
