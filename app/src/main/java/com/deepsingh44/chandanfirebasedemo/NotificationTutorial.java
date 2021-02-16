package com.deepsingh44.chandanfirebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationTutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_tutorial);
    }

    public void good(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("good").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Subscrition Done";
                if (!task.isSuccessful()) {
                    msg = "Subscrition Failed";
                }
                Log.d("error", msg);
                Toast.makeText(NotificationTutorial.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bad(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("bad").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Subscrition Done";
                if (!task.isSuccessful()) {
                    msg = "Subscrition Failed";
                }
                Log.d("error", msg);
                Toast.makeText(NotificationTutorial.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}