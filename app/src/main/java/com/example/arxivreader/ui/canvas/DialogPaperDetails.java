package com.example.arxivreader.ui.canvas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.arxivreader.R;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.vm.CanvasViewModel;

public class DialogPaperDetails extends DialogFragment {

    private View root;
    private final Paper paper;

    public DialogPaperDetails(Paper paper) {
        this.paper = paper;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater i = getLayoutInflater();
        root = i.inflate(R.layout.canvas_paper_details, null);

        TextView title = root.findViewById(R.id.paper_canvas_title);
        title.append(paper.getTitle());
        TextView authors = root.findViewById(R.id.paper_canvas_authors);
        authors.append(paper.getAuthorsFull());
        TextView cat = root.findViewById(R.id.paper_canvas_category);
        cat.append(paper.getCategory());
        TextView pub = root.findViewById(R.id.paper_canvas_published);
        pub.append(paper.getPublished());
        TextView sum = root.findViewById(R.id.paper_canvas_summary);
        sum.append(paper.getSummary());
        builder.setView(root);
        builder.setTitle("Details of Paper").setPositiveButton("OPEN IN BROWSER", (dialog, which) -> {
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v->{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paper.getLink()));
                getActivity().startActivity(browserIntent);
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 900;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

}
