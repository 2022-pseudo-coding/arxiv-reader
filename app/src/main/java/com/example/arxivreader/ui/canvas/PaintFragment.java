package com.example.arxivreader.ui.canvas;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arxivreader.MainActivity;
import com.example.arxivreader.R;
import com.example.arxivreader.databinding.FragmentPaintBinding;
import com.example.arxivreader.model.entity.Node;
import com.example.arxivreader.ui.vm.CanvasViewModel;

import java.util.List;

import me.jagar.mindmappingandroidlibrary.Views.Item;
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation;
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView;


public class PaintFragment extends Fragment {

    private String canvasTitle;
    private MindMappingView mindMappingView;
    private FragmentPaintBinding binding;
    private CanvasViewModel canvasViewModel;

    public CanvasViewModel getCanvasViewModel() {
        return canvasViewModel;
    }

    public String getCanvasTitle() {
        return canvasTitle;
    }

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
            canvasTitle = getArguments().getString("title");
        }
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.getSupportActionBar().setTitle(canvasTitle);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        canvasViewModel = new ViewModelProvider(requireActivity()).get(CanvasViewModel.class);
        binding = FragmentPaintBinding.inflate(inflater, container, false);
        binding.saveCanvasBtn.setOnClickListener(v -> {
            // save canvas
            MainActivity.dbExecute(() -> {
                MainActivity.getCanvasDao().insertNodes(canvasViewModel.getAllNodesForSaving().toArray(new Node[0]));
                MainActivity.dbHandle(() -> {
                    Toast.makeText(getContext(), "Canvas Successfully Saved", Toast.LENGTH_SHORT).show();
                });
            });
        });
        mindMappingView = binding.mindMapping;
        MainActivity.dbExecute(() -> {
            List<Node> allNodes = MainActivity.getCanvasDao().getAllNodesFromCanvas(canvasTitle);
            canvasViewModel.postNodes(allNodes);
            if (allNodes.isEmpty()) {
                MainActivity.dbHandle(() -> {
                    Item root = genItem(canvasTitle, true, "#ffffff");
                    mindMappingView.addCentralItem(root, true);
                    root.setY(-1000);
                    root.setX(-400);
                    canvasViewModel.addNodeItem(new Node(canvasTitle, "#ffffff", true, canvasTitle, null, 0), root);
                });
            } else {
                MainActivity.dbHandle(() -> {
                    int maxLevel = 0;
                    for (Node temp : allNodes) {
                        maxLevel = Math.max(temp.getLevel(), maxLevel);
                    }
                    for (int level = 0; level < maxLevel + 1; level++) {
                        for (Node curr : allNodes) {
                            if (curr.getLevel() == level) {
                                if (level == 0) {
                                    // root
                                    Item root = genItem(curr.getTitle(), curr.isPlain(), curr.getColor());
                                    mindMappingView.addCentralItem(root, true);
                                    root.setY(-1000);
                                    root.setX(-400);
                                    canvasViewModel.addNodeItem(curr, root);
                                } else {
                                    // regular
                                    int x, y;
                                    if (curr.getLocation() == ItemLocation.RIGHT) {
                                        x = 600;
                                        y = 100;
                                    } else {
                                        x = 50;
                                        y = 500;
                                    }
                                    Item currItem = addNode(canvasViewModel.findNodeParentItem(curr), curr.getTitle(), curr.isPlain(), curr.getLocation(), x, y, curr.getColor());
                                    canvasViewModel.addNodeItem(curr, currItem);
                                }
                            }
                        }
                    }
                });
            }
        });
        return binding.getRoot();
    }

    public Item addNode(Item parent, String title, boolean isPlainNode, int location, int x, int y, String color) {
        Item item = genItem(title, isPlainNode, color);
        mindMappingView.addItem(item,
                parent,
                0, 0,
                location, true, null);
        item.setX(parent.getX() + x);
        item.setY(parent.getY() + y);
        return item;
    }

    private Item genItem(String title, boolean isPlainNode, String color) {
        Item item = new Item(getContext(), title, "", false);
        item.removeAllViews();
        item.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        LinearLayout left = new LinearLayout(getContext());
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(color));
        shape.setCornerRadius(50);
        left.setBackground(shape);
        left.addView(item.getTitle());
        left.setGravity(Gravity.CENTER);
        item.getTitle().setGravity(Gravity.CENTER);
        item.getTitle().setTextSize(20);
        item.getTitle().setLayoutParams(new LinearLayout.LayoutParams(400, 200));

        LinearLayout right = new LinearLayout(getContext());
        if (isPlainNode) {
            // add node
            right.addView(getButton(R.drawable.ic_baseline_add_box_24, v -> {
                startDialog(new DialogCanvas.DialogNode(getContext(), this, item));
            }));
            // add papers
            right.addView(getButton(R.drawable.ic_baseline_bookmark_24, v -> {
                startDialog(new DialogCanvas.DialogPaper(getContext(), this, getViewLifecycleOwner(), item));
            }));
            // add notes
            right.addView(getButton(R.drawable.ic_baseline_sticky_note_2_24, v -> {
                startDialog(new DialogCanvas.DialogNote(getContext(), this, item));
            }));
        }

        item.addView(left);
        item.addView(right);
        item.setPadding(50, 20, 50, 20);
        return item;
    }

    private Button getButton(int drawable, View.OnClickListener l) {
        Button button = new Button(getContext());
        button.setOnClickListener(l);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        button.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
        return button;
    }

    private void startDialog(DialogCanvas dialogCanvas) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogCanvas.show(ft, "Canvas Dialog");
    }
}