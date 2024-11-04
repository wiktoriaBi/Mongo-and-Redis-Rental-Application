package org.example.mappers;
import org.example.mgd.RentMgd;
import org.example.model.Rent;
import org.bson.Document;
import org.example.utils.consts.DatabaseConstants;

import javax.xml.crypto.Data;

public class RentMapper {

    // nie wykorzystujemy dokumentów, skoro mamy nasz wlasny model repozytorium Mgd
    public static RentMgd toMongoRent (Rent rentModel) {
        //return new RentMgd (
        //
        //) mialem renta dopisac, nie zdazylem :/

        return new Document(DatabaseConstants.ID, rentModel.getId())
                .append(DatabaseConstants.CLIENT, rentModel.getClient().getId())
                .append(DatabaseConstants.VEHICLE, rentModel.getVehicle().getId())
                .append(DatabaseConstants.RENT_BEGIN_TIME, rentModel.getBeginTime())
                .append(DatabaseConstants.RENT_END_TIME, rentModel.getEndTime())
                .append(DatabaseConstants.RENT_RENT_COST, rentModel.getRentCost())
                .append(DatabaseConstants.RENT_ACTIVE, rentModel.isActive());
    }

    public static Rent toRent(RentMgd rentMgd) {

        return new Rent(
                rentMgd.getEntityId(),
                rentMgd.getBeginTime(),
                rentMgd.getEndTime(),
                rentMgd.getClient(), //todo clientmapper
                rentMgd.getVehicle(), //todo vehiclemapper
                rentMgd.isActive()
        );


    }
}
