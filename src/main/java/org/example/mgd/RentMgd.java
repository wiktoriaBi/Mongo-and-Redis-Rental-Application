package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.model.Client;
import org.example.model.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


 //  Rent moze zapisywac Vehicle jako poddokument, zeby cena nie mogla zostac zmieniona podczas wypozyczenia?
 // A klienta jako zwykle id, zeby dalo sie go namierzyc mimo zmiany danych osobowych
@Getter @Setter
public class RentMgd extends AbstractEntityMgd{

    @BsonCreator
    public RentMgd(
            @BsonProperty(DatabaseConstants.ID) UUID entityId,
            @BsonProperty(DatabaseConstants.RENT_BEGIN_TIME) LocalDateTime beginTime,
            @BsonProperty(DatabaseConstants.RENT_END_TIME) LocalDateTime endTime,
            @BsonProperty(DatabaseConstants.CLIENT) ClientMgd client,
            @BsonProperty(DatabaseConstants.VEHICLE) VehicleMgd vehicle,
            @BsonProperty(DatabaseConstants.RENT_ACTIVE) boolean active) {
        super(entityId);
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.client = client;
        this.vehicle = vehicle;
        this.active = active;
        this.rentCost = ChronoUnit.HOURS.between(endTime, beginTime) * vehicle.getBasePrice();
    }

    @BsonProperty(DatabaseConstants.RENT_BEGIN_TIME)
    private LocalDateTime beginTime;

    @BsonProperty(DatabaseConstants.RENT_END_TIME)
    private LocalDateTime endTime;

    @BsonProperty(DatabaseConstants.CLIENT)
    private ClientMgd client;

    @BsonProperty(DatabaseConstants.VEHICLE)
    private VehicleMgd vehicle;

    @BsonProperty(DatabaseConstants.RENT_RENT_COST)
    private Double rentCost = 0.0;

    @BsonProperty(DatabaseConstants.RENT_ACTIVE)
    private boolean active;


}
