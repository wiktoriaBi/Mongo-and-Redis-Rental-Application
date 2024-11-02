package org.example.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.AbstractEntity;
import org.example.model.Client;
import org.example.model.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Rent extends AbstractEntity {

    @Column(name = DatabaseConstants.RENT_BEGIN_TIME, nullable = false)
    private LocalDateTime beginTime;

    @Column(name = DatabaseConstants.RENT_END_TIME, nullable = false)
    private LocalDateTime endTime;

    private Client client;

    private Vehicle vehicle;

    @Column(name = DatabaseConstants.RENT_RENT_COST, nullable = false)
    private Double rentCost = 0.0;


    // jesli wypozyczenie sie zakonczylo, nalezy zmienic na false

    @Column(name = DatabaseConstants.RENT_ACTIVE, nullable = false)
    private boolean active;

    public Rent(LocalDateTime beginTime, LocalDateTime endTime, Client client, Vehicle vehicle) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.client = client;
        this.vehicle = vehicle;
        this.active = true;
        this.rentCost = ChronoUnit.HOURS.between(endTime, beginTime) * vehicle.getBasePrice();
    }

    public Rent(LocalDateTime beginTime, LocalDateTime endTime, Client client, Vehicle vehicle, boolean active) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.client = client;
        this.vehicle = vehicle;
        this.active = active;
        this.rentCost = ChronoUnit.HOURS.between(endTime, beginTime) * vehicle.getBasePrice();
    }

    public void recalculateRentCost() {
        this.rentCost = ChronoUnit.HOURS.between(endTime, beginTime) * vehicle.getBasePrice();
    }
}
