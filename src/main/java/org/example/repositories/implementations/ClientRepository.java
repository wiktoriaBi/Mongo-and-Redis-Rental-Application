package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Client;
import org.example.repositories.implementations.ObjectRepository;
import org.example.repositories.interfaces.IClientRepository;

public class ClientRepository extends ObjectRepository<Client> implements IClientRepository {

    public ClientRepository(Class<Client> entityClass) {
        super(entityClass);
    }

    @Override
    public Client findByEmail(String email) {
        return null;
    }
}
