package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.ClientType;
import org.example.model.Default;
import org.example.model.Gold;
import org.example.model.Silver;
import org.example.repositories.interfaces.IClientTypeRepository;

//todo chyba nie powinno byc abstract, co jak szukamy nieznanego clientType z rent?
public class ClientTypeRepository extends ObjectRepository<ClientType> implements IClientTypeRepository{


    public ClientTypeRepository(EntityManager em, Class<ClientType> entityClass) {
        super(entityClass);
    }

    @Override
    public ClientType findByType(String type) {
        //TypedQuery<ClientType> query = getEm().createQuery("SELECT ct FROM ClientType ct WHERE TYPE(ct) = :type", getEntityClass());
        //query.setParameter("type", getClassForDiscriminator(type));
        //return query.getSingleResult();
        return null;
    }

    // mapowanie typu do klasy
    private Class<? extends ClientType> getClassForDiscriminator(String type) {
        switch (type.toLowerCase()) {
            case "default":
                return Default.class;
            case "silver":
                return Silver.class;
            case "gold":
                return Gold.class;
            default:
                throw new IllegalArgumentException("Unknown client type: " + type);
        }
    }
}
