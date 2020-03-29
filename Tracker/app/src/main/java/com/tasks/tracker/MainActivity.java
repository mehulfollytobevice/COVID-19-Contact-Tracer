package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tasks.tracker.util.TrackerApi;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private Button get_started;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private CollectionReference collectionReference=firestore.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if (user!=null){
                    user=firebaseAuth.getCurrentUser();
                    final String currentUserId= firebaseAuth.getUid();
                    collectionReference.whereEqualTo("userId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e!=null){
                                        return;

                                    }
                                    if (!queryDocumentSnapshots.isEmpty()){
                                        for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                            TrackerApi tracker=TrackerApi.getInstance();
                                            tracker.setUserid(snapshot.getString("userId"));
                                            tracker.setUsername(snapshot.getString("username"));
//                                            Todo: start intent and go to the next activity from here
                                            startActivity(new Intent(MainActivity.this,Tracker2Activity.class));
//                                            Todo: finish this activity
                                            finish();
                                        }
                                    }
                                }
                            });
                }
            }
        };
        get_started=findViewById(R.id.get_started);
        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
