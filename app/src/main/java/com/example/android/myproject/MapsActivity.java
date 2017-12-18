package com.example.android.myproject;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

//com.google.android.gms.maps.GoogleMap

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public  static  String LOG_TAG ="MainActivity";
    public static String REQUEST_URL_LOCATTION = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
    public static String REQUEST_URL_WEATHER = "http://dataservice.accuweather.com/currentconditions/v1/";


    private GoogleMap mMap;
    public static double lat;
    public static double longi;
    private EditText location;
    private TextView weatherData;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setContentView(R.layout.activity_main);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();

//Extract the data…
        String name = bundle.getString("NAME");

//Create the text view
        textView = (TextView) findViewById(R.id.display_name);
        //name = "Sakshi";
        textView.setText("Hello "+name);
        lat  = 23.215635;
         longi = 72.63694149;

        location = (EditText) findViewById(R.id.location);
        weatherData = (TextView) findViewById(R.id.test);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        //weatherData.setText(name);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = location.getText().toString();
                if(s==null||s=="")
                {
                    Toast.makeText(MapsActivity.this,"Hello please enter location ",Toast.LENGTH_SHORT);
                }
                else {

                    geocoderTry(location.getText().toString());
                    URL url = doJob();
                    new getWeatherTask().execute(url);


                }

            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public URL doJob() {
        String s = makeURLLocation();
        URL url = createUrl(s);
        return url;

    }
    public String makeURLLocation(){

        Uri baseUri = Uri.parse(REQUEST_URL_LOCATTION);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("apikey", "CERsr91x3ux7J8miG0GU11cdmMaOcI3W");
        String loc1 = Double.toString(lat);
        String loc2 =Double.toString(longi);
        String parameterQ= loc1 +","+loc2;
        uriBuilder.appendQueryParameter("q", parameterQ);
        uriBuilder.appendQueryParameter("language", "en-us");
        uriBuilder.appendQueryParameter("details", "false");
        uriBuilder.appendQueryParameter("toplevel", "false");
        Log.v(LOG_TAG,uriBuilder.toString());


        // Log.e("EARTHQUAKEACTIVITY",uriBuilder.toString());
        // return new EarthquakeLoader(this, uriBuilder.toString());
        return uriBuilder.toString();

    }
    public String makeURLWeather(String location){
        String r = REQUEST_URL_WEATHER + location;

        Uri baseUri = Uri.parse(r);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("apikey", "CERsr91x3ux7J8miG0GU11cdmMaOcI3W");
        //uriBuilder.appendQueryParameter("q", "28.65381,77.22897");
        uriBuilder.appendQueryParameter("language", "en-us");
        uriBuilder.appendQueryParameter("details", "false");
        //uriBuilder.appendQueryParameter("toplevel", "false");


        // Log.e("EARTHQUAKEACTIVITY",uriBuilder.toString());
        // return new EarthquakeLoader(this, uriBuilder.toString());
        Log.v(LOG_TAG,uriBuilder.toString());
        return uriBuilder.toString();
    }




    // creates a ur l
    private static URL createUrl(String stringUrl){

        URL url = null;
        try{
            url = new URL(stringUrl);
        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error Creating Url", e);
        }
        return url;

    }
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(100000 /*milliseconds*/);
            urlConnection.setConnectTimeout(150000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode()== 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG, "Error Response Code : " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem Retrieving the json Response", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {

        // to store the json Response
        StringBuilder output = new StringBuilder();

        if (inputStream != null){

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){

                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    public static String getlocation(String jsonResponse) {

        // if the json Response is empty or null, then return
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        String key1="";
        try{

            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            key1 = baseJsonResponse.getString("Key");



        } catch(JSONException e) {
            Log.e("QueryUtils.java", "Problem Parsing the result.", e);
        }

        return  key1;

    }


    public static String getWeather(String jsonResponse){
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        String weather="";

        try{

            JSONArray baseJsonResponse = new JSONArray(jsonResponse);
            int i=0;
            JSONObject o = baseJsonResponse.getJSONObject(i);


            String weatherText   = o.getString("WeatherText");
            JSONObject temperature  = o.getJSONObject("Temperature");
            JSONObject metric = temperature.getJSONObject("Metric");
            Double Value = metric.getDouble("Value");
            String Unit = metric.getString("Unit");
            weather = weatherText+":"+Value+Unit;



        } catch(JSONException e) {
            Log.e("QueryUtils.java", "Problem Parsing the result.", e);
        }
        return weather;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(lat, longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Gandhinagar"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }


    public void changemarker(GoogleMap googleMap){

        mMap = googleMap;
        mMap.clear();
        //lat =  28.65381;
        //longi = 77.22897;
        LatLng sydney = new LatLng(lat, longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public  void geocoderTry(String loc) {
        Geocoder geocoder  = new Geocoder(this);
        Log.v("location is:",loc);

        try {
            List<Address> geoResults = geocoder.getFromLocationName(loc, 1);
            while (geoResults.size()==0) {
                //geoResults = geocoder.getFromLocationName("gandhinagar ", 1);
                Toast.makeText(this,"in geocoder",Toast.LENGTH_LONG);
            }
            if (geoResults.size()>0) {
                Toast.makeText(this,"deeeppppp",Toast.LENGTH_LONG);
                Address addr = geoResults.get(0);
                 lat   = addr.getLatitude();
                longi = addr.getLongitude();
                String s = Double.toString(lat);
                String t = Double.toString(longi);
                Log.v("hi","Latitude is:"+ s);
                changemarker(mMap);


                //test.setText(s +t );



            }
        }


        catch (Exception e) {
            Log.v("hi", (e.getMessage()));
        }
    }

    private class getWeatherTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {



            //perform httpRequest to the URl and Receive JSON Response

            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(urls[0]);
                Log.v(LOG_TAG,"JSON_RESPONSE:"+jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem Closing inputStream", e);
            }
            String location = getlocation(jsonResponse);


            Log.v(LOG_TAG,"LOCATION KEY IS:"+location);



////////////////////////////////

            String t = makeURLWeather(location);
            URL url2 = createUrl(t);
            String jsonResponse2 = null;


            try {
                jsonResponse2 = makeHttpRequest(url2);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem Closing inputStream", e);
            }

            String weather = getWeather(jsonResponse2);
            Log.v(LOG_TAG,"WEATHER:"+weather);
            return  weather;



        }

        protected void onProgressUpdate(Integer... progress) {
            //etProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            weatherData.setText(result);
            //showDialog("Downloaded " + result + " bytes");
        }
    }


}
