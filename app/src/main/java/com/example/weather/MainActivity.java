package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    EditText inputCity;
    TextView tv_result;
    Button inputButton;

    String cityName;

    /*----If you want to use this app for every city and country
          you need to generate API key from "openWeatherMap.org"-----*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputButton = (Button) findViewById(R.id.btn_weather);
        tv_result = (TextView) findViewById(R.id.result);
        inputCity = (EditText) findViewById(R.id.et_city);

    }

    public void btnListener(View view){
        //get the city name from editText
        cityName = String.valueOf(inputCity.getText());

        //for keyboard to hide after pressing button
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(inputCity.getWindowToken(), 0);

        //this will load the json from openWeather.org
        DownloadJson jsonTask = new DownloadJson();

        /*---This is the Sample LINK from openweathermap.org ----
             https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22


             //----------------------------------------------------//
             YOUR Generated API key goes here (appid=YOUR_API_KEY)

             Paste this address at the place of "UPPER LINK"

             -->https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid=YOUR_API_KEY
             //----------------------------------------------------//


         */

        jsonTask.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
    }



    public class DownloadJson extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection;
            String result = "";

            try {

                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {

                e.printStackTrace();

                return null;

            } catch (IOException e) {

                e.printStackTrace();

                return null;

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(cityName != ""){

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    String weatherInfo = jsonObject.getString("weather");

                    String weather_result = "";

                    JSONArray arr = new JSONArray(weatherInfo);

                    for(int i = 0; i<arr.length() ; i++){

                        JSONObject jsonPart = arr.getJSONObject(i);

                        String main = jsonPart.getString("main");
                        String description = jsonPart.getString("description");



                        if(main != "" && description != ""){

                            weather_result = main + ": "+description + "\n";

                        }


                    }
                    if(weather_result != "")
                        tv_result.setText(weather_result);
                    else {
                        Toast.makeText(MainActivity.this, "could not find weather", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                        Toast.makeText(MainActivity.this, "could not find weather", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}
