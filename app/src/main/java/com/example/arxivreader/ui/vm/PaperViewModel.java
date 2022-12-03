package com.example.arxivreader.ui.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;

import java.util.ArrayList;
import java.util.List;

public class PaperViewModel extends ViewModel {

    private final MutableLiveData<List<Paper>> papersLiveData;

    public PaperViewModel() {
        papersLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<List<Paper>> getPapersLiveData() {
        return papersLiveData;
    }

    public List<Paper> getPapers() {
        return papersLiveData.getValue();
    }
}