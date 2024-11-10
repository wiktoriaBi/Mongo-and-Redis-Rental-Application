package org.example.repositories.interfaces;

import org.example.mgd.ClientTypeMgd;
import org.example.model.Client;


public interface IClientRepository extends IObjectRepository<Client, ClientTypeMgd> {

    Client findByEmail(String email);

}
