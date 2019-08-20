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

package net.hyx.app.volumenotification.model;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
//import ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VolumeControlModel {

    public static final String EXTRA_ITEM = "item";
    public static final String EXTRA_ITEM_ID = "item_id";
/*
        <string name="btn_def_label_media">Media volume</string>
    <string name="btn_def_label_call">Call volume</string>
    <string name="btn_def_label_ring">Ring volume</string>
    <string name="btn_def_label_alarm">Alarm volume</string>
    <string name="btn_def_label_notification">Notification volume</string>
    <string name="btn_def_label_system">System volume</string>
    <string name="btn_def_label_dial">Tone dialing volume</string>
    <string name="btn_def_label_default">Default volume</string>*/


    private final SettingsModel settings;

    public VolumeControlModel(Context context) {
        settings = new SettingsModel(context);
    }

    public List<String> getEntries() {
        return Arrays.asList(settings.getResources().getStringArray(R.array.pref_volume_control_entries));
    }

    public List<String> getIconEntries() {
        return Arrays.asList(settings.getResources().getStringArray(R.array.pref_icon_entries));
    }

    public List<VolumeControl> getList() {
        List<VolumeControl> list = new ArrayList<>();
        for (int pos = 0; pos < getEntries().size(); pos++) {
            VolumeControl item = getItem(pos);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public VolumeControl getItem(int position) {
        String value = settings.getPreferences().getString("pref_buttons_list_item_" + position, "");
        if (!value.isEmpty()) {
            return (new Gson()).fromJson(value, VolumeControl.class);
        }
        if (position >= 0 && position < getEntries().size()) {
            return getDefaultItem(position);
        }
        return null;
    }

    public VolumeControl getItemById(int id) {
        List<VolumeControl> items = getList();
        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = items.get(pos);
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public VolumeControl parseItem(VolumeControl item) {
        if (item != null) {
            if (item.icon <= 0 || item.icon >= getIconEntries().size()) {
                item.icon = getDefaultIcon(item.id);
            }
            if (item.label.isEmpty()) {
                item.label = getDefaultLabel(item.id);
            }
            return item;
        }
        return new VolumeControl(0, 0, 0, 0, "");
    }

    public VolumeControl getDefaultItem(int position) {
        int id = position + 1;
        int status = (id >= 1 && id <= 3) ? 1 : 0;
        return new VolumeControl(id, position, status, getDefaultIcon(id), getDefaultLabel(id));
    }

    public String getDefaultLabel(int id) {
        int index = id - 1;
        if (index >= 0 && index < getEntries().size()) {
            return getEntries().get(index);
        }
        return "";
    }

    public int getDefaultIcon(int id) {
        int index = id - 1;
        if (index >= 0 && index < getIconEntries().size()) {
            return index;
        }
        return 0;
    }

    public int getIconDrawable(int index) {
        return settings.getResourceDrawable(R.array.pref_icon_entries, index);
    }

    private Editor editItem(VolumeControl item) {
        return settings.getPreferences().edit().putString("pref_buttons_list_item_" + item.position, (new Gson()).toJson(item));
    }

    public void saveItem(VolumeControl item) {
        editItem(item).commit();
    }

    public void saveList(List<VolumeControl> list) {
        for (int pos = 0; pos < list.size(); pos++) {
            VolumeControl item = list.get(pos);
            item.position = pos;
            editItem(item).commit();
        }
    }

}
