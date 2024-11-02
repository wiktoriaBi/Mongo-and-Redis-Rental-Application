package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class Client extends AbstractEntity {

    @Column(name = DatabaseConstants.CLIENT_FIRST_NAME, nullable = false)
    private String firstName;

    @Column(name = DatabaseConstants.CLIENT_LAST_NAME, nullable = false)
    private String lastName;

    @Column(name = DatabaseConstants.CLIENT_EMAIL, unique = true)
    private String email;

    @Embedded
    private Address address;

    private ClientType clientType;

    @OneToMany(mappedBy = "client")
    private List<Rent> currentRents = new ArrayList<>();

    public Client(String firstName, String lastName, String email, Address address, ClientType clientType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.clientType = clientType;
    }
}
