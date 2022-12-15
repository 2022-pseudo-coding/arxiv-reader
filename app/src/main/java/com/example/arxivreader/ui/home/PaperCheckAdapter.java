package com.example.arxivreader.ui.home;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.vm.DirViewModel;

public class PaperCheckAdapter extends PaperDisplayAdapter{

    private final LifecycleOwner owner;

    public PaperCheckAdapter(DirViewModel viewModel, int dirPosition, MainActivity mainActivity, LifecycleOwner owner) {
        super(viewModel, dirPosition, mainActivity);
        this.owner = owner;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        super.onBindViewHolder(holder,position);
        Paper paper = super.viewModel.getDirSpecificPaper(super.dirPosition, position);
        holder.upper.setOnClickListener(v->{
            super.viewModel.selectPaperForCanvas(paper);
        });
        super.viewModel.getSelectedPaperLiveData().observe(owner, observedPaper -> {
            if(paper.equals(observedPaper)){
                holder.upper.setBackgroundColor(Color.parseColor("#ead4af"));
            }else {
                holder.upper.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
    }
}
