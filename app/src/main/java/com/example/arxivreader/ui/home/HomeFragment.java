package com.example.arxivreader.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.databinding.FragmentHomeBinding;
import com.example.arxivreader.ui.vm.DirViewModel;

public class HomeFragment extends Fragment {

    private DirViewModel dirViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dirViewModel = new ViewModelProvider(requireActivity()).get(DirViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        /**
         * dir list
         */
        RecyclerView recyclerView = binding.dirList;

        Context context = recyclerView.getContext();
        LifecycleOwner owner = getViewLifecycleOwner();
        DirAdapter dirAdapter = new DirAdapter(dirViewModel, getFragmentManager());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(dirAdapter);

        dirViewModel.getDirMapLiveData().observe(owner, map->{
            dirAdapter.update();
        });

        /**
         * add btn
         */
        binding.dirAddBtn.setOnClickListener(v->{
            DialogNameFragment dialogFragment = DialogNameFragment.newInstance(null);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialogFragment.show(ft, "New Directory");
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}