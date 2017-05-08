package com.appjumper.silkscreen.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson操作封装类
 * Created by Botx on 2017/3/20.
 */

public class GsonUtil {

    public static <T> T getEntity(String jsonStr, Class<T> entityClass) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        T entity = null;
        entity = gson.fromJson(jsonStr, entityClass);
        return entity;
    }

    public static <T> List<T> getEntityList(String jsonStr, Class<T> entityClass) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        List<T> list = new ArrayList<T>();
        //list = gson.fromJson(jsonStr, new TypeToken<List<T>>(){}.getType());

        JsonArray jsonArr = new JsonParser().parse(jsonStr).getAsJsonArray();
        for (JsonElement element : jsonArr) {
            list.add(gson.fromJson(element, entityClass));
        }

        return list;
    }

}
