package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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

public class Search_Results_Activity extends AppCompatActivity {

    private RecyclerView list_of_results;
    private TextView no_results;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference user_Details_colleotion=db.collection("UserDetails");
    private FirebaseUser user;

    private List<Details> search_results;
    private SearchListAdapter searchListAdapter;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extra=getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__results_);
        phone_number=extra.getString("phone_number");
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        list_of_results=findViewById(R.id.list_of_search_results);
        no_results=findViewById(R.id.no_results_shown);

        search_results=new ArrayList<>();

        list_of_results.setHasFixedSize(true);
        list_of_results.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        user_Details_colleotion.whereEqualTo("phone_number",phone_number)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                Details detail=snapshot.toObject(Details.class);
                                search_results.add(detail);
                            }
                            searchListAdapter=new SearchListAdapter(search_results,Search_Results_Activity.this);
                            list_of_results.setAdapter(searchListAdapter);
                            searchListAdapter.notifyDataSetChanged();
                        }
                        else {
                            no_results.setVisibility(View.VISIBLE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
