package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tasks.tracker.model.Details;
import com.tasks.tracker.util.TrackerApi;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity {
    private ProgressBar details_progress;
    private EditText name_details;
    private EditText phone_number;
    private Button create_details;

    private String currentuserId;
    private String currentusername;


    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private CollectionReference details_collection=db.collection("UserDetails");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        auth=FirebaseAuth.getInstance();
        details_progress=findViewById(R.id.details_progress);
        name_details=findViewById(R.id.name_detail);
        phone_number=findViewById(R.id.phone_number_detail);
        create_details=findViewById(R.id.create_details_button);


        if (TrackerApi.getInstance()!=null){
            currentuserId=TrackerApi.getInstance().getUserid();
            currentusername=TrackerApi.getInstance().getUsername();
        }

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if (user!=null){
//                    user already logged in
//                    let it be as it is
                }
                else {
//                    user is null , that means user needs to log in
                    startActivity(new Intent(DetailsActivity.this,LoginActivity.class));
                }
            }
        };

        create_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Todo: Get All the details and put it in the collection
                saveDetails();

            }
        });


    }

    private void saveDetails() {
        String name=name_details.getText().toString().trim();
        currentusername=TrackerApi.getInstance().getUsername();
        String username_detail=currentusername;
        String phone_number_detail=phone_number.getText().toString().trim();
        details_progress.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username_detail) && !TextUtils.isEmpty(phone_number_detail)){
            Details details=new Details();
            details.setName_user(name);
            details.setUsername(username_detail);
            details.setPhone_number(phone_number_detail);
            details.setDate_Added(new Timestamp(new Date()));

            details_collection.add(details)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            details_progress.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(DetailsActivity.this,Tracker2Activity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivity.this, "  Something went wrong ", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
            Toast.makeText(this, "Please Fill everything", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit? Your account has been created but you have not entered your details , this might create problems later.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DetailsActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=auth.getCurrentUser();
        auth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (auth!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
