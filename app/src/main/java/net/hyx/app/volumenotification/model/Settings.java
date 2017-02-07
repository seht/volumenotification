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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;

import net.hyx.app.volumenotification.R;

public class Settings {

    public final Resources resources;
    public final SharedPreferences preferences;
    private final Context context;

    public Settings(Context context) {
        this.context = context;
        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences.Editor edit() {
        return preferences.edit();
    }

    public int getAppTheme() {
        if (getAppThemeDark()) {
            return R.style.style_app_theme_dark;
        }
        return R.style.style_app_theme_light;
    }

    public boolean getDialogAlertNonceChecked(int pref_key) {
        return preferences.getBoolean("pref_dialog_alert_nonce_checked_" + pref_key, false);
    }

    public boolean getAppThemeDark() {
        boolean default_value = resources.getBoolean(R.bool.pref_dark_app_theme_default);
        return preferences.getBoolean("pref_dark_app_theme", default_value);
    }

    public boolean getEnabled() {
        boolean default_value = resources.getBoolean(R.bool.pref_enabled_default);
        return preferences.getBoolean("pref_enabled", default_value);
    }

    public boolean getTranslucent() {
        boolean default_value = resources.getBoolean(R.bool.pref_translucent_default);
        return preferences.getBoolean("pref_translucent", default_value);
    }

    public boolean getHideStatus() {
        boolean default_value = resources.getBoolean(R.bool.pref_hide_status_default);
        return preferences.getBoolean("pref_hide_status", default_value);
    }

    public boolean getToggleMute() {
        boolean default_value = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean("pref_toggle_mute", default_value);
    }

    public boolean getToggleSilent() {
        boolean default_value = resources.getBoolean(R.bool.pref_toggle_silent_default);
        return preferences.getBoolean("pref_toggle_silent", default_value);
    }

    public boolean getTopPriority() {
        boolean default_value = resources.getBoolean(R.bool.pref_top_priority_default);
        return preferences.getBoolean("pref_top_priority", default_value);
    }

    public boolean getHideLocked() {
        boolean default_value = resources.getBoolean(R.bool.pref_hide_locked_default);
        return preferences.getBoolean("pref_hide_locked", default_value);
    }

    public String getTheme() {
        String default_value = resources.getString(R.string.pref_theme_default);
        return preferences.getString("pref_theme", default_value);
    }

    public String getCustomThemeBackgroundColor() {
        String default_value = resources.getString(R.string.pref_custom_theme_background_color_default);
        return preferences.getString("pref_custom_theme_background_color", default_value);
    }

    public String getCustomThemeIconColor() {
        String default_value = resources.getString(R.string.pref_custom_theme_icon_color_default);
        return preferences.getString("pref_custom_theme_icon_color", default_value);
    }

    public int getColor(String pref_value) {
        int color = 0;
        try {
            if (!pref_value.isEmpty()) {
                color = Color.parseColor(pref_value);
            }
        } catch (IllegalArgumentException e) {
            //
        }
        return color;
    }

    public int getDrawable(int resource, int index) {
        int drawable;
        TypedArray drawables = context.getResources().obtainTypedArray(resource);
        drawable = drawables.getResourceId(index, 0);
        drawables.recycle();
        return drawable;
    }

}
