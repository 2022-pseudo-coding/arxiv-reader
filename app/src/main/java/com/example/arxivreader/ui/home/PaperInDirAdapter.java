package com.example.arxivreader.ui.home;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.databinding.ItemPaperDirBinding;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.vm.DirViewModel;
import com.google.android.material.button.MaterialButton;

// papers in a dir
public class PaperInDirAdapter extends RecyclerView.Adapter<PaperInDirAdapter.ViewHolder>{

    private DirViewModel viewModel;
    private int dirPosition;
    private MainActivity mainActivity;

    public PaperInDirAdapter(DirViewModel viewModel, int dirPosition, MainActivity mainActivity){
        this.viewModel = viewModel;
        this.mainActivity = mainActivity;
        this.dirPosition = dirPosition;
    }

    public void update(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaperInDirAdapter.ViewHolder(ItemPaperDirBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paper paper = viewModel.getDirPapers(dirPosition).get(position);
        String title = paper.getTitle();
        holder.category.append(paper.getCategory());
        holder.authors.append(paper.getAuthorsFull());
        holder.title.setText(title);
        holder.summary.append(paper.getSummary());
        holder.published.append(paper.getPublished());

        holder.go.setOnClickListener(v->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paper.getLink()));
            mainActivity.startActivity(browserIntent);
        });

        holder.upper.setOnLongClickListener(v->{
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.upper);
            ClipData data = new ClipData(
                    paper.getId(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                    new ClipData.Item(paper.getId())
            );
            v.startDragAndDrop(data, shadowBuilder, null, 0);
            return true;
        });

        holder.delete.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Delete " + title);
            builder.setPositiveButton("Yes", (dialog, which)->{
                MainActivity.dbExecute(()->{
                    MainActivity.getPaperDao().deletePapers(paper);
                    viewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());
                    MainActivity.dbHandle(()->{
                        Toast.makeText(builder.getContext(), title + " has been deleted", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    });
                });

            });
            builder.setNegativeButton("No", (dialog, which)->{
                dialog.dismiss();
            });
            builder.create().show();
        });

        holder.upper.setOnClickListener(v->{
            holder.expanded = !holder.expanded;
            holder.details.setVisibility(holder.expanded ? View.VISIBLE: View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return viewModel.getDirPapers(dirPosition).size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView authors;
        public final TextView category;
        public final TextView published;
        public final TextView summary;
        public final MaterialButton go;
        private final MaterialButton delete;
        public final ConstraintLayout upper;
        public final ConstraintLayout details;
        public boolean expanded = false;

        public ViewHolder(ItemPaperDirBinding binding){
            super(binding.getRoot());
            title = binding.paperDirTitle;
            authors = binding.paperDirAuthors;
            category = binding.paperDirCategory;
            published = binding.paperDirPublished;
            summary = binding.paperDirSummary;
            go = binding.go;
            upper = binding.paperDirUpper;
            details = binding.paperDirDetails;
            delete = binding.deletePaper;
        }
    }
}
