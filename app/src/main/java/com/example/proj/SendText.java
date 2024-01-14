package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SendText extends AppCompatActivity {
    TextInputEditText message;
    Button send;
    ImageView back;
    FirebaseUser fuser;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    GoogleApiClient mGoogleApiClient;
    GoogleApi googleApi;
    GoogleMap mMap;
//    private GeoFire geoFire;
    Location location;
    LocationRequest mLocationRequest;
    private LatLng pickupLocation;
    long UserID = 0;
    DatabaseReference move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.send_text);
        ActivityCompat.requestPermissions(SendText.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);


        //hooks
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back);





        fuser = FirebaseAuth.getInstance().getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference("TextRequest");

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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainAction.class);
                startActivity(i);
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location mLastLocation = null;
                String msg = message.getText().toString();

                if (!msg.isEmpty()) {
                    String senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    /* DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TextRequest");*/
               /* Double Long = location.getLongitude();
                Double Lat = location.getLatitude();*/
                    String fname = getIntent().getStringExtra("fname");
                    String phone = getIntent().getStringExtra("phone");
                    String lon = getIntent().getStringExtra("lon");
                    String lat = getIntent().getStringExtra("lat");
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String audio = "";
                    String image = "";







                    messageHelper helperClass = new messageHelper(msg, senderID);
                    ref.child(String.valueOf(UserID + 1)).setValue(helperClass);

                /*    FinalReportClass fhelperClass = new FinalReportClass(fname, phone, lon, lat, userID, msg, audio, image);
                    move.child(String.valueOf(UserID + 1)).setValue(fhelperClass);*/


                   /* Intent A = new Intent(getApplicationContext(), SendAudio.class);
                    A.putExtra("message", msg);*/

                    /*Intent I = new Intent(getApplicationContext(), MainAction.class);
                    I.putExtra("message", msg);*/


                    Intent i = new Intent(SendText.this, ReporterHome.class);
                    startActivity(i);
                    finish();

                } else {
                    message.setEnabled(true);
                    message.setError("Message cannot be empty");
                    message.requestFocus();

                }


            }
        });


    }


}