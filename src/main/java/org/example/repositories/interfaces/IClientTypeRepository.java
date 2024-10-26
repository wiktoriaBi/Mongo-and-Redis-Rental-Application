package org.example.repositories.interfaces;

import org.example.model.ClientType;
import org.example.repositories.interfaces.IObjectRepository;

public interface IClientTypeRepository extends IObjectRepository<ClientType> {

    ClientType findByType(String type);
}
