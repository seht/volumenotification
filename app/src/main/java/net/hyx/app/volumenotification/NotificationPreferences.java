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

package net.hyx.app.volumenotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

class NotificationPreferences {

    private Context context;
    private Resources resources;
    private SharedPreferences preferences;

    NotificationPreferences(Context context) {
        this.context = context;
        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    SharedPreferences.Editor edit() {
        return preferences.edit();
    }

    boolean getEnabled() {
        boolean enabled_default = resources.getBoolean(R.bool.pref_enabled_default);
        return preferences.getBoolean("pref_enabled", enabled_default);
    }

    boolean getTransparent() {
        boolean translucent_default = resources.getBoolean(R.bool.pref_translucent_default);
        return preferences.getBoolean("pref_translucent", translucent_default);
    }

    boolean getHideStatus() {
        boolean hide_status_default = resources.getBoolean(R.bool.pref_hide_status_default);
        return preferences.getBoolean("pref_hide_status", hide_status_default);
    }

    boolean getToggleMute() {
        boolean toggle_mute_default = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean("pref_toggle_mute", toggle_mute_default);
    }

    boolean getTopPriority() {
        boolean top_priority_default = resources.getBoolean(R.bool.pref_top_priority_default);
        return preferences.getBoolean("pref_top_priority", top_priority_default);
    }

    boolean getHideLocked() {
        boolean hide_locked_default = resources.getBoolean(R.bool.pref_hide_locked_default);
        return preferences.getBoolean("pref_hide_locked", hide_locked_default);
    }

    boolean getButtonChecked(int pos) {
        String pref_key = "pref_buttons_checked_btn_" + pos;
        int default_res = resources.getIdentifier(pref_key + "_default", "bool", context.getPackageName());
        boolean checked_default = resources.getBoolean(default_res);
        return preferences.getBoolean(pref_key, checked_default);
    }

    int getButtonSelection(int pos) {
        String pref_key = "pref_buttons_selection_btn_" + pos;
        int default_res = resources.getIdentifier(pref_key + "_default", "integer", context.getPackageName());
        int selection_default = resources.getInteger(default_res);
        return preferences.getInt(pref_key, selection_default);
    }

    String getTheme() {
        String theme_default = resources.getString(R.string.pref_theme_default);
        return preferences.getString("pref_theme", theme_default);
    }

    int getCustomThemeBackgroundColor() {
        String color_default = resources.getString(R.string.pref_custom_theme_background_color_default);
        return getColor("pref_custom_theme_background_color", color_default);
    }

    int getCustomThemeIconColor() {
        String color_default = resources.getString(R.string.pref_custom_theme_icon_color_default);
        return getColor("pref_custom_theme_icon_color", color_default);
    }

    private int getColor(String pref_key, String default_value) {
        int color = 0;
        String color_value = preferences.getString(pref_key, default_value);
        try {
            if (!color_value.isEmpty()) {
                color = Color.parseColor(color_value);
            }
        } catch (IllegalArgumentException e) {
            // Toast.makeText(context, resources.getString(R.string.pref_theme_custom_color_error_message), Toast.LENGTH_SHORT).show();
        }
        if (color == 0) {
            color = Color.parseColor(default_value);
            edit().putString(pref_key, default_value).commit();
        }
        return color;
    }

}
