package org.example.repositories.interfaces;

import org.example.model.Client;


public interface IClientRepository extends IObjectRepository<Client> {

    Client findByEmail(String email);

}
