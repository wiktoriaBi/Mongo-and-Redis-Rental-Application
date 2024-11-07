package org.example.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.model.ClientType;


public record ClientCreateDTO (
        String firstName,
        String lastName,
        String email,
        ClientType clientType,
        String cityName,
        String streetName,
        String streetNumber
){}
