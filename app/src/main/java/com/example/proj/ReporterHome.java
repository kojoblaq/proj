package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import org.parceler.Parcels;


public class ReporterHome extends AppCompatActivity {
    SupportMapFragment mapFragment;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    FusedLocationProviderClient client;
    GoogleMap mMap;
    Location location;
    LocationRequest mlocationRequest;
    LocationCallback callback;
    com.google.android.gms.common.api.GoogleApi GoogleApi;
    Intent intent;
    CardView alert;
    long UserID = 0;
    long sendid = 0;
    private LatLng pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reporter_home);
        alert = findViewById(R.id.alert);
        alert.bringToFront();


        alert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainAction.class);
                startActivity(intent);
                finish();
                return false;
            }

            ;
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        ref = FirebaseDatabase.getInstance().getReference().child("Reports");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserID = (dataSnapshot.getChildrenCount());
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });


        //initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //initialize Location call back
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        // Let's check for permission

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            //if the permission is granted then you can call the getCurrent location method

            getCurrentLocation();



            /* text_page textPage = new text_page();*/


        } else {
            //if permission ids not granted, we ask for it or request for it
            ActivityCompat.requestPermissions(ReporterHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }


    }


    private void getCurrentLocation() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        }
        client.requestLocationUpdates(mlocationRequest, callback, Looper.myLooper());

        //initialize task location
        Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                // when successful usage
                if (location != null) {
                    // sync the home
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //initialize LatLng
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            //create our marker options
                            MarkerOptions options = new MarkerOptions().position(latLng).title("I AM HERE");

                            DatabaseReference loc = FirebaseDatabase.getInstance().getReference("Reporters_Loc");
                            loc.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserID = (dataSnapshot.getChildrenCount());
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }

                            });

                            String longi = String.valueOf(location.getLongitude());
                            String latti = String.valueOf(location.getLatitude());


                            locationHelper helperClass = new locationHelper(longi, latti);
                            loc.child(String.valueOf(UserID + 1)).setValue(helperClass);



                            Intent A = new Intent(getApplicationContext(), MainAction.class);
                            A.putExtra("lon", longi);
                            A.putExtra("lat", latti);


                            Intent I = new Intent(getApplicationContext(), SendImage.class);
                            I.putExtra("lon", longi);
                            I.putExtra("lat", latti);

                            Intent T = new Intent(getApplicationContext(), SendText.class);
                            T.putExtra("lon", longi);
                            T.putExtra("lat", latti);

                            //Zoom camera
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                            //let's add maker ont the home
                            googleMap.addMarker(options);


                        }
                    });

                }

            }


        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        /*LocationServices.getFusedLocationProviderClient(this);
        String userId = FirebaseAuth.getInstance().getCur brentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PersonnelAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);*/
    }


}
