/*
 * Copyright (C) 2017 Seht (R) Hyx Ltd.
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
import android.support.v4.view.MotionEventCompat;
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
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.helper.ItemTouchHelperAdapter;
import net.hyx.app.volumenotification.helper.ItemTouchHelperViewHolder;
import net.hyx.app.volumenotification.helper.OnStartDragListener;
import net.hyx.app.volumenotification.model.ButtonsModel;

import java.util.Collections;
import java.util.List;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    public static final String EXTRA_ITEM = "item";
    public static final float ALPHA_DISABLED = 0.25f;

    private static List<VolumeControl> items;
    private final OnStartDragListener dragStartListener;
    private final ButtonsModel model;

    public ListViewAdapter(Context context, OnStartDragListener dragStartListener) {
        this.dragStartListener = dragStartListener;
        model = new ButtonsModel(context);
        items = model.getButtonList();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_view_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        VolumeControl item = model.getParseButtonItem(items.get(position));

        View itemView = holder.itemView;
        LinearLayout itemWrapper = (LinearLayout) itemView.findViewById(R.id.list_item_wrapper);
        ImageView itemHandle = (ImageView) itemView.findViewById(R.id.list_item_handle);

        ImageView itemIcon = (ImageView) itemView.findViewById(R.id.list_item_icon);
        TextView itemLabel = (TextView) itemView.findViewById(R.id.list_item_label);
        TextView itemHint = (TextView) itemView.findViewById(R.id.list_item_hint);

        itemIcon.setImageResource(model.getButtonIconDrawable(item.icon));
        itemLabel.setText(item.label);
        itemHint.setText(model.getDefaultButtonLabel(item.id));

        if (item.status == 0) {
            itemWrapper.setAlpha(ALPHA_DISABLED);
        }

        itemHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
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
                Intent intent = new Intent(context, ItemViewActivity.class);
                VolumeControl item = items.get(position);
                item.position = position;
                intent.putExtra(EXTRA_ITEM, item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        model.saveButtonList(items);
        return true;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        VolumeControl item = items.get(position);
        item.status = (item.status == 1) ? 0 : 1;
        model.saveButtonItem(item);
        //items.set(position, item);
        //notifyDataSetChanged();
        notifyItemChanged(position);
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

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onItemSelected() {
            //System.out.println("selected");
        }

        @Override
        public void onItemClear() {
            //System.out.println("clear");
        }

    }

}
