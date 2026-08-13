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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import net.hyx.app.volumenotification.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsModel {

    public static final String SHARED_PREF_NAME = "net.hyx.app.volumenotification_preferences";
    public static final String PREF_DARK_APP_THEME = "pref_dark_app_theme";
    public static final String PREF_DARK_APP_THEME_SET = "pref_dark_app_theme_set";
    public static final String PREF_ENABLED = "pref_enabled";
    public static final String PREF_BOOT = "pref_boot";
    public static final String PREF_FOREGROUND_SERVICE = "pref_foreground_service";
    public static final String PREF_TOGGLE_MUTE = "pref_toggle_mute";
    public static final String PREF_TOGGLE_SILENT = "pref_toggle_silent";
    public static final String PREF_TOP_PRIORITY = "pref_top_priority";
    public static final String PREF_HIDE_STATUS = "pref_hide_status";
    public static final String PREF_HIDE_LOCKED = "pref_hide_locked";
    public static final String PREF_TRANSLUCENT = "pref_translucent";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_CUSTOM_THEME_BACKGROUND_COLOR = "pref_custom_theme_background_color";
    public static final String PREF_CUSTOM_THEME_ICON_COLOR = "pref_custom_theme_icon_color";
    public static final String PREF_NOTIFICATION_HEIGHT = "pref_notification_height";
    public static final String PREF_CONTROL_LIST_ITEM_PREFIX = "pref_control_list_item_";
    public static final String PREF_DIALOG_ALERT_NONCE_COUNT_PREFIX = "pref_dialog_alert_nonce_count_";

    private static volatile SettingsModel instance;

    private final Resources resources;
    private final SharedPreferences preferences;

    public static SettingsModel getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsModel.class) {
                if (instance == null) {
                    instance = new SettingsModel(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public SettingsModel(Context context) {
        resources = context.getResources();
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        migrateLegacyDefaultPreferences(context.getApplicationContext());
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

    public int getNonceDialogCount(int id) {
        return preferences.getInt(PREF_DIALOG_ALERT_NONCE_COUNT_PREFIX + id, 0);
    }

    public void setNonceDialogCount(int id, int count) {
        preferences.edit().putInt(PREF_DIALOG_ALERT_NONCE_COUNT_PREFIX + id, count).apply();
    }

    public boolean getAppThemeDark() {
        if (!preferences.getBoolean(PREF_DARK_APP_THEME_SET, false)) {
            return isSystemDarkMode();
        }
        boolean defValue = resources.getBoolean(R.bool.pref_dark_app_theme_default);
        return preferences.getBoolean(PREF_DARK_APP_THEME, defValue);
    }

    public void setAppThemeDark(boolean value) {
        preferences.edit()
                .putBoolean(PREF_DARK_APP_THEME, value)
                .putBoolean(PREF_DARK_APP_THEME_SET, true)
                .apply();
    }

    public int getAppNightMode() {
        if (!preferences.getBoolean(PREF_DARK_APP_THEME_SET, false)) {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        return getAppThemeDark() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
    }

    private boolean isSystemDarkMode() {
        int nightMode = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public int getStatusIcon() {
        return (getHideStatus()) ? android.R.color.transparent : R.drawable.ic_stat_icon;
    }

    public boolean isEnabled() {
        boolean defValue = resources.getBoolean(R.bool.pref_enabled_default);
        return preferences.getBoolean(PREF_ENABLED, defValue);
    }

    public boolean startsAtBoot() {
        boolean defValue = resources.getBoolean(R.bool.pref_boot_default);
        return preferences.getBoolean(PREF_BOOT, defValue);
    }

    public boolean hasForegroundService() {
        boolean defValue = resources.getBoolean(R.bool.pref_foreground_service_default);
        return preferences.getBoolean(PREF_FOREGROUND_SERVICE, defValue);
    }

    public boolean getToggleMute() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_mute_default);
        return preferences.getBoolean(PREF_TOGGLE_MUTE, defValue);
    }

    public boolean getToggleSilent() {
        boolean defValue = resources.getBoolean(R.bool.pref_toggle_silent_default);
        return preferences.getBoolean(PREF_TOGGLE_SILENT, defValue);
    }

    public boolean getTopPriority() {
        boolean defValue = resources.getBoolean(R.bool.pref_top_priority_default);
        return preferences.getBoolean(PREF_TOP_PRIORITY, defValue);
    }

    public boolean getHideStatus() {
        boolean defValue = resources.getBoolean(R.bool.pref_hide_status_default);
        return preferences.getBoolean(PREF_HIDE_STATUS, defValue);
    }

    public boolean getHideLocked() {
        boolean defValue = resources.getBoolean(R.bool.pref_hide_locked_default);
        return preferences.getBoolean(PREF_HIDE_LOCKED, defValue);
    }

    public boolean getTranslucent() {
        boolean defValue = resources.getBoolean(R.bool.pref_translucent_default);
        return preferences.getBoolean(PREF_TRANSLUCENT, defValue);
    }

    public String getTheme() {
        String defValue = resources.getString(R.string.pref_theme_default);
        return preferences.getString(PREF_THEME, defValue);
    }

    public String getCustomThemeBackgroundColor() {
        String defValue = resources.getString(R.string.pref_custom_theme_background_color_default);
        return preferences.getString(PREF_CUSTOM_THEME_BACKGROUND_COLOR, defValue);
    }

    public String getCustomThemeIconColor() {
        String defValue = resources.getString(R.string.pref_custom_theme_icon_color_default);
        return preferences.getString(PREF_CUSTOM_THEME_ICON_COLOR, defValue);
    }

    public String getNotificationHeight() {
        String defValue = resources.getString(R.string.pref_notification_height_default);
        return preferences.getString(PREF_NOTIFICATION_HEIGHT, defValue);
    }

    public boolean isValidColor(String value) {
        if (value == null) {
            return false;
        }
        String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) {
            return false;
        }
        try {
            Color.parseColor(normalizedValue);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public int getColor(String value) {
        if (!isValidColor(value)) {
            return 0;
        }
        return Color.parseColor(value.trim());
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

    private void migrateLegacyDefaultPreferences(Context context) {
        SharedPreferences legacyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (legacyPreferences.getAll().isEmpty()) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        boolean copiedValue = false;
        for (Map.Entry<String, ?> entry : legacyPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            if (preferences.contains(key)) {
                continue;
            }

            Object value = entry.getValue();
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> setValue = (Set<String>) value;
                editor.putStringSet(key, setValue);
            }
            if (PREF_DARK_APP_THEME.equals(key) && Boolean.TRUE.equals(value)) {
                editor.putBoolean(PREF_DARK_APP_THEME_SET, true);
            }
            copiedValue = true;
        }

        if (copiedValue) {
            editor.apply();
        }
    }

}
