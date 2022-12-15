package com.example.arxivreader.ui.upload;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arxivreader.R;
import com.example.arxivreader.databinding.FragmentUploadBinding;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.paper.DialogDirFragment;

import java.util.Arrays;
import java.util.Date;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private EditText title;
    private EditText authors;
    private EditText link;
    private EditText time;
    private EditText summary;
    private EditText category;

    public UploadFragment() {
        // Required empty public constructor
    }

    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        title = binding.uploadTitle;
        authors = binding.uploadAuthors;
        link = binding.uploadLink;
        time = binding.uploadTime;
        summary = binding.uploadSummary;
        category = binding.uploadCategory;
        binding.uploadBtn.setOnClickListener(v -> {
            String titleText = title.getText().toString();
            String authorsText = authors.getText().toString();
            String linkText = link.getText().toString();
            String timeText = time.getText().toString();
            String summaryText = summary.getText().toString();
            String categoryText = category.getText().toString();

            if ("".equals(titleText) || "".equals(authorsText) || "".equals(linkText) ||
                    "".equals(timeText) || "".equals(summaryText) || "".equals(categoryText)) {
                Toast.makeText(getContext(), "Please fill all the entries above", Toast.LENGTH_SHORT).show();
            } else {
                if (!linkText.startsWith("https://") || !linkText.startsWith("http://")) {
                    linkText = "https://" + linkText;
                }
                String id = String.valueOf((titleText + authorsText + linkText).hashCode());
                Paper paper = new Paper(id, titleText, categoryText, summaryText, timeText, linkText,
                        Arrays.asList(authorsText.split(";")));
                DialogDirFragment dialogFragment = DialogDirFragment.newInstance(paper);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                dialogFragment.show(ft, "Upload Paper");
            }
        });
        return binding.getRoot();
    }
}