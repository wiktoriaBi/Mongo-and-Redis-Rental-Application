package org.example.repositories.interfaces;

import org.example.mgd.ClientTypeMgd;
import org.example.model.ClientType;

public interface IClientTypeRepository extends IObjectRepository<ClientType, ClientTypeMgd> {

    ClientType findByType(String type);
}
