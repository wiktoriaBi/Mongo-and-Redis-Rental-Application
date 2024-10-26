package org.example.services.interfaces;

import org.example.commons.dto.RentCreateDTO;
import org.example.model.Rent;

import java.util.UUID;

public interface IRentService {

    Rent createRent(RentCreateDTO createRentDTO);

    Rent findRentById(UUID id);

    void removeRent(UUID id);


}
