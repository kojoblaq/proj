package com.example.proj;

import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj.helperClasses.MyReportAdapter;
import com.example.proj.helperClasses.reportClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/*import okhttp3.Route;
import okhttp3.internal.connection.RouteException;*/

public class Home extends AppCompatActivity {
    SupportMapFragment mapFragment;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    FusedLocationProviderClient client;
    GoogleMap mMap;
    CardView reportcard;
    Location location;
    LocationRequest mlocationRequest;
    LocationCallback callback;
    com.google.android.gms.common.api.GoogleApi GoogleApi;
    ImageView picture;
    TextView topic, message;
    Button btnRegister;
    private String customerId = "";
    long UserID = 0;
    DatabaseReference reference;


    List<reportClass> fetchreport;
    RecyclerView recyclerView;
    MyReportAdapter helperAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);



    }
}