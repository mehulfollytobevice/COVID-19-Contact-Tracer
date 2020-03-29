package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tasks.tracker.model.Details;
import com.tasks.tracker.ui.SearchListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SpecialActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText searchBar;
    private Button search_button;
    private RecyclerView recyclerView;
    private TextView search_to_see_results;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference user_Details_colleotion=db.collection("UserDetails");
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        searchBar=findViewById(R.id.search_for);
        search_button=findViewById(R.id.search_button_special);
        search_to_see_results=findViewById(R.id.no_results);

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        searchListAdapter=new SearchListAdapter(search_results,SpecialActivity.this);
//        recyclerView.setAdapter(searchListAdapter);

        search_button.setOnClickListener(this);



    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_button_special:
                Intent intent=new Intent(SpecialActivity.this,Search_Results_Activity.class);
                intent.putExtra("phone_number",searchBar.getText().toString().trim());
                startActivity(intent);
                break;
        }
    }

}
