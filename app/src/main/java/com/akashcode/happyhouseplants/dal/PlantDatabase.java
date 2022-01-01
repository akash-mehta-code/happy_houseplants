package com.akashcode.happyhouseplants.dal;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Objects;

@Database(
        entities = {Plant.class},
        version = 1)
@TypeConverters(Plant.DatesTypeConverter.class)
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();

    private static PlantDatabase INSTANCE;

    public static PlantDatabase getInstance(Context context) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PlantDatabase.class, "Plant Database")
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
