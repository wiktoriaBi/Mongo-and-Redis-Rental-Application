package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Vehicle;
import org.example.utils.consts.DatabaseConstants;

@Entity
@Table(name = DatabaseConstants.MOTOR_VEHICLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class MotorVehicle extends Vehicle {

    @Column(name = DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, nullable = false)
    private Integer engine_displacement;

    public MotorVehicle(String plateNumber, Double basePrice, Integer engine_displacement) {
        super(plateNumber, basePrice);
        this.engine_displacement = engine_displacement;
    }
}
