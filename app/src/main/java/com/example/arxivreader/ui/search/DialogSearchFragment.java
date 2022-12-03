package com.example.arxivreader.ui.search;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;

import com.example.arxivreader.network.SearchService;
import com.example.arxivreader.ui.vm.PaperViewModel;

import java.util.HashMap;
import java.util.Map;

public class DialogSearchFragment extends DialogFragment {

    private SearchService searchService;
    private PaperViewModel paperViewModel;
    private EditText title;
    private EditText author;
    private EditText summary;
    private CheckBox ifAnd;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            searchService = ((SearchService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            searchService = null;
        }
    };


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static DialogSearchFragment newInstance() {
        return new DialogSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paperViewModel = new ViewModelProvider(requireActivity()).get(PaperViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        final MainActivity activity = (MainActivity) getActivity();
        final Intent serviceIntent = new Intent(activity, SearchService.class);
        activity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        LayoutInflater i = getLayoutInflater();
        View view = i.inflate(R.layout.fragment_dialog_search, null);
        builder.setView(view);

        builder.setTitle("Search papers here")
                .setPositiveButton("Search", (dialog, which) -> {
                });
        title = view.findViewById(R.id.search_title);
        author = view.findViewById(R.id.search_author);
        summary = view.findViewById(R.id.search_abstract);
        ifAnd = view.findViewById(R.id.search_if_and);
        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog != null){
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v->{
                String title = this.title.getText().toString();
                String author = this.author.getText().toString();
                String summary = this.summary.getText().toString();
                if(isStrEmpty(title) && isStrEmpty(author) && isStrEmpty(summary)){
                    // invalid
                    Toast.makeText(getContext(), "You should at least fill one entry", Toast.LENGTH_SHORT).show();
                }else {
                    // valid
                    Map<String, String> searchParams = new HashMap<>();
                    putIfNotEmpty(searchParams,"ti", title);
                    putIfNotEmpty(searchParams,"au", author);
                    putIfNotEmpty(searchParams,"abs", summary);
                    searchService.search(searchParams, ifAnd.isChecked(), paperViewModel);
                    dismiss();
                }
            });
        }
    }

    private boolean isStrEmpty(String str){
        return str == null || str.length() == 0;
    }

    private void putIfNotEmpty(Map<String, String> map, String key, String val){
        if(!isStrEmpty(val)){
            map.put(key, val);
        }
    }
}