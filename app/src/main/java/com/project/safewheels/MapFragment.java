package com.project.safewheels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.project.safewheels.Entity.Favorite;
import com.project.safewheels.Entity.FavoriteAddresses;
import com.project.safewheels.Entity.RoadWork;
import com.project.safewheels.Tools.DirectionsParser;
import com.project.safewheels.Tools.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class handle all operations that related to the google map
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView vMaps;
    private Geocoder geocoder;
    private MarkerOptions markerOptions = new MarkerOptions();
    MarkerOptions meMarker = new MarkerOptions();
    SmsManager smsManager;
    LatLng destLatLng;
    MarkerOptions destMarker;
    Handler handler;
    Runnable runnable;
    Timer timer;
    ArrayList<ArrayList<LatLng>> paths;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FavoriteAddresses addresses;
    String phoneNumber;
    private Button btn_dest;
    private ImageButton btn_like;
    private ImageButton btn_unlike;
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
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private String mLastUpdateTime;
    private final static int DELAY = 15000;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String API_KEY = "AIzaSyDdIC2V-gln9f5dr3V791hJZuxz1SX5kb0";
    private static final RectangularBounds LAT_LNG_BOUNDS = RectangularBounds.newInstance(
            new LatLng( -37.904116, 144.907608 ), new LatLng( -37.785368, 145.067425));
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(20);
    private static final PatternItem GAP = new Gap(20);
    private static final List<PatternItem> PATTERN_ITEMS = Arrays.asList(DOT, DASH, GAP);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        vMaps = (MapView) rootView.findViewById(R.id.mapView);
        vMaps.onCreate(savedInstanceState);

        vMaps.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        btn_dest = (Button)rootView.findViewById(R.id.btn_dest);
        btn_unlike = (ImageButton)rootView.findViewById(R.id.btn_unlike);
        btn_like = (ImageButton) rootView.findViewById(R.id.btn_like);
        lv_info = (LinearLayout)rootView.findViewById(R.id.layout_info);
        tv_distance = (TextView)rootView.findViewById(R.id.tv_distance);
        tv_duration = (TextView)rootView.findViewById(R.id.tv_duration);
        tv_exit = (TextView)rootView.findViewById(R.id.tv_exit);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        addresses = getFavoriteList();
        if (addresses == null){
            List<Favorite> list = new ArrayList<>();
            addresses = new FavoriteAddresses(list);
        }

        phoneNumber = getActivity().getIntent().getStringExtra("phoneNumber");

        Places.initialize(getActivity().getApplicationContext(), API_KEY);

        if (!Places.isInitialized()){
            Places.initialize(getActivity().getApplicationContext(), API_KEY);
        }

        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
        getChildFragmentManager().findFragmentById(R.id.auto_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setCountry("au");
        autocompleteSupportFragment.setHint("Where do you want to go...");
        autocompleteSupportFragment.setLocationBias(LAT_LNG_BOUNDS);
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull final Place place) {
                mMap.clear();
                btn_dest.setVisibility(View.VISIBLE);

                setFavoriteButton(place, null);

                destLatLng = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        destLatLng, DEFAULT_ZOOM));
                destMarker = new MarkerOptions();

                meMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                meMarker.position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                destMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                destMarker.position(destLatLng);
                destMarker.title(place.getName());
                destMarker.snippet(place.getAddress());
                mMap.addMarker(destMarker);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        btn_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_dest.setVisibility(View.GONE);
                showInfos();
                startLocationUpdates();
                runMessageHandler();
            }
        });

        getLocationPermission();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        vMaps.getMapAsync(this);

        mRequestingLocationUpdates = false;
        mSettingsClient = LocationServices.getSettingsClient(getActivity().getApplicationContext());

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingRequest();

        smsManager = SmsManager.getDefault();

        return rootView;
    }

    private void setFavoriteButton(final Place place, final Favorite favorite) {
        if (favorite == null){
            if (addresses.size() == 0 || !addresses.isContain(place.getId())){
                btn_unlike.setVisibility(View.GONE);
                btn_like.setVisibility(View.VISIBLE);
                btn_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favorite favorite = new Favorite(place.getId(), place.getAddress(), place.getLatLng());
                        showInputDialog(favorite);
                    }
                });
            }else{
                btn_like.setVisibility(View.GONE);
                btn_unlike.setVisibility(View.VISIBLE);
                btn_unlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Removed " + place.getName() + " from My favorite", Toast.LENGTH_SHORT).show();
                        btn_unlike.setVisibility(View.GONE);
                        btn_like.setVisibility(View.VISIBLE);
                        addresses.remove(place.getId());
                        saveFavorite(addresses);
                    }
                });
            }
        }else{
            btn_like.setVisibility(View.GONE);
            btn_unlike.setVisibility(View.VISIBLE);
            btn_unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Removed " + favorite.getName() + " from My favorite", Toast.LENGTH_SHORT).show();
                    btn_unlike.setVisibility(View.GONE);
                    btn_like.setVisibility(View.VISIBLE);
                    addresses.remove(favorite.getId());
                    saveFavorite(addresses);
                }
            });
        }

    }

    private void showInputDialog(final Favorite favorite) {
        final EditText editText = new EditText(getActivity());
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Please insert the name of the Place")
                .setView(editText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favorite.setName(editText.getText().toString());
                        btn_like.setVisibility(View.GONE);
                        btn_unlike.setVisibility(View.VISIBLE);
                        addresses.add(favorite);
                        saveFavorite(addresses);
                        Toast.makeText(getContext(), "Added " + favorite.getName() + " to My favorite", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideSoftKeyboard();
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void saveFavorite(FavoriteAddresses favoriteAddress){
        Gson gson = new Gson();
        String json = gson.toJson(favoriteAddress);
        editor.putString("addresses" ,json);
        editor.apply();
    }

    private FavoriteAddresses getFavoriteList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("addresses", "");
        FavoriteAddresses favoriteAddresses = gson.fromJson(json, FavoriteAddresses.class);
        return favoriteAddresses;
    }

    private void runMessageHandler() {
        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run() {
                String text = "Safe wheels notification!\nThe user of the app have arrived at the destination. Thank you";
                startLocationUpdates();
                Location dest = new Location("");
                dest.setLatitude(destLatLng.latitude);
                dest.setLongitude(destLatLng.longitude);
                if (mLastKnownLocation.distanceTo(dest) < 500){
                    sendTextMessage(text);
                    System.out.println("Arrived");
                    tv_distance.setText("Arrived!");
                    tv_duration.setText("");
                    tv_distance.setTextColor(Color.GREEN);
                    mMap.clear();
                    getDeviceLocation(0);
                    stopLocationUpdates();
                    Toast.makeText(getContext(), "Arrived! Quit from navigation mode.", Toast.LENGTH_LONG).show();

            }
        }
    };
        handler.postDelayed(runnable, DELAY);
    }

    private void runTimer(int duration){
            ScheduledTimer scheduledTimer = new ScheduledTimer();
            scheduledTimer.execute(Integer.toString(duration));
    }

    private String getAddressFromLastKnownLocation() {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        return address;
    }

    private void sendTextMessage(String text){
        smsManager.sendTextMessage(phoneNumber, null, text, null, null);
//        Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_LONG).show();
        stopLocationUpdates();
        if (timer != null){
            timer.cancel();
            timer = null;
        }else{
            handler.removeCallbacks(runnable);
        }
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

//        getLocationPermission();
        updateLocationUI();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                TextView tv_title = v.findViewById(R.id.title);
                TextView tv_snippet = v.findViewById(R.id.snippet);

                tv_title.setText(marker.getTitle());
                tv_snippet.setText(marker.getSnippet());
                return v;
            }
        });

        if (getArguments() != null && getArguments().getBoolean("navigate")){
            getDeviceLocation(3);
            int position = getArguments().getInt("position");
            setFavoriteButton(null, addresses.getAddressList().get(position));
            navigate(position, null);
        }else{
            getDeviceLocation(0);
        }
    }

    private void navigate(int placePosition, LatLng start) {
        if (start == null){
            Bundle bundle = new Bundle();
            bundle.putBoolean("navigate", false);
            Favorite favorite = addresses.getAddressList().get(placePosition);
            destLatLng = favorite.getLatLng();
            destMarker = new MarkerOptions();
            destMarker.title(favorite.getName());
            destMarker.snippet(favorite.getAddress());
        }else{

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                destLatLng, DEFAULT_ZOOM));
        destMarker = new MarkerOptions();

        meMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        destMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destMarker.position(destLatLng);
        mMap.addMarker(destMarker);
        btn_dest.setVisibility(View.VISIBLE);
    }

    private String getRequestUrl(LatLng latLng1, LatLng latLng2, int method) {
        String url = "";
        String curr_lat = "curr_lat=" + latLng1.latitude;
        String curr_lon = "curr_lon=" + latLng1.longitude;
        String dest_lat = "dest_lat=" + destLatLng.latitude;
        String dest_lon = "dest_lon=" + destLatLng.longitude;
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
            url = "https://cq2slzjdcf.execute-api.ap-southeast-2.amazonaws.com/getCrash/?" + curr_lat + "&" + curr_lon + "&" + dest_lat + "&" + dest_lon;
        }else if (method == 3){
            url = "https://liva02vfda.execute-api.ap-southeast-2.amazonaws.com/getLanes?" + curr_lat + "&" + curr_lon + "&" + dest_lat + "&" + dest_lon;
        }else{
            url = "https://pmhn8jleph.execute-api.ap-southeast-2.amazonaws.com/getRoadWorks?" + curr_lat + "&" + curr_lon + "&" + dest_lat + "&" + dest_lon;
        }

        return url;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getDeviceLocation(final int operationCode) {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            switch (operationCode){
                                case 0:
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    break;
                                case 1:
                                    showInfos();
                                    break;
                                case 2:
                                    meMarker.position(new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()));
                                    break;
                            }
                        }
                        else {
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

    private void showInfos(){
        getAccidentFromLocation();
        getBicycleLaneFromLocation();
        getRoadWorkInfo();
        String url = getRequestUrl(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), destLatLng, 1);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
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
        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        String url = getRequestUrl(latLng, null, 4);
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
        String url = getRequestUrl(latLng, destLatLng, 3);
        BicycleLaneAsync bicycleLaneAsync = new BicycleLaneAsync();
        bicycleLaneAsync.execute(url);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
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
                responseString = RestClient.requestFromUrl(strings[0], "");
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
                polylineOptions.color(Color.CYAN);
                polylineOptions.geodesic(true);
            }

            PolylineOptions me = new PolylineOptions();
            LatLng first = polylineOptions.getPoints().get(0);
            me.add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), first);
            me.color(Color.BLUE);
            me.geodesic(true);
            me.width(15);
            me.pattern(PATTERN_ITEMS);
            mMap.addPolyline(me);
            mMap.addPolyline(polylineOptions);
            lv_info.setVisibility(View.VISIBLE);
            int durationInt = getIntFromString(duration);
            runTimer(durationInt);
            tv_duration.setText(duration);
            tv_distance.setText(distance);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 17));
            tv_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lv_info.setVisibility(View.GONE);
                    mMap.clear();
                    getDeviceLocation(0);
                    handler.removeCallbacks(runnable);
                    stopLocationUpdates();
                }
            });
        }
    }

    private int getIntFromString(String duration) {
        int number = 0;
        for (int i = 0; i < duration.length(); i++){
            if (Character.isDigit(duration.charAt(i))){
                number = number * 10 + duration.charAt(i);
            }
        }
        return number;
    }

    private class AccidentAsyncTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
            ArrayList<HashMap<String, String>> attributes = new ArrayList<>();
            try {
                String key = getResources().getString(R.string.crash_api_key);
                String result = RestClient.requestFromUrl(strings[0], key);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++){
                    HashMap<String, String > hm = new HashMap<>();
                    JSONObject  jsonObject = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("COORDINATES");
                    String count = Integer.toString(jsonObject.getInt("ACCIDENT"));
                    hm.put("count", count);
                    hm.put("start", jsonArray1.getString(0));
                    hm.put("dest", jsonArray1.getString(1));
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
                String startString = hashMap.get("start");
                String[] starts = startString.split(" ");
                LatLng start = new LatLng(Double.parseDouble(starts[1]), Double.parseDouble(starts[0]));
                String destString = hashMap.get("dest");
                String[] dests = destString.split(" ");
                LatLng dest = new LatLng(Double.parseDouble(dests[1]), Double.parseDouble(dests[0]));
                String countString = hashMap.get("count");
                int count = Integer.parseInt(countString);
                PolylineOptions crash = new PolylineOptions();
                crash.add(start, dest).width(18);
                if (count <= 2){
                    crash.color(getResources().getColor(R.color.lesstwo));
                }else if (count <= 4){
                    crash.color(getResources().getColor(R.color.lessfour));
                }else{
                    crash.color(getResources().getColor(R.color.greatfout));
                }
                mMap.addPolyline(crash);
            }
        }
    }

    private class BicycleLaneAsync extends AsyncTask<String, Void, ArrayList<ArrayList<LatLng>>>{

        @Override
        protected ArrayList<ArrayList<LatLng>> doInBackground(String... strings) {
            if (paths != null){
                return paths;
            }else{
                ArrayList<ArrayList<LatLng>> newPath = new ArrayList<>();
                try {
                    String key = getResources().getString(R.string.crash_api_key);
                    String result = RestClient.requestFromUrl(strings[0], key);
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
                        newPath.add(points);
                    }
                    return newPath;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<LatLng>> paths) {
            for (ArrayList<LatLng> points: paths){
                if (!points.isEmpty()){
                    for(int i =0; i < points.size()-1; i++){
                        Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true)
                        .add(points.get(i), points.get(i+1))
                        .color(Color.parseColor("#3A9742"))
                                .width(9));
                        polyline.setTag("Bicycle Lanes");
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
                String key = getResources().getString(R.string.crash_api_key);
                String result = RestClient.requestFromUrl(strings[0], key);
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
                    roadWorkMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworks));
                }else{
                    roadWorkMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.skeching));
                }
                roadWorkMarker.title(roadWork.getIncident_type());
                roadWorkMarker.snippet(roadWork.getIncident_desc());
                roadWorkMarker.position(roadWork.getLatLng());
                mMap.addMarker(roadWorkMarker).showInfoWindow();
            }
        }
    }

    private class ScheduledTimer extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            final String address = getAddressFromLastKnownLocation();
            final String api = "https://www.google.com/maps/search/?api=1&query=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
            int delay = 0;
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    String text = "Safe wheels ALERT! The user has not arrive the destination in time. And the last known location is " +
                            address + ". ";
                    String text1 = "Your can access the location at: " + api;
                    sendTextMessage(text);
                    sendTextMessage(text1);
                    System.out.println(text.length());
                    System.out.println("timer runned");
                }
            };
            int duration = Integer.parseInt(strings[0]);
            if (duration <= 10){
                delay = duration + 5;
            }else if (duration <= 20){
                delay = duration + 10;
            }else{
                delay = duration + 20;
            }
//            timer.schedule(task, (delay) * 60);
            timer.schedule(task, 30000);
            return null;
        }
    }
}

