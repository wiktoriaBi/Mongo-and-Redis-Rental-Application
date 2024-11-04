package org.example.mappers;

import org.bson.Document;
import org.example.mgd.CarMgd;
import org.example.model.Car;
import org.example.model.Rent;
import org.example.utils.consts.DatabaseConstants;

public class CarMapper {

    public static Car toCar(CarMgd carMgd) {
        return new Car(
                carMgd.getEntityId(),
                carMgd.getPlateNumber(),
                carMgd.getBasePrice(),
                carMgd.getEngine_displacement()
        );
    }

    public static CarMgd toMongoCar(Car car) {
        return new CarMgd(
                car.getId(),
                car.getPlateNumber(),
                car.getBasePrice(),
                car.isArchive(),
                car.isRented(),
                car.getEngine_displacement()
                );
    }
}
