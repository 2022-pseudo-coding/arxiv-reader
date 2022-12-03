package com.example.arxivreader.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;

@Database(entities = {Paper.class,
        Directory.class}, version = 5, exportSchema = false)
@TypeConverters({ListConverter.class})
public abstract class PaperDatabase extends RoomDatabase {
    public abstract PaperDao dao();
}
