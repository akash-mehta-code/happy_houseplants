package com.akashcode.happyhouseplants;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private PlantListAdapter plantListAdapter;
    private PlantListAdapter.PlantListOnClickListener plantListOnClickListener;
    private List<Plant> plantList;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> loadPlants());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.addPlant);
        fab.setOnClickListener(this::onClick);
        plantListOnClickListener = (view, position) -> openViewPlantActivity(position);
        plantListAdapter = new PlantListAdapter(this, plantListOnClickListener);
        initRecyclerView();
        loadPlants();
    }

    private void openViewPlantActivity(int position) {
        Intent intent = new Intent(MainActivity.this, ViewPlantActivity.class);
        Bundle b = new Bundle();
        b.putString("plantName", plantList.get(position).getName());
        intent.putExtras(b);
        someActivityResultLauncher.launch(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPlant:
                openEditPlantActivity();
                break;
            default:
                break;
        }
    }

    private void openEditPlantActivity() {
        Intent intent = new Intent(MainActivity.this, EditPlantActivity.class);
        someActivityResultLauncher.launch(intent);
    }

    private void loadPlants() {
        PlantDatabase plantDb = PlantDatabase.getInstance(this.getApplicationContext());
        plantList = plantDb.plantDao().listPlants();
        sortPlants(plantList);
        plantListAdapter.setPlantList(plantList);
        plantListAdapter.notifyDataSetChanged();
    }

    private void sortPlants(List<Plant> plantList) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SortType sortType = SortType.valueOf(sharedPref.getString("sortType", SortType.DAYS_UNTIL_NEXT_WATERING.name()));
        switch (sortType) {
            case PLANT_NAME:
                Collections.sort(plantList, new Plant.SortByName());
                break;
            case WATER_LEVEL:
                Collections.sort(plantList, new Plant.SortByWaterLevel());
                break;
            case DAYS_UNTIL_NEXT_WATERING:
                Collections.sort(plantList, new Plant.SortByDaysUntilNextWatering());
                break;
            default:
                return;
        }
    }

    private enum SortType {
        PLANT_NAME,
        DAYS_UNTIL_NEXT_WATERING,
        WATER_LEVEL
    }

    private void initRecyclerView() {
        RecyclerView recView = findViewById(R.id.plantListRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recView.addItemDecoration(dividerItemDecoration);
        recView.setAdapter(plantListAdapter);
    }


}