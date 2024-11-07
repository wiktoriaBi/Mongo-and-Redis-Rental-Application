package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import org.example.model.Silver;
import org.example.repositories.interfaces.IClientSilverRepository;

public class ClientSilverRepository extends ObjectRepository<Silver> implements IClientSilverRepository {
    public ClientSilverRepository(EntityManager em, Class<Silver> entityClass) {
        super(entityClass);
    }
}
