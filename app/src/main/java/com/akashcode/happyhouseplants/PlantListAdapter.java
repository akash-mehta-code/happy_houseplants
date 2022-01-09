package com.akashcode.happyhouseplants;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.w3c.dom.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Plant> plantList = new ArrayList<>();
    private PlantListOnClickListener plantListOnClickListener;
    private Filter plantFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Plant> plantList = PlantDatabase.getInstance(context).plantDao().searchPlants("%"+charSequence.toString().toLowerCase()+"%");
            FilterResults result = new FilterResults();
            result.values = plantList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            plantList.clear();
            plantList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

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
        Plant plant = plantList.get(position);
        holder.plantListItem.setText(plant.getName());
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        long progress = 0;
        long daysUntilNextWatering = 0;
        Long daysBetweenWatering = plant.getDaysBetweenWatering();
        List<Long> wateringDates = plant.getWateringDates();
        if (!wateringDates.isEmpty() && Objects.nonNull(daysBetweenWatering)) {
            Long daysSinceLastWatering = Duration.ofMillis(today - wateringDates.get(wateringDates.size()-1)).toDays();
            progress = 100 - 100*daysSinceLastWatering/ daysBetweenWatering;
            if (progress < 0) {
                progress = 0;
            }
            daysUntilNextWatering = daysBetweenWatering - daysSinceLastWatering;
            if (daysUntilNextWatering < 0) {
                daysUntilNextWatering = 0;
            }
        }
        holder.waterProgressIndicator.setProgressCompat(((int) progress), false);
        holder.daysUntilNextWatering.setText(String.valueOf(daysUntilNextWatering));
        if (daysUntilNextWatering == 0) {
            holder.daysUntilNextWatering.setTextColor(ContextCompat.getColor(this.context, R.color.red));
            holder.daysUntilNextWatering.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.daysUntilNextWatering.setTextColor(ContextCompat.getColor(this.context, R.color.white));
            holder.daysUntilNextWatering.setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    @Override
    public Filter getFilter() {
        return plantFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView plantListItem;
        LinearProgressIndicator waterProgressIndicator;
        TextView daysUntilNextWatering;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            plantListItem = itemView.findViewById(R.id.plantListItemNameTextView);
            waterProgressIndicator = itemView.findViewById(R.id.waterProgressBar);
            daysUntilNextWatering = itemView.findViewById(R.id.daysUntilNextWateringValue);

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
