package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Text_page extends AppCompatActivity implements LocationListener {
    TextInputEditText message;
    TextView tv;
    Button send;
    ImageView back;
    FirebaseUser fuser;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference move;
    long UserID = 0;
    long moveID = 0;
    LocationRequest mlocationRequest;
    Location location;
    String fname;
    private ProgressDialog mprogress;
    String emergencyType;
    Calendar calender;
    DatabaseReference alert;
    long alert_id = 0;
    SimpleDateFormat simpleDateFormat;
    String Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.text_page);
        ActivityCompat.requestPermissions(Text_page.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

//hooks
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back);
        calender = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpleDateFormat.format(calender.getTime());


        tv = findViewById(R.id.tv);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }


        alert = FirebaseDatabase.getInstance().getReference("Alert");
        alert.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    alert_id = (int) snapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        String emg = getIntent().getStringExtra("emergency");
        emergencyType = emg;
        Log.d("onrecieve", " on recorded" + emg);


        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refreeee = FirebaseDatabase.getInstance().getReference().child("Reporter").child(userid);
        refreeee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = snapshot.child("userid").getValue().toString();
                    Log.d("datachanged", " onddd" + uid);
                    if (uid.equals(userid)) {

                        String firstName = snapshot.child("fname").getValue().toString();
                        Log.d("first name", " onddd" + firstName);
                        fname = firstName.toString();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        move = FirebaseDatabase.getInstance().getReference("FinalReport");
        move.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    moveID = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

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
                Intent i = new Intent(getApplicationContext(), Choose.class);
                startActivity(i);
                finish();
            }
        });
        /* ref = FirebaseDatabase.getInstance().getReference("Personnels").child(userid);*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getUserLocation();

            }
        });


    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        final String lon = String.valueOf(location.getLongitude());
        final  String lat = String.valueOf(location.getLatitude());
        final String msg = message.getText().toString();
        /*final String fname = tv.getText().toString();*/

        if (!msg.isEmpty()) {

            String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String audio = "";
            String image = "";


            /*String fname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();*/


            /* String fname = getIntent().getStringExtra("fname");*/







                    /*String lon;
                    String lat;

                    lon = String.valueOf(latLng.longitude);

                    lat = String.valueOf(latLng.latitude);*/


            FinalReportClass fhelperClass = new FinalReportClass(fname, phone, lon, lat,
                    userid, msg, audio, image, emergencyType, Date);
            move.child(String.valueOf(moveID + 1)).setValue(fhelperClass);

            messageHelper helperClass = new messageHelper(msg, userid);
            ref.child(String.valueOf(UserID + 1)).setValue(helperClass);

            alert_notification help = new alert_notification(fname, lon, lat, phone);
            alert.child(String.valueOf(alert_id +1)).setValue(help);

            Intent i = new Intent(getApplicationContext(), ReporterHome.class);
            startActivity(i);
            finish();


        } else {
            message.setEnabled(true);
            message.setError("Message cannot be empty");
            message.requestFocus();

        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}