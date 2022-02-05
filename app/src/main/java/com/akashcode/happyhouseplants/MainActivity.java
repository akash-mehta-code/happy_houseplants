package com.akashcode.happyhouseplants;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.akashcode.happyhouseplants.dal.PlantHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar materialToolbar;
    private PlantListAdapter plantListAdapter;
    private PlantListAdapter.PlantListOnClickListener plantListOnClickListener;
    private List<Plant> plantList;
    private
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                loadPlants();
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String plantName = result.getData().getStringExtra("plantName");
                    if (StringUtils.isNotBlank(plantName)) {
                        int index = IntStream.range(0, plantList.size())
                                .filter(i -> plantList.get(i).getName().equals(plantName))
                                .findFirst()
                                .orElse(-1);
                        if (index != -1) {

                            openViewPlantActivity(index);
                        }
                    }
                }
            });

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
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        PlantHelper.SortType sortType = PlantHelper.SortType.valueOf(sharedPref.getString("sortType", PlantHelper.SortType.DAYS_UNTIL_NEXT_WATERING.name()));
        PlantHelper.sortPlants(plantList, sortType);
        plantListAdapter.setPlantList(plantList);
        plantListAdapter.notifyDataSetChanged();
        materialToolbar.setTitle(String.format("Happy Houseplants (%d)", plantList.size()));
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