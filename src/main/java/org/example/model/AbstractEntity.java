package org.example.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class AbstractEntity implements Serializable {

    private UUID id;

    public AbstractEntity(UUID id) {
        this.id = id;
    }
}
