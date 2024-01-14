package com.example.proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Reporter_Verify extends AppCompatActivity {
    private TextInputLayout phone;
    private Button register;
    private ProgressBar progress;
    private String verificationCodeBySystem;
    private int send_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reporter_verify);

        initializeViews();
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        String phoneNo = getIntent().getStringExtra("phone");
        sendVerificationCodeToUser(phoneNo);
        final String fname = getIntent().getStringExtra("f_name");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = phone.getEditText().getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    showError("Wrong Verification code");
                    return;
                }

                progress.setVisibility(View.VISIBLE);
                verifycode(code);
            }
        });
    }

    private void initializeViews() {
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
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

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    Log.d("Verification", "Code sent successfully. Verification ID: " + s);
                    verificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    try {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            Log.d("Verification", "Verification completed with code: " + code);
                            progress.setVisibility(View.VISIBLE);
                            verifycode(code);
                        }
                    } catch (Exception e) {
                        Log.e("Verification", "Exception during verification completion: " + e.getMessage());
                    }
                }

//                @Override
//                public void onVerificationFailed(@NonNull FirebaseException e) {
//                    Log.e("Verification", "Verification failed: " + e.getMessage());
//                    Toast.makeText(Reporter_Verify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e("Verification", "Verification failed", e);
                    String errorMessage = getErrorMessage(e);
                    Toast.makeText(Reporter_Verify.this, errorMessage, Toast.LENGTH_SHORT).show();
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
        final String fname = getIntent().getStringExtra("fname");
        final String phoneNo = getIntent().getStringExtra("phone");

        mAuth.signInWithCredential(credential).addOnCompleteListener(Reporter_Verify.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserToDatabase(fname, phoneNo);
                } else {
                    showError(task.getException().getMessage());
                }
            }
        });
    }

    private void saveUserToDatabase(String fname, String phoneNo) {
        DatabaseReference reporter = FirebaseDatabase.getInstance().getReference("Reporter");
        reporter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    send_id = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelperClass helperClass = new UserHelperClass(fname, phoneNo, userid);
        reporter.child(userid).setValue(helperClass);

        Intent intent = new Intent(getApplicationContext(), ReporterHome.class);
        intent.putExtra("fname", fname);
        intent.putExtra("phone", phoneNo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(Reporter_Verify.this, message, Toast.LENGTH_SHORT).show();
        phone.requestFocus();
    }
}
