package org.example.services.implementations;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.model.Address;
import org.example.model.Client;
import org.example.model.ClientType;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;

import java.util.UUID;

@RequiredArgsConstructor
public class ClientService {

    private final EntityManager em;

    private final IClientRepository clientRepository;
    private final IClientTypeRepository clientTypeRepository;

    public Client addClient(String firstName, String lastName, String email, Address address, UUID clientTypeId) {
        ClientType clientType = null;
        try {
            clientType = clientTypeRepository.findById(clientTypeId);
            if (clientType == null) {
                throw new RuntimeException("Client type not found");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        Client client = new Client(firstName, lastName, email, address, clientType);
        return clientRepository.save(client);
    }

    public void updateClientAddress(UUID clientId, String city, String street, String number) {
        Client client = clientRepository.findById(clientId);
        Address address = new Address(city, street, number);
        client.setAddress(address);
    }

    public void updateClientFirstName(UUID clientId, String firstName) {
        Client client = clientRepository.findById(clientId);
        client.setFirstName(firstName);
    }

    public void updateClientLastName(UUID clientId, String lastName) {
        Client client = clientRepository.findById(clientId);
        client.setLastName(lastName);
    }

    public void updateClientEmail(UUID clientId, String email) {
        Client client = clientRepository.findById(clientId);
        client.setEmail(email);
    }

    public void updateClientType(UUID clientId, ClientType clientType) {
        Client client = clientRepository.findById(clientId);
        client.setClientType(clientType);
    }

    public void removeClient(UUID clientId) {
        Client foundClient = clientRepository.findById(clientId);
        clientRepository.remove(foundClient);
    }

}
