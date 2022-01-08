package com.akashcode.happyhouseplants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WateringHistoryAdapter extends RecyclerView.Adapter<WateringHistoryAdapter.MyViewHolder>{
    private Context context;
    private List<Long> wateringHistory;

    public WateringHistoryAdapter(Context context, List<Long> wateringHistory) {
        this.context = context;
        this.wateringHistory = wateringHistory;
    }

    @NonNull
    @Override
    public WateringHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.watering_history_item, parent, false);

        return new WateringHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
        String wateringDate = simple.format(new Date(wateringHistory.get(position)));
        holder.wateringHistoryItem.setText(wateringDate);
    }

    @Override
    public int getItemCount() {
        return wateringHistory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView wateringHistoryItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wateringHistoryItem = itemView.findViewById(R.id.wateringDate);
        }
    }
}
