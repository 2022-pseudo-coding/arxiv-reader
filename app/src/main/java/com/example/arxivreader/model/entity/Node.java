package com.example.arxivreader.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Node {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "canvas")
    private String canvas;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "location")
    private int location;
    @ColumnInfo(name = "isPlain")
    private boolean isPlain;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "parent")
    private String parent;
    @ColumnInfo(name = "level")
    private int level;

    public Node(){}

    public Node(String canvas, String color, boolean isPlain, String title, Node parent, int location) {
        this.canvas = canvas;
        this.location = location;
        this.color = color;
        this.isPlain = isPlain;
        this.title = title;
        if(parent == null){
            this.level = 0;
            this.parent = "";
        }else{
            this.level = parent.getLevel() + 1;
            this.parent = parent.getId();
        }
        this.id = String.valueOf((canvas + title + this.parent).hashCode());
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setCanvas(String canvas) {
        this.canvas = canvas;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getCanvas() {
        return canvas;
    }

    public String getTitle() {
        return title;
    }

    public String getParent() {
        return parent;
    }

    public void setPlain(boolean plain) {
        isPlain = plain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isPlain() {
        return isPlain;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
