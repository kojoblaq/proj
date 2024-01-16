package com.example.proj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj.helperClasses.MyReportAdapter;
import com.example.proj.helperClasses.reportClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<reportClass> fetchdata;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    RecyclerView recyclerView;
    MyReportAdapter helperAdapter;
    DatabaseReference reporterDB;
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    LatLng data;
    DatabaseReference alert;
    FusedLocationProviderClient client;
    LocationCallback callback;
    LocationRequest mlocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Calendar calendar = Calendar.getInstance();
        final String currentdate = DateFormat.getDateInstance().format(calendar.getTime());

        recyclerView = findViewById(R.id.libraryRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(helperAdapter);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        fetchdata = new ArrayList<>();
        clearAll();


        data = new LatLng(5.9887656, -0.345454);
        arrayList.add(data);


//        getCurrentLocation();

        reporterDB = FirebaseDatabase.getInstance().getReference("FinalReport");
        reporterDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    reportClass data = ds.getValue(reportClass.class);
                    fetchdata.add(data);
                }
                helperAdapter = new MyReportAdapter(getApplicationContext(), fetchdata);
                recyclerView.setAdapter(helperAdapter);
                helperAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        alert = FirebaseDatabase.getInstance().getReference("Alert");
        alert.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                notification();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void notification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("n", "iXEND Alerts",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
//                .setSmallIcon(R.mipmap.personnel_logo)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.personnel_logo))
//                .setAutoCancel(true)
//                .setContentTitle("iXEND NEW ALERT")
//                .setContentText("YOU HAVE A NEW EMERGENCY ALERT");
//
//        // Set content intent to open MainActivity
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(contentIntent);
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        managerCompat.notify(999, builder.build());
//    }

    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "n";  // Change to a unique and descriptive ID
            NotificationChannel channel = new NotificationChannel(channelId, "iXEND Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setSmallIcon(R.mipmap.personnel_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.personnel_logo))
                .setAutoCancel(true)
                .setContentTitle("iXEND NEW ALERT")
                .setContentText("YOU HAVE A NEW EMERGENCY ALERT");

        // Set content intent to open MainActivity
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(contentIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(999, builder.build());
    }



    public void clearAll() {
        if (fetchdata != null) {
            fetchdata.clear();
            if (helperAdapter != null) {
                helperAdapter.notifyDataSetChanged();
            }
        }
        fetchdata = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //initialize Location call back
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };


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


        gMap = googleMap;


        // Add a marker in Sydney and move the camera
       /* if (location != null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
       gMap = googleMap;
        if (location != null) {
            final LatLng me = new LatLng(location.getLatitude(), location.getLatitude());
            gMap.addMarker(new MarkerOptions().position(me).title("ME"));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(me));


                           for (int i = 0; i < arrayList.size(); i++) {
                                gMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title("Reporter"));
                                gMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));

                            }
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                   *//* DisplayTrack(me, data);*//*
                    return false;
                }
            });


        }
*/


    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 100).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            // ...
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    updateMapWithLocation(location);
                }
            }
        }, Looper.myLooper());
    }

    private void updateMapWithLocation(Location location) {
        mapFragment.getMapAsync(googleMap -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions options = new MarkerOptions().position(latLng).title("ME");

            DatabaseReference loc = FirebaseDatabase.getInstance().getReference("Reporter_Loc");
            displayTrack(latLng,loc);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

            googleMap.addMarker(options);
        });
    }

    private void displayTrack(LatLng startLocation, DatabaseReference endLocation) {
        // Create a Uri for the Google Maps directions
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                startLocation.latitude + "," + startLocation.longitude +
                "&destination=" + endLocation);

        // Create an intent to open Google Maps
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps"); // Specify the package name for Google Maps

        // Check if there's an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Google Maps is not installed, direct the user to the Play Store
            Uri playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
            startActivity(playStoreIntent);
        }
    }

}