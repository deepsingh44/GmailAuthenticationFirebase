package com.deepsingh44.chandanfirebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {
    private EditText tmobile, totp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks myCallbacks;
    private FirebaseAuth mAuth;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        tmobile = findViewById(R.id.mymobile);
        totp = findViewById(R.id.myotp);
        firebaseLogin();
    }

    public void generate(View view) {
        String phone = tmobile.getText().toString();
        Log.e("error", "+91" + phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,              // verify mobile number
                60,                              // duration of timeout
                TimeUnit.SECONDS,    // timeout unit
                PhoneAuth.this,       // CurrentActivity for callback
                myCallbacks);
    }

    private void firebaseLogin() {
        mAuth = FirebaseAuth.getInstance();
        myCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                totp.setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Log.e("error", "code is sent");
            }
        };
    }

    public void verify(View view) {
        String otp = totp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider
                .getCredential(verificationCode, otp);
        signinWithPhone(credential);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.e("error", "Successfully Login");
        }
    }

    private void signinWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneAuth.this, "Success", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

    }

}