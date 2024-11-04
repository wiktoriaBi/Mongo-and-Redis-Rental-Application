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
import java.util.UUID;

@Getter @Setter
public class Rent extends AbstractEntity {

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Client client;

    private Vehicle vehicle;

    private Double rentCost = 0.0;


    // jesli wypozyczenie sie zakonczylo, nalezy zmienic na false

    private boolean active;

    public Rent(UUID id, LocalDateTime beginTime, LocalDateTime endTime, Client client, Vehicle vehicle, boolean active) {
        super(id);
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
