package com.example.andriypuhach.android_teamvoy_test;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class Serializer implements JsonSerializer<DateTime>, JsonDeserializer<DateTime>
{

    private static final String PATTERN = "yyyy/MM/dd";
    final DateTimeFormatter fmt = DateTimeFormat.forPattern(PATTERN);


    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context)
    {
        String retVal = fmt.print(src);
        return new JsonPrimitive(retVal);
    }


    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        String normJson=json.toString().substring(1,json.toString().length()-1);
        return fmt.parseLocalDate(normJson).toDateTimeAtStartOfDay();
    }
}