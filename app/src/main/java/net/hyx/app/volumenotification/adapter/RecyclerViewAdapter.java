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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.activity.ItemViewActivity;
import net.hyx.app.volumenotification.controller.NotificationController;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.helper.ItemTouchHelperAdapter;
import net.hyx.app.volumenotification.helper.ItemTouchHelperViewHolder;
import net.hyx.app.volumenotification.helper.OnStartDragListener;
import net.hyx.app.volumenotification.model.VolumeControlModel;

import java.util.Collections;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    private static final float ALPHA_DISABLED = 0.25f;
    private static final float ALPHA_ENABLED = 1.0f;
    private final Context context;
    private final OnStartDragListener dragStartListener;
    private final VolumeControlModel model;
    private final List<VolumeControl> items;

    public RecyclerViewAdapter(Context context, OnStartDragListener dragStartListener) {
        this.context = context;
        this.dragStartListener = dragStartListener;
        model = new VolumeControlModel(context);
        items = model.getList();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_view_item, parent, false);
        return new ItemViewHolder(view, items, model);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        VolumeControl item = model.parseItem(items.get(position));

        View itemView = holder.itemView;
        LinearLayout itemWrapper = (LinearLayout) itemView.findViewById(R.id.list_item_wrapper);
        ImageView itemHandle = (ImageView) itemView.findViewById(R.id.list_item_handle);

        ImageView itemIcon = (ImageView) itemView.findViewById(R.id.list_item_icon);
        TextView itemLabel = (TextView) itemView.findViewById(R.id.list_item_label);
        TextView itemHint = (TextView) itemView.findViewById(R.id.list_item_hint);

        itemIcon.setImageResource(model.getIconDrawable(item.icon));
        itemLabel.setText(item.label);
        itemHint.setText(model.getDefaultLabel(item.id));

        if (item.status != 0) {
            itemWrapper.setAlpha(ALPHA_ENABLED);
        } else {
            itemWrapper.setAlpha(ALPHA_DISABLED);
        }

        NotificationController.newInstance(context).create();

        itemHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return true;
            }

        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                int position = holder.getAdapterPosition();
                VolumeControl item = items.get(position);
                Intent intent = new Intent(context, ItemViewActivity.class);
                intent.putExtra(VolumeControlModel.EXTRA_ITEM, item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        model.saveList(items);
        notifyItemMoved(fromPosition, toPosition);
        notifyDataSetChanged();
        //NotificationController.newInstance(context).create();
        return true;
    }

    @Override
    public void onItemSwiped(int position) {
        VolumeControl item = items.get(position);
        item.status = (item.status != 0) ? 0 : 1;
        items.set(position, item);
        model.saveList(items);
        notifyDataSetChanged();
        //NotificationController.newInstance(context).create();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        List<VolumeControl> items;
        VolumeControlModel model;

        public ItemViewHolder(View itemView, List<VolumeControl> items, VolumeControlModel model) {
            super(itemView);
            this.items = items;
            this.model = model;
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {
            model.saveList(items);
        }

    }

}
