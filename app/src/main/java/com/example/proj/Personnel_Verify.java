package com.example.proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Personnel_Verify extends AppCompatActivity {
    TextInputLayout phone;
    Button register;
    ProgressBar progress;
    String verificationCodeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.personnel_verify);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);


        String phoneNo = getIntent().getStringExtra("phone");
        sendVerificationCodeToUser(phoneNo);
        String fname = getIntent().getStringExtra("fname");
        Log.d("onchange", "onDataChange: " + fname );



        // when the uses a different phone for the authentication

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we get the code
                String code = phone.getEditText().getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    phone.setError("Wrong Verification code");
                    phone.requestFocus();
                }
                progress.setVisibility(View.VISIBLE);
                verifycode(code);
            }
        });


    }

    private void sendVerificationCodeToUser(String phone) {
        Log.d("Verification", "Sending verification code to: " + phone);
        String user_phone = "+233" + phone;
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(user_phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)    // Timeout duration
                        .setActivity(this)                    // Activity for callback binding
                        .setCallbacks(mCallbacks)             // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                // when code is sent, the verification should be done automatically


                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        progress.setVisibility(View.VISIBLE);
                        verifycode(code);
                    }

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e("Verification", "Verification failed", e);
                    String errorMessage = getErrorMessage(e);
                    Toast.makeText(Personnel_Verify.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                private String getErrorMessage(FirebaseException exception) {
                    String defaultMessage = "Verification failed. Please try again.";
                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        return "Invalid phone number format.";
                    } else if (exception instanceof FirebaseTooManyRequestsException) {
                        return "Quota exceeded. Please try again later.";
                    }
                    return defaultMessage;
                }
            };

    private void verifycode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInUserByCredential(credential);


    }

    private void signInUserByCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential).addOnCompleteListener(Personnel_Verify.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Personnel_Verify.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // to disallow the user from using the back button to get to this page,
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();


                } else {
                    Toast.makeText(Personnel_Verify.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    phone.requestFocus();

                }

            }
        });
    }
}