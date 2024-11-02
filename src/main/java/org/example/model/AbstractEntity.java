package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class AbstractEntity implements Serializable {

    @Column(name = DatabaseConstants.ID)
    private UUID id;

}
