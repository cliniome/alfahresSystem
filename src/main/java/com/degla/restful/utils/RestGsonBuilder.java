package com.degla.restful.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by snouto on 18/05/15.
 */
public class RestGsonBuilder {

    public static Gson createGson()
    {
        GsonBuilder builder = new GsonBuilder();



        return builder.create();

    }





}
