package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Reporter_SignUp extends AppCompatActivity {
    private TextInputLayout regFname, regPhone;
    private Button btnRegister;
    private DatabaseReference reference;
    private long userId = 0;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reporter_signup);

        regFname = findViewById(R.id.fname);
        regPhone = findViewById(R.id.phone);
        btnRegister = findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Reporter");


        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ReporterHome.class));
            finish();
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userId= (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnRegister.setOnClickListener(v -> {
            String fname = regFname.getEditText().getText().toString().trim();
            String phone = regPhone.getEditText().getText().toString().trim();

            if (isValidMobile(phone) && !fname.isEmpty()) {
                regFname.setError(null);
                regFname.setErrorEnabled(false);
                regPhone.setError(null);
                regPhone.setErrorEnabled(false);

                Intent intent = new Intent(getApplicationContext(), Reporter_Verify.class);
                intent.putExtra("phone", phone);
                intent.putExtra("fname", fname);
                startActivity(intent);

                Intent i = new Intent(getApplicationContext(), MainAction.class);
                i.putExtra("fname", fname);
                i.putExtra("phone", phone);

                Intent a = new Intent(getApplicationContext(), SendAudio.class);
                a.putExtra("fname", fname);
                a.putExtra("phone", phone);

                Intent t = new Intent(getApplicationContext(), Text_page.class);
                t.putExtra("fname", fname);
                t.putExtra("phone", phone);
            } else {
                if (fname.isEmpty()) {
                    regFname.setError("Full name required");
                }

                if (!isValidMobile(phone)) {
                    regPhone.setError("Valid phone number required");
                }
            }
        });
    }
    private boolean isValidMobile(String phone) {
        return Pattern.matches("[0-9]+", phone) && phone.length() == 10;
    }
}