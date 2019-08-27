/*
 * Copyright 2017 https://github.com/seht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hyx.app.volumenotification.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.activity.ItemViewActivity;
import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.helper.ItemTouchListener;
import net.hyx.app.volumenotification.helper.DragHandleListener;
import net.hyx.app.volumenotification.helper.RecyclerViewListener;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.widget.DragHandleImageView;

import java.util.Collections;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> implements ItemTouchListener {

    private static final float ALPHA_DISABLED = 0.25f;
    private static final float ALPHA_ENABLED = 1.0f;

    private final Context context;
    private final DragHandleListener dragHandleListener;
    private final VolumeControlModel model;
    private final List<VolumeControl> items;

    public RecyclerViewAdapter(Context context, DragHandleListener dragHandleListener) {
        this.context = context.getApplicationContext();
        this.dragHandleListener = dragHandleListener;
        model = new VolumeControlModel(context);
        items = model.getList();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_view, parent, false);
        return new ItemViewHolder(view, items, model, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        VolumeControl item = items.get(position);
        VolumeControl defaultItem = model.getDefaultControls().get(item.type);

        View itemView = holder.itemView;
        LinearLayout itemWrapper = itemView.findViewById(R.id.list_item_wrapper);

        DragHandleImageView itemHandle = itemView.findViewById(R.id.list_item_handle);
        ImageView itemIcon = itemView.findViewById(R.id.list_item_icon);
        TextView itemLabel = itemView.findViewById(R.id.list_item_label);
        TextView itemHint = itemView.findViewById(R.id.list_item_hint);

        itemIcon.setImageResource(model.getIconId(item.icon));
        itemLabel.setText(item.label);
        itemHint.setText(defaultItem.label);

        if (item.status == 1) {
            itemWrapper.setAlpha(ALPHA_ENABLED);
        } else {
            itemWrapper.setAlpha(ALPHA_DISABLED);
        }

        itemHandle.setOnTouchListener(new DragHandleImageView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dragHandleListener.onStartDrag(holder);
                        return true;
                    case MotionEvent.ACTION_UP:
                        return v.performClick();
                }
                return false;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeControl item = items.get(holder.getAdapterPosition());
                Intent intent = new Intent(v.getContext(), ItemViewActivity.class);
                intent.putExtra(VolumeControlModel.ITEM_FIELD, item);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemSwiped(int position) {
        VolumeControl item = items.get(position);
        item.status = (item.status == 1) ? 0 : 1;
        items.set(position, item);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements RecyclerViewListener {

        Context context;
        List<VolumeControl> items;
        VolumeControlModel model;

        public ItemViewHolder(View itemView, List<VolumeControl> items, VolumeControlModel model, Context context) {
            super(itemView);
            this.items = items;
            this.model = model;
            this.context = context;
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {
            model.saveList(items);
            NotificationServiceController.newInstance(context).startService();
        }

    }

}
