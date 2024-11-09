package repositories;

import org.example.model.Car;
import org.example.repositories.implementations.CarRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarRepositoryTest {

    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @AfterEach
    void dropDatabase() {
        carRepository.getMongoClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
    }

    @Test
    void createCar() {
        Car car = carRepository.createCar("AA123", 100.0,3, Car.TransmissionType.MANUAL);
        assertEquals(car.getId(), carRepository.findById(car.getId()).getId());
        Car car2 = carRepository.createCar("DRUGIEAUTO", 1000.0,6, Car.TransmissionType.AUTOMATIC);
        assertEquals(car2.getId(), carRepository.findById(car2.getId()).getId());
        assertEquals(2, carRepository.findAll().size());
    }

    @Test
    void createCar_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Car car = carRepository.createCar(plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
        assertEquals(car.getId(), carRepository.findById(car.getId()).getId());
        assertThrows(RuntimeException.class,
                ()-> carRepository.createCar(plateNumber, 1000.0,6, Car.TransmissionType.AUTOMATIC));
        assertEquals(1, carRepository.findAll().size());
    }

    @Test
    void findCarById_NotFoundException() {
        String plateNumber = "AAA1234";
        carRepository.createCar(plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
        assertThrows(RuntimeException.class, ()-> carRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateCar() {
        Car car = carRepository.createCar("AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        Double newPrice = 200.0;
        Car modifiedCar = Car.builder().basePrice(newPrice).id(car.getId()).build();
        carRepository.update(modifiedCar);
        assertEquals(newPrice, carRepository.findById(car.getId()).getBasePrice());
    }

    @Test
    void deleteCar() {
        Car car = carRepository.createCar("AAB123", 100.0,3, Car.TransmissionType.MANUAL);
        carRepository.deleteById(car.getId());
        assertEquals(0, carRepository.findAll().size());
    }
}