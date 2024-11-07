package org.example.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


public record RentCreateDTO (
        LocalDateTime endTime,
        UUID clientId,
        UUID vehicleId
) {}
