package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Client extends AbstractEntity {

    private String firstName;

    private String lastName;

    private String email;

    private ClientType clientType;

    private String cityName;

    private String streetName;

    private String streetNumber;

    private List<Rent> currentRents = new ArrayList<>();

    public Client(UUID uuid, String firstName, String lastName, String email,
                  ClientType clientType, String cityName, String streetName, String streetNumber) {
        super(uuid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.clientType = clientType;
    }
}
