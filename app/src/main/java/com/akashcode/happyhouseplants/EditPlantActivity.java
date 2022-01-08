package com.akashcode.happyhouseplants;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.akashcode.happyhouseplants.dal.Plant;
import com.akashcode.happyhouseplants.dal.PlantDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class EditPlantActivity extends AppCompatActivity implements View.OnClickListener {
    Plant plant;
    TextInputEditText plantNameEditText;
    TextInputEditText plantDaysBetweenWateringEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        View saveEditsView = findViewById(R.id.saveEdits);
        saveEditsView.setOnClickListener(this::onClick);
        saveEditsView.setOnFocusChangeListener((v, hasFocus) -> hideKeyboard(v, hasFocus));
        View editPlayLayout = findViewById(R.id.editPlantLayout);
        editPlayLayout.setOnFocusChangeListener((v, hasFocus) -> hideKeyboard(v, hasFocus));
        findViewById(R.id.discardEdits).setOnClickListener(this::onClick);
        plantNameEditText = findViewById(R.id.plantNameEditText);

        plantDaysBetweenWateringEditText = findViewById(R.id.plantDaysBetweenWateringEditText);

        Bundle extras = getIntent().getExtras();
        String plantName = Objects.isNull(extras)? null : extras.getString("plantName");
        if (!Objects.isNull(plantName)) {
            setPlantValues(plantName);
        }
    }

    private void setPlantValues(String plantName) {
        plant = PlantDatabase.getInstance(this).plantDao().getPlant(plantName);
        plantNameEditText.setText(plant.getName());
        if (Objects.nonNull(plant.getDaysBetweenWatering())) {
            plantDaysBetweenWateringEditText.setText(plant.getDaysBetweenWatering().toString());
        }
    }

    @Override
    public void onBackPressed() {
        saveOrDiscardEdits();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveEdits:
                saveEditsDialog();
                break;
            case R.id.discardEdits:
                discardEdits();
                break;
            default:
                break;
        }
    }

    private void discardEdits() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Discard Edits?");
        dialogBuilder.setMessage("This will discard the changes you have made.");
        dialogBuilder.setPositiveButton("DISCARD", (dialogInterface, i) -> finish());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void saveEditsDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Save Plant?");
        dialogBuilder.setPositiveButton("SAVE", (dialogInterface, i) -> savePlant());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void savePlant() {
        PlantDatabase plantDb = PlantDatabase.getInstance(this.getApplicationContext());

        List<Long> wateringDates = new ArrayList<>();
        if (!Objects.isNull(this.plant)) {
            plantDb.plantDao().deletePlant(this.plant);
            wateringDates = this.plant.getWateringDates();
        }

        String plantName = plantNameEditText.getText().toString();
        if (StringUtils.isBlank(plantName)) {
            Snackbar.make(plantNameEditText, "Plant Name cannot be empty.", BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(R.id.saveEdits).show();
            return;
        }

        String daysBetweenWatering = plantDaysBetweenWateringEditText.getText().toString();
        Plant plant = new Plant(plantName);
        if (StringUtils.isNotBlank(daysBetweenWatering)) {
            plant.setDaysBetweenWatering(Long.parseLong(daysBetweenWatering));
        }
        plant.setWateringDates(wateringDates);
        plantDb.plantDao().addPlant(plant);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("plantName", plant.getName());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void saveOrDiscardEdits() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Save or Discard Edits?");
        dialogBuilder.setMessage("Do you want to save or discard the edits you have made?");
        dialogBuilder.setPositiveButton("SAVE", (dialogInterface, i) -> savePlant());
        dialogBuilder.setNegativeButton("DISCARD", (dialogInterface, i) -> finish());
        dialogBuilder.show();
    }

    public void hideKeyboard(View view, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}