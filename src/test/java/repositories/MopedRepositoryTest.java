package repositories;

import org.example.mgd.MopedMgd;
import org.example.model.Moped;
import org.example.repositories.implementations.MopedRepository;
import org.example.repositories.interfaces.IMopedRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MopedRepositoryTest {
    private IMopedRepository mopedRepository;

    @BeforeEach
    void setUp() {
        mopedRepository = new MopedRepository(Moped::new, MopedMgd::new, MopedMgd.class);
    }

    @AfterEach
    void dropDatabase() {
        mopedRepository.getMongoClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
    }

    @Test
    void createBicycle() {
        Moped newMoped = mopedRepository.create("AA123", 100.0, 2);
        assertEquals(newMoped.getId(), mopedRepository.findById(newMoped.getId()).getId());
        Moped bicycle2 = mopedRepository.create("DRUGIMOTOROWER", 1000.0,6);
        assertEquals(bicycle2.getId(), mopedRepository.findById(bicycle2.getId()).getId());
        assertEquals(2, mopedRepository.findAll().size());
    }

    @Test
    void createBicycle_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Moped newMoped = mopedRepository.create(plateNumber, 100.0,3);
        assertEquals(newMoped.getId(), mopedRepository.findById(newMoped.getId()).getId());
        assertThrows(RuntimeException.class,
                ()-> mopedRepository.create(plateNumber, 1000.0,6));
        assertEquals(1, mopedRepository.findAll().size());
    }

    @Test
    void findBicycleById_NotFoundException() {
        String plateNumber = "AAA1234";
        mopedRepository.create(plateNumber, 100.0,3);
        assertThrows(RuntimeException.class, ()-> mopedRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateMoped() {
        Moped newMoped = mopedRepository.create("AABB123", 100.0,2);
        Double newPrice = 200.0;
        Integer newEngine = 6;
        Moped modifiedBicycle = Moped.builder().basePrice(newPrice).id(newMoped.getId()).engine_displacement(newEngine).build();
        mopedRepository.update(modifiedBicycle);
        assertEquals(newPrice, mopedRepository.findById(newMoped.getId()).getBasePrice());
        assertEquals(newEngine, mopedRepository.findById(newMoped.getId()).getEngine_displacement());
    }

    @Test
    void deleteMoped() {
        Moped bicycle = mopedRepository.create("AAB123", 100.0,3);
        mopedRepository.deleteById(bicycle.getId());
        assertEquals(0, mopedRepository.findAll().size());
    }
}
