package org.example.redis;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnectionManager {

    private static JedisPooled jedisPooled;

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
        if (jedisPooled != null) {
            return;
        }
        Properties properties = loadConfig();
        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(30)
                .timeoutMillis(10)
                .build();

        try {
            jedisPooled = new JedisPooled(new HostAndPort(host, port), clientConfig);
        }
        catch (JedisException e) {
            throw new RuntimeException("Błąd podczas łączenia z Redis: " + e.getMessage());
        }
    }

    public static JedisPooled getConnection() {
        try {
            connect();
            jedisPooled.ping();
            //System.out.println(">>>ping "+output);
        }
        catch (JedisException e) {
            return null;
        }
        return jedisPooled;
    }

    public static void close() {
        if (jedisPooled != null){
            jedisPooled.close();
        }
    }
}
