package com.example.arxivreader.ui.paper;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arxivreader.R;
import com.example.arxivreader.ui.vm.PaperViewModel;

/**
 * A fragment representing a list of Items.
 */
public class PaperFragment extends Fragment {

    PaperViewModel paperViewModel;

    public PaperFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView =(RecyclerView) inflater.inflate(R.layout.fragment_paper_list, container, false);
        paperViewModel = new ViewModelProvider(requireActivity()).get(PaperViewModel.class);

        Context context = recyclerView.getContext();
        PaperInSearchAdapter adapter = new PaperInSearchAdapter(paperViewModel.getPapers(), getFragmentManager());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        paperViewModel.getPapersLiveData().observe(getViewLifecycleOwner(), adapter::update);
        return recyclerView;
    }
}