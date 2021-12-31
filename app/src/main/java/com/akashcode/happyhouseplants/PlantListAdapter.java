package com.akashcode.happyhouseplants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akashcode.happyhouseplants.dal.Plant;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.MyViewHolder> {
    private Context context;
    private List<Plant> plantList = new ArrayList<>();
    private PlantListOnClickListener plantListOnClickListener;

    public PlantListAdapter(Context context, PlantListOnClickListener listener) {
        this.context = context;
        plantListOnClickListener = listener;
    }

    public void setPlantList(List<Plant> plantList) {
        Objects.requireNonNull(plantList);
        this.plantList = plantList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plant_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.plantListItem.setText(plantList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView plantListItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            plantListItem = itemView.findViewById(R.id.plantListItemNameTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            plantListOnClickListener.onClick(view, getAdapterPosition());
        }
    }

    public interface PlantListOnClickListener {
        void onClick(View view, int position);
    }
}
