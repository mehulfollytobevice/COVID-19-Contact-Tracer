package com.tasks.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.tasks.tracker.InteractionsActivity;
import com.tasks.tracker.R;
import com.tasks.tracker.model.Details;
import com.tasks.tracker.model.Log_details;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InteractionAdapter extends RecyclerView.Adapter<InteractionAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Log_details> interaction;
    private Context context;

    public InteractionAdapter(List<Log_details> search_results,Context context){
        this.interaction=search_results;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    public void setInteraction(List<Log_details> search_details){
        interaction=search_details;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.interaction_list_item,parent,false);
        return new InteractionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (interaction!=null){
            final Log_details current=interaction.get(position);
            holder.username_interaction.setText("Username:"+current.getUsername().toString().trim());
            holder.phone_number_interaction.setText("Phone Number:"+current.getPhone_number().toString().trim());
            holder.name_interaction.setText("Name: "+current.getName().toString().trim());
            holder.location_interaction.setText(String.format("Location Crossed:%s , %s", current.getLocation().getLatitude(), current.getLocation().getLongitude()));
            Date javaDate=current.getDate_crossed().toDate();
            holder.date_interaction.setText("Date crossed:"+javaDate.toString().trim());

            holder.interaction_iteration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, InteractionsActivity.class);
                    intent.putExtra("username",current.getUsername().toString().trim());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });


//            Todo: Geo-encode the latitude and the longitude to give street address,city ,state, country



//            Geocoder geocoder;
//            geocoder = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = null;
//            try {
//                addresses = geocoder.getFromLocation(current.getLocation().getLatitude(),current.getLocation().getLongitude() , 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();

//            holder.date_interaction.setText("Location:"+address.toString().trim()+" , "+city.toString().trim()+" , "+state.toString().trim()+" , "+country.toString().trim());


        }
        else {
            holder.username_interaction.setText("No result");
            holder.phone_number_interaction.setText("No result");
            holder.name_interaction.setText("No result");
            holder.location_interaction.setText("No result");
            holder.date_interaction.setText("No result");
        }

    }

    @Override
    public int getItemCount() {
        if (interaction!=null){
            return interaction.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name_interaction;
        public TextView phone_number_interaction;
        public TextView username_interaction;
        public TextView location_interaction;
        public TextView date_interaction;
        public Button interaction_iteration;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_interaction=itemView.findViewById(R.id.name_of_interaction);
            phone_number_interaction=itemView.findViewById(R.id.phone_number_of_interaction);
            username_interaction=itemView.findViewById(R.id.username_of_interaction);
            location_interaction=itemView.findViewById(R.id.location_crossed);
            date_interaction=itemView.findViewById(R.id.date_of_interaction);
            interaction_iteration=itemView.findViewById(R.id.iteration_interactions);
        }
    }
}
