package org.example.mgd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@RequiredArgsConstructor
@Getter @Setter
public abstract class AbstractEntityMgd implements Serializable {

    @BsonProperty(DatabaseConstants.ID)
    private final UUID entityId;

}
