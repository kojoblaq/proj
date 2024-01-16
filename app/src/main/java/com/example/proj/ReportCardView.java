package com.example.proj;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

public class ReportCardView extends AppCompatActivity {
    TextView emergencyType, message, current_time, phoneNo, fname, txtAudio, txtMsg;
    ImageView imageView, play, pause, stop;
    Button btnRoute, call;
    DatabaseReference reference;
    DatabaseReference alert;
    MediaPlayer mediaPlayer;
    ProgressDialog progressDialog;
    private boolean playpause;
    private boolean initialStage = true;

    String phone;
    String name;
    String audio;
    String image;
    String txt;
    String lat;
    String lon;
    String date;
    String emergencytype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportcardview);

//hooks
        emergencyType = findViewById(R.id.emergencyType);
        message = findViewById(R.id.txtMessage);
        imageView = findViewById(R.id.ipic);
        btnRoute = findViewById(R.id.route);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        play.setEnabled(false);
        stop.setEnabled(false);
        pause.setEnabled(false);
        current_time = findViewById(R.id.current_date);
        phoneNo = findViewById(R.id.phone);
        call = findViewById(R.id.call);
        fname = findViewById(R.id.name);
        txtAudio = findViewById(R.id.txtAudio);
        txtMsg = findViewById(R.id.txtMsg);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);


        phone = getIntent().getStringExtra("phone");
        audio = getIntent().getStringExtra("audio");
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");
        txt = getIntent().getStringExtra("txt");
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        date = getIntent().getStringExtra("date");
        emergencytype = getIntent().getStringExtra("emergency");

        if (image != "") {
            Glide.with(imageView).load(image).into(imageView);
            message.setText(txt);
        } else if (txt != "") {
            message.setText(txt);
        } else if (audio != "") {
            play.setEnabled(true);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!playpause) {
                        pause.setEnabled(true);
                        play.setEnabled(false);
                        if (initialStage) {
                            new Player().execute(audio);
                        } else {
                            if (!mediaPlayer.isPlaying())
                                mediaPlayer.start();
                        }
                        playpause = true;
                    } else {
                        play.setEnabled(true);
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        playpause = false;
                    }
                }
            });
        }

        emergencyType.setText(emergencytype);
        current_time.setText(date);
        phoneNo.setText(phone);
        fname.setText(name);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));//change the number.
                startActivity(callIntent);
                sendSMS();

            }
        });


        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //initialize Uri
                    Uri uri = Uri.parse("https://www.google.com.gh/maps/dir//" + lat + "," + lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    // set flags
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    // set flags
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }

                sendSMS();
            }
        });


    }

    private void sendSMS() {

        String message = "KEEP CALM, HELP IS ON ITS WAY";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, "iXEND PERSONNEL", message, null, null);
        Toast.makeText(this, "FEEDBACK SENT", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean prepared = false;
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playpause = false;
                        play.setEnabled(true);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();
                prepared = true;

            } catch (IOException e) {
                Log.e("MyAUDIO", e.getMessage());
                prepared = false;

            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();


            }
            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("loading...");
            progressDialog.show();
        }
    }
}