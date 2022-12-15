package com.example.arxivreader.ui.vm;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.model.entity.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.jagar.mindmappingandroidlibrary.Views.Item;

public class CanvasViewModel extends ViewModel {

    private final MutableLiveData<List<Canvas>> canvasListLiveData;
    private final MutableLiveData<Map<Node, Item>> nodesMapLiveData;

    public CanvasViewModel() {
        canvasListLiveData = new MutableLiveData<>(new ArrayList<>());
        nodesMapLiveData = new MutableLiveData<>(new HashMap<>());
    }

    public MutableLiveData<List<Canvas>> getCanvasListLiveData() {
        return canvasListLiveData;
    }

    public List<Canvas> getCanvasList() {
        return canvasListLiveData.getValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Node getItemNode(Item item){
        return nodesMapLiveData.getValue().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Item findNodeParentItem(Node node){
        Node parent = nodesMapLiveData.getValue().keySet().stream().filter(temp->node.getParent().equals(temp.getId())).findAny().orElse(null);
        return nodesMapLiveData.getValue().get(parent);
    }

    public void addNodeItem(Node node, Item item) {
        Map<Node, Item> nodeItemMap = nodesMapLiveData.getValue();
        nodeItemMap.put(node, item);
        nodesMapLiveData.setValue(nodeItemMap);
    }

    public Set<Node> getAllNodesForSaving() {
        return nodesMapLiveData.getValue().keySet();
    }

    public void postNodes(List<Node> nodes){
        Map<Node, Item> nodeItemMap = nodesMapLiveData.getValue();
        for(Node node:nodes){
            nodeItemMap.put(node, null);
        }
        nodesMapLiveData.postValue(nodeItemMap);
    }
}