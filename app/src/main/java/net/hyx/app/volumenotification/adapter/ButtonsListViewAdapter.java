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
import android.widget.TextView;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.activity.ButtonsItemActivity;
import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.helper.ItemTouchHelperAdapter;
import net.hyx.app.volumenotification.helper.ItemTouchHelperViewHolder;
import net.hyx.app.volumenotification.helper.OnStartDragListener;
import net.hyx.app.volumenotification.model.Buttons;
import net.hyx.app.volumenotification.object.ButtonsItem;

import java.util.Collections;
import java.util.List;


public class ButtonsListViewAdapter extends RecyclerView.Adapter<ButtonsListViewAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    private static List<ButtonsItem> items;
    private final OnStartDragListener dragStartListener;
    private final Context context;

    public ButtonsListViewAdapter(Context context, OnStartDragListener dragStartListener) {
        this.context = context;
        this.dragStartListener = dragStartListener;
        items = (new Buttons(context)).getButtonList();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item_buttons, parent, false);
        return new ItemViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        Buttons model = new Buttons(context);
        ButtonsItem item = model.getParseButtonItem(items.get(position));

        String default_label = model.getDefaultButtonLabel(item.id);
        holder.btn_label_default.setText(default_label);
        holder.btn_label.setText(item.label);
        holder.btn_icon.setImageResource(model.getButtonIconDrawable(item.icon));

        holder.btn_handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        (new Buttons(context)).saveButtonList(items);
        NotificationFactory.newInstance(context).startService();
        return true;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnClickListener {

        //public final View item_view;

        public final ImageView btn_handle;
        public final ImageView btn_icon;

        public final TextView btn_label;
        public final TextView btn_label_default;

        private final Context context;

        public ItemViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            btn_handle = (ImageView) itemView.findViewById(R.id.handle);
            btn_icon = (ImageView) itemView.findViewById(R.id.list_btn_icon);
            btn_label = (TextView) itemView.findViewById(R.id.list_btn_label);
            btn_label_default = (TextView) itemView.findViewById(R.id.list_btn_label_default);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            //System.out.println("selected");
        }

        @Override
        public void onItemClear() {
            //System.out.println("clear");
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context, ButtonsItemActivity.class);
            ButtonsItem item = items.get(position);
            item.position = position;
            intent.putExtra("item", item);
            context.startActivity(intent);
        }

    }

}
