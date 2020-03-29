package com.tasks.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tasks.tracker.model.Log_details;
import com.tasks.tracker.ui.InteractionAdapter;

import java.util.ArrayList;
import java.util.List;

public class InteractionsActivity extends AppCompatActivity {


    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference interactions_colleotion;
    private FirebaseUser user;
    private String interactions_of_user;

    private List<Log_details> interactions_list;
    private RecyclerView recyclerView;
    private InteractionAdapter interactionAdapter;
    private TextView no_interactions;
    private Button see_interactions_on_map ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactions);
        Bundle extra=getIntent().getExtras();
        interactions_of_user=extra.getString("username");
        interactions_colleotion=db.collection(interactions_of_user);

        no_interactions=findViewById(R.id.interactions_not_present);

        interactions_list=new ArrayList<>();
        recyclerView=findViewById(R.id.list_of_interactions_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        see_interactions_on_map=findViewById(R.id.see_interactions_on_map);

        see_interactions_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction_intent=new Intent(InteractionsActivity.this,MapsActivity.class);
                interaction_intent.putExtra("User",interactions_of_user);
                startActivity(interaction_intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        interactions_colleotion.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot interactions:queryDocumentSnapshots){
                                Log_details interaction=interactions.toObject(Log_details.class);
                                interactions_list.add(interaction);
                            }
                            interactionAdapter=new InteractionAdapter(interactions_list,InteractionsActivity.this);
                            recyclerView.setAdapter(interactionAdapter);
                            interactionAdapter.notifyDataSetChanged();
                        }
                        else{
                            no_interactions.setVisibility(View.VISIBLE);
                            see_interactions_on_map.setEnabled(false);
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
