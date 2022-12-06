package com.example.arxivreader.ui.home;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arxivreader.databinding.ItemDirBinding;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.ui.vm.DirViewModel;

import java.util.HashSet;
import java.util.Set;

// all the dirs
public class DirAdapter extends RecyclerView.Adapter<DirAdapter.ViewHolder> {

    private final DirViewModel viewModel;
    private final FragmentManager manager;
    private final Set<PaperInDirAdapter> paperAdapters = new HashSet<>();

    public DirAdapter(DirViewModel viewModel, FragmentManager manager) {
        this.viewModel = viewModel;
        this.manager = manager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDirBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Directory dir = viewModel.getDirs().get(position);
        holder.name.setText(dir.getName());
        holder.upper.setOnClickListener(v -> {
            holder.expanded = !holder.expanded;
            holder.paperList.setVisibility(holder.expanded ? View.VISIBLE : View.GONE);
        });
        holder.upper.setOnLongClickListener(v -> {
            DialogNameFragment dialogFragment = DialogNameFragment.newInstance(dir);
            FragmentTransaction ft = manager.beginTransaction();
            dialogFragment.show(ft, "Change Directory Name");
            return true;
        });

        RecyclerView paperView = holder.paperList;
        Context context = paperView.getContext();
        PaperInDirAdapter paperAdapter = new PaperInDirAdapter(viewModel, position);
        paperView.setLayoutManager(new LinearLayoutManager(context));
        paperView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        paperAdapters.add(paperAdapter);
        paperView.setAdapter(paperAdapter);
    }

    public void update() {
        for(PaperInDirAdapter temp: paperAdapters){
            temp.update();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return viewModel.getDirs().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final RecyclerView paperList;
        public final ConstraintLayout upper;
        public boolean expanded = false;

        public ViewHolder(ItemDirBinding binding) {
            super(binding.getRoot());
            name = binding.dirName;
            paperList = binding.dirPaperList;
            upper = binding.upper;
        }
    }

    protected DirViewModel getViewModel(){
        return viewModel;
    }

    protected FragmentManager getManager(){
        return manager;
    }
}