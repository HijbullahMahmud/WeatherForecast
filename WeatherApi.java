package com.mahmud.weatherforecast;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by User on 5/11/2017.
 */

public interface WeatherApi {


    @GET("v1/public/yql")
    Call<WeatherMain> getWeatherData(
            @QueryMap Map<String, String>queryMap
            );


}

// get(("v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D\"nome%2C%20ak\")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")