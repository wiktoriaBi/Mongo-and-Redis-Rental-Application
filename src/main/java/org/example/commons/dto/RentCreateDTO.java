package org.example.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@Getter @Setter
public class RentCreateDTO {

        private LocalDateTime endTime;

        private UUID clientId;

        private UUID vehicleId;

}
