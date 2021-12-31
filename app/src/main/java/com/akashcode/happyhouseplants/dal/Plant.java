package com.akashcode.happyhouseplants.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Plant {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

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
}
