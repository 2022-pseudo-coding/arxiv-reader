package com.example.arxivreader.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Node {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "canvas")
    private String canvas;

    public Node(@NonNull String id, String canvas){
        this.id = id;
        this.canvas = canvas;
    }

    public String getCanvas() {
        return canvas;
    }

    public void setCanvas(String canvas) {
        this.canvas = canvas;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", canvas='" + canvas + '\'' +
                '}';
    }
}
