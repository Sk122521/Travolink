package com.hfad.travelx;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GeocodingActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    public static final String TAG = "mapDebug";
    private ImageButton mBtnLocate;
    private EditText msearchaddress;
    private String location;
    private Geocoder geocoder;
    private String type;
    private MaterialButton getLocationButton;
    private Double latitude=null,longitude=null;
    private String exactlocation=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoding);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync( this);
       // type = getIntent().getExtras().get("type").toString();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mBtnLocate = findViewById(R.id.btn_locate);
        msearchaddress = findViewById(R.id.search_locationEdittext);
        getLocationButton = (MaterialButton)findViewById(R.id.send_locationButton);
        mBtnLocate.setOnClickListener(this :: geoLocate);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (latitude == null){
                   Toast.makeText(GeocodingActivity.this, "Click On Search button after entering location", Toast.LENGTH_LONG).show();
               }else{
                    Intent intent1 = new Intent();
                    intent1.putExtra("latitude",latitude.toString());
                    intent1.putExtra("longitude",longitude.toString());
                    intent1.putExtra("exactlocation",exactlocation);
                    setResult(RESULT_OK,intent1);
                    finish();
               }
           }
       });

    }
    private void geoLocate(View view){
        mMap.clear();
       hideSoftwareKeyboard(view);
        try {
            location = msearchaddress.getText().toString();
            geocoder = new Geocoder(this,Locale.getDefault());
            List<Address> addresses =  geocoder.getFromLocationName(location,1);
            if (addresses.size()>0){
                Address address = addresses.get(0);
                Toast.makeText(this,address.getAddressLine(0),Toast.LENGTH_LONG).show();
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title(address.getAddressLine(0)).draggable(true);
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(),address.getLongitude()),16));
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                exactlocation = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideSoftwareKeyboard(View view) {
        InputMethodManager inputMethodManager  = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
       inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerDragListener(this);
    }
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    LatLng latLng1 = marker.getPosition();
        try {
            List<Address> addresses2 =  geocoder.getFromLocation(latLng1.latitude,latLng1.longitude,1);
            if (addresses2.size()>0){
                Address address = addresses2.get(0);
                String Streetaddress = address.getAddressLine(0);
               marker.setTitle(Streetaddress);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                exactlocation = Streetaddress;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}