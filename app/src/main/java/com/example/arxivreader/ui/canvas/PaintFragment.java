package com.example.arxivreader.ui.canvas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.databinding.FragmentPaintBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaintFragment extends Fragment {

    private String title;
    FragmentPaintBinding binding;

    public PaintFragment() {
    }

    public static PaintFragment newInstance(String title) {
        PaintFragment fragment = new PaintFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.getSupportActionBar().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaintBinding.inflate(inflater, container, false);
        binding.addNodeBtn.setOnClickListener(v->{
            Toast.makeText(getContext(), "123123", Toast.LENGTH_SHORT).show();
        });
        return binding.getRoot();
    }
}