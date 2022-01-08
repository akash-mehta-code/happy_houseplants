package com.akashcode.happyhouseplants;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ViewPlantActivity extends AppCompatActivity implements View.OnClickListener {
    private Plant plant;
    private TextView plantDaysBetweenWateringView;
    private View plantDaysBetweenWateringLayout;
    private TextView plantLastWateredDateView;
    private View plantLastWateredDateLayout;
    private View wateringHistoryButtonView;
    private TextView plantNameView;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String plantName = result.getData().getStringExtra("plantName");
                    displayPlant(plantName);
                }});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plant);

        wateringHistoryButtonView = findViewById(R.id.wateringHistoryButton);
        plantDaysBetweenWateringView = findViewById(R.id.plantDaysBetweenWateringViewMode);
        plantDaysBetweenWateringLayout = findViewById(R.id.plantDaysBetweenWateringLayout);
        plantLastWateredDateView = findViewById(R.id.plantLastWateredDateViewMode);
        plantLastWateredDateLayout = findViewById(R.id.plantLastWateredDateLayout);
        plantNameView = findViewById(R.id.plantNameViewMode);

        findViewById(R.id.backFromView).setOnClickListener(this);
        findViewById(R.id.deletePlant).setOnClickListener(this);
        findViewById(R.id.editPlant).setOnClickListener(this);
        findViewById(R.id.waterPlant).setOnClickListener(this);
        wateringHistoryButtonView.setOnClickListener(this);

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
            case R.id.waterPlant:
                waterPlant(view);
                break;
            case R.id.wateringHistoryButton:
                showAllWateringDates(view);
                break;
            default:
                break;
        }
    }

    private void showAllWateringDates(View view) {
        Intent intent = new Intent(ViewPlantActivity.this, WateringHistoryActivity.class);
        Bundle b = new Bundle();
        b.putString("plantName", plant.getName());
        intent.putExtras(b);
        someActivityResultLauncher.launch(intent);
    }

    private void waterPlant(View view) {
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select Watering Date");
        datePickerBuilder.setSelection(today);
        MaterialDatePicker<Long> datePicker = datePickerBuilder.build();
        datePicker.show(getSupportFragmentManager(), "DATE PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            long pickedDate = datePicker.getSelection().longValue();
            if (pickedDate > today) {
                Snackbar.make(view, "Cannot select a future water date.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            List<Long> wateringDates = plant.getWateringDates();
            if (wateringDates.contains(pickedDate)) {
                return;
            }
            plant.addWateringDate(pickedDate);
            wateringDates = plant.getWateringDates();
            if (wateringDates.size() > 1) {
                Long timeBetweenWatering = wateringDates.get(wateringDates.size()-1) - wateringDates.get(wateringDates.size()-2);
                Long daysBetweenWatering = Duration.ofMillis(timeBetweenWatering).toDays();
                if (Objects.isNull(plant.getDaysBetweenWatering()) || plant.getDaysBetweenWatering().equals(0)) {

                    plant.setDaysBetweenWatering(daysBetweenWatering);
                } else if (!daysBetweenWatering.equals(plant.getDaysBetweenWatering())) {
                    updateDaysBetweenWateringDialog(plant.getDaysBetweenWatering(), daysBetweenWatering);
                }
            }
            PlantDatabase.getInstance(ViewPlantActivity.this).plantDao().updatePlant(plant);
            displayPlant(plant.getName());
        });
    }

    private void updateDaysBetweenWateringDialog(Long currentDaysBetweenWatering, Long newDaysBetweenWatering) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(ViewPlantActivity.this);
        dialogBuilder.setTitle("Update Days Between Watering?");
        dialogBuilder.setMessage(
                String.format("Days between watering is set to %d. Most recent watering is done after %d days. " +
                        "Do you want to update Days Between Watering to %d?", currentDaysBetweenWatering, newDaysBetweenWatering, newDaysBetweenWatering));
        dialogBuilder.setPositiveButton("YES", (dialogInterface, i) -> {
            plant.setDaysBetweenWatering(newDaysBetweenWatering);

            PlantDatabase.getInstance(ViewPlantActivity.this).plantDao().updatePlant(plant);
            displayPlant(plant.getName());
        });
        dialogBuilder.setNegativeButton("NO", (dialogInterface, i) -> { });
        dialogBuilder.show();
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

    private void displayPlant(String plantName) {
        plant = PlantDatabase.getInstance(this).plantDao().getPlant(plantName);

        plantNameView.setText(plant.getName());

        Long daysBetweenWatering = plant.getDaysBetweenWatering();
        if (Objects.nonNull(daysBetweenWatering)) {
            plantDaysBetweenWateringView.setText(daysBetweenWatering.toString());
            plantDaysBetweenWateringLayout.setVisibility(View.VISIBLE);
        } else {
            plantDaysBetweenWateringLayout.setVisibility(View.GONE);
        }

        DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));
        List<Long> wateringDates = plant.getWateringDates();
        if (wateringDates.isEmpty()) {
            plantLastWateredDateLayout.setVisibility(View.GONE);
            wateringHistoryButtonView.setVisibility(View.GONE);
        } else {
            String lastWateredDate = simple.format(new Date(wateringDates.get(wateringDates.size()-1)));
            plantLastWateredDateView.setText(lastWateredDate);
            plantLastWateredDateLayout.setVisibility(View.VISIBLE);
            wateringHistoryButtonView.setVisibility(View.VISIBLE);
        }
    }
}