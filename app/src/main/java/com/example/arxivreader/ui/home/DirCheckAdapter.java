package com.example.arxivreader.ui.home;

import android.graphics.Color;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;


import com.example.arxivreader.MainActivity;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.ui.vm.DirViewModel;

public class DirCheckAdapter extends DirDisplayAdapter {

    private LifecycleOwner owner;

    public DirCheckAdapter(DirViewModel viewModel, FragmentManager manager, LifecycleOwner owner, MainActivity mainActivity) {
        super(viewModel, manager, mainActivity);
        this.owner = owner;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Directory dir = getViewModel().getDirs().get(position);
        holder.name.setText(dir.getName());
        holder.upper.setOnClickListener(v -> {
            getViewModel().selectDirForAdd(dir);
        });
        holder.upper.setOnLongClickListener(v -> {
            DialogNameFragment dialogFragment = DialogNameFragment.newInstance(dir);
            FragmentTransaction ft = getManager().beginTransaction();
            dialogFragment.show(ft, "Change Directory Name");
            return true;
        });
        getViewModel().getSelectedDirLiveData().observe(owner, directory -> {
            if(dir.equals(directory)){
                holder.upper.setBackgroundColor(Color.parseColor("#ead4af"));
            }else {
                holder.upper.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
    }

}
