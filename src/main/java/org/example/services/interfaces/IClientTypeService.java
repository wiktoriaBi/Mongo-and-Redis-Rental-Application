package org.example.services.interfaces;

import org.example.commons.dto.create.ClientTypeCreateDTO;
import org.example.commons.dto.update.ClientTypeUpdateDTO;
import org.example.model.ClientType;
import org.example.model.Default;
import org.example.model.Gold;
import org.example.model.Silver;
import org.example.repositories.mongo.interfaces.IClientRepository;

import java.util.List;
import java.util.UUID;

public interface IClientTypeService extends IObjectService {

    Default createDefaultType(ClientTypeCreateDTO createDTO);

    Silver createSilverType(ClientTypeCreateDTO createDTO);

    Gold createGoldType(ClientTypeCreateDTO createDTO);

    ClientType findClientTypeById(UUID id);

    List<ClientType> findAll();

    void updateClientType(ClientTypeUpdateDTO updateDTO);

    void removeClientType(UUID clientTypeId);

    IClientRepository getClientRepository();
}
