package com.example.arxivreader.ui.home;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.databinding.ItemDirBinding;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.ui.vm.DirViewModel;

import java.util.HashSet;
import java.util.Set;

// all the dirs
public class DirDisplayAdapter extends RecyclerView.Adapter<DirDisplayAdapter.ViewHolder> {

    private final DirViewModel viewModel;
    private final FragmentManager manager;
    private final MainActivity mainActivity;
    private final Set<PaperDisplayAdapter> paperAdapters = new HashSet<>();
    protected boolean displayPaper;
    private LifecycleOwner owner;

    public DirDisplayAdapter(DirViewModel viewModel, FragmentManager manager, MainActivity mainActivity) {
        this.viewModel = viewModel;
        this.manager = manager;
        this.mainActivity = mainActivity;
        this.displayPaper = true;
    }

    public DirDisplayAdapter(DirViewModel viewModel, FragmentManager manager, MainActivity mainActivity, LifecycleOwner owner) {
        this.viewModel = viewModel;
        this.manager = manager;
        this.mainActivity = mainActivity;
        this.owner = owner;
        this.displayPaper = false;
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
        holder.upper.setOnDragListener((v, e) -> {
            switch (e.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (e.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.parseColor("#ead4af"));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.parseColor("#ffffff"));
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = e.getClipData().getItemAt(0);
                    CharSequence dragData = item.getText();
                    MainActivity.dbExecute(()->{
                        MainActivity.getPaperDao().updatePaperDir(dragData.toString(), dir.getName());
                        viewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());
                        MainActivity.dbHandle(()->{
                            Toast.makeText(v.getContext(), "Paper moved to " + dir.getName(), Toast.LENGTH_SHORT).show();
                        });
                    });
                    v.invalidate();
                    return true;
            }
            return false;
        });

        RecyclerView paperView = holder.paperList;
        Context context = paperView.getContext();
        PaperDisplayAdapter paperAdapter;
        if(this.displayPaper){
            paperAdapter = new PaperDisplayAdapter(viewModel, position, mainActivity);
        }else{
            paperAdapter = new PaperCheckAdapter(viewModel, position, mainActivity, owner);
        }
        paperView.setLayoutManager(new LinearLayoutManager(context));
        paperView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        paperAdapters.add(paperAdapter);
        paperView.setAdapter(paperAdapter);
    }

    public void update() {
        for (PaperDisplayAdapter temp : paperAdapters) {
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

    protected DirViewModel getViewModel() {
        return viewModel;
    }

    protected FragmentManager getManager() {
        return manager;
    }
}