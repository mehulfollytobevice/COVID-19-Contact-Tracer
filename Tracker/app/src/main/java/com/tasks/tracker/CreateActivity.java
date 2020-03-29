package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tasks.tracker.util.TrackerApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateActivity extends AppCompatActivity {
    
    private Button create_account;
    private EditText email_create;
    private EditText pass_create;
    private EditText create_username;
    private ProgressBar create_progress;
    
    private FirebaseUser user;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        firebaseAuth=FirebaseAuth.getInstance();
        create_account=findViewById(R.id.acctButton_create);
        email_create=findViewById(R.id.email_acct_create);
        pass_create=findViewById(R.id.password_acct_create);
        create_username=findViewById(R.id.usernameacct_create);
        create_progress=findViewById(R.id.progress_acct);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if (user!=null){
//                    user is already logged in
                }
                else{
//                    user needs to create an account
                }
            }
        };
        
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !TextUtils.isEmpty(email_create.getText().toString().trim()) && !TextUtils.isEmpty(pass_create.getText().toString().trim()) && !TextUtils.isEmpty(create_username.getText().toString().trim())){
                    if (pass_create.getText().toString().length()>=6) {
                        createAccount(email_create.getText().toString().trim(), pass_create.getText().toString().trim(), create_username.getText().toString().trim());
                    }
                    else {
                        Toast.makeText(CreateActivity.this, "Password should be have atleast 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(CreateActivity.this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
            create_progress.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                user=firebaseAuth.getCurrentUser();
                                assert user != null;
                                final String userid=user.getUid();
                                Map<String ,String> hashmap=new HashMap<>();
                                hashmap.put("userId",userid);
                                hashmap.put("username",username);

                                collectionReference.add(hashmap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (Objects.requireNonNull(task.getResult()).exists()){
                                                                    create_progress.setVisibility(View.INVISIBLE);
                                                                    String name=task.getResult().getString("username");

                                                                    TrackerApi tracker=TrackerApi.getInstance();
                                                                    tracker.setUserid(userid);
                                                                    tracker.setUsername(name);

                                                                    Intent intent=new Intent(CreateActivity.this,DetailsActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else {
                                                                    create_progress.setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            create_progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateActivity.this, "Please try again with other details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
        }
    }
}
