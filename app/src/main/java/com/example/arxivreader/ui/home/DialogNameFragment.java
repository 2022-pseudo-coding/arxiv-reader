package com.example.arxivreader.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.vm.DirViewModel;

public class DialogNameFragment extends DialogFragment {

    private TextView newName;
    private final Directory directory;

    public DialogNameFragment(Directory directory) {
        this.directory = directory;
    }

    public static DialogNameFragment newInstance(Directory directory) {
        return new DialogNameFragment(directory);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater i = getLayoutInflater();
        View view = i.inflate(R.layout.fragment_dialog_name, null);

        builder.setView(view);

        builder.setTitle("New Directory Name")
                .setPositiveButton("OK", (dialog, which) -> {
                });
        if (directory !=null){
            builder.setNegativeButton("DELETE", (dialog, which) -> {
            });
        }
        newName = view.findViewById(R.id.add_dir_name);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        DirViewModel viewModel = new ViewModelProvider(requireActivity()).get(DirViewModel.class);
        if (dialog != null) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            if(directory !=null){
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                    MainActivity.dbExecute(()->{
                        MainActivity.getPaperDao().deleteDirs(directory);
                        viewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());
                        dismiss();
                    });
                });
            }

            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = this.newName.getText().toString();
                if ("".equals(title)) {
                    // empty
                    Toast.makeText(getContext(), "You should fill the name", Toast.LENGTH_SHORT).show();
                } else {
                    // valid
                    if (directory == null) {
                        // insert
                        Directory dir = new Directory(title);
                        MainActivity.dbExecute(() -> {
                            try {
                                MainActivity.getPaperDao().insertDirectories(dir);
                                viewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());
                                dismiss();
                            } catch (SQLiteConstraintException e) {
                                MainActivity.dbHandle(() -> {
                                    Toast.makeText(getContext(), "Cannot create directory: repeated name", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    } else {
                        // update
                        MainActivity.dbExecute(() -> {
                            try {
                                MainActivity.getPaperDao().updateDirName(title, directory.getName());
                                for (Paper paper : viewModel.getDirMap().get(directory)) {
                                    MainActivity.getPaperDao().updatePaperDir(paper.getId(), title);
                                }
                                viewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());
                                dismiss();
                            } catch (SQLiteConstraintException e) {
                                MainActivity.dbHandle(() -> {
                                    Toast.makeText(getContext(), "Cannot alter the directory's name: repeated name", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                }
            });
        }
    }
}