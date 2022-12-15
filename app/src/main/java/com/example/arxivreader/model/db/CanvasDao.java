package com.example.arxivreader.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.arxivreader.model.entity.Node;
import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.model.entity.Paper;

import java.util.List;
import java.util.Map;

@Dao
public interface CanvasDao {

    @Insert
    void insertCanvas(Canvas... canvas);

    @Delete
    void deleteCanvas(Canvas... canvas);

    @Query("select * from canvas")
    List<Canvas> getAllCanvas();

    @Query("update canvas set name=:newName where canvas.name=:oldName")
    void updateCanvasName(String newName, String oldName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNodes(Node... nodes);

    @Query("select * from node where node.canvas=:canvas")
    List<Node> getAllNodesFromCanvas(String canvas);
}
