package org.example.repositories.mongo.interfaces;

import org.example.mgd.clientType.ClientTypeMgd;

import java.util.UUID;

public interface IClientTypeRepository extends IObjectRepository<ClientTypeMgd> {

    ClientTypeMgd findAnyClientType(UUID id);

}
