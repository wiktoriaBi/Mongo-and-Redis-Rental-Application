package org.example.services.implementations;

import lombok.RequiredArgsConstructor;
import org.example.commons.dto.create.ClientTypeCreateDTO;
import org.example.commons.dto.update.ClientTypeUpdateDTO;
import org.example.mgd.*;
import org.example.model.ClientType;
import org.example.model.Default;
import org.example.model.Gold;
import org.example.model.Silver;
import org.example.repositories.mongo.implementations.ClientRepository;
import org.example.repositories.mongo.implementations.ClientTypeRepository;
import org.example.repositories.mongo.interfaces.IClientRepository;
import org.example.repositories.mongo.interfaces.IClientTypeRepository;
import org.example.services.interfaces.IClientTypeService;

import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
public class ClientTypeService extends ObjectService implements IClientTypeService {

    private final IClientTypeRepository clientTypeRepository;
    private final IClientRepository clientRepository;

    public ClientTypeService() {
        super();
        this.clientTypeRepository = new ClientTypeRepository(super.getClient(), ClientTypeMgd.class);
        this.clientRepository = new ClientRepository(super.getClient(), ClientMgd.class);
    }

    @Override
    public IClientRepository getClientRepository() {
        return clientRepository;
    }

    @Override
    public Default createDefaultType(ClientTypeCreateDTO createDTO) {
        DefaultMgd defaultType = new DefaultMgd(
                UUID.randomUUID(),
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return new Default(clientTypeRepository.save(defaultType));
    }

    @Override
    public Silver createSilverType(ClientTypeCreateDTO createDTO) {
        SilverMgd silverMgd = new SilverMgd(
                UUID.randomUUID(),
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return new Silver(clientTypeRepository.save(silverMgd));
    }

    @Override
    public Gold createGoldType(ClientTypeCreateDTO createDTO) {
        GoldMgd goldMgd = new GoldMgd(
                UUID.randomUUID(),
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return new Gold(clientTypeRepository.save(goldMgd));
    }

    @Override
    public ClientType findClientTypeById(UUID id) {
        return new ClientType(clientTypeRepository.findById(id));
    }

    @Override
    public List<ClientType> findAll() {
        return clientTypeRepository.findAll().stream().map(ClientType::new).toList();
    }

    @Override
    public void updateClientType(ClientTypeUpdateDTO updateDTO) {
        ClientType modifiedClientType = ClientType.builder().
                id(updateDTO.getId()).
                discount(updateDTO.getDiscount()).
                maxVehicles(updateDTO.getMaxVehicles())
                .build();
        clientTypeRepository.findById(updateDTO.getId());
        clientTypeRepository.save(new ClientTypeMgd(modifiedClientType));
    }

    @Override
    public void removeClientType(UUID clientTypeId) {
        ClientTypeMgd clientType = clientTypeRepository.findAnyClientType(clientTypeId);
        if (!clientRepository.findByType(clientType.getClass()).isEmpty()) {
            throw new RuntimeException ("ClientType with provided ID exist in client(s). Unable to delete ClientType!");
        }
        clientTypeRepository.deleteById(clientTypeId);
    }
}
