package com.example.arxivreader.ui.paper;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.model.entity.Directory;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.home.DialogNameFragment;
import com.example.arxivreader.ui.home.DirCheckAdapter;
import com.example.arxivreader.ui.vm.DirViewModel;

public class DialogDirFragment extends DialogFragment {

    private DirViewModel dirViewModel;
    private View rootView;
    private Paper paper;

    public DialogDirFragment(Paper paper) {
        this.paper = paper;
    }

    public static DialogDirFragment newInstance(Paper paper) {
        return new DialogDirFragment(paper);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dirViewModel = new ViewModelProvider(requireActivity()).get(DirViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater i = getLayoutInflater();
        rootView = i.inflate(R.layout.fragment_home, null);
        builder.setView(rootView);

        RecyclerView recyclerView = rootView.findViewById(R.id.dir_list);
        Context context = recyclerView.getContext();
        DirCheckAdapter dirAdapter = new DirCheckAdapter(dirViewModel, getFragmentManager(), this, (MainActivity) requireActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(dirAdapter);

        dirViewModel.getDirMapLiveData().observe(this, map -> {
            dirAdapter.update();
        });
        rootView.findViewById(R.id.dir_add_btn).setOnClickListener(v -> {
            DialogNameFragment dialogFragment = DialogNameFragment.newInstance(null);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialogFragment.show(ft, "New Directory");
        });

        builder.setTitle("Save a paper")
                .setPositiveButton("Save", (dialog, which) -> {
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dirViewModel.getSelectedDirLiveData().setValue(null);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Directory toSave = dirViewModel.getSelectedDir();
                if (toSave == null) {
                    Toast.makeText(getContext(), "Please select a directory first", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.dbExecute(() -> {
                        try {
                            paper.setDirectory(toSave.getName());
                            MainActivity.getPaperDao().insertPapers(paper);
                            dirViewModel.getDirMapLiveData().postValue(MainActivity.getPaperDao().getDirAndPapers());

                            MainActivity.dbHandle(() -> {
                                Toast.makeText(getContext(), "Paper successfully added", Toast.LENGTH_SHORT).show();
                                dismiss();
                            });
                        } catch (SQLiteConstraintException e) {
                            MainActivity.dbHandle(() -> {
                                Toast.makeText(getContext(), "This paper has been saved already", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            });
        }
    }
}