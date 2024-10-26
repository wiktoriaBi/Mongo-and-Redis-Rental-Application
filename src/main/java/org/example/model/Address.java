package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.consts.DatabaseConstants;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Address {

    @Column(name = DatabaseConstants.ADDRESS_CITY_NAME)
    private String cityName;

    @Column(name = DatabaseConstants.ADDRESS_STREET_NAME)
    private String streetName;

    @Column(name = DatabaseConstants.ADDRESS_STREET_NUMBER)
    private String streetNumber;


}
