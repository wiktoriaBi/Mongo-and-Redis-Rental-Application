package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
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

    @ManyToOne(optional = false)
    @JoinColumn( name = DatabaseConstants.CLIENT_CLIENT_TYPE_ID,
    referencedColumnName = DatabaseConstants.PK,
    foreignKey = @ForeignKey(name = DatabaseConstants.CLIENT_CLIENT_TYPE_ID_FK))
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
