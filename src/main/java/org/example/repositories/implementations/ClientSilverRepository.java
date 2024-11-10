package org.example.repositories.implementations;

import org.example.mgd.ClientTypeMgd;
import org.example.model.Silver;
import org.example.repositories.interfaces.IClientSilverRepository;

import java.util.function.Function;

public class ClientSilverRepository extends ObjectRepository<Silver, ClientTypeMgd> implements IClientSilverRepository {
    public ClientSilverRepository(Function<ClientTypeMgd, Silver> toModelMapper,
                                  java.util.function.Function<Silver, ClientTypeMgd> toMgdMapper,
                                  Class<ClientTypeMgd> mgdClass) {
        super(toModelMapper,toMgdMapper, mgdClass);
    }
}
