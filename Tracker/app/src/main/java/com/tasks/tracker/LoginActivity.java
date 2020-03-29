package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "What is happening";
    private ProgressBar login_progress_bar;
    private Button login_button;
    private Button create_acc_button;
    private EditText login_email;
    private EditText login_password;
    private boolean login_See=false;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");
    private CollectionReference collectionReference_special=db.collection("Special_Users");
    private FirebaseUser currentuser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();
        login_progress_bar=findViewById(R.id.progress_login);
        create_acc_button=findViewById(R.id.createAccButton_login);
        login_button=findViewById(R.id.login_Button);
        login_email=findViewById(R.id.email_login);
        login_password=findViewById(R.id.password_login);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmailAndPassword(login_email.getText().toString().trim(),login_password.getText().toString().trim());
                create_acc_button.setEnabled(false);
            }
        });

        create_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateActivity.class));
                finish();
            }
        });
    }

    private void loginWithEmailAndPassword(String email, String password) {
        login_progress_bar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            currentuser=firebaseAuth.getCurrentUser();
                            assert currentuser != null;
                            final String currentuserid=currentuser.getUid();
                            final String current_userEmail=currentuser.getEmail();
                            Toast.makeText(LoginActivity.this, current_userEmail, Toast.LENGTH_SHORT).show();
                            login_progress_bar.setVisibility(View.INVISIBLE);


                            collectionReference_special.whereEqualTo("email",current_userEmail)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                                            if (e!=null){
                                                Log.d("Special_exception", "onEvent: "+e.toString());
                                                return;
                                            }
                                            if (!queryDocumentSnapshots.isEmpty()){
                                                login_See=true;
                                                for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                                    TrackerApi tracker=TrackerApi.getInstance();
                                                    tracker.setUsername(snapshot.getString("username"));
                                                    tracker.setUserid(snapshot.getString("userId"));
                                                    startActivity(new Intent(LoginActivity.this,SpecialActivity.class));
                                                    finish();
                                                    return;
                                                }
                                            }
                                            else{
                                                Toast.makeText(LoginActivity.this, "Not a super user", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onEvent: Not a superuser");
                                                login_progress_bar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                            collectionReference.whereEqualTo("userId",currentuserid)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e!=null){
                                                return;
                                            }
                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()){
                                                for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                                    TrackerApi tracker=TrackerApi.getInstance();
                                                    tracker.setUsername(snapshot.getString("username"));
                                                    tracker.setUserid(snapshot.getString("userId"));
//                                                    Todo: start activity and go to next activity
                                                    startActivity(new Intent(LoginActivity.this,Tracker2Activity.class));
//                                                    Todo: finish activity
                                                    finish();
                                                }
                                            }
                                            else {
                                                if (login_See==true){
                                                    login_See=false;
                                                    return;
                                                }
                                                else {
                                                    Toast.makeText(LoginActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onEvent: Something went wrong "+login_See);
                                                    login_progress_bar.setVisibility(View.INVISIBLE);
                                                    login_See=false;
                                                }

                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            login_progress_bar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        else {
            login_progress_bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please fill everything", Toast.LENGTH_SHORT).show();
        }
    }
}
