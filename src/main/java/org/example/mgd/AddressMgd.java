package org.example.mgd;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Getter @Setter
public class AddressMgd extends AbstractEntityMgd {

    public AddressMgd(@BsonProperty(DatabaseConstants.ID) UUID entityId,
                      @BsonProperty(DatabaseConstants.ADDRESS_CITY_NAME) String cityName,
                      @BsonProperty(DatabaseConstants.ADDRESS_STREET_NAME) String streetName,
                      @BsonProperty(DatabaseConstants.ADDRESS_STREET_NUMBER) String streetNumber) {
        super(entityId);
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
    }

    @BsonProperty(DatabaseConstants.ADDRESS_CITY_NAME)
    private String cityName;

    @BsonProperty(DatabaseConstants.ADDRESS_STREET_NAME)
    private String streetName;

    @BsonProperty(DatabaseConstants.ADDRESS_STREET_NUMBER)
    private String streetNumber;

}
