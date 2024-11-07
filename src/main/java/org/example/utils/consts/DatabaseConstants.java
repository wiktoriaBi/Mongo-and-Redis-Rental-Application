package org.example.utils.consts;

import org.example.mgd.*;
public class DatabaseConstants {

    //connection
    public static final String connectionString = "mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_three_nodes";

    // abstractEntity
    public static final String ID = "id";

    public static final String DATABASE_NAME = "rentacar";

    // Account
    public static final String ACCOUNT_USERNAME = "username";
    public static final String ACCOUNT_PASSWORD = "password";

    //ClientEmbeddedMgd
    public static final String CLIENT_EMBEDDED_ACCOUNT = "account";
    public static final String CLIENT= "client";

    // Vehicle
    public static final String BSON_DISCRIMINATOR_KEY = "_clazz";
    public static final String VEHICLE= "vehicle";
    public static final String VEHICLE_PLATE_NUMBER = "plateNumber";
    public static final String VEHICLE_BASE_PRICE = "basePrice";
    public static final String VEHICLE_ARCHIVE = "archive";
    public static final String VEHICLE_RENTED = "rented";

    // MotorVehicle
    public static final String MOTOR_VEHICLE_TABLE = "motor_vehicle";
    public static final String MOTOR_VEHICLE_ENGINE_DISPLACEMENT = "engineDisplacement";

    // Moped
    public static final String MOPED_DISCRIMINATOR = "moped";
    public static final String MOPED_MOTOR_VEHICLE_ID_FK = "moped_motor_vehicle_id_fk";

    //Car
    public static final String CAR_DISCRIMINATOR = "car";

    public static final String CAR_TRANSMISSION_TYPE = "transmissionType";

    // Bicycle
    public static final String BICYCLE_DISCRIMINATOR = "bicycle";

    public static final String BICYCLE_PEDAL_NUMBER = "pedal_number";

    public static final String BICYCLE_VEHICLE_ID_FK = "bicycle_vehicle_id_fk";

    // Address

    public static final String ADDRESS_CITY_NAME = "city_name";
    public static final String ADDRESS_STREET_NAME = "street_name";
    public static final String ADDRESS_STREET_NUMBER = "street_number";

    // ClientType

    public static final String CLIENT_TYPE_TABLE = "client_type";
    public static final String CLIENT_TYPE_TYPE = "type";


    public static final String CLIENT_TYPE_DISCOUNT = "discount";
    public static final String CLIENT_TYPE_MAX_VEHICLES = "max_vehicles";

    // Default
    public static final String DEFAULT_TABLE = "default";
    public static final String DEFAULT_DISCRIMINATOR = "default";

    public static final String SILVER_TABLE = "silver";
    public static final String GOLD_TABLE = "gold";

    // Client

    public static final String CLIENT_FIRST_NAME = "first_name";
    public static final String CLIENT_LAST_NAME = "last_name";
    public static final String CLIENT_EMAIL = "email";
    public static final String CLIENT_ADDRESS = "address";
    public static final String CLIENT_ACTIVE_RENTS = "active_rents";



    public static final String CLIENT_CLIENT_TYPE_ID = "client_type_id";
    public static final String CLIENT_CLIENT_TYPE_ID_FK = "client_type_id_fk";

    // Rent

    public static final String RENT_TABLE = "rent";
    public static final String RENT_BEGIN_TIME = "begin_time";
    public static final String RENT_END_TIME = "end_time";

    public static final String RENT_RENT_COST = "rent_cost";
    public static final String RENT_ACTIVE = "active";

    //Collection names
    public static final String CLIENT_COLLECTION_NAME = "clients";
    public static final String VEHICLE_COLLECTION_NAME = "vehicles";
    public static final String RENT_COLLECTION_NAME = "rents";
    public static final String CLIENT_TYPE_COLLECTION_NAME = "client_types";

    //Collection types
    public static final Class<ClientMgd> CLIENT_COLLECTION_TYPE = ClientMgd.class;
    public static final Class<VehicleMgd> VEHICLE_COLLECTION_TYPE = VehicleMgd.class;
    public static final Class<BicycleMgd> BICYCLE_COLLECTION_TYPE = BicycleMgd.class;
    public static final Class<MopedMgd> MOPED_COLLECTION_TYPE = MopedMgd.class;
    public static final Class<CarMgd> CAR_COLLECTION_TYPE = CarMgd.class;
    public static final Class<RentMgd> RENT_COLLECTION_TYPE = RentMgd.class;
    public static final Class<ClientTypeMgd> CLIENT_TYPE_COLLECTION_TYPE = ClientTypeMgd.class;



}
