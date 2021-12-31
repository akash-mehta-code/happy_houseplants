package com.akashcode.happyhouseplants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditPlantActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        findViewById(R.id.saveEdits).setOnClickListener(this::onClick);
        findViewById(R.id.discardEdits).setOnClickListener(this::onClick);
        findViewById(R.id.deletePlant).setOnClickListener(this::onClick);
    }

    @Override
    public void onBackPressed() {
        saveOrDiscardEdits();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveEdits:
                saveEdits();
                break;
            case R.id.discardEdits:
                discardEdits();
                break;
            case R.id.deletePlant:
                deletePlant();
                break;
            default:
                break;
        }
    }

    private void deletePlant() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Delete Plant?");
        dialogBuilder.setMessage("This will permanently delete this plant.");
        dialogBuilder.setPositiveButton("DELETE", (dialogInterface, i) -> openMainActivity());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void discardEdits() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Discard Edits?");
        dialogBuilder.setMessage("This will discard the changes you have made.");
        dialogBuilder.setPositiveButton("DISCARD", (dialogInterface, i) -> openMainActivity());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void saveEdits() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Save Plant?");
        dialogBuilder.setPositiveButton("SAVE", (dialogInterface, i) -> openMainActivity());
        dialogBuilder.setNegativeButton("CANCEL", (dialogInterface, i) -> { });
        dialogBuilder.show();
    }

    private void saveOrDiscardEdits() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditPlantActivity.this);
        dialogBuilder.setTitle("Save or Discard Edits?");
        dialogBuilder.setMessage("Do you want to save or discard the edits you have made?");
        dialogBuilder.setPositiveButton("SAVE", (dialogInterface, i) -> openMainActivity());
        dialogBuilder.setNegativeButton("DISCARD", (dialogInterface, i) -> openMainActivity());
        dialogBuilder.show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}