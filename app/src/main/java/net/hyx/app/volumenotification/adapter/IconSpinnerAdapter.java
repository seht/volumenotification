/*
 * Copyright 2017 Seth Montenegro
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
import net.hyx.app.volumenotification.model.SettingsModel;

import java.util.List;

public class IconSpinnerAdapter extends ArrayAdapter<String> {

    private final int resource;
    private final SettingsModel settings;

    public IconSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        this.resource = resource;
        settings = new SettingsModel(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, R.layout.adapter_icon_spinner_view);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, R.layout.adapter_icon_spinner_dropdown_view);
    }

    private View getCustomView(int position, ViewGroup parent, int layout) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        ImageView image = (ImageView) view.findViewById(R.id.pref_icon_image);
        image.setImageResource(settings.getResourceDrawable(resource, position));
        return view;
    }

}
