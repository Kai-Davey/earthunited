package com.example.earthunited_v001.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earthunited_v001.MainActivity;
import com.example.earthunited_v001.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    private EditText usernameRegEditText;
    private EditText emailRegEditText;
    private EditText passwordRegEditText;
    private EditText PostalRegEditText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LocationManager locationManager;

    private double latitude = 0;
    private double longatude = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
        //          .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        usernameEditText.setText("Test@gmail.com");
        passwordEditText = findViewById(R.id.password);
        passwordEditText.setText("123123");
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocationManager mLocationManager;

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                LocationManager locManager;
                locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListener);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longatude = location.getLongitude();
                }
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome);//+ model.getDisplayName()
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void btnLoginClicked(View view) {
        //Log user in and move to main screen
        if ((usernameEditText.getText().toString().matches("")) || (passwordEditText.getText().toString().matches(""))) {
            showLoginFailed("Please fill in all your details");
        } else {

            mAuth.signInWithEmailAndPassword(usernameEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithEmail:success");
                                //Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUiWithUser(null);

                                //updateUI(user);
                            } else {
                                showLoginFailed("Authentication failed.");
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                //        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    });
        }
    }

    public void btnRegisterClick(View view) {
        //move user to register screen
        //Intent intent = new Intent(this, );
        //startActivity(intent);
        setContentView(R.layout.activity_join);
        EditText edtTextTextPostalAddress = findViewById(R.id.editTextTextPostalAddress);
        edtTextTextPostalAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //Log.i(TAG,"Enter pressed");
                    btnJoinCreateAccountClick(null);
                }
                return false;
            }
        });
    }

    //Register
    public void btnJoinBackClick(View view) {
        setContentView(R.layout.activity_login);
    }

    public void btnJoinCreateAccountClick(View view) {
        usernameRegEditText = findViewById(R.id.username);
        emailRegEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordRegEditText = findViewById(R.id.password);
        PostalRegEditText = findViewById(R.id.editTextTextPostalAddress);

        //add validation


        mAuth.createUserWithEmailAndPassword(emailRegEditText.getText().toString(), passwordRegEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            createNewUserFirestore(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            showLoginFailed("Authentication failed.");
                            //updateUI(null);
                        }
                    }
                });
    }

    public void createNewUserFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("Username", usernameRegEditText.getText().toString());
        userData.put("PostalCode", PostalRegEditText.getText().toString());


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if(latitude == 0){
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


        db.collection("Users").document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "DocumentSnapshot successfully written!");
                //Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                SetCountry();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.w(TAG, "Error writing document", e);
                showLoginFailed("Authentication failed.");
            }
        });

// Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
//                     updateUiWithUser(null);
        //db.collection("City").document(stateName).update("amount", FieldValue.increment(1));
        //db.collection("Town").document(cityName).update("amount", FieldValue.increment(1));
    }
    private String cityName;
    private String stateName;
    private String countryName;

    private void SetCountry(){
        db.collection("Country").document(countryName).update("amount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "DocumentSnapshot successfully written!");
                SetCity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.w(TAG, "Error writing document", e);
                if(e.getMessage().contains("NOT_FOUND")){
                    Map<String, Object> CountryData = new HashMap<>();
                    CountryData.put("amount", 1);
                    db.collection("Country").document(countryName).set(CountryData);
                    SetCity();
                }

            }
        });
    }

    private void SetCity(){
        db.collection("City").document(stateName).update("amount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "DocumentSnapshot successfully written!");
                SetTown();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.w(TAG, "Error writing document", e);
                if(e.getMessage().contains("NOT_FOUND")){
                    Map<String, Object> CountryData = new HashMap<>();
                    CountryData.put("amount", 1);
                    db.collection("City").document(stateName).set(CountryData);
                    SetTown();
                }

            }
        });
    }

    private void SetTown(){
        db.collection("Town").document(cityName).update("amount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                updateUiWithUser(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.w(TAG, "Error writing document", e);
                if(e.getMessage().contains("NOT_FOUND")){
                    Map<String, Object> CountryData = new HashMap<>();
                    CountryData.put("amount", 1);
                    db.collection("Town").document(cityName).set(CountryData);
                    Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                    updateUiWithUser(null);
                }

            }
        });
    }


    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,int status,Bundle extras){}
    };

    private void updateWithNewLocation(Location location) {
        //TextView myLocationText = (TextView) findViewById(R.id.text);
        String latLongString = "";
        if (location != null) {
            latitude = location.getLatitude();
            longatude = location.getLongitude();
        } else {
            latLongString = "No location found";
        }
        //myLocationText.setText("Your Current Position is:\n" + latLongString);
    }

}
