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

package net.hyx.app.volumenotification.model;

import android.content.Context;

import com.google.gson.Gson;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.factory.NotificationFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonsModel {

    private final Context context;
    private final SettingsModel settings;

    public ButtonsModel(Context context) {
        this.context = context;
        settings = new SettingsModel(context);
    }

    public List<String> getButtonEntries() {
        return Arrays.asList(settings.getResources().getStringArray(R.array.pref_buttons_entries));
    }

    public List<String> getButtonIconEntries() {
        return Arrays.asList(settings.getResources().getStringArray(R.array.pref_buttons_icon_entries));
    }

    public List<VolumeControl> getButtonList() {
        List<VolumeControl> list = new ArrayList<>();
        for (int pos = 0; pos < getButtonEntries().size(); pos++) {
            VolumeControl item = getButtonItem(pos);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public VolumeControl getButtonItem(int position) {
        String value = settings.getPreferences().getString("pref_buttons_list_item_" + position, "");
        if (!value.isEmpty()) {
            return (new Gson()).fromJson(value, VolumeControl.class);
        }
        if (position >= getButtonEntries().size()) {
            return null;
        }
        return getDefaultButtonItem(position);
    }

    public VolumeControl getButtonItemById(int id) {
        List<VolumeControl> items = getButtonList();
        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = items.get(pos);
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public VolumeControl getParseButtonItem(int id) {
        VolumeControl item = getButtonItemById(id);
        return getParseButtonItem(item);
    }

    public VolumeControl getParseButtonItem(VolumeControl item) {
        if (item == null) {
            return new VolumeControl(0, 0, 0, 0, "");
        }
        if (item.icon == 0 || item.icon >= getButtonIconEntries().size()) {
            item.icon = getDefaultButtonIcon(item.id);
        }
        if (item.label.isEmpty()) {
            item.label = getDefaultButtonLabel(item.id);
        }
        return item;
    }

    public VolumeControl getDefaultButtonItem(int position) {
        int id = position + 1;
        int status = (id <= 0 || id > 3) ? 0 : 1;
        return new VolumeControl(id, position, status, getDefaultButtonIcon(id), getDefaultButtonLabel(id));
    }

    public String getDefaultButtonLabel(int id) {
        int index = id - 1;
        if (index < 0 || index >= getButtonEntries().size()) {
            return "";
        }
        return getButtonEntries().get(index);
    }

    public int getDefaultButtonIcon(int id) {
        int index = id - 1;
        if (index < 0 || index >= getButtonIconEntries().size()) {
            index = 0;
        }
        return index;
    }

    public int getButtonIconDrawable(int index) {
        return settings.getResourceDrawable(R.array.pref_buttons_icon_entries, index);
    }

    public void saveButtonItem(VolumeControl item) {
        saveButtonItem(item, true);
    }

    public void saveButtonItem(VolumeControl item, boolean submit) {
        settings.edit().putString("pref_buttons_list_item_" + item.position, (new Gson()).toJson(item)).commit();
        if (submit) {
            NotificationFactory.newInstance(context).create();
        }
    }

    public void saveButtonList(List<VolumeControl> list) {
        for (int pos = 0; pos < list.size(); pos++) {
            VolumeControl item = list.get(pos);
            item.position = pos;
            saveButtonItem(item, false);
        }
        NotificationFactory.newInstance(context).create();
    }

}
