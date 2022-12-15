package com.example.arxivreader.ui.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirViewModel extends ViewModel {

    private final MutableLiveData<Map<Directory, List<Paper>>> dirMap;
    private MutableLiveData<Directory> selectedDir;
    private MutableLiveData<Paper> selectedPaper;

    public DirViewModel() {
        dirMap = new MutableLiveData<>(new HashMap<>());
        selectedDir = new MutableLiveData<>(null);
        selectedPaper = new MutableLiveData<>(null);
    }

    public MutableLiveData<Map<Directory, List<Paper>>> getDirMapLiveData() {
        return dirMap;
    }

    public Map<Directory, List<Paper>> getDirMap() {
        return dirMap.getValue();
    }

    public List<Directory> getDirs() {
        List<Directory> result = new ArrayList<>(getDirMap().keySet());
        Collections.sort(result, (dirA, dirB) -> {
            return dirA.getName().compareTo(dirB.getName());
        });
        return result;
    }

    public List<Paper> getDirPapers(int dirPosition) {
        Directory directory = getDirs().get(dirPosition);
        return getDirMap().get(directory);
    }

    public MutableLiveData<Directory> getSelectedDirLiveData(){
        return selectedDir;
    }

    public void selectDirForAdd(Directory directory){
        selectedDir.postValue(directory);
    }

    public Directory getSelectedDir() {
        return selectedDir.getValue();
    }

    public Paper getSelectedPaper(){
        return selectedPaper.getValue();
    }

    public void selectPaperForCanvas(Paper paper){
        selectedPaper.postValue(paper);
    }

    public Paper getDirSpecificPaper(int dirPosition, int paperPosition){
        return getDirPapers(dirPosition).get(paperPosition);
    }

    public MutableLiveData<Paper> getSelectedPaperLiveData(){
        return selectedPaper;
    }
}