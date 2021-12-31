package com.akashcode.happyhouseplants;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewPlantActivity extends AppCompatActivity implements View.OnClickListener {
    Plant plant;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String plantName = result.getData().getStringExtra("plantName");
                    displayPlant(plantName);
                }});

    private void displayPlant(String plantName) {
        plant = PlantDatabase.getInstance(this).plantDao().getPlant(plantName);

        TextView plantNameView = findViewById(R.id.plantNameViewMode);
        plantNameView.setText(plant.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plant);

        findViewById(R.id.backFromView).setOnClickListener(this::onClick);
        findViewById(R.id.deletePlant).setOnClickListener(this::onClick);
        findViewById(R.id.editPlant).setOnClickListener(this::onClick);
        String plantName = getIntent().getExtras().getString("plantName");
        displayPlant(plantName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backFromView:
                finish();
                break;
            case R.id.deletePlant:
                deletePlantDialog();
                break;
            case R.id.editPlant:
                openEditPlantActivity();
                break;
            default:
                break;
        }
    }

    private void openEditPlantActivity() {
        Intent intent = new Intent(ViewPlantActivity.this, EditPlantActivity.class);
        Bundle b = new Bundle();
        b.putString("plantName", plant.getName());
        intent.putExtras(b);
        someActivityResultLauncher.launch(intent);
    }

    private void deletePlantDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(ViewPlantActivity.this);
        dialogBuilder.setTitle("Delete Plant?");
        dialogBuilder.setMessage("This will permanently delete this plant.");
        dialogBuilder.setPositiveButton("DELETE", (dialogInterface, i) -> deletePlant());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void deletePlant() {
        PlantDatabase.getInstance(this).plantDao().deletePlant(plant);
        finish();
    }
}