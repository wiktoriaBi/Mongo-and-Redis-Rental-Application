package repositories;

import org.example.mgd.BicycleMgd;
import org.example.model.Bicycle;

import org.example.repositories.implementations.BicycleRepository;
import org.example.repositories.interfaces.IBicycleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BicycleRepositoryTest {

    private IBicycleRepository bicycleRepository;

    @BeforeEach
    void setUp() {
        bicycleRepository = new BicycleRepository(Bicycle::new, BicycleMgd::new, BicycleMgd.class);
    }

    @AfterEach
    void dropDatabase() {
        bicycleRepository.getMongoClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
    }

    @Test
    void createBicycle() {
        Bicycle newBicycle = bicycleRepository.create("AA123", 100.0, 2);
        assertEquals(newBicycle.getId(), bicycleRepository.findById(newBicycle.getId()).getId());
        Bicycle bicycle2 = bicycleRepository.create("DRUGIROWER", 1000.0,6);
        assertEquals(bicycle2.getId(), bicycleRepository.findById(bicycle2.getId()).getId());
        assertEquals(2, bicycleRepository.findAll().size());
    }

    @Test
    void createBicycle_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Bicycle car = bicycleRepository.create(plateNumber, 100.0,3);
        assertEquals(car.getId(), bicycleRepository.findById(car.getId()).getId());
        assertThrows(RuntimeException.class,
                ()-> bicycleRepository.create(plateNumber, 1000.0,6));
        assertEquals(1, bicycleRepository.findAll().size());
    }

    @Test
    void findBicycleById_NotFoundException() {
        String plateNumber = "AAA1234";
        bicycleRepository.create(plateNumber, 100.0,3);
        assertThrows(RuntimeException.class, ()-> bicycleRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateBicycle() {
        Bicycle car = bicycleRepository.create("AABB123", 100.0,2);
        Double newPrice = 200.0;
        Integer newPedalsNum = 6;
        Bicycle modifiedBicycle = Bicycle.builder().basePrice(newPrice).id(car.getId()).pedalsNumber(newPedalsNum).build();
        bicycleRepository.update(modifiedBicycle);
        assertEquals(newPrice, bicycleRepository.findById(car.getId()).getBasePrice());
        assertEquals(newPedalsNum, bicycleRepository.findById(car.getId()).getPedalsNumber());
    }

    @Test
    void deleteBicycle() {
        Bicycle bicycle = bicycleRepository.create("AAB123", 100.0,3);
        bicycleRepository.deleteById(bicycle.getId());
        assertEquals(0, bicycleRepository.findAll().size());
    }
}
