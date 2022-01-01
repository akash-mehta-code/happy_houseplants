package com.akashcode.happyhouseplants.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private Integer daysBetweenWatering = null;

    @ColumnInfo(name = "wateringDates")
    private List<Long> wateringDates = new ArrayList<>();

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

    public Integer getDaysBetweenWatering() {
        return daysBetweenWatering;
    }

    public void setDaysBetweenWatering(Integer daysBetweenWatering) {
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
}
