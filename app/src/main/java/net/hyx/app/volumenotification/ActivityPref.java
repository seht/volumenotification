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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class ActivityPref extends AppCompatActivity {

    protected NotificationPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new NotificationPreferences(this);

        setTheme(preferences.getAppTheme());

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NotificationSettingsFragment())
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class NotificationSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_notification, true);

            addPreferencesFromResource(R.xml.preferences_notification);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ((PreferenceCategory) findPreference("pref_cat_config"))
                        .removePreference(findPreference("pref_toggle_mute"));
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            NotificationFactory.startService(getActivity());
        }

    }

}
