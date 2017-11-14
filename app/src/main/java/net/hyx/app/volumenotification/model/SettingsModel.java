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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import net.hyx.app.volumenotification.R;

public class SettingsModel {

    private final Context context;
    private final Resources resources;
    private final SharedPreferences preferences;

    public SettingsModel(Context context) {
        this.context = context;
        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Resources getResources() {
        return resources;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public int getAppTheme() {
        if (getAppThemeDark()) {
            return R.style.style_app_theme_dark;
        }
        return R.style.style_app_theme_light;
    }

    public boolean getDialogAlertNonceChecked(int id) {
        return preferences.getBoolean("pref_dialog_alert_nonce_checked_" + id, false);
    }

    public boolean getAppThemeDark() {
        boolean defValue = resources.getBoolean(R.bool.pref_dark_app_theme_default);
        return preferences.getBoolean("pref_dark_app_theme", defValue);
    }

    public boolean getEnabled() {
        boolean defValue = resources.getBoolean(R.bool.pref_enabled_default);
        return preferences.getBoolean("pref_enabled", defValue);
    }

    public boolean getToggleMute() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean("pref_toggle_mute", defValue);
    }

    public boolean getToggleSilent() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_silent_default);
        return preferences.getBoolean("pref_toggle_silent", defValue);
    }

    public boolean getTopPriority() {
        boolean defValue = resources.getBoolean(R.bool.pref_top_priority_default);
        return preferences.getBoolean("pref_top_priority", defValue);
    }

    public boolean getHideStatus() {
        boolean defValue = resources.getBoolean(R.bool.pref_hide_status_default);
        return preferences.getBoolean("pref_hide_status", defValue);
    }

    public boolean getHideLocked() {
        boolean defValue = resources.getBoolean(R.bool.pref_hide_locked_default);
        return preferences.getBoolean("pref_hide_locked", defValue);
    }

    public boolean getTranslucent() {
        boolean defValue = resources.getBoolean(R.bool.pref_translucent_default);
        return preferences.getBoolean("pref_translucent", defValue);
    }

    public String getTheme() {
        String defValue = resources.getString(R.string.pref_theme_default);
        return preferences.getString("pref_theme", defValue);
    }

    public String getCustomThemeBackgroundColor() {
        String defValue = resources.getString(R.string.pref_custom_theme_background_color_default);
        return preferences.getString("pref_custom_theme_background_color", defValue);
    }

    public String getCustomThemeIconColor() {
        String defValue = resources.getString(R.string.pref_custom_theme_icon_color_default);
        return preferences.getString("pref_custom_theme_icon_color", defValue);
    }

    public int getColor(String value) {
        int color = 0;
        try {
            if (!value.isEmpty()) {
                color = Color.parseColor(value);
            }
        } catch (IllegalArgumentException ex) {
            //
        }
        return color;
    }

    public int getResourceDrawable(int resource, int index) {
        int drawable;
        TypedArray drawables = resources.obtainTypedArray(resource);
        drawable = drawables.getResourceId(index, 0);
        drawables.recycle();
        return drawable;
    }

    public int getStyleAttributeColor(int style, int attribute) {
        TypedArray attrs = context.obtainStyledAttributes(style, new int[]{attribute});
        int color = attrs.getColor(0, 0);
        attrs.recycle();
        return color;
    }

    public int getThemeAttributeColor(Theme theme, int attribute) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attribute, typedValue, false);
        return getStyleAttributeColor(typedValue.data, attribute);
    }

}
