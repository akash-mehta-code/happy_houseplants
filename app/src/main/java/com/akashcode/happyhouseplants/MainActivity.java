package com.akashcode.happyhouseplants;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar materialToolbar;
    private PlantListAdapter plantListAdapter;
    private PlantListAdapter.PlantListOnClickListener plantListOnClickListener;
    private List<Plant> plantList;
    private
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> loadPlants());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialToolbar = findViewById(R.id.materialToolBarMain);
        setSupportActionBar(materialToolbar);

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
        recView.setAdapter(plantListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                plantListAdapter.getFilter().filter(s);
                return false;
            }
        });

        MenuItem addPlantItem = menu.findItem(R.id.addPlant);
        addPlantItem.setOnMenuItemClickListener(menuItem -> {
            openEditPlantActivity();
            return true;
        });
        return true;
    }
}