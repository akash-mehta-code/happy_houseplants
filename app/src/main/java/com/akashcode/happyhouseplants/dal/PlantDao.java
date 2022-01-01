package com.akashcode.happyhouseplants.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlantDao {
    @Query("SELECT * FROM plant")
    List<Plant> listPlants();

    @Insert
    void addPlant(Plant plant);

    @Delete
    void deletePlant(Plant plant);

    @Query("SELECT * FROM plant WHERE name = :plantName")
    Plant getPlant(String plantName);

    @Update
    void updatePlant(Plant plant);
}
