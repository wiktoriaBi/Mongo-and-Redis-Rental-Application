package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@Getter
public class AbstractEntity implements Serializable {

    @Id
    @Column(name = DatabaseConstants.PK)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(name = DatabaseConstants.VERSION)
    private Long version;
}
