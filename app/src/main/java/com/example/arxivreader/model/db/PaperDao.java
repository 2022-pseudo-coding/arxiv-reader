package com.example.arxivreader.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;

import java.util.List;
import java.util.Map;

@Dao
public interface PaperDao {
    @Insert
    void insertPapers(Paper... papers);

    @Insert
    void insertDirectories(Directory... directories);

    @Query("update directory set name=:newName where directory.name=:oldName")
     void updateDirName(String newName, String oldName);

    @Query("update paper set directory=:newDirName where paper.id=:id")
    void updatePaperDir(String id, String newDirName);

    @Query("select * from directory left join paper on directory.name = paper.directory")
    Map<Directory, List<Paper>> getDirAndPapers();

    @Delete
    void deletePapers(Paper ...papers);
}
