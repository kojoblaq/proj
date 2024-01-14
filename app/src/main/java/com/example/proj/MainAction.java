package com.example.proj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainAction extends AppCompatActivity implements LocationListener {
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    TextView tv_count, close;
    ImageView stop, back;
    ProgressBar progress;
    CheckBox police, fire, ambulance;
    CardView record, note, camera;
    private ArrayList<String> mResult = new ArrayList<>();
    private long UserID = 0;
    private long userid = 0;
    String lon;
    String lat;
    String Date;
    long alert_id= 0;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String emergency_type;
    DatabaseReference ref;
    DatabaseReference move;
    DatabaseReference alert;
    DatabaseReference reference;
    private ProgressDialog dialog;
    String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_acttion);

        police = findViewById(R.id.police);
        fire = findViewById(R.id.fire);
        ambulance = findViewById(R.id.ambulance);
        tv_count = findViewById(R.id.tv_count);
        progress = findViewById(R.id.progress);
        close = findViewById(R.id.close);
        stop = findViewById(R.id.stop);
        record = findViewById(R.id.record);
        note = findViewById(R.id.note);
        back = findViewById(R.id.back);
        camera = findViewById(R.id.camera);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording.3gp";
        dialog = new ProgressDialog(this);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpleDateFormat.format(calendar.getTime());

        mResult = new ArrayList<>();

        note.setEnabled(false);
        record.setEnabled(false);
        camera.setEnabled(false);


        if (ContextCompat.checkSelfPermission(MainAction.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainAction.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        if (ContextCompat.checkSelfPermission(MainAction.this, RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainAction.this, new String[]{RECORD_AUDIO}, 100);
        }


        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                        fname = firstName.toString();

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // my checkboxes and their responses
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setEnabled(true);
                record.setEnabled(true);
                camera.setEnabled(true);
                if (fire.isChecked()) {
                    mResult.add("FIRE EMERGENCY");
                } else {
                    mResult.remove("FIRE EMERGENCY");
                }
            }
        });


        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note.setEnabled(true);
                record.setEnabled(true);
                camera.setEnabled(true);
                if (police.isChecked()) {
                    mResult.add("POLICE EMERGENCY");
                } else {
                    mResult.remove("POLICE EMERGENCY");
                }
            }
        });

        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note.setEnabled(true);
                record.setEnabled(true);
                camera.setEnabled(true);

                if (ambulance.isChecked()) {
                    mResult.add("AMBULANCE REPORT");
                }
            }
        });


        // Start Countdown
        StartTimer();


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


        ref = FirebaseDatabase.getInstance().getReference("ImageReport");

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


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ReporterHome.class);
                startActivity(intent);
                finish();

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReporterHome.class);
                startActivity(intent);
                finish();

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ReporterHome.class);
                startActivity(i);
                finish();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : mResult)
                    stringBuilder.append(s).append(" || ");
                String emergency = stringBuilder.toString();
                emergency_type = emergency;
                Intent i = new Intent(MainAction.this, SendAudio.class);
                i.putExtra("fname", fname);
                i.putExtra("emergency", emergency_type);
                startActivity(i);
                finish();
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : mResult)
                    stringBuilder.append(s).append(" || ");
                String emergency = stringBuilder.toString();
                emergency_type = emergency;
                Intent intent = new Intent(getApplicationContext(), Text_page.class);
                intent.putExtra("fname", fname);
                intent.putExtra("emergency", emergency_type);
                startActivity(intent);
                finish();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getuserLocation();
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cam, 1000);


            }
        });


    }

    public void getuserLocation() {
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

    public void StartTimer() {

        progress.setVisibility(View.VISIBLE);
        new CountDownTimer(30000 + 100, 1) {
            @Override
            public void onTick(long l) {

                tv_count.setText(String.valueOf(l / 1000));

                progress.setProgress(Integer.parseInt(String.valueOf(l / 300)));

            }

            @Override
            public void onFinish() {


                tv_count.setText("0");
                Intent intent = new Intent(getApplicationContext(), ReporterHome.class);
                startActivity(intent);
                finish();

            }


        }.start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                File file = new File(String.valueOf(bitmap));
                Uri uri = Uri.fromFile(file);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                byte[] imageBytes = baos.toByteArray();
                String imageString = imageBytes.toString();

                dialog.setMessage("Sending Report");
                dialog.show();


                final StorageReference reference = FirebaseStorage.getInstance().getReference().child("report/" + file);
                UploadTask uploadTask = reference.putBytes(imageBytes);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onSuccess(final Uri uri) {


                                        /* String lon = getIntent().getStringExtra("lon");
                                String lat = getIntent().getStringExtra("lat");*/

                                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                                final String audio = "";
                                final String message = "";

                                StringBuilder stringBuilder = new StringBuilder();
                                for (String s : mResult)
                                    stringBuilder.append(s).append(" || ");
                                String emergency = stringBuilder.toString();
                                emergency_type = emergency;


                                FinalReportClass fhelperClass = new FinalReportClass(fname, phone, lon, lat, userID,
                                        message, audio, String.valueOf(uri), emergency_type,Date);
                                move.child(String.valueOf(userid + 1)).setValue(fhelperClass);


                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("image", String.valueOf(uri));
                                ref.child(String.valueOf(UserID + 1)).setValue(hashMap);

                                alert_notification help = new alert_notification(fname, lon, lat, phone);
                                alert.child(String.valueOf(alert_id +1)).setValue(help);

                                Intent A = new Intent(getApplicationContext(), SendAudio.class);
                                A.putExtra("image", hashMap);

                                Intent I = new Intent(getApplicationContext(), Text_page.class);
                                I.putExtra("image", hashMap);


                            }
                        });

                        dialog.hide();
                        /*      Log.d("UploadComplete", "Image successfully uploaded URL: %s", taskSnapshot.getDownloadUrl().toString());*/
                        Toast.makeText(MainAction.this, "Report Sent", Toast.LENGTH_SHORT).show();
                    }
                });


                Intent i = new Intent(MainAction.this, ReporterHome.class);
                startActivity(i);
                finish();


            }


        }


    }

    @Override
    public void onLocationChanged(@NonNull final Location location) {
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

