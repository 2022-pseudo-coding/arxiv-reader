package com.example.arxivreader.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.model.entity.Node;
import com.example.arxivreader.model.utils.ListConverter;

@Database(entities = {Paper.class,
        Directory.class, Canvas.class, Node.class}, version = 10, exportSchema = false)
@TypeConverters({ListConverter.class})
public abstract class PaperDatabase extends RoomDatabase {
    public abstract PaperDao paperDao();
    public abstract CanvasDao canvasDao();
}
