package com.example.farooq.googlemapgoogleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CODE = 1234;
    private static final int PLACE_PICKER_REQUEST = 1;
    private boolean mLocationPermission = false;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private Marker mMarker;
    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageButton mGPS,mInfo,mPlacePicker;
    //variable
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private LatLngBounds LAT_LANG_BOUND = new LatLngBounds(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));
    private PlacesInfo mPlace;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                Log.d(TAG, "AdapterView : getting Result");
                result.setResultCallback(mUpdatePlcaceDetailsCallBack);
                Log.d(TAG, "AdapterView : passing Result");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPersmissionResult : requesting permission");
        mLocationPermission = false;
        switch (requestCode){
            case REQUEST_CODE:{
                if(grantResults.length >  0 ){
                    for (int i=0; i < grantResults.length ;i++){
                        if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                            mLocationPermission=false;
                            Log.d(TAG,"permission failed");
                            return;
                        }
                    }
                }
            }
        }
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mGPS = (ImageButton) findViewById(R.id.gps_current);
        mInfo = (ImageButton) findViewById(R.id.info);
        mPlacePicker = (ImageButton) findViewById(R.id.place_picker);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        getLocationPermission();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission : getting Location Permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission : Permission FINE_LOCATION");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission : permission COURSE_LOCATION ");
                mLocationPermission = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(MapActivity.this, permissions, REQUEST_CODE);
                getLocationPermission();
            }
        } else {
            ActivityCompat.requestPermissions(MapActivity.this, permissions, REQUEST_CODE);
            getLocationPermission();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: instialize our Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady : on Map ready now");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (mLocationPermission) {
            Log.d(TAG, "onMapReady : checking Permission");
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "onMapReady : location Enabled");
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }
    private void getDeviceLocation() {
        Log.d(TAG,"getDeviceLocation : getting device Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermission) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"getDeviceLocation : device Location Sucessfully get");
                            // Set the map's camera position to the current location of the device.
                            Location location =(Location) task.getResult();
                            if(location != null){
                                Log.d(TAG,"getDeviceLocation : device Location is not null");
                                moveToCamera(new LatLng(location.getLatitude(),location.getLongitude()), DEFAULT_ZOOM, "My Location");
                            }
                            else {
                                Log.d(TAG,"getDeviceLocation : device Location is  null");
                            }

                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void init(){
        Log.d(TAG,"InitRunning :  now in init");
        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG,"onClick : GooglePlayServicesRepairableException "+e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG,"onClick : GooglePlayServicesNotAvailableException "+e.getMessage());
                }
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick : info click");
                try {
                    if (mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){

                }
            }
        });
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick : gps click");
                getDeviceLocation();
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Log.d(TAG, "InitRunning : GoogleApi Client");
        mSearchText.setOnItemClickListener(mAutoCompleteListner);
        Log.d(TAG, "InitRunning : onitem click listner");
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LANG_BOUND,null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }
                return true;
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG,"geoLocate :  geolocating");
        String search = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(search,1);
        }catch (IOException e){

        }
        if(list.size()>0){
            Address address= list.get(0);
            Log.d(TAG,"geolocate Find a location : "+ address.toString());
            moveToCamera(new LatLng(address.getLatitude(),address.getLongitude()),
                    DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }
    private void moveToCamera(LatLng latLng,float zoom,PlacesInfo placesInfo){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));
        if (placesInfo!=null){
            try{
                String snippet = "Address   : " + placesInfo.getAddress() + " \n"+
                                 "Website   : " + placesInfo.getWebsiteUri() + " \n"+
                        "Rating    : " + placesInfo.getRating() + " \n"+
                        "Phone No  : " + placesInfo.getPhoneNumber() + "\n"+
                        "Coordinaor: " + placesInfo.getLatLng() + "\n"+
                        "Attribute : " + placesInfo.getAttribute()  ;
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(placesInfo.getName()).snippet(snippet);
                        mMarker=mMap.addMarker(markerOptions);
            }catch (NullPointerException e){
                Log.d(TAG,"moveToCameraPlace : NullPointerException is at "+e.getMessage());
            }
        }else{

            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideSoftKeyboard();
    }
    private void moveToCamera(LatLng latLng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals("My Location")){
            MarkerOptions options =  new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }
    //=================== Google plcaes api ===============
    private AdapterView.OnItemClickListener mAutoCompleteListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "AdapterView : onItemSelected");
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            Log.d(TAG, "AdapterView : getting Result");
            result.setResultCallback(mUpdatePlcaceDetailsCallBack);
            Log.d(TAG, "AdapterView : passing Result");
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlcaceDetailsCallBack;
    {
        mUpdatePlcaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    Log.d(TAG, "OnResults : Places query is not running successfully" + places.getStatus().toString());
                    places.release();
                    return;
                }
                final Place place = places.get(0);

                try {
                    mPlace = new PlacesInfo();
                    mPlace.setAddress(place.getAddress().toString());
                    mPlace.setName(place.getName().toString());
                    mPlace.setId(place.getId());
                  //  mPlace.setAttribute(place.getAttributions().toString());
                    mPlace.setLatLng(place.getLatLng());
                    mPlace.setWebsiteUri(place.getWebsiteUri());
                    mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                    mPlace.setRating(place.getRating());

                    Log.d(TAG,"OnResults : Result : " + mPlace.toString());
                }catch (NullPointerException e){
                    Log.d(TAG,"OnResults : NullPointerException " + e.getMessage());
                }
                moveToCamera(new LatLng(place.getViewport().getCenter().latitude,
                        place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mPlace);
                places.release();
            }
        };
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
