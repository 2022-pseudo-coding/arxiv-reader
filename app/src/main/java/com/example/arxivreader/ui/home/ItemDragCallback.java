package com.example.arxivreader.ui.home;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arxivreader.MainActivity;

class ItemDragCallback extends ItemTouchHelper.SimpleCallback {

    private final RecyclerView paperView;

    ItemDragCallback(RecyclerView paperView) {
        // Choose drag and swipe directions
        // Up and down is chosen for dragging
        // Nothing is chosen for swiping
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.paperView = paperView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    // An item will be dropped into this folder
    private View folder;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

            // Here you are notified that the drag operation began

            if (folder != null) {
                folder.setBackgroundResource(0); // Clear former folder background
                folder = null;
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {

            // Here you are notified that the last operation ended

            if (folder != null) {
                // Set folder background to a color indicating
                // that an item was dropped into it
                folder.setBackgroundColor(
                        ContextCompat.getColor(
                                paperView.getContext(), android.R.color.holo_green_dark
                        )
                );
                // You can remove item from the list here and add it to the folder
                // Remember to notify RecyclerView about it
                paperView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }
    }


    @Override
    public void onChildDraw(
            @NonNull Canvas c,
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive
    ) {
        Log.e("onChildDraw", dX + "," + dY);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {

            // Here you are notified that the drag operation is in progress

            if (folder != null) {
                folder.setBackgroundResource(0); // Clear former folder background
                folder = null;
            }

            float itemActualPosition = viewHolder.itemView.getTop() + dY + viewHolder.itemView.getHeight() / 2;

            // Find folder under dragged item
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);

                // Exclude dragged item from detection
                if (!child.equals(viewHolder.itemView)) {

                    // Accept folder which encloses item position
                    if (child.getTop() < itemActualPosition && itemActualPosition < child.getBottom()) {

                        folder = child;
                        // Set folder background to a color indicating
                        // that an item will be dropped into it upon release
                        folder.setBackgroundColor(
                                ContextCompat.getColor(
                                        recyclerView.getContext(), android.R.color.holo_green_light
                                )
                        );
                        break;
                    }
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
