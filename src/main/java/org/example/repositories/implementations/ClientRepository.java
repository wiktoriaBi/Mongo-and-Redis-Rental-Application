package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Client;
import org.example.repositories.implementations.ObjectRepository;
import org.example.repositories.interfaces.IClientRepository;

public class ClientRepository extends ObjectRepository<Client> implements IClientRepository {

    public ClientRepository(EntityManager em, Class<Client> entityClass) {
        super(em, entityClass);
    }

    @Override
    public Client findByEmail(String email) {
        TypedQuery<Client> query = getEm().createQuery("SELECT cl FROM Client cl WHERE cl.email = :email", getEntityClass());
        query.setParameter("email", email);
        return query.getSingleResultOrNull();
    }
}
