package repositories;

import org.example.model.Car;
import org.example.repositories.implementations.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private CarRepository carRepository = new CarRepository();;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createCar() {

        Car car = carRepository.createCar("AA123", 100.0,3, Car.TransmissionType.MANUAL);

        assertEquals(car, carRepository.findById(car.getId()));

    }

    //@Test
    //void findById() {
    //}
}