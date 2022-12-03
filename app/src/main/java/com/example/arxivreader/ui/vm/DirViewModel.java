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

    public DirViewModel() {
//        Map<Directory, List<Paper>> test = new HashMap<>();
//        test.put(new Directory("dir1"), Arrays.asList(
//                new Paper("id1", "title1", "cat1", "sum1", "pub1", "link1", Arrays.asList("auth1", "auth2")),
//                new Paper("id2", "title2", "cat2", "sum2", "pub2", "link2", Arrays.asList("auth1", "auth2"))));
//        test.put(new Directory("dir2"), Arrays.asList(
//                new Paper("id3", "title3", "cat3", "sum3", "pub3", "link3", Arrays.asList("auth1", "auth2")),
//                new Paper("id4", "title4", "cat4", "sum4", "pub4", "link4", Arrays.asList("auth1", "auth2"))));
//
//        dirMap = new MutableLiveData<>(test);
        dirMap = new MutableLiveData<>(new HashMap<>());
        selectedDir = new MutableLiveData<>(null);
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
}