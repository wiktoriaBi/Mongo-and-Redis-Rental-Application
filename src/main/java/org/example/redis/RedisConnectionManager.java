package org.example.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnectionManager {

    private static Jedis jedis;

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = RedisConnectionManager.class.getClassLoader().getResourceAsStream("redis.properties")) {
            if (input == null) {
                throw new IOException("Plik konfiguracyjny 'redis.propeties' nie znaleziony");
            }
            properties.load(input);
        }
        catch (IOException e) {
            throw new RuntimeException("Nie udało się załadować konfigurcji Redisa: " + e.getMessage());
        }
        return properties;
    }

    public static void connect() {
        Properties properties = loadConfig();
        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));

        try {
            jedis = new Jedis(host, port);
        }
        catch (JedisException e) {
            throw new RuntimeException("Błąd podczas łączenia z Redis: " + e.getMessage());
        }
    }

    public static Jedis getConnection() {
        if (jedis == null) {
            throw new IllegalStateException("Połączenie z Redis nie zostało zainicjalizowane.");
        }
        return jedis;
    }

    public static void close() {
        if (jedis != null){
            jedis.close();
        }
    }
}
