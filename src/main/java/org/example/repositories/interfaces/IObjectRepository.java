package org.example.repositories.interfaces;

import java.util.UUID;

/**
 * Ogolne repozytorium, z bazowymi metodami dla wszystkich obiektow(encji)
 * @param <T>
 */
public interface IObjectRepository<T> {

    T findById(UUID id);

}
