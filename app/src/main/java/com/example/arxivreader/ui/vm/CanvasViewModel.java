package com.example.arxivreader.ui.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasViewModel extends ViewModel {

    private MutableLiveData<List<Canvas>> canvasListLiveData;

    public CanvasViewModel() {
        canvasListLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<List<Canvas>> getCanvasListLiveData() {
        return canvasListLiveData;
    }

    public List<Canvas> getCanvasList(){
        return canvasListLiveData.getValue();
    }

    // TODO: 2022/12/7 convert db stru to view stru

}