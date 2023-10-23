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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import net.hyx.app.volumenotification.R;

import java.util.Arrays;
import java.util.List;

public class SettingsModel {

    private final Resources resources;
    private final SharedPreferences preferences;

    public SettingsModel(Context context) {
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

    public List<String> getIconEntries() {
        return Arrays.asList(resources.getStringArray(R.array.pref_icon_entries));
    }

    public boolean getAppThemeDark() {
        boolean defValue = resources.getBoolean(R.bool.pref_dark_app_theme_default);
        return preferences.getBoolean("pref_dark_app_theme", defValue);
    }

    public boolean getToggleMute() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean("pref_toggle_mute", defValue);
    }

    public boolean getToggleSilent() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_silent_default);
        return preferences.getBoolean("pref_toggle_silent", defValue);
    }

    public int getStyleAttributeColor(Theme theme, int style, int attribute) {
        TypedArray attrs = theme.obtainStyledAttributes(style, new int[]{attribute});
        int color = attrs.getColor(0, 0);
        attrs.recycle();
        return color;
    }

    public int getThemeAttributeColor(Theme theme, int attribute) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attribute, typedValue, false);
        return getStyleAttributeColor(theme, typedValue.data, attribute);
    }

}
