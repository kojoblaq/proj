package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;


public class Personnel_SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextInputLayout regID, regphone;
    Button btnregister;
    FirebaseDatabase rootNode;
    FirebaseAuth.AuthStateListener FirebaseAuthListener;
    DatabaseReference reference;
    long UserID = 0;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.personnel_signup);


        // Hooks
        regID = findViewById(R.id.PID);
        regphone = findViewById(R.id.phone);
        btnregister = findViewById(R.id.register);


        Spinner spinner = findViewById(R.id.spinner);

        String[] personnel = getResources().getStringArray(R.array.Personnel);
        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personnel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Personnel");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserID = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String PID = Objects.requireNonNull(regID.getEditText()).getText().toString();
                final String phone = Objects.requireNonNull(regphone.getEditText()).getText().toString();

                regphone.setError(null);
                regphone.setErrorEnabled(false);
                regID.setError(null);
                regID.setErrorEnabled(false);


                if (PID.isEmpty()) {
                    regID.setError("ID Required");
                    return;

                } else {
                    regID.setError(null);
                    regID.setErrorEnabled(false);

                }


                if(!PID.equals("vvu217cs")){
                    Toast.makeText(Personnel_SignUp.this, "ID Error", Toast.LENGTH_SHORT).show();
                    regID.setError("INVALID ID");
                    regID.requestFocus();
                    return;
                }else {
                    regID.setError(null);
                    regID.setErrorEnabled(false);
                }




                if (phone.isEmpty()) {
                    regphone.setError("Phone Number Required");
                    return;

                } else {
                    regphone.setError(null);
                    regphone.setErrorEnabled(false);
                }



                isValidMobile(phone);


                Intent intent = new Intent(getApplicationContext(), Personnel_Verify.class);
                intent.putExtra("phone", phone);
                startActivity(intent);


                PersonnelHelperClass helperClass = new PersonnelHelperClass(PID, phone);


                reference.child(String.valueOf(UserID + 1)).setValue(helperClass);

            }
        });


    }


    private boolean isValidMobile(String phone) {
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() == 10;
        }
        return false;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            String valueFromSpinner = parent.getItemAtPosition(40).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}