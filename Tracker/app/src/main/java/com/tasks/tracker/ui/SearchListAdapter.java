package com.tasks.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tasks.tracker.InteractionsActivity;
import com.tasks.tracker.R;
import com.tasks.tracker.Search_Results_Activity;
import com.tasks.tracker.model.Details;
import com.tasks.tracker.model.Log_details;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Details> search_results;
    private Context context;

    public SearchListAdapter(List<Details> search_results,Context context){
        this.search_results=search_results;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    public void setSearch_results(List<Details> search_details){
        search_results=search_details;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.recyclerlist_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (search_results!=null){
            final Details current=search_results.get(position);
            holder.username_result.setText("Username:"+current.getUsername().toString().trim());
            holder.phone_number_result.setText("Phone Number:"+current.getPhone_number().toString().trim());
            holder.name_result.setText("Name: "+current.getName_user().toString().trim());
            holder.interactions_button.setEnabled(true);
            holder.interactions_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, InteractionsActivity.class);
                    intent.putExtra("username",current.getUsername().toString().trim());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }
        else {
            holder.username_result.setText("No result");
            holder.phone_number_result.setText("No result");
            holder.name_result.setText("No result");
            holder.interactions_button.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        if (search_results!=null){
            return search_results.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name_result;
        public TextView phone_number_result;
        public TextView username_result;
        public Button interactions_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_result=itemView.findViewById(R.id.name_of_the_person);
            phone_number_result=itemView.findViewById(R.id.phone_number_result_list);
            username_result=itemView.findViewById(R.id.username_of_result_list);
            interactions_button=itemView.findViewById(R.id.see_interactions_button);

        }
    }
}
