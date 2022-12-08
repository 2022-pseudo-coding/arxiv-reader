package com.example.arxivreader.ui.canvas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.databinding.FragmentCanvasBinding;
import com.example.arxivreader.model.entity.Canvas;
import com.example.arxivreader.ui.vm.CanvasViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CanvasFragment extends Fragment {

    private CanvasViewModel canvasViewModel;
    private FragmentCanvasBinding binding;
    private FloatingActionButton addButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        canvasViewModel = new ViewModelProvider(requireActivity()).get(CanvasViewModel.class);

        binding = FragmentCanvasBinding.inflate(inflater, container, false);
        addButton = binding.addCanvas;
        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View view = getLayoutInflater().inflate(R.layout.fragment_dialog_canvas, null);
            builder.setView(view);
            builder.setTitle("Create a New Canvas");
            builder.setPositiveButton("Yes", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2->{
                String name = ((EditText)view.findViewById(R.id.new_canvas_name)).getText().toString();
                if(!"".equals(name)){
                    MainActivity.dbExecute(()->{
                        try{
                            MainActivity.getCanvasDao().insertCanvas(new Canvas(name));
                            canvasViewModel.getCanvasListLiveData().postValue(MainActivity.getCanvasDao().getAllCanvas());
                            dialog.dismiss();
                        }catch (SQLiteConstraintException e){
                            MainActivity.dbHandle(()->{
                                Toast.makeText(getContext(), "Duplicate name for the canvas", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Please give a name for the canvas", Toast.LENGTH_SHORT).show();
                }
            });
        });

        RecyclerView recyclerView = binding.canvasList;
        CanvasAdapter adapter = new CanvasAdapter(canvasViewModel, getLayoutInflater(), addButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        canvasViewModel.getCanvasListLiveData().observe(getViewLifecycleOwner(), canvas -> {
            adapter.notifyDataSetChanged();
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}