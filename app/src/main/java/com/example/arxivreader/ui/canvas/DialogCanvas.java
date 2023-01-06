package com.example.arxivreader.ui.canvas;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.model.entity.Node;
import com.example.arxivreader.ui.home.DirDisplayAdapter;
import com.example.arxivreader.ui.vm.CanvasViewModel;
import com.example.arxivreader.ui.vm.DirViewModel;

import me.jagar.mindmappingandroidlibrary.Views.Item;
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation;

public abstract class DialogCanvas extends DialogFragment {

    protected final Context paintContext;
    protected final PaintFragment paintFragment;
    protected final int resource;
    protected CanvasViewModel canvasViewModel;
    protected final String title;
    protected View root;
    protected final Item parent;

    public DialogCanvas(Context paintContext, PaintFragment paintFragment, int resource, String title, Item parent) {
        this.paintContext = paintContext;
        this.parent = parent;
        this.paintFragment = paintFragment;
        this.resource = resource;
        this.title = title;
    }

    public abstract View.OnClickListener update();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater i = getLayoutInflater();
        root = i.inflate(resource, null);

        builder.setView(root);

        builder.setTitle(title).setPositiveButton("OK", (dialog, which) -> {
        }).setNegativeButton("NO", (dialog, which) -> {
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        canvasViewModel = new ViewModelProvider(requireActivity()).get(CanvasViewModel.class);
        if (dialog != null) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(update());
        }
    }

    /**
     * NODE
     */
    public static class DialogNode extends DialogCanvas {
        public DialogNode(Context paintContext, PaintFragment paintFragment, Item item) {
            super(paintContext, paintFragment, R.layout.canvas_node, "Add a Plain Node", item);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View.OnClickListener update() {
            return l -> {
                String nodeName = ((EditText) root.findViewById(R.id.canvas_node_edit)).getText().toString();
                if ("".equals(nodeName)) {
                    Toast.makeText(paintContext, "You must give a name for the node", Toast.LENGTH_SHORT).show();
                } else {
                    Item curr = paintFragment.addNode(parent, nodeName, "plain", ItemLocation.BOTTOM, -350, -500, "#ffffff");
                    paintFragment.getCanvasViewModel().addNodeItem(new Node(paintFragment.getCanvasTitle(),
                            "#ffffff", "plain",
                            nodeName, paintFragment.getCanvasViewModel().getItemNode(parent), ItemLocation.BOTTOM), curr);
                    dismiss();
                }
            };
        }
    }

    /**
     * NOTE
     */
    public static class DialogNote extends DialogCanvas {
        public DialogNote(Context paintContext, PaintFragment paintFragment, Item item) {
            super(paintContext, paintFragment, R.layout.canvas_note, "Add a Note Node", item);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View.OnClickListener update() {
            return l -> {
                String noteContent = ((EditText) root.findViewById(R.id.canvas_note_edit)).getText().toString();
                if ("".equals(noteContent)) {
                    Toast.makeText(paintContext, "You must write something for the note", Toast.LENGTH_SHORT).show();
                } else {
                    Item curr = paintFragment.addNode(parent, noteContent, "note", ItemLocation.RIGHT, 100, -800, "#d5c3fe");
                    paintFragment.getCanvasViewModel().addNodeItem(new Node(paintFragment.getCanvasTitle(),
                            "#d5c3fe", "note",
                            noteContent, paintFragment.getCanvasViewModel().getItemNode(parent), ItemLocation.RIGHT), curr);
                    dismiss();
                }
            };
        }
    }

    /**
     * PAPER
     */
    public static class DialogPaper extends DialogCanvas {

        private final LifecycleOwner owner;
        private DirViewModel dirViewModel;
        private final int location;

        public DialogPaper(Context paintContext, PaintFragment paintFragment, LifecycleOwner owner, Item item, int location) {
            super(paintContext, paintFragment, R.layout.fragment_home, "Add a Paper Node", item);
            this.owner = owner;
            this.location = location;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View.OnClickListener update() {
            return l -> {
                if (dirViewModel.getSelectedPaper() == null) {
                    Toast.makeText(paintContext, "You must select a paper", Toast.LENGTH_SHORT).show();
                } else {
                    String title = dirViewModel.getSelectedPaper().getTitle();
                    int x = (location == ItemLocation.BOTTOM) ? -350 : 100;
                    int y = (location == ItemLocation.BOTTOM) ? -500 : -800;
                    Item curr = paintFragment.addNode(parent, title, "paper", location, x, y, "#c3fef3");
                    paintFragment.getCanvasViewModel().addNodeItem(new Node(paintFragment.getCanvasTitle(),
                            "#c3fef3", "paper",
                            title, paintFragment.getCanvasViewModel().getItemNode(parent), location), curr);
                    dismiss();
                }
            };
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            dirViewModel = new ViewModelProvider(requireActivity()).get(DirViewModel.class);

            LayoutInflater i = getLayoutInflater();
            root = i.inflate(resource, null);

            // no add btn
            root.findViewById(R.id.dir_add_btn).setVisibility(View.GONE);

            // adapter
            RecyclerView recyclerView = root.findViewById(R.id.dir_list);
            Context context = recyclerView.getContext();
            DirDisplayAdapter dirAdapter = new DirDisplayAdapter(dirViewModel, getFragmentManager(), (MainActivity) requireActivity(), owner);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(dirAdapter);

            dirViewModel.getDirMapLiveData().observe(this, map -> {
                dirAdapter.update();
            });

            builder.setView(root);

            builder.setTitle(super.title).setPositiveButton("OK", (dialog, which) -> {
            });
            return builder.create();
        }
    }
}
