package org.example.commons.dto;

import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class VehicleCreateDTO {

    private final String plateNumber;

    private final Double basePrice;

}
