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
import android.support.v4.app.NotificationCompat;

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
        boolean transparent_default = resources.getBoolean(R.bool.pref_transparent_default);
        return preferences.getBoolean("pref_transparent", transparent_default);
    }

    boolean getHideStatusIcon() {
        boolean status_icon_default = resources.getBoolean(R.bool.pref_hide_status_icon_default);
        return preferences.getBoolean("pref_hide_status_icon", status_icon_default);
    }

    boolean getToggleMute() {
        boolean toggle_mute_default = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean("pref_toggle_mute", toggle_mute_default);
    }

    boolean getButtonChecked(int position) {
        String pref_key = "pref_buttons_btn_" + position + "_checked";
        int default_res = resources.getIdentifier(pref_key + "_default", "bool", context.getPackageName());
        boolean checked_default = resources.getBoolean(default_res);
        return preferences.getBoolean(pref_key, checked_default);
    }

    int getButtonSelection(int position) {
        String pref_key = "pref_buttons_btn_" + position + "_selection";
        int default_res = resources.getIdentifier(pref_key + "_default", "integer", context.getPackageName());
        int selection_default = resources.getInteger(default_res);
        return preferences.getInt(pref_key, selection_default);
    }

    int getPriority() {
        boolean max_priority_default = resources.getBoolean(R.bool.pref_max_priority_default);
        boolean max_priority = preferences.getBoolean("pref_max_priority", max_priority_default);
        if (max_priority) {
            return NotificationCompat.PRIORITY_MAX;
        }
        return NotificationCompat.PRIORITY_MIN;
    }

    int getVisibility() {
        boolean private_visibility_default = resources.getBoolean(R.bool.pref_private_visibility_default);
        boolean private_visibility = preferences.getBoolean("pref_private_visibility", private_visibility_default);
        if (private_visibility) {
            return NotificationCompat.VISIBILITY_SECRET;
        }
        return NotificationCompat.VISIBILITY_PUBLIC;
    }

    String getTheme() {
        String theme_default = resources.getString(R.string.pref_theme_default);
        return preferences.getString("pref_theme", theme_default);
    }

    int getThemeCustomBackgroundColor() {
        String color_default = resources.getString(R.string.pref_theme_custom_background_default);
        return getColor("pref_theme_custom_background", color_default);
    }

    int getThemeCustomForegroundColor() {
        String color_default = resources.getString(R.string.pref_theme_custom_foreground_default);
        return getColor("pref_theme_custom_foreground", color_default);
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
