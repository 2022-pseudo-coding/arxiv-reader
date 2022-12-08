package com.example.arxivreader.ui.paper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.databinding.ItemPaperSearchBinding;
import com.example.arxivreader.model.entity.Paper;
import com.google.android.material.button.MaterialButton;

import java.util.List;

// papers in the search
public class PaperInSearchAdapter extends RecyclerView.Adapter<PaperInSearchAdapter.ViewHolder> {

    private List<Paper> papers;
    private final FragmentManager manager;
    private final MainActivity mainActivity;

    public PaperInSearchAdapter(List<Paper> papers, FragmentManager manager, MainActivity mainActivity) {
        this.papers = papers;
        this.mainActivity = mainActivity;
        this.manager = manager;
    }

    public void update(List<Paper> papers){
        this.papers = papers;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPaperSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Paper item = papers.get(position);
        holder.title.setText(item.getTitle());
        holder.authors.setText(item.getAuthorsLimited());
        holder.category.setText(item.getCategory());
        holder.collect.setOnClickListener(v -> {
            DialogDirFragment dialogFragment = DialogDirFragment.newInstance(item);
            FragmentTransaction ft = manager.beginTransaction();
            Fragment prev = manager.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            dialogFragment.show(ft, "New Paper");
        });

        holder.go.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
            mainActivity.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return papers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView authors;
        public final TextView category;
        public final MaterialButton collect;
        public final MaterialButton go;

        public ViewHolder(ItemPaperSearchBinding binding) {
            super(binding.getRoot());
            title = binding.paperTitle;
            authors = binding.paperAuthors;
            category = binding.paperCategory;
            collect = binding.collect;
            go = binding.go;
        }
    }
}