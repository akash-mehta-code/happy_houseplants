package com.akashcode.happyhouseplants.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
public class Plant {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "daysBetweenWatering")
    private Long daysBetweenWatering = null;

    // Dates are stored in milisecond timestamps
    @ColumnInfo(name = "wateringDates")
    private List<Long> wateringDates = new ArrayList<>();

    @ColumnInfo(name = "BoughtOnDate")
    private Long boughtOnDate = null;

    @ColumnInfo(name = "PurchasePrice")
    private Double purchasePrice = null;

    @ColumnInfo(name = "Notes")
    private String notes = null;

    public Plant(@NonNull String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public Long getDaysBetweenWatering() {
        return daysBetweenWatering;
    }

    public void setDaysBetweenWatering(Long daysBetweenWatering) {
        this.daysBetweenWatering = daysBetweenWatering;
    }

    public static class DatesTypeConverter {
        @TypeConverter
        public List<Long> fromString(String dates) {
            return new Gson().fromJson(dates, new TypeToken<ArrayList<Long>>(){}.getType());
        }

        @TypeConverter
        public String fromList(List<Long> dates) {
            return new Gson().toJson(dates);
        }
    }

    public void addWateringDate(@NonNull Long dateInMillis) {
        Objects.requireNonNull(dateInMillis);
        wateringDates.add(dateInMillis);
        Collections.sort(wateringDates);
    }

    public List<Long> getWateringDates() {
        return new ArrayList<>(wateringDates);
    }

    public void setWateringDates(List<Long> dates) {
        wateringDates = dates;
    }

    public Long getBoughtOnDate() {
        return boughtOnDate;
    }

    public void setBoughtOnDate(Long boughtOnDate) {
        this.boughtOnDate = boughtOnDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static class SortByName implements Comparator<Plant> {
        @Override
        public int compare(Plant plant, Plant t1) {
            return plant.name.compareTo(t1.name);
        }
    }

    public static class SortByDaysUntilNextWatering implements Comparator<Plant> {
        @Override
        public int compare(Plant plant, Plant t1) {
            long today = MaterialDatePicker.todayInUtcMilliseconds();
            long daysUntilNextWatering = 0;
            Long daysBetweenWatering = plant.getDaysBetweenWatering();
            List<Long> wateringDates = plant.getWateringDates();
            if (!wateringDates.isEmpty() && Objects.nonNull(daysBetweenWatering)) {
                Long daysSinceLastWatering = Duration.ofMillis(today - wateringDates.get(wateringDates.size()-1)).toDays();
                daysUntilNextWatering = daysBetweenWatering - daysSinceLastWatering;
            }
            long daysUntilNextWateringOther = 0;
            Long daysBetweenWateringOther = t1.getDaysBetweenWatering();
            List<Long> wateringDatesOther = t1.getWateringDates();
            if (!wateringDatesOther.isEmpty() && Objects.nonNull(daysBetweenWateringOther)) {
                Long daysSinceLastWateringOther = Duration.ofMillis(today - wateringDatesOther.get(wateringDatesOther.size()-1)).toDays();
                daysUntilNextWateringOther = daysBetweenWateringOther - daysSinceLastWateringOther;
            }
            return (int) (daysUntilNextWatering - daysUntilNextWateringOther);
        }
    }

    public static class SortByWaterLevel implements Comparator<Plant> {
        @Override
        public int compare(Plant plant, Plant t1) {
            long today = MaterialDatePicker.todayInUtcMilliseconds();
            long progress = 0;
            Long daysBetweenWatering = plant.getDaysBetweenWatering();
            List<Long> wateringDates = plant.getWateringDates();
            if (!wateringDates.isEmpty() && Objects.nonNull(daysBetweenWatering)) {
                Long daysSinceLastWatering = Duration.ofMillis(today - wateringDates.get(wateringDates.size() - 1)).toDays();
                progress = 100 - 100 * daysSinceLastWatering / daysBetweenWatering;
                if (progress < 0) {
                    progress = 0;
                }
            }
            long progressOther = 0;
            Long daysBetweenWateringOther = t1.getDaysBetweenWatering();
            List<Long> wateringDatesOther = t1.getWateringDates();
            if (!wateringDatesOther.isEmpty() && Objects.nonNull(daysBetweenWateringOther)) {
                Long daysSinceLastWateringOther = Duration.ofMillis(today - wateringDatesOther.get(wateringDatesOther.size() - 1)).toDays();
                progressOther = 100 - 100 * daysSinceLastWateringOther / daysBetweenWateringOther;
                if (progressOther < 0) {
                    progressOther = 0;
                }
            }
            return (int) (progressOther - progress);
        }
    }
}
