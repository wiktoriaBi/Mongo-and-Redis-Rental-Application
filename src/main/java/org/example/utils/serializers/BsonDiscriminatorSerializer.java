package org.example.utils.serializers;

import com.google.gson.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.lang.reflect.Type;

public class BsonDiscriminatorSerializer implements JsonSerializer<Object> {

    private final Gson defaultGson = new GsonBuilder().create();

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        Class<?> clazz = src.getClass();
        String discriminator = clazz.getAnnotation(BsonDiscriminator.class).value();

        JsonObject jsonObject = defaultGson.toJsonTree(src).getAsJsonObject();
        if (discriminator != null) {
            jsonObject.addProperty("_clazz", discriminator);
        }
        return jsonObject;
    }


}
