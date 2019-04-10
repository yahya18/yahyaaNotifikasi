package com.example.yahya.yahyanotifikasi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class profileActivity extends AppCompatActivity {

    public static final String NODE_USERS = "users";
    private FirebaseAuth mAuth;

    private static final String Channel_id = "yahyaNotif";
    private static final String Channel_name = "Notif";
    private static final String Channel_desc = "myNotif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button buttonRemind = findViewById(R.id.buttonRemind);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Channel_id, Channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Channel_desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        buttonRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotif();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getInstance()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstantIdResult> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult().getToken();
                            saveToken(token);
                        } else{
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void saveToken(String token){
        String email = mAuth.getCurrentUser().getEmail();
        User user = new User(email, token);

        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS);

        dbUsers.child(mAuth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(profileActivity.this, "Token Saved", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayNotif(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, Channel_id)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("How to success?")
                        .setContentText("Sholat")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,mBuilder.build());

    }

}