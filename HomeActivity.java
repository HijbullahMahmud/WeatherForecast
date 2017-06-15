package com.mahmud.weatherforecast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    TextView cityNameTV, dateTv, sunriseTv, sunsetTv, windTv, timeshowTV, humidityTV, highTempTv, lowTempTv, currentTempTv, conditionTv;
    ImageView   lowTempIv, highTempIV;
    Spinner spinner;

    SwipeRefreshLayout swipeRefreshLayout;

    String selectedItem = "";

    ArrayAdapter<CharSequence> spinnerAdapter;
    WeatherApi weatherApi;

    SharedPreferences sharedPreference;
    String citySharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //   networkLibraryInitializer();
        // getWeatherData();
        // refreshWeather(curCity);

        spinner = (Spinner) findViewById(R.id.citySpinner);

        cityNameTV = (TextView) findViewById(R.id.cityNameTV);
        dateTv = (TextView) findViewById(R.id.dateTv);
        timeshowTV = (TextView) findViewById(R.id.timeShowTV);

        sunriseTv = (TextView) findViewById(R.id.sunriseTv);
        sunsetTv = (TextView) findViewById(R.id.sunsetTv);

        windTv = (TextView) findViewById(R.id.windTv);
        humidityTV = (TextView) findViewById(R.id.humidityTV);

        highTempTv = (TextView) findViewById(R.id.highTempTV);
        lowTempTv = (TextView) findViewById(R.id.lowTempTV);
        currentTempTv = (TextView) findViewById(R.id.currntTempTv);
        conditionTv = (TextView) findViewById(R.id.conditionTv);
        lowTempIv = (ImageView) findViewById(R.id.lowTempIV);
        highTempIV = (ImageView) findViewById(R.id.highTempIV);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipRefresh);


        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.citySpinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // spinnerTest.setText(getBaseContext(), parent.getItemAtPosition(position)+"");
               // Toast.makeText(HomeActivity.this, parent.getItemAtPosition(position) + "", Toast.LENGTH_SHORT).show();
                selectedItem = String.valueOf(parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(25);


                // spinnerTest.setText(selectedItem);
                showWeatherData(selectedItem);
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(HomeActivity.this, "Select Your City", Toast.LENGTH_SHORT).show();

            }
        });


        if (isInternetConnected() == false) {
            cityNameTV.setText("Connection failed");
            cityNameTV.setTextColor(getResources().getColor(R.color.red));
            spinner.setVisibility(View.GONE);

        } else {

              


        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showWeatherData(selectedItem);
                swipeRefreshLayout.setRefreshing(false); 
               // Toast.makeText(HomeActivity.this, "refreshed", Toast.LENGTH_SHORT).show();
            }


        });

    }



    private void showWeatherData(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // weatherApi = retrofit.create(WeatherApi.class);
       // final String city = "dhaka";



        String query = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='c'", city);
        final Map<String, String> data = new HashMap<>();
        data.put("format", "json");
        data.put("q", query);
        weatherApi = retrofit.create(WeatherApi.class);



        //   String url =String.format("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22dhaka%22)&format=json");

        Call<WeatherMain> call = weatherApi.getWeatherData(data);
        call.enqueue(new Callback<WeatherMain>() {
            @Override
            public void onResponse(Call<WeatherMain> call, Response<WeatherMain> response) {
               // Toast.makeText(HomeActivity.this, "onResponse for " + curCity.toString() + "", Toast.LENGTH_SHORT).show();
                response.body();

               /* SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString("weatherData", weatherApi.getWeatherData(data).toString());
                editor.commit();*/

                 String[] dateArray = response.body().getQuery().getResults().getChannel().getLastBuildDate().split(" ");
                 String time = dateArray[4] +" "+dateArray[5] +" "+dateArray[6];
                 String date = dateArray[0] +" "+dateArray[1] +" "+dateArray[2] +" "+dateArray[3];

                cityNameTV.setText(response.body().getQuery().getResults().getChannel().getLocation().getCountry());

               // cityNameTV.setText(city);
                dateTv.setText(date);
                timeshowTV.setText(time);

                sunriseTv.setText("Sunrise : "+response.body().getQuery().getResults().getChannel().getAstronomy().getSunrise());
                sunsetTv.setText("Sunset : "+response.body().getQuery().getResults().getChannel().getAstronomy().getSunset());
                windTv.setText("Wind : "+response.body().getQuery().getResults().getChannel().getWind().getSpeed()+" mph");
                humidityTV.setText("Humidity : " + response.body().getQuery().getResults().getChannel().getAtmosphere().getHumidity() + "%");

                lowTempIv.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                highTempIV.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                highTempTv.setText(response.body().getQuery().getResults().getChannel().getItem().getForecast().get(0).getHigh()+(char) 0x00B0 +"C");
                lowTempTv.setText(response.body().getQuery().getResults().getChannel().getItem().getForecast().get(0).getLow()+(char) 0x00B0 +"C");
                currentTempTv.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp()+(char) 0x00B0 +"C");
                conditionTv.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getText());
            }

            @Override
            public void onFailure(Call<WeatherMain> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();


            }
        });


    }





    //check the internet is connected
    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refreshMenuOnClick(MenuItem item) {
         if (isInternetConnected()== true){
             showWeatherData(selectedItem);
             Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
         }else {
             Toast.makeText(this, "Failed ! Check internet connection", Toast.LENGTH_SHORT).show();
         }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_resourse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }




           /* @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                switch (requestCode) {
                    case MY_PERMISSION_REQUEST_LOCATION: {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                try {
                                  //  cityNameTV.setText(hereLocation(location.getLatitude(), location.getLongitude()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Not Found !", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                                cityNameTV.setText("Internet connection failed");
                            }
                        }
                    }
                }
            }*/



           /* private class LocationWeatherException extends Exception {
                LocationWeatherException(String detailMessage) {
                    super(detailMessage);
                }
            }*/

}





