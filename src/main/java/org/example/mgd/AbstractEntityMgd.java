package org.example.mgd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@Getter @Setter
public abstract class AbstractEntityMgd implements Serializable {

    @BsonProperty(DatabaseConstants.ID)
    private final UUID id;

}
