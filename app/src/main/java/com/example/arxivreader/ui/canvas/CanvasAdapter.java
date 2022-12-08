package com.example.arxivreader.ui.canvas;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.databinding.ItemCanvasBinding;
import com.example.arxivreader.databinding.ItemDirBinding;
import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.ui.home.DirAdapter;
import com.example.arxivreader.ui.vm.CanvasViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CanvasAdapter extends RecyclerView.Adapter<CanvasAdapter.ViewHolder> {

    private CanvasViewModel viewModel;
    private LayoutInflater inflater;
    private FloatingActionButton fab;

    public CanvasAdapter(CanvasViewModel viewModel, LayoutInflater inflater, FloatingActionButton fab) {
        this.viewModel = viewModel;
        this.inflater = inflater;
        this.fab = fab;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CanvasAdapter.ViewHolder(ItemCanvasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Canvas canvas = viewModel.getCanvasList().get(position);
        holder.name.setText(canvas.getName());
        holder.deleteCanvas.setOnClickListener(v -> {
            MainActivity.dbExecute(() -> {
                MainActivity.getCanvasDao().deleteCanvas(canvas);
                viewModel.getCanvasListLiveData().postValue(MainActivity.getCanvasDao().getAllCanvas());
            });
        });

        holder.upper.setOnClickListener(v -> {
            fab.setVisibility(View.GONE);
            FragmentManager manager = ((MainActivity) v.getContext()).getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction().add(
                    PaintFragment.newInstance(canvas.getName()), null
            ).commit();
        });

        holder.upper.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View view = inflater.inflate(R.layout.fragment_dialog_canvas, null);
            builder.setView(view);
            builder.setTitle("Rename a Canvas");
            builder.setPositiveButton("Yes", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2 -> {
                String name = ((EditText) view.findViewById(R.id.new_canvas_name)).getText().toString();
                if (!"".equals(name)) {
                    MainActivity.dbExecute(() -> {
                        try {
                            MainActivity.getCanvasDao().updateCanvasName(name, canvas.getName());
                            viewModel.getCanvasListLiveData().postValue(MainActivity.getCanvasDao().getAllCanvas());
                            dialog.dismiss();
                        } catch (SQLiteConstraintException e) {
                            MainActivity.dbHandle(() -> {
                                Toast.makeText(v.getContext(), "Duplicate name for the canvas", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    Toast.makeText(v.getContext(), "Please give a name for the canvas", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return viewModel.getCanvasList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final MaterialButton deleteCanvas;
        public final ConstraintLayout upper;

        public ViewHolder(ItemCanvasBinding binding) {
            super(binding.getRoot());
            name = binding.canvasTitle;
            upper = binding.canvasUpper;
            deleteCanvas = binding.deleteCanvas;
        }
    }
}
