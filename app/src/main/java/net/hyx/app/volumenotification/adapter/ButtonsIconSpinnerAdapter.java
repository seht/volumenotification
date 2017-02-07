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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.model.Settings;

public class ButtonsIconSpinnerAdapter extends ArrayAdapter<String> {

    private int resource;
    private Settings settings;

    public ButtonsIconSpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        this.resource = resource;
        settings = new Settings(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, R.layout.view_widget_pref_buttons_icon);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, R.layout.view_widget_pref_buttons_icon_dropdown);
    }

    private View getCustomView(int position, ViewGroup parent, int layout) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        ImageView image = (ImageView) view.findViewById(R.id.pref_buttons_icon_image);
        image.setImageResource(settings.getDrawable(resource, position));
        return view;
    }

}
