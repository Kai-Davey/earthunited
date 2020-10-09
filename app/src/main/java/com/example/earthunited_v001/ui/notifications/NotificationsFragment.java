package com.example.earthunited_v001.ui.notifications;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.errorprone.annotations.Var;
import com.google.firebase.database.*;

import com.example.earthunited_v001.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    private ImageButton mButton;
    private EditText edtMessage;
    private ListView lstMessages;
    private MapView mapView;
    private MapboxMap mapboxMap;

    private double latitude = 0;
    private double longatude = 0;

    private View root;
    //DB
    private DatabaseReference mDatabase;
    private Context ThisContextc;

    private  String cityName;
    private String stateName;
    private String countryName;

    private LocationComponent locationComponent = null;

    private ArrayAdapter<String> arrayAdapter;

    private String strPlace = "Country";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Mapbox.getInstance(this.getContext(), getString(R.string.mapbox_access_token));

        root = inflater.inflate(R.layout.chat, container, false);

        SetCountry();

        ThisContextc = root.getContext();

        mapView = (MapView) root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap2) {

                mapboxMap = mapboxMap2;
                mapboxMap.getUiSettings().setRotateGesturesEnabled(false);

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Create and customize the LocationComponent's options
                        LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(root.getContext())
                                .elevation(5)
                                .accuracyAlpha(.6f)
                                .accuracyColor(Color.RED)
                                .foregroundDrawable(R.drawable.town)
                                .build();

                        // Get an instance of the component
                        locationComponent = mapboxMap.getLocationComponent();

                        LocationComponentActivationOptions locationComponentActivationOptions =
                                LocationComponentActivationOptions.builder(root.getContext(), style)
                                        .locationComponentOptions(customLocationComponentOptions)
                                        .build();

                        // Activate with options
                        locationComponent.activateLocationComponent(locationComponentActivationOptions);

                        // Enable to make component visible
                        if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationComponent.setLocationComponentEnabled(true);

                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()))
                                .zoom(10)
                                .build());
                        mapboxMap.getUiSettings().setAllGesturesEnabled(false);

                        //SetCountry();
                    }
                });

            }
        });


        final Button btnCountry = root.findViewById(R.id.btnCountry);
        final Button btnCity = root.findViewById(R.id.btnCity);
        final Button btnTown = root.findViewById(R.id.btnTown);

        btnCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPlace = "Country";
                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));
                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));

                mDatabase.child(countryName + "/Messages").addValueEventListener(postListener);

                mDatabase.child(cityName + "/Messages").removeEventListener(postListener);
                mDatabase.child(stateName + "/Messages").removeEventListener(postListener);

            }
        });



        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPlace = "City";
                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));
                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));

                mDatabase.child(countryName + "/Messages").removeEventListener(postListener);

                mDatabase.child(stateName + "/Messages").addValueEventListener(postListener);
                mDatabase.child(cityName + "/Messages").removeEventListener(postListener);

            }
        });



        btnTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPlace = "Town";

                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));


                mDatabase.child(countryName + "/Messages").removeEventListener(postListener);

                mDatabase.child(stateName + "/Messages").removeEventListener(postListener);
                mDatabase.child(cityName + "/Messages").addValueEventListener(postListener);

            }
        });





        mButton = root.findViewById(R.id.SendnewMessage);
        mButton.setOnClickListener(this);

        edtMessage = root.findViewById(R.id.editText);
        lstMessages = (ListView) root.findViewById(R.id.messages_view);
        arrayAdapter = new ArrayAdapter<String>(ThisContextc, R.layout.message_listview, R.id.testmessage);
        lstMessages.setAdapter(arrayAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        try {
            if(strPlace.equals("Country")){
                mDatabase.child(countryName + "/Messages").addValueEventListener(postListener);
            }else if(strPlace.equals("City")){
                mDatabase.child(stateName + "/Messages").addValueEventListener(postListener);
            }else{//Town
                mDatabase.child(cityName + "/Messages").addValueEventListener(postListener);
            }

        } catch (Exception es) {

        }


        return root;
    }


    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            arrayAdapter.clear();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                if (postSnapshot.hasChild("Message")) {
                    String key = postSnapshot.child("Message").getValue(String.class);
                    arrayAdapter.add(key);
                }

            }


            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    private void SetCountry() {
        Geocoder geocoder = new Geocoder(root.getContext(), Locale.getDefault());
        List<Address> addresses = null;

        if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (latitude == 0) {
            latitude = getLastKnownLocation().getLatitude();
            longatude = getLastKnownLocation().getLongitude();
        }

        try {
            addresses = geocoder.getFromLocation(latitude, longatude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address all = addresses.get(0);

        cityName = addresses.get(0).getLocality();
        stateName = addresses.get(0).getAdminArea();
        countryName = addresses.get(0).getCountryName();

        /*mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()))
                .zoom(2)
                .build());*/
    }

    private void SetCity() {

    }

    private void SetTown() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SendnewMessage:
                sendnewmessage();
                break;
        }
    }

    private void sendnewmessage() {
        String strMessage = edtMessage.getText().toString();
        String key = "";
        if(strPlace.equals("Country")){
            key = mDatabase.child(countryName + "/Messages").push().getKey();
        }else if(strPlace.equals("City")){
            key = mDatabase.child(stateName + "/Messages").push().getKey();
        }else{//Town
            key = mDatabase.child( cityName+ "/Messages").push().getKey();
        }



        Messages msg = new Messages(key, strMessage);

        Map<String, Object> childUpdates = new HashMap<>();

        if(strPlace.equals("Country")){
            childUpdates.put(countryName + "/Messages/" + key, msg);
        }else if(strPlace.equals("City")){
            childUpdates.put(stateName + "/Messages/" + key, msg);
        }else{//Town
            childUpdates.put(cityName + "/Messages/" + key, msg);
        }


        mDatabase.updateChildren(childUpdates);

    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public class Messages {

        public String ID;
        public String Message;

        public Messages() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Messages(String ID, String Message) {
            this.ID = ID;
            this.Message = Message;
        }

    }
}