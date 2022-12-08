package com.example.arxivreader.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Canvas {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    public Canvas(@NonNull String name){
        this.name = name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Canvas canvas = (Canvas) o;
        return name.equals(canvas.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
