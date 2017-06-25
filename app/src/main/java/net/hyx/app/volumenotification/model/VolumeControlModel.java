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

package net.hyx.app.volumenotification.model;

import android.content.Context;

import com.google.gson.Gson;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.factory.NotificationFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VolumeControlModel {

    private final Context context;
    private final SettingsModel settings;

    public VolumeControlModel(Context context) {
        this.context = context;
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
        if (position >= getEntries().size()) {
            return null;
        }
        return getDefaultItem(position);
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

    public VolumeControl getParseItem(int id) {
        VolumeControl item = getItemById(id);
        return getParseItem(item);
    }

    public VolumeControl getParseItem(VolumeControl item) {
        if (item == null) {
            return new VolumeControl(0, 0, 0, 0, "");
        }
        if (item.icon == 0 || item.icon >= getIconEntries().size()) {
            item.icon = getDefaultIcon(item.id);
        }
        if (item.label.isEmpty()) {
            item.label = getDefaultLabel(item.id);
        }
        return item;
    }

    public VolumeControl getDefaultItem(int position) {
        int id = position + 1;
        int status = (id <= 0 || id > 3) ? 0 : 1;
        return new VolumeControl(id, position, status, getDefaultIcon(id), getDefaultLabel(id));
    }

    public String getDefaultLabel(int id) {
        int index = id - 1;
        if (index < 0 || index >= getEntries().size()) {
            return "";
        }
        return getEntries().get(index);
    }

    public int getDefaultIcon(int id) {
        int index = id - 1;
        if (index < 0 || index >= getIconEntries().size()) {
            index = 0;
        }
        return index;
    }

    public int getIconDrawable(int index) {
        return settings.getResourceDrawable(R.array.pref_icon_entries, index);
    }

    public void saveItem(VolumeControl item) {
        saveItem(item, true);
    }

    public void saveItem(VolumeControl item, boolean createNotification) {
        settings.getPreferences().edit().putString("pref_buttons_list_item_" + item.position, (new Gson()).toJson(item)).commit();
        if (createNotification) {
            NotificationFactory.newInstance(context).create();
        }
    }

    public void saveList(List<VolumeControl> list) {
        for (int pos = 0; pos < list.size(); pos++) {
            VolumeControl item = list.get(pos);
            item.position = pos;
            saveItem(item, false);
        }
        NotificationFactory.newInstance(context).create();
    }

}
