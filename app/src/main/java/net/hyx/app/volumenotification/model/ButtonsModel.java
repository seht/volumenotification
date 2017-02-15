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
import net.hyx.app.volumenotification.entity.ButtonsItem;
import net.hyx.app.volumenotification.factory.NotificationFactory;

import java.util.ArrayList;
import java.util.List;

public class ButtonsModel {

    private final SettingsModel settings;
    private Context context;

    public ButtonsModel(Context context) {
        this.context = context;
        settings = new SettingsModel(context);
    }

    public String[] getButtonEntries() {
        return settings.resources.getStringArray(R.array.pref_buttons_entries);
    }

    public List<ButtonsItem> getButtonList() {
        List<ButtonsItem> list = new ArrayList<>();
        for (int pos = 0; pos < getButtonEntries().length; pos++) {
            ButtonsItem item = getButtonItem(pos);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public ButtonsItem getButtonItemById(int id) {
        List<ButtonsItem> items = getButtonList();
        for (int pos = 0; pos < items.size(); pos++) {
            ButtonsItem item = items.get(pos);
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public ButtonsItem getParseButtonItem(int id) {
        ButtonsItem item = getButtonItemById(id);
        if (item != null) {
            return getParseButtonItem(item);
        }
        return null;
    }

    public ButtonsItem getParseButtonItem(ButtonsItem item) {
        if (item.icon == 0 || item.icon >= getButtonIconEntries().length) {
            item.icon = getDefaultButtonIcon(item.id);
        }
        if (item.label.isEmpty()) {
            item.label = getDefaultButtonLabel(item.id);
        }
        return item;
    }

    public ButtonsItem getButtonItem(int position) {
        String value = settings.preferences.getString("pref_buttons_list_item_" + position, "");
        if (!value.isEmpty()) {
            return (new Gson()).fromJson(value, ButtonsItem.class);
        }
        if (position > getButtonEntries().length) {
            return null;
        }
        return getDefaultButtonItem(position);
    }

    public ButtonsItem getDefaultButtonItem(int position) {
        int id = position + 1;
        int status = (id > 3) ? 0 : 1;
        return new ButtonsItem(id, position, status, getDefaultButtonIcon(id), getDefaultButtonLabel(id));
    }

    public String getDefaultButtonLabel(int id) {
        int index = id - 1;
        return getButtonEntries()[index];
    }

    public int getDefaultButtonIcon(int id) {
        return id - 1;
    }

    public int getButtonIconDrawable(int index) {
        return settings.getDrawable(R.array.pref_buttons_icon_entries, index);
    }

    public String[] getButtonIconEntries() {
        return settings.resources.getStringArray(R.array.pref_buttons_icon_entries);
    }

    public void saveButtonItem(ButtonsItem item) {
        saveButtonItem(item, true);
    }

    private void saveButtonItem(ButtonsItem item, boolean create) {
        settings.edit().putString("pref_buttons_list_item_" + item.position, (new Gson()).toJson(item)).commit();
        if (create) {
            NotificationFactory.newInstance(context).create();
        }
    }

    public void saveButtonList(List<ButtonsItem> list) {
        for (int pos = 0; pos < list.size(); pos++) {
            ButtonsItem item = list.get(pos);
            item.position = pos;
            saveButtonItem(item, false);
        }
        NotificationFactory.newInstance(context).create();
    }

}
