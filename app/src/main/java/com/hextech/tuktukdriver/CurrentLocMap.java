package com.hextech.tuktukdriver;


import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.List;

public class CurrentLocMap extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_google_map);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /*
  * In this method, Start PlaceAutocomplete activity
  * PlaceAutocomplete activity provides a -
  * search box to search Google places
  */
    public void findPlace(View view) {
        try {
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        LatLng current = new LatLng(30.7220986, 76.7685763);
//        googleMap.addMarker(new MarkerOptions().position(current)
//                .title("Hex Technologie pvt. ltd."));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));

        LatLng origin = new LatLng(30.7220986, 76.7685763);
        LatLng destination = new LatLng(30.7235319, 76.7652874);
        DrawRouteMaps.getInstance(this)
                .draw(origin, destination, googleMap);
        DrawMarker.getInstance(this).draw(googleMap, origin, R.drawable.a, "Origin Location");
        DrawMarker.getInstance(this).draw(googleMap, destination, R.drawable.b, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        float zoomLevel = 16; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, zoomLevel));

    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                ((TextView) findViewById(R.id.et_location))
                        .setText(place.getName()+",\n"+
                                place.getAddress() +"\n" + place.getPhoneNumber());
                getLocationFromAddress(place.getAddress().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {

            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            System.out.println("addresssss >>>>>>> " + location);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}