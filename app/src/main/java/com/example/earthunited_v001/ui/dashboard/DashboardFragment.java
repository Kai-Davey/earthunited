package com.example.earthunited_v001.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.strictmode.CredentialProtectedWhileLockedViolation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.earthunited_v001.MainActivity;
import com.example.earthunited_v001.R;
import com.example.earthunited_v001.ui.donationFrag;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
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
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.light.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String sAreaDistence = "Country";
    private String sCountry = "";

    private View root;
    private LatLng LatLngNew;

    private int iClick = 0;
    private String sPlace = "";
    private HashMap<String, String> lstCountries =  new HashMap<>();
    private HashMap<String, String> lstCities =  new HashMap<>();
    private HashMap<String, String> lstTowns =  new HashMap<>();

    private LocationComponent locationComponent = null;

    private Button btnCountry, btnCity, btnTown;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(this.getContext(), getString(R.string.mapbox_access_token));

        root = inflater.inflate(R.layout.map, container, false);

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        //GetAllCountires and amounts
        db.collection("Country").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> dsdsa = task.getResult().getDocuments();

                    for (DocumentSnapshot anArray : dsdsa) {
                        Map<String, Object> dq = anArray.getData();
                        String sAmount = dq.values().toArray()[0].toString();
                        String sPlace = anArray.getId();
                        lstCountries.put(sPlace, sAmount.toString());
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("City").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> dsdsa = task.getResult().getDocuments();

                    for (DocumentSnapshot anArray : dsdsa) {
                        Map<String, Object> dq = anArray.getData();
                        String sAmount = dq.values().toArray()[0].toString();
                        String sPlace = anArray.getId();
                        lstCities.put(sPlace, sAmount.toString());
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("Town").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> dsdsa = task.getResult().getDocuments();

                    for (DocumentSnapshot anArray : dsdsa) {
                        Map<String, Object> dq = anArray.getData();
                        String sAmount = dq.values().toArray()[0].toString();
                        String sPlace = anArray.getId();
                        lstTowns.put(sPlace, sAmount.toString());
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        btnCountry = root.findViewById(R.id.btnMapCountry);
        btnCity = root.findViewById(R.id.btnMapCity);
        btnTown = root.findViewById(R.id.btnMapTown);


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





                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        //Setup markers for each country
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude()))
                                .zoom(2)
                                .build());

                        PopulateCountries(mapboxMap);
                    }
                });



                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull final Marker marker) {

                        if(sAreaDistence.equals("Country")) {
                            String texts = marker.getTitle();
                            String cleanTexts = texts.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");

                            if(lstCountries.containsKey(cleanTexts)){
                                marker.setSnippet(lstCountries.get(cleanTexts) + " Earth Warriors");
                            }else{
                                marker.setSnippet("0 Earth Warriors");
                            }
                        }else if(sAreaDistence.equals("City")){
                            if(lstCities.containsKey(marker.getTitle())){
                                marker.setSnippet(lstCities.get(marker.getTitle()) + " Earth Warriors");
                            }else{
                                marker.setSnippet("0 Earth Warriors");
                            }
                        }else{
                            //Town
                            if(lstTowns.containsKey(marker.getTitle())){
                                marker.setSnippet(lstTowns.get(marker.getTitle()) + " Earth Warriors");
                            }else{
                                marker.setSnippet("0 Earth Warriors");
                            }
                        }


                        if (sPlace.equals("")) {
                            sPlace = marker.getTitle();
                        } else {
                            if (sPlace.equals(marker.getTitle())) {
                                iClick++;
                            } else {
                                iClick = 0;
                                sPlace = marker.getTitle();
                            }
                        }

                        if(iClick >= 2){
                            iClick = 0;
                            if(sAreaDistence.equals("Country")){
                                sAreaDistence = "City";
                                //Show City level
                                //btn color make tint btnClicked...btnNotClicked
                                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));
                                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));

                                LatLngNew = marker.getPosition();
                                //first zoom in to city level.
                                mapboxMap.setPrefetchZoomDelta(10);
                                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                        .target(marker.getPosition())
                                        .zoom(4)
                                        .build());

                                mapboxMap.clear();


                                //then display all cities for that country
                                try {
                                    String text = marker.getTitle();
                                    String cleanText = text.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
                                    sCountry = cleanText;

                                    JSONObject  obj = new JSONObject (readJSONFromAsset("regionjson/"+cleanText+".json"));

                                    JSONArray jsArray = obj.getJSONArray("regions");

                                    IconFactory iconFactory = IconFactory.getInstance(root.getContext());

                                    for(int i=0; i < jsArray.length(); i++) {
                                        JSONObject jsonobject = jsArray.getJSONObject(i);
                                        String name       = jsonobject.getString("toponymName");

                                        String lat    = jsonobject.getString("lat");
                                        String lng  = jsonobject.getString("lng");

                                        String adminCode1 = jsonobject.getString("adminCode1");


                                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).title(name).icon(iconFactory.fromResource(R.drawable.town)));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else if(sAreaDistence.equals("City")){
                                sAreaDistence = "Town";
                                //btn color make tint btnClicked...btnNotClicked


                                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));


                                //first zoom in to city level.
                                mapboxMap.setPrefetchZoomDelta(10);
                                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                        .target(marker.getPosition())
                                        .zoom(8)
                                        .build());

                                mapboxMap.clear();


                                //then display all cities for that country
                                try {
                                    String text = marker.getTitle();
                                    //String cleanText = text.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");

                                    JSONObject  obj = new JSONObject (readJSONFromAsset("regioncitydata/"+sCountry+ "/" + text+ ".json"));

                                    JSONArray jsArray = obj.getJSONArray("cities");

                                    IconFactory iconFactory = IconFactory.getInstance(root.getContext());

                                    for(int i=0; i < jsArray.length(); i++) {
                                        JSONObject jsonobject = jsArray.getJSONObject(i);
                                        String name       = jsonobject.getString("name");

                                        String lat    = jsonobject.getString("latitude");
                                        String lng  = jsonobject.getString("longitude");

                                        //String adminCode1 = jsonobject.getString("adminCode1");


                                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).title(name).icon(iconFactory.fromResource(R.drawable.town)));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        }

                        return false;

                    }
                });




            }
        });

        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sCountry.equals("")){
                    Toast.makeText(getApplicationContext(), "Please select a country", Toast.LENGTH_LONG).show();

                }else{
                    sAreaDistence = "City";
                    //Show City level
                    //btn color make tint btnClicked...btnNotClicked
                    btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                    btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));
                    btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));

                    //first zoom in to city level.
                    mapboxMap.setPrefetchZoomDelta(10);
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(LatLngNew)
                            .zoom(4)
                            .build());

                    mapboxMap.clear();


                    //then display all cities for that country
                    try {
                        JSONObject  obj = new JSONObject (readJSONFromAsset("regionjson/"+sCountry+".json"));

                        JSONArray jsArray = obj.getJSONArray("regions");

                        IconFactory iconFactory = IconFactory.getInstance(root.getContext());

                        for(int i=0; i < jsArray.length(); i++) {
                            JSONObject jsonobject = jsArray.getJSONObject(i);
                            String name       = jsonobject.getString("toponymName");

                            String lat    = jsonobject.getString("lat");
                            String lng  = jsonobject.getString("lng");

                            String adminCode1 = jsonobject.getString("adminCode1");


                            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).title(name).icon(iconFactory.fromResource(R.drawable.town)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        btnCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(2)
                        .build());

                btnCountry.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnClicked));
                btnCity.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));
                btnTown.setBackgroundTintList(root.getResources().getColorStateList(R.color.btnNotClicked));

                mapboxMap.clear();
                sAreaDistence = "Country";
                PopulateCountries(mapboxMap);
            }

        });

        FloatingActionButton btnDonate = root.findViewById(R.id.btnDonate);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new donationFrag());
            }

        });


        return root;
    }

    

    public String readJSONFromAsset(String FileName) {
        String json = null;
            try {
                InputStream is = root.getContext().getAssets().open(FileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        return json;
    }

    private void PopulateCountries(MapboxMap mapboxMap) {
        IconFactory iconFactory = IconFactory.getInstance(root.getContext());




        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(42.546245,1.601554)).title("Andorra").icon(iconFactory.fromResource(R.drawable.andorra)));


        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(23.424076,53.847818)).title("UnitedArabEmirates").icon(iconFactory.fromResource(R.drawable.unitedarabemirates)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(33.93911,67.709953)).title("Afghanistan").icon(iconFactory.fromResource(R.drawable.afghanistan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.060816,-61.796428)).title("AntiguaandBarbuda").icon(iconFactory.fromResource(R.drawable.antiguaandbarbuda)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.220554,-63.068615)).title("Anguilla").icon(iconFactory.fromResource(R.drawable.anguilla)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.153332,20.168331)).title("Albania").icon(iconFactory.fromResource(R.drawable.albania)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(40.069099,45.038189)).title("Armenia").icon(iconFactory.fromResource(R.drawable.armenia)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.226079,-69.060087)).title("NetherlandsAntilles").icon(iconFactory.fromResource(R.drawable.netherlandsantilles)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-11.202692,17.873887)).title("Angola").icon(iconFactory.fromResource(R.drawable.angola)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-75.250973,-0.071389)).title("Antarctica").icon(iconFactory.fromResource(R.drawable.antarctica)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-38.416097,-63.616672)).title("Argentina").icon(iconFactory.fromResource(R.drawable.argentina)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-14.270972,-170.132217)).title("AmericanSamoa").icon(iconFactory.fromResource(R.drawable.americansamoa)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(47.516231,14.550072)).title("Austria").icon(iconFactory.fromResource(R.drawable.austria)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-25.274398,133.775136)).title("Australia").icon(iconFactory.fromResource(R.drawable.australia)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.52111,-69.968338)).title("Aruba").icon(iconFactory.fromResource(R.drawable.aruba)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(40.143105,47.576927)).title("Azerbaijan").icon(iconFactory.fromResource(R.drawable.azerbaijan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(43.915886,17.679076)).title("BosniaandHerzegovina").icon(iconFactory.fromResource(R.drawable.bosniaandherzegovina)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(13.193887,-59.543198)).title("Barbados").icon(iconFactory.fromResource(R.drawable.barbados)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(23.684994,90.356331)).title("Bangladesh").icon(iconFactory.fromResource(R.drawable.bangladesh)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(50.503887,4.469936)).title("Belgium").icon(iconFactory.fromResource(R.drawable.belgium)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.238333,-1.561593)).title("BurkinaFaso").icon(iconFactory.fromResource(R.drawable.burkinafaso)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(42.733883,25.48583)).title("Bulgaria").icon(iconFactory.fromResource(R.drawable.bulgaria)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(25.930414,50.637772)).title("Bahrain").icon(iconFactory.fromResource(R.drawable.bahrain)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-3.373056,29.918886)).title("Burundi").icon(iconFactory.fromResource(R.drawable.burundi)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(9.30769,2.315834)).title("Benin").icon(iconFactory.fromResource(R.drawable.benin)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(32.321384,-64.75737)).title("Bermuda").icon(iconFactory.fromResource(R.drawable.bermuda)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(4.535277,114.727669)).title("Brunei").icon(iconFactory.fromResource(R.drawable.brunei)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-16.290154,-63.588653)).title("Bolivia").icon(iconFactory.fromResource(R.drawable.bolivia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-14.235004,-51.92528)).title("Brazil").icon(iconFactory.fromResource(R.drawable.brazil)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(25.03428,-77.39628)).title("Bahamas").icon(iconFactory.fromResource(R.drawable.bahamas)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(27.514162,90.433601)).title("Bhutan").icon(iconFactory.fromResource(R.drawable.bhutan)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-54.423199,3.413194)).title("BouvetIsland").icon(iconFactory.fromResource(R.drawable.bouvetisland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-22.328474,24.684866)).title("Botswana").icon(iconFactory.fromResource(R.drawable.botswana)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(53.709807,27.953389)).title("Belarus").icon(iconFactory.fromResource(R.drawable.belarus)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.189877,-88.49765)).title("Belize").icon(iconFactory.fromResource(R.drawable.belize)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(56.130366,-106.346771)).title("Canada").icon(iconFactory.fromResource(R.drawable.canada)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-12.164165,96.870956)).title("Cocos[Keeling]Islands").icon(iconFactory.fromResource(R.drawable.cocoskeelingislands)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-4.038333,21.758664)).title("Congo[DRC]").icon(iconFactory.fromResource(R.drawable.congo[drc])));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(6.611111,20.939444)).title("CentralAfricanRepublic").icon(iconFactory.fromResource(R.drawable.centralafricanrepublic)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-0.228021,15.827659)).title("Congo[Republic]").icon(iconFactory.fromResource(R.drawable.congo[republic])));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(46.818188,8.227512)).title("Switzerland").icon(iconFactory.fromResource(R.drawable.switzerland)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.539989,-5.54708)).title("Côted'Ivoire").icon(iconFactory.fromResource(R.drawable.côtedivoire)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-21.236736,-159.777671)).title("CookIslands").icon(iconFactory.fromResource(R.drawable.cookislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-35.675147,-71.542969)).title("Chile").icon(iconFactory.fromResource(R.drawable.chile)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.369722,12.354722)).title("Cameroon").icon(iconFactory.fromResource(R.drawable.cameroon)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(35.86166,104.195397)).title("China").icon(iconFactory.fromResource(R.drawable.china)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(4.570868,-74.297333)).title("Colombia").icon(iconFactory.fromResource(R.drawable.colombia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(9.748917,-83.753428)).title("CostaRica").icon(iconFactory.fromResource(R.drawable.costarica)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(21.521757,-77.781167)).title("Cuba").icon(iconFactory.fromResource(R.drawable.cuba)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(16.002082,-24.013197)).title("CapeVerde").icon(iconFactory.fromResource(R.drawable.capeverde)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-10.447525,105.690449)).title("ChristmasIsland").icon(iconFactory.fromResource(R.drawable.christmasisland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(35.126413,33.429859)).title("Cyprus").icon(iconFactory.fromResource(R.drawable.cyprus)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(49.817492,15.472962)).title("CzechRepublic").icon(iconFactory.fromResource(R.drawable.czechrepublic)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(51.165691,10.451526)).title("Germany").icon(iconFactory.fromResource(R.drawable.germany)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(11.825138,42.590275)).title("Djibouti").icon(iconFactory.fromResource(R.drawable.djibouti)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(56.26392,9.501785)).title("Denmark").icon(iconFactory.fromResource(R.drawable.denmark)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.414999,-61.370976)).title("Dominica").icon(iconFactory.fromResource(R.drawable.dominica)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.735693,-70.162651)).title("DominicanRepublic").icon(iconFactory.fromResource(R.drawable.dominicanrepublic)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(28.033886,1.659626)).title("Algeria").icon(iconFactory.fromResource(R.drawable.algeria)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-1.831239,-78.183406)).title("Ecuador").icon(iconFactory.fromResource(R.drawable.ecuador)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(58.595272,25.013607)).title("Estonia").icon(iconFactory.fromResource(R.drawable.estonia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(26.820553,30.802498)).title("Egypt").icon(iconFactory.fromResource(R.drawable.egypt)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(24.215527,-12.885834)).title("WesternSahara").icon(iconFactory.fromResource(R.drawable.westernsahara)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.179384,39.782334)).title("Eritrea").icon(iconFactory.fromResource(R.drawable.eritrea)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(40.463667,-3.74922)).title("Spain").icon(iconFactory.fromResource(R.drawable.spain)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(9.145,40.489673)).title("Ethiopia").icon(iconFactory.fromResource(R.drawable.ethiopia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(61.92411,25.748151)).title("Finland").icon(iconFactory.fromResource(R.drawable.finland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-16.578193,179.414413)).title("Fiji").icon(iconFactory.fromResource(R.drawable.fiji)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-51.796253,-59.523613)).title("FalklandIslands[IslasMalvinas]").icon(iconFactory.fromResource(R.drawable.falklandislands[islasmalvinas])));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.425554,150.550812)).title("Micronesia").icon(iconFactory.fromResource(R.drawable.micronesia)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(61.892635,-6.911806)).title("FaroeIslands").icon(iconFactory.fromResource(R.drawable.faroeislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(46.227638,2.213749)).title("France").icon(iconFactory.fromResource(R.drawable.france)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-0.803689,11.609444)).title("Gabon").icon(iconFactory.fromResource(R.drawable.gabon)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(55.378051,-3.435973)).title("UnitedKingdom").icon(iconFactory.fromResource(R.drawable.unitedkingdom)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.262776,-61.604171)).title("Grenada").icon(iconFactory.fromResource(R.drawable.grenada)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(42.315407,43.356892)).title("Georgia").icon(iconFactory.fromResource(R.drawable.georgia)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(3.933889,-53.125782)).title("FrenchGuiana").icon(iconFactory.fromResource(R.drawable.frenchguiana)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(49.465691,-2.585278)).title("Guernsey").icon(iconFactory.fromResource(R.drawable.guernsey)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.946527,-1.023194)).title("Ghana").icon(iconFactory.fromResource(R.drawable.ghana)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(36.137741,-5.345374)).title("Gibraltar").icon(iconFactory.fromResource(R.drawable.gibraltar)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(71.706936,-42.604303)).title("Greenland").icon(iconFactory.fromResource(R.drawable.greenland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(13.443182,-15.310139)).title("Gambia").icon(iconFactory.fromResource(R.drawable.gambia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(9.945587,-9.696645)).title("Guinea").icon(iconFactory.fromResource(R.drawable.guinea)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(16.995971,-62.067641)).title("Guadeloupe").icon(iconFactory.fromResource(R.drawable.guadeloupe)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(1.650801,10.267895)).title("EquatorialGuinea").icon(iconFactory.fromResource(R.drawable.equatorialguinea)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(39.074208,21.824312)).title("Greece").icon(iconFactory.fromResource(R.drawable.greece)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-54.429579,-36.587909)).title("SouthGeorgiaandtheSouthSandwichIslands").icon(iconFactory.fromResource(R.drawable.southgeorgiaandthesouthsandwichislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.783471,-90.230759)).title("Guatemala").icon(iconFactory.fromResource(R.drawable.guatemala)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(13.444304,144.793731)).title("Guam").icon(iconFactory.fromResource(R.drawable.guam)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(11.803749,-15.180413)).title("Guinea-Bissau").icon(iconFactory.fromResource(R.drawable.guinea-bissau)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(4.860416,-58.93018)).title("Guyana").icon(iconFactory.fromResource(R.drawable.guyana)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(31.354676,34.308825)).title("GazaStrip").icon(iconFactory.fromResource(R.drawable.gazastrip)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(22.396428,114.109497)).title("HongKong").icon(iconFactory.fromResource(R.drawable.hongkong)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-53.08181,73.504158)).title("HeardIslandandMcDonaldIslands").icon(iconFactory.fromResource(R.drawable.heardislandandmcdonaldislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.199999,-86.241905)).title("Honduras").icon(iconFactory.fromResource(R.drawable.honduras)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(45.1,15.2)).title("Croatia").icon(iconFactory.fromResource(R.drawable.croatia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.971187,-72.285215)).title("Haiti").icon(iconFactory.fromResource(R.drawable.haiti)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(47.162494,19.503304)).title("Hungary").icon(iconFactory.fromResource(R.drawable.hungary)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-0.789275,113.921327)).title("Indonesia").icon(iconFactory.fromResource(R.drawable.indonesia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(53.41291,-8.24389)).title("Ireland").icon(iconFactory.fromResource(R.drawable.ireland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(31.046051,34.851612)).title("Israel").icon(iconFactory.fromResource(R.drawable.israel)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(54.236107,-4.548056)).title("IsleofMan").icon(iconFactory.fromResource(R.drawable.isleofman)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(20.593684,78.96288)).title("India").icon(iconFactory.fromResource(R.drawable.india)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-6.343194,71.876519)).title("BritishIndianOceanTerritory").icon(iconFactory.fromResource(R.drawable.britishindianoceanterritory)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(33.223191,43.679291)).title("Iraq").icon(iconFactory.fromResource(R.drawable.iraq)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(32.427908,53.688046)).title("Iran").icon(iconFactory.fromResource(R.drawable.iran)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(64.963051,-19.020835)).title("Iceland").icon(iconFactory.fromResource(R.drawable.iceland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.87194,12.56738)).title("Italy").icon(iconFactory.fromResource(R.drawable.italy)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(49.214439,-2.13125)).title("Jersey").icon(iconFactory.fromResource(R.drawable.jersey)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.109581,-77.297508)).title("Jamaica").icon(iconFactory.fromResource(R.drawable.jamaica)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(30.585164,36.238414)).title("Jordan").icon(iconFactory.fromResource(R.drawable.jordan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(36.204824,138.252924)).title("Japan").icon(iconFactory.fromResource(R.drawable.japan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-0.023559,37.906193)).title("Kenya").icon(iconFactory.fromResource(R.drawable.kenya)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.20438,74.766098)).title("Kyrgyzstan").icon(iconFactory.fromResource(R.drawable.kyrgyzstan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.565679,104.990963)).title("Cambodia").icon(iconFactory.fromResource(R.drawable.cambodia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-3.370417,-168.734039)).title("Kiribati").icon(iconFactory.fromResource(R.drawable.kiribati)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-11.875001,43.872219)).title("Comoros").icon(iconFactory.fromResource(R.drawable.comoros)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.357822,-62.782998)).title("SaintKittsandNevis").icon(iconFactory.fromResource(R.drawable.saintkittsandnevis)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(40.339852,127.510093)).title("NorthKorea").icon(iconFactory.fromResource(R.drawable.northkorea)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(35.907757,127.766922)).title("SouthKorea").icon(iconFactory.fromResource(R.drawable.southkorea)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(29.31166,47.481766)).title("Kuwait").icon(iconFactory.fromResource(R.drawable.kuwait)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(19.513469,-80.566956)).title("CaymanIslands").icon(iconFactory.fromResource(R.drawable.caymanislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(48.019573,66.923684)).title("Kazakhstan").icon(iconFactory.fromResource(R.drawable.kazakhstan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(19.85627,102.495496)).title("Laos").icon(iconFactory.fromResource(R.drawable.laos)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(33.854721,35.862285)).title("Lebanon").icon(iconFactory.fromResource(R.drawable.lebanon)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(13.909444,-60.978893)).title("SaintLucia").icon(iconFactory.fromResource(R.drawable.saintlucia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(47.166,9.555373)).title("Liechtenstein").icon(iconFactory.fromResource(R.drawable.liechtenstein)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.873054,80.771797)).title("SriLanka").icon(iconFactory.fromResource(R.drawable.srilanka)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(6.428055,-9.429499)).title("Liberia").icon(iconFactory.fromResource(R.drawable.liberia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-29.609988,28.233608)).title("Lesotho").icon(iconFactory.fromResource(R.drawable.lesotho)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(55.169438,23.881275)).title("Lithuania").icon(iconFactory.fromResource(R.drawable.lithuania)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(49.815273,6.129583)).title("Luxembourg").icon(iconFactory.fromResource(R.drawable.luxembourg)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(56.879635,24.603189)).title("Latvia").icon(iconFactory.fromResource(R.drawable.latvia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(26.3351,17.228331)).title("Libya").icon(iconFactory.fromResource(R.drawable.libya)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(31.791702,-7.09262)).title("Morocco").icon(iconFactory.fromResource(R.drawable.morocco)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(43.750298,7.412841)).title("Monaco").icon(iconFactory.fromResource(R.drawable.monaco)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(47.411631,28.369885)).title("Moldova").icon(iconFactory.fromResource(R.drawable.moldova)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(42.708678,19.37439)).title("Montenegro").icon(iconFactory.fromResource(R.drawable.montenegro)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-18.766947,46.869107)).title("Madagascar").icon(iconFactory.fromResource(R.drawable.madagascar)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.131474,171.184478)).title("MarshallIslands").icon(iconFactory.fromResource(R.drawable.marshallislands)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.608635,21.745275)).title("Macedonia[FYROM]").icon(iconFactory.fromResource(R.drawable.macedonia[fyrom])));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.570692,-3.996166)).title("Mali").icon(iconFactory.fromResource(R.drawable.mali)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(21.913965,95.956223)).title("Myanmar[Burma]").icon(iconFactory.fromResource(R.drawable.myanmar[burma])));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(46.862496,103.846656)).title("Mongolia").icon(iconFactory.fromResource(R.drawable.mongolia)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(22.198745,113.543873)).title("Macau").icon(iconFactory.fromResource(R.drawable.macau)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.33083,145.38469)).title("NorthernMarianaIslands").icon(iconFactory.fromResource(R.drawable.northernmarianaislands)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(14.641528,-61.024174)).title("Martinique").icon(iconFactory.fromResource(R.drawable.martinique)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(21.00789,-10.940835)).title("Mauritania").icon(iconFactory.fromResource(R.drawable.mauritania)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(16.742498,-62.187366)).title("Montserrat").icon(iconFactory.fromResource(R.drawable.montserrat)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(35.937496,14.375416)).title("Malta").icon(iconFactory.fromResource(R.drawable.malta)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-20.348404,57.552152)).title("Mauritius").icon(iconFactory.fromResource(R.drawable.mauritius)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(3.202778,73.22068)).title("Maldives").icon(iconFactory.fromResource(R.drawable.maldives)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-13.254308,34.301525)).title("Malawi").icon(iconFactory.fromResource(R.drawable.malawi)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(23.634501,-102.552784)).title("Mexico").icon(iconFactory.fromResource(R.drawable.mexico)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(4.210484,101.975766)).title("Malaysia").icon(iconFactory.fromResource(R.drawable.malaysia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-18.665695,35.529562)).title("Mozambique").icon(iconFactory.fromResource(R.drawable.mozambique)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-22.95764,18.49041)).title("Namibia").icon(iconFactory.fromResource(R.drawable.namibia)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-20.904305,165.618042)).title("NewCaledonia").icon(iconFactory.fromResource(R.drawable.newcaledonia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(17.607789,8.081666)).title("Niger").icon(iconFactory.fromResource(R.drawable.niger)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-29.040835,167.954712)).title("NorfolkIsland").icon(iconFactory.fromResource(R.drawable.norfolkisland)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(9.081999,8.675277)).title("Nigeria").icon(iconFactory.fromResource(R.drawable.nigeria)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.865416,-85.207229)).title("Nicaragua").icon(iconFactory.fromResource(R.drawable.nicaragua)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(52.132633,5.291266)).title("Netherlands").icon(iconFactory.fromResource(R.drawable.netherlands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(60.472024,8.468946)).title("Norway").icon(iconFactory.fromResource(R.drawable.norway)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(28.394857,84.124008)).title("Nepal").icon(iconFactory.fromResource(R.drawable.nepal)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-0.522778,166.931503)).title("Nauru").icon(iconFactory.fromResource(R.drawable.nauru)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-19.054445,-169.867233)).title("Niue").icon(iconFactory.fromResource(R.drawable.niue)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-40.900557,174.885971)).title("NewZealand").icon(iconFactory.fromResource(R.drawable.newzealand)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(21.512583,55.923255)).title("Oman").icon(iconFactory.fromResource(R.drawable.oman)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(8.537981,-80.782127)).title("Panama").icon(iconFactory.fromResource(R.drawable.panama)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-9.189967,-75.015152)).title("Peru").icon(iconFactory.fromResource(R.drawable.peru)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-17.679742,-149.406843)).title("FrenchPolynesia").icon(iconFactory.fromResource(R.drawable.frenchpolynesia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-6.314993,143.95555)).title("PapuaNewGuinea").icon(iconFactory.fromResource(R.drawable.papuanewguinea)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.879721,121.774017)).title("Philippines").icon(iconFactory.fromResource(R.drawable.philippines)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(30.375321,69.345116)).title("Pakistan").icon(iconFactory.fromResource(R.drawable.pakistan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(51.919438,19.145136)).title("Poland").icon(iconFactory.fromResource(R.drawable.poland)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(46.941936,-56.27111)).title("SaintPierreandMiquelon").icon(iconFactory.fromResource(R.drawable.saintpierreandmiquelon)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-24.703615,-127.439308)).title("PitcairnIslands").icon(iconFactory.fromResource(R.drawable.pitcairnislands)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.220833,-66.590149)).title("PuertoRico").icon(iconFactory.fromResource(R.drawable.puertorico)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(31.952162,35.233154)).title("PalestinianTerritories").icon(iconFactory.fromResource(R.drawable.palestinianterritories)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(39.399872,-8.224454)).title("Portugal").icon(iconFactory.fromResource(R.drawable.portugal)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(7.51498,134.58252)).title("Palau").icon(iconFactory.fromResource(R.drawable.palau)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-23.442503,-58.443832)).title("Paraguay").icon(iconFactory.fromResource(R.drawable.paraguay)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(25.354826,51.183884)).title("Qatar").icon(iconFactory.fromResource(R.drawable.qatar)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-21.115141,55.536384)).title("Réunion").icon(iconFactory.fromResource(R.drawable.réunion)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(45.943161,24.96676)).title("Romania").icon(iconFactory.fromResource(R.drawable.romania)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(44.016521,21.005859)).title("Serbia").icon(iconFactory.fromResource(R.drawable.serbia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(61.52401,105.318756)).title("Russia").icon(iconFactory.fromResource(R.drawable.russia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-1.940278,29.873888)).title("Rwanda").icon(iconFactory.fromResource(R.drawable.rwanda)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(23.885942,45.079162)).title("SaudiArabia").icon(iconFactory.fromResource(R.drawable.saudiarabia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-9.64571,160.156194)).title("SolomonIslands").icon(iconFactory.fromResource(R.drawable.solomonislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-4.679574,55.491977)).title("Seychelles").icon(iconFactory.fromResource(R.drawable.seychelles)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.862807,30.217636)).title("Sudan").icon(iconFactory.fromResource(R.drawable.sudan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(60.128161,18.643501)).title("Sweden").icon(iconFactory.fromResource(R.drawable.sweden)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(1.352083,103.819836)).title("Singapore").icon(iconFactory.fromResource(R.drawable.singapore)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-24.143474,-10.030696)).title("SaintHelena").icon(iconFactory.fromResource(R.drawable.sainthelena)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(46.151241,14.995463)).title("Slovenia").icon(iconFactory.fromResource(R.drawable.slovenia)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(77.553604,23.670272)).title("SvalbardandJanMayen").icon(iconFactory.fromResource(R.drawable.svalbardandjanmayen)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(48.669026,19.699024)).title("Slovakia").icon(iconFactory.fromResource(R.drawable.slovakia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(8.460555,-11.779889)).title("SierraLeone").icon(iconFactory.fromResource(R.drawable.sierraleone)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(43.94236,12.457777)).title("SanMarino").icon(iconFactory.fromResource(R.drawable.sanmarino)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(14.497401,-14.452362)).title("Senegal").icon(iconFactory.fromResource(R.drawable.senegal)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(5.152149,46.199616)).title("Somalia").icon(iconFactory.fromResource(R.drawable.somalia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(3.919305,-56.027783)).title("Suriname").icon(iconFactory.fromResource(R.drawable.suriname)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(0.18636,6.613081)).title("SãoToméandPríncipe").icon(iconFactory.fromResource(R.drawable.sãotoméandpríncipe)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(13.794185,-88.89653)).title("ElSalvador").icon(iconFactory.fromResource(R.drawable.elsalvador)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(34.802075,38.996815)).title("Syria").icon(iconFactory.fromResource(R.drawable.syria)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-26.522503,31.465866)).title("Swaziland").icon(iconFactory.fromResource(R.drawable.swaziland)));
        // mapboxMap.addMarker(new MarkerOptions().position(new LatLng(21.694025,-71.797928)).title("TurksandCaicosIslands").icon(iconFactory.fromResource(R.drawable.turksandcaicosislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.454166,18.732207)).title("Chad").icon(iconFactory.fromResource(R.drawable.chad)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-49.280366,69.348557)).title("FrenchSouthernTerritories").icon(iconFactory.fromResource(R.drawable.frenchsouthernterritories)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(8.619543,0.824782)).title("Togo").icon(iconFactory.fromResource(R.drawable.togo)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.870032,100.992541)).title("Thailand").icon(iconFactory.fromResource(R.drawable.thailand)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(38.861034,71.276093)).title("Tajikistan").icon(iconFactory.fromResource(R.drawable.tajikistan)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-8.967363,-171.855881)).title("Tokelau").icon(iconFactory.fromResource(R.drawable.tokelau)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-8.874217,125.727539)).title("Timor-Leste").icon(iconFactory.fromResource(R.drawable.timor-leste)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(38.969719,59.556278)).title("Turkmenistan").icon(iconFactory.fromResource(R.drawable.turkmenistan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(33.886917,9.537499)).title("Tunisia").icon(iconFactory.fromResource(R.drawable.tunisia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-21.178986,-175.198242)).title("Tonga").icon(iconFactory.fromResource(R.drawable.tonga)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(38.963745,35.243322)).title("Turkey").icon(iconFactory.fromResource(R.drawable.turkey)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(10.691803,-61.222503)).title("TrinidadandTobago").icon(iconFactory.fromResource(R.drawable.trinidadandtobago)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-7.109535,177.64933)).title("Tuvalu").icon(iconFactory.fromResource(R.drawable.tuvalu)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(23.69781,120.960515)).title("Taiwan").icon(iconFactory.fromResource(R.drawable.taiwan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-6.369028,34.888822)).title("Tanzania").icon(iconFactory.fromResource(R.drawable.tanzania)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(48.379433,31.16558)).title("Ukraine").icon(iconFactory.fromResource(R.drawable.ukraine)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(1.373333,32.290275)).title("Uganda").icon(iconFactory.fromResource(R.drawable.uganda)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(37.09024,-95.712891)).title("UnitedStates").icon(iconFactory.fromResource(R.drawable.unitedstates)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-32.522779,-55.765835)).title("Uruguay").icon(iconFactory.fromResource(R.drawable.uruguay)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.377491,64.585262)).title("Uzbekistan").icon(iconFactory.fromResource(R.drawable.uzbekistan)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.902916,12.453389)).title("VaticanCity").icon(iconFactory.fromResource(R.drawable.vaticancity)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(12.984305,-61.287228)).title("SaintVincentandtheGrenadines").icon(iconFactory.fromResource(R.drawable.saintvincentandthegrenadines)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(6.42375,-66.58973)).title("Venezuela").icon(iconFactory.fromResource(R.drawable.venezuela)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.420695,-64.639968)).title("BritishVirginIslands").icon(iconFactory.fromResource(R.drawable.britishvirginislands)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(18.335765,-64.896335)).title("U.S.VirginIslands").icon(iconFactory.fromResource(R.drawable.u.s.virginislands)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(14.058324,108.277199)).title("Vietnam").icon(iconFactory.fromResource(R.drawable.vietnam)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-15.376706,166.959158)).title("Vanuatu").icon(iconFactory.fromResource(R.drawable.vanuatu)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-13.768752,-177.156097)).title("WallisandFutuna").icon(iconFactory.fromResource(R.drawable.wallisandfutuna)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-13.759029,-172.104629)).title("Samoa").icon(iconFactory.fromResource(R.drawable.samoa)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(42.602636,20.902977)).title("Kosovo").icon(iconFactory.fromResource(R.drawable.kosovo)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(15.552727,48.516388)).title("Yemen").icon(iconFactory.fromResource(R.drawable.yemen)));
        //mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-12.8275,45.166244)).title("Mayotte").icon(iconFactory.fromResource(R.drawable.mayotte)));

        //mapboxMap.addMarker(new MarkerOptions().setTitle("I'm a marker :]").setSnippet("This is a snippet about this marker that will show up here").position(new LatLng(-30.559482,22.937506)));

        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-30.559482,22.937506)).title("SouthAfrica").icon(iconFactory.fromResource(R.drawable.southafrica)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-13.133897,27.849332)).title("Zambia").icon(iconFactory.fromResource(R.drawable.zambia)));
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(-19.015438,29.154857)).title("Zimbabwe").icon(iconFactory.fromResource(R.drawable.zimbabwe)));

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction t = this.getFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
        LinearLayout idMain = root.findViewById(R.id.idMain);
        idMain.setVisibility(View.GONE);
    }
}