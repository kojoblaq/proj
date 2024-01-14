package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;

//import javax.swing.text.html.ImageView;

public class SendAudio extends AppCompatActivity implements LocationListener {
    CardView record;
    ImageView back;
    TextView text;
    Button cancel, stop;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private ProgressDialog mprogress;
    private DatabaseReference dbref;
    DatabaseReference move, alert;
    private StorageReference filepath;
    long UserID = 0;
    long userid = 0;
    long report_id = 0;
    long alert_id = 0;
    String lon;
    String lat;
    String f_name;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Location location;
    DatabaseReference reference;
    String emergencyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_audio);

        record = findViewById(R.id.record);
        back = findViewById(R.id.back);
        text = findViewById(R.id.text);
        cancel = findViewById(R.id.cancel);
        stop = findViewById(R.id.stop);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpleDateFormat.format(calendar.getTime());

        stop.setEnabled(false);

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

        mprogress = new ProgressDialog(this);

        if (ContextCompat.checkSelfPermission(SendAudio.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SendAudio.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        if (ContextCompat.checkSelfPermission(SendAudio.this, RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendAudio.this, new String[]{RECORD_AUDIO}, 100);
        }

        emergencyType = getIntent().getStringExtra("emergency");

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("Reporter").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    /*reportid = snapshot.getChildrenCount();*/
                    String uid = snapshot.child("userid").getValue().toString();

                    if (uid.equals(userID)) {

                        String firstName = snapshot.child("fname").getValue().toString();
                        Log.d("onchange", "onDataChange: " + firstName);
                        f_name = firstName.toString();

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
                    userid = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Audio");

        dbref = FirebaseDatabase.getInstance().getReference().child("AudioReport");
        dbref.addValueEventListener(new ValueEventListener() {
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

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        outputFile += "/recorded_audio.3gp";
        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainAction.class);
                startActivity(i);
                finish();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                text.setText("recording...");
                record.setEnabled(false);
                stop.setEnabled(true);

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                stop.setEnabled(false);
                record.setEnabled(true);

                Toast.makeText(SendAudio.this,
                        "successfully recorded",
                        Toast.LENGTH_SHORT).show();
                text.setText("tap to record..");

                getUserLocation();
                uploadfile();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                try {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                text.setText("tap to record..");
                stop.setEnabled(false);
                record.isClickable();*/
                Intent i = new Intent(getApplicationContext(), MainAction.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void uploadfile() {
        mprogress.setMessage("Uploading Audio...");
        mprogress.show();
        long audio_id = 0;

        File file = new File(String.valueOf(outputFile));
        /*  final StorageReference filepath = mStorage.child("Audio").child("New_Audio.3gp" + file);*/

        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Audio");
        final Uri uri = Uri.fromFile(file);
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mprogress.hide();
                text.setText("REPORT SENT");

                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String msg = "";
                                String image = "";

                                FinalReportClass fhelperClass = new FinalReportClass(f_name, phone, lon, lat,
                                        userID, msg, String.valueOf(uri), image, emergencyType, Date);
                                move.child(String.valueOf(userid + 1)).setValue(fhelperClass);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("Audio_Link", String.valueOf(uri));
                                dbref.child(String.valueOf(UserID + 1)).setValue(hashMap);


                                alert_notification help = new alert_notification(f_name, lon, lat, phone);
                                alert.child(String.valueOf(alert_id + 1)).setValue(help);


                            }
                        });
                    }
                });

                Intent i = new Intent(getApplicationContext(), ReporterHome.class);
                startActivity(i);
                finish();
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


        final String lonnn = String.valueOf(location.getLongitude());

        final String lattttt = String.valueOf(location.getLatitude());

        lon = lonnn.toString();
        lat = lattttt.toString();


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
