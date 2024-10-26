package org.example.repositories.implementations;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.model.AbstractEntity;
import org.example.repositories.interfaces.IObjectRepository;

import java.util.UUID;


@RequiredArgsConstructor
@Getter
public abstract class ObjectRepository<T extends AbstractEntity> implements IObjectRepository<T> {

    private final EntityManager em;

    private final Class<T> entityClass;

    @Override
    public T save(T object) {
        if (object.getId() == null) {
            em.persist(object);
        }
        else {
            em.merge(object);
        }
        return object;
    }

    @Override
    public T findById(UUID id){
        return em.find(entityClass, id);
    }

    @Override
    public void remove(T object) {
        em.remove(object);
    }
}
