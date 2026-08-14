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

package net.hyx.app.volumenotification.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.model.SettingsModel;

/**
 * @see {https://developer.android.com/guide/topics/ui/settings/organize-your-settings}
 * @see {https://github.com/googlesamples/android-preferences}
 */
public class SettingsActivity extends BaseActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        //PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback,
        OnSharedPreferenceChangeListener {

    private SettingsModel settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSettingsModel();
        setContentView(R.layout.activity_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        settings.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        settings.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsModel.PREF_BOOT.equals(key)) {
            NotificationServiceController.newInstance(this).checkEnableStartAtBoot();
        }
        NotificationServiceController.newInstance(this).startService();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartScreen(@NonNull PreferenceFragmentCompat caller, PreferenceScreen pref) {
        final Bundle args = pref.getExtras();
        final SettingsFragment fragment = new SettingsFragment();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment, pref.getKey())
                .addToBackStack(null)
                .commit();

        return true;
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final PreferenceFragmentCompat fragment = new NotificationThemeFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();

        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(SettingsModel.SHARED_PREF_NAME);
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            if (getActivity() == null) {
                return;
            }
            final SettingsModel settings = SettingsModel.getInstance(getActivity());
            if (!settings.getPreferences().contains(SettingsModel.PREF_THEME)) {
                settings.getPreferences().edit()
                        .putString(SettingsModel.PREF_THEME, settings.getDefaultNotificationTheme())
                        .apply();
            }
            Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    String colorValue = String.valueOf(newValue);
                    if (settings.isValidColor(colorValue)) {
                        return true;
                    }
                    Toast.makeText(getActivity(), R.string.pref_custom_theme_color_error_message, Toast.LENGTH_SHORT).show();
                    return false;
                }
            };

            Preference backgroundColorPref = findPreference(SettingsModel.PREF_CUSTOM_THEME_BACKGROUND_COLOR);
            Preference iconColorPref = findPreference(SettingsModel.PREF_CUSTOM_THEME_ICON_COLOR);

            if (backgroundColorPref != null) {
                backgroundColorPref.setOnPreferenceChangeListener(changeListener);
            }
            if (iconColorPref != null) {
                iconColorPref.setOnPreferenceChangeListener(changeListener);
            }
        }

    }

    public static class NotificationThemeFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(SettingsModel.SHARED_PREF_NAME);
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
            setPreferencesFromResource(R.xml.settings_preferences_custom_theme, rootKey);
            if (getActivity() == null) {
                return;
            }
            final SettingsModel settings = SettingsModel.getInstance(getActivity());
            Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    String colorValue = String.valueOf(newValue);
                    if (settings.isValidColor(colorValue)) {
                        return true;
                    }
                    Toast.makeText(getActivity(), R.string.pref_custom_theme_color_error_message, Toast.LENGTH_SHORT).show();
                    return false;
                }
            };

            Preference backgroundColorPref = findPreference(SettingsModel.PREF_CUSTOM_THEME_BACKGROUND_COLOR);
            Preference iconColorPref = findPreference(SettingsModel.PREF_CUSTOM_THEME_ICON_COLOR);

            if (backgroundColorPref != null) {
                backgroundColorPref.setOnPreferenceChangeListener(changeListener);
            }
            if (iconColorPref != null) {
                iconColorPref.setOnPreferenceChangeListener(changeListener);
            }
        }
    }

}
