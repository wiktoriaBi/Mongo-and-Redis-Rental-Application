package org.example.utils.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.json.JsonObjectMapper;

public class CustomGsonObjectMapper implements JsonObjectMapper {
    private final Gson gson;
        public CustomGsonObjectMapper() {
            gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Object.class, new BsonDiscriminatorSerializer())
                    .create();
        }

        public String toJson(Object obj) {
            return gson.toJson(obj);
        }

        public <T> T fromJson(String json, Class<T> classOfT) {
            return gson.fromJson(json, classOfT);
        }

}
