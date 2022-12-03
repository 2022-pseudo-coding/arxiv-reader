package com.example.arxivreader.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Paper {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "summary")
    private String summary;

    @ColumnInfo(name = "published")
    private String published;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "authors")
    private List<String> authors;

    @ColumnInfo(name = "directory")
    private String directory;

    public Paper(@NonNull String id, String title, String category, String summary, String published, String link, List<String> authors) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.published = published;
        this.link = link;
        this.authors = authors;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getAuthorsLimited() {
        StringBuilder result = new StringBuilder();
        int cnt = 0;
        String sep = ", ";
        for (String author:authors){
            result.append(author).append(sep);
            cnt ++;
            if(cnt == 3){
                result.append("...").append(sep);
                break;
            }
        }
        return result.substring(0, result.length() - sep.length());
    }

    public String getAuthorsFull() {
        StringBuilder result = new StringBuilder();
        String sep = ", ";
        for (String author:authors){
            result.append(author).append(sep);
        }
        return result.substring(0, result.length() - sep.length());
    }

    public String getPublished() {
        return published;
    }

    public String getLink() {
        return link;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for(String author:authors){
            temp.append(author).append("/");
        }
        return "Paper{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", summary='" + summary + '\'' +
                ", published='" + published + '\'' +
                ", link='" + link + '\'' +
                ", authors=" + temp.toString() +
                '}';
    }
}
