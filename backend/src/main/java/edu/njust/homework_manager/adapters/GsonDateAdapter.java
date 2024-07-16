package edu.njust.homework_manager.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;


// This class is used to serialize Date objects to timestamp(long) values
public class GsonDateAdapter implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(date.getTime());
    }
}
