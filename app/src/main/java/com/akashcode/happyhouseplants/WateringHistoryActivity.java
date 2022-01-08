package com.akashcode.happyhouseplants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;

import java.util.Objects;

public class WateringHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private WateringHistoryAdapter wateringHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watering_history);
        findViewById(R.id.closeWateringHistory).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        String plantName = Objects.isNull(extras)? null : extras.getString("plantName");
        if (!Objects.isNull(plantName)) {

            initRecyclerView(plantName);
        }
    }

    private void initRecyclerView(String plantName) {
        Plant plant = PlantDatabase.getInstance(this).plantDao().getPlant(plantName);
        wateringHistoryAdapter = new WateringHistoryAdapter(this, plant.getWateringDates());
        RecyclerView recView = findViewById(R.id.wateringHistoryRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recView.addItemDecoration(dividerItemDecoration);
        recView.setAdapter(wateringHistoryAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeWateringHistory:
                finish();
                break;
            default:
                break;
        }
    }
}