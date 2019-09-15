package com.project.safewheels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.project.safewheels.Entity.RoadWork;
import com.project.safewheels.Tools.DirectionsParser;
import com.project.safewheels.Tools.ReadAndWrite;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        LocationListener {

    private MapView vMaps;
    private Geocoder geocoder;
    private ArrayList<LatLng> listPoints;
    private MarkerOptions markerOptions = new MarkerOptions();
    MarkerOptions meMarker = new MarkerOptions();
    SmsManager smsManager;
    LatLng destLatLng;
    MarkerOptions destMarker;
    Handler handler;
    Runnable runnable;

    private Button btn_dest;
    private LinearLayout lv_info;
    private TextView tv_exit;
    private TextView tv_duration;
    private TextView tv_distance;
    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingRequest;
    private boolean mRequestingLocationUpdates;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private String mLastUpdateTime;
    private final static int DELAY = 60000;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String API_KEY = "AIzaSyDdIC2V-gln9f5dr3V791hJZuxz1SX5kb0";
    private static final RectangularBounds LAT_LNG_BOUNDS = RectangularBounds.newInstance(
            new LatLng( -37.904116, 144.907608 ), new LatLng( -37.785368, 145.067425));


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        vMaps = (MapView) rootView.findViewById(R.id.mapView);
        vMaps.onCreate(savedInstanceState);

        vMaps.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        btn_dest = (Button)rootView.findViewById(R.id.btn_dest);
        lv_info = (LinearLayout)rootView.findViewById(R.id.layout_info);
        tv_distance = (TextView)rootView.findViewById(R.id.tv_distance);
        tv_duration = (TextView)rootView.findViewById(R.id.tv_duration);
        tv_exit = (TextView)rootView.findViewById(R.id.tv_exit);

        Places.initialize(getActivity().getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(getActivity());

        if (!Places.isInitialized()){
            Places.initialize(getActivity().getApplicationContext(), API_KEY);
        }

        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
        getChildFragmentManager().findFragmentById(R.id.auto_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setCountry("au");
        autocompleteSupportFragment.setLocationBias(LAT_LNG_BOUNDS);
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                btn_dest.setVisibility(View.VISIBLE);
                destLatLng = place.getLatLng();
                System.out.println(destLatLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        destLatLng, DEFAULT_ZOOM));
                destMarker = new MarkerOptions();

                meMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                meMarker.position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                destMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                destMarker.position(destLatLng);
                destMarker.title(place.getAddress());
                mMap.addMarker(destMarker);
                btn_dest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_dest.setVisibility(View.GONE);
                        String url = getRequestUrl(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), destLatLng, 1);
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);
                        runHandler();
                    }
                });


            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });




        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        listPoints = new ArrayList<>();

        vMaps.getMapAsync(this);

        mRequestingLocationUpdates = false;
//        updateValuesFromBundle(savedInstanceState);
        mSettingsClient = LocationServices.getSettingsClient(getActivity().getApplicationContext());

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingRequest();

        smsManager = SmsManager.getDefault();

        getLocationPermission();
        return rootView;
    }

    private void runHandler() {
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                String str = ReadAndWrite.readFromFile(getActivity().getApplicationContext());
                if (!str.isEmpty()){
                    String phoneNumber = str.split(",")[1];
                    if (checkSmsPermission(Manifest.permission.SEND_SMS)){
                        startLocationUpdates();
                        Location dest = new Location("");
                        dest.setLatitude(destLatLng.latitude);
                        dest.setLongitude(destLatLng.longitude);
                        if (mLastKnownLocation.distanceTo(dest) < 500){
                            sendTextMessage(phoneNumber);
                        }
                        handler.postDelayed(runnable, DELAY);
                    }
                }
            }
        }, DELAY);
    }

    private boolean checkSmsPermission(String permission){
        int check = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void sendTextMessage(String phoneNumber){
        String message = ReadAndWrite.readMessageText();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_LONG).show();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState){
        if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)){
            mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
        }

        if (savedInstanceState.keySet().contains(KEY_LOCATION)){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)){
            mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
        }
        updateLocationUI();
    }

    public void onSavedInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void buildLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingRequest = builder.build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastKnownLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        updateLocationUI();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (listPoints.size() == 2){
                    listPoints.clear();
                    mMap.clear();
                }
                listPoints.add(latLng);

                markerOptions.position(latLng);
                if (listPoints.size() == 1){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2){
                    btn_dest.setVisibility(View.GONE);
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1), 1);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        });
        getDeviceLocation();

    }

    private String getRequestUrl(LatLng latLng1, LatLng latLng2, int method) {
        String url = "";
        if (method == 1){
            String str_org = "origin=" + latLng1.latitude + "," + latLng1.longitude;
            String str_dest = "destination=" + latLng2.latitude + "," + latLng2.longitude;
            String sensor = "sensor=false";
            String mode = "avoid=highways&mode=bicycling";
            String param = str_org + "&" + str_dest + "&" +"key=" + API_KEY + "&"+ sensor + "&" + mode;
            String output = "json";
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        }
        else if (method == 2){
            String lat = "lat=" + latLng1.latitude;
            String lon = "lon=" + latLng1.longitude;
            String key = getString(R.string.crash_api_key);
            url = "https://rvi11qkvd7.execute-api.ap-southeast-2.amazonaws.com/queryLatLon/?" + lat + "&" + lon;
        }else if (method == 3){
            String lat = "curr_lat=" + latLng1.latitude;
            String lon = "curr_lon=" + latLng1.longitude;
            url = "https://1u0g9wjenh.execute-api.ap-southeast-2.amazonaws.com/getBikeLanesV3/?" + lon + "&" + lat;
        }else{
            url = "https://bapwx0jn83.execute-api.ap-southeast-2.amazonaws.com/roadworksv1";
        }

        return url;
    }

    private String requestFromUrl(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            String longitude = String.valueOf(mLastKnownLocation.getLongitude());
                            String latitude = String.valueOf(mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            getAccidentFromLocation();
                            getBicycleLaneFromLocation();
                            getRoadWorkInfo();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        List<Address> addressList;
        LatLng p1 = null;

        try {
            addressList = geocoder.getFromLocationName(strAddress, 5);
            if (addressList == null || addressList.isEmpty()) {
                return null;
            }

            Address location = addressList.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return p1;
    }

    private void getRoadWorkInfo(){
        String url = getRequestUrl(null, null, 4);
        RoadWorkAsyncTask roadWorkAsyncTask = new RoadWorkAsyncTask();
        roadWorkAsyncTask.execute(url);
    }

    private void getAccidentFromLocation(){
        Double lon = mLastKnownLocation.getLongitude();
        Double lat = mLastKnownLocation.getLatitude();
        LatLng latLng = new LatLng(lat, lon);
        String url = getRequestUrl(latLng, null, 2);
        AccidentAsyncTask accidentAsyncTask = new AccidentAsyncTask();
        accidentAsyncTask.execute(url);
    }

    private void getBicycleLaneFromLocation(){
        Double lon = mLastKnownLocation.getLongitude();
        Double lat = mLastKnownLocation.getLatitude();
        LatLng latLng = new LatLng(lat, lon);
        String url = getRequestUrl(latLng, null, 3);
        BicycleLaneAsync bicycleLaneAsync = new BicycleLaneAsync();
        bicycleLaneAsync.execute(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        getDeviceLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("bicycle.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null){
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private class GetCycleLaneAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return loadJSONFromAsset();
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<LatLng> locations = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("features");
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("geometry");
                    JSONArray jsonArray1 = jsonObject2.getJSONArray("paths");
                    JSONArray jsonArray2 = jsonArray1.getJSONArray(0);
                    for (int j = 0; j < jsonArray2.length(); j++){
                        JSONArray jsonArray3 = jsonArray2.getJSONArray(j);
                        LatLng latLng = new LatLng(jsonArray3.getDouble(1), jsonArray3.getDouble(0));
                        locations.add(latLng);
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .add(locations.get(0), locations.get(locations.size()-1))
                            .width(5)
                            .color(Color.BLUE));
                    System.out.println(locations.get(0).toString());
                    locations.clear();
                }
                System.out.println("Done");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestFromUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    private class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionParser = new DirectionsParser();
                routes = directionParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            String duration = "";
            String distance = "";
            String start = "";
            String dest = "";

            for (List<HashMap<String, String>> path: lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (int i = 0; i < path.size(); i++){
                    if (i == 0){
                        HashMap<String, String> hash = path.get(0);
                        duration = hash.get("duration");
                        distance = hash.get("distance");
                        start = hash.get("start");
                        dest = hash.get("dest");
                    }
                    else{
                        double lat = Double.parseDouble(path.get(i).get("lat"));
                        double lon = Double.parseDouble(path.get(i).get("lng"));

                        points.add(new LatLng(lat, lon));
                    }
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            mMap.addPolyline(polylineOptions);
            lv_info.setVisibility(View.VISIBLE);
            tv_duration.setText(duration);
            tv_distance.setText(distance);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 20));
            tv_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lv_info.setVisibility(View.GONE);
                    mMap.clear();
                    getDeviceLocation();
                    handler.removeCallbacks(runnable);
                }
            });
        }
    }

    private class AccidentAsyncTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
            ArrayList<HashMap<String, String>> attributes = new ArrayList<>();
            try {
                String result = requestFromUrl(strings[0]);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length()-1; i++){
                    HashMap<String, String > hm = new HashMap<>();
                    JSONObject  jsonObject = jsonArray.getJSONObject(i);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("ACCIDENT_INFO");
                    hm.put("lat", String.valueOf(jsonObject1.getDouble("LATITUDE")));
                    hm.put("lon", String.valueOf(jsonObject1.getDouble("LONGITUDE")));
                    hm.put("BICYCLIST", jsonObject1.getString("BICYCLIST"));
                    attributes.add(hm);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return attributes;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            for (HashMap<String, String> hashMap: hashMaps){
                MarkerOptions accidents = new MarkerOptions();
                Double lat = Double.parseDouble(hashMap.get("lat"));
                Double lon = Double.parseDouble(hashMap.get("lon"));
                LatLng latLng = new LatLng(lat, lon);
                accidents.position(latLng);
                accidents.title("Accident could happen here");
                accidents.icon(BitmapDescriptorFactory.fromResource(R.drawable.accident));
                mMap.addMarker(accidents);
            }
        }
    }

    private class BicycleLaneAsync extends AsyncTask<String, Void, ArrayList<ArrayList<LatLng>>>{

        @Override
        protected ArrayList<ArrayList<LatLng>> doInBackground(String... strings) {
            ArrayList<ArrayList<LatLng>> paths = new ArrayList<>();
            try {
                String result = requestFromUrl(strings[0]);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++){
                    ArrayList<LatLng> points = new ArrayList<>();;
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("COORDINATES");
                    for (int j = 0; j < jsonArray1.length(); j++){
                        String str = jsonArray1.getString(j);
                        String[] strs = str.split(" ");
                        LatLng latLng = new LatLng(Double.parseDouble(strs[1]), Double.parseDouble(strs[0]));
                        points.add(latLng);
                    }
                    paths.add(points);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return paths;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<LatLng>> paths) {
            for (ArrayList<LatLng> points: paths){
                if (!points.isEmpty()){
                    for(int i =0; i < points.size()-1; i++){
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.add(points.get(i), points.get(i+1))
                                .color(Color.GREEN)
                                .width(5);
                        mMap.addPolyline(polylineOptions);
                    }

                }
            }
        }
    }

    private class RoadWorkAsyncTask extends AsyncTask<String, Void, ArrayList<RoadWork>>{

        @Override
        protected ArrayList<RoadWork> doInBackground(String... strings) {
            ArrayList<RoadWork> roadWorks = new ArrayList<>();
            try {
                String result = requestFromUrl(strings[0]);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("coordinates");
                    LatLng latLng = new LatLng(jsonArray1.getDouble(1), jsonArray1.getDouble(0));
                    String type = jsonObject.getString("incident_type");
                    String roadName = jsonObject.getString("incident_road_name");
                    String status = jsonObject.getString("incident_status");
                    String desc = jsonObject.getString("incident_desc");
                    RoadWork roadWork = new RoadWork(type, status, desc, latLng, roadName);
                    roadWorks.add(roadWork);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return roadWorks;
        }

        @Override
        protected void onPostExecute(ArrayList<RoadWork> roadWorks) {
            MarkerOptions roadWorkMarker = new MarkerOptions();
            for (RoadWork roadWork: roadWorks){
                if (roadWork.getIncident_status().equals("active")){
                    roadWorkMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.roadwork));
                }else{
                    roadWorkMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.roadwork));
                }
                roadWorkMarker.title(roadWork.getIncident_type());
                roadWorkMarker.snippet(roadWork.getIncident_desc());
                roadWorkMarker.position(roadWork.getLatLng());
                mMap.addMarker(roadWorkMarker);
            }
        }


    }
}

