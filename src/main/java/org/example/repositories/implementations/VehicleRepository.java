package org.example.repositories.implementations;

import com.mongodb.MongoCommandException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import org.example.mgd.CarMgd;
import org.example.model.AbstractEntity;
import org.example.model.Bicycle;
import org.example.model.Car;
import org.example.model.Moped;
import org.example.model.Vehicle;
import org.example.repositories.implementations.ObjectRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;


public class VehicleRepository<T extends Vehicle> extends ObjectRepository<T> implements IVehicleRepository<T> {

    private Class<?> mgdClass;


    public VehicleRepository(Class<T> entityClass) {
        super(entityClass);

        if (entityClass.equals(Car.class)) {
            mgdClass = DatabaseConstants.CAR_COLLECTION_TYPE;
        }
        else if (entityClass.equals(Bicycle.class)) {
            mgdClass = DatabaseConstants.BICYCLE_COLLECTION_TYPE;
        }
        else if (entityClass.equals(Moped.class)) {
            mgdClass = DatabaseConstants.MOPED_COLLECTION_TYPE;
        }

    }

    public T findVehicleById(UUID uuid) {
        initDatabaseCon("rentacar");
        ClientSession clientSession = mongoClient.startSession();

        try {
            //MongoCollection<CarMgd> mongoCollection

        } catch (MongoCommandException e) {
            clientSession.abortTransaction();
        }
        finally {
            clientSession.close();
        }
        return null;



    }

    @Override
    public T findByPlateNumber(String plateNumber) {

        //todo implement
        return null;
    }
}
