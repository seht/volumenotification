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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityMain extends AppCompatActivity {

    protected NotificationPreferences preferences;

    private int getAppTheme() {
        if (preferences.getAppThemeDark()) {
            return R.style.style_app_theme_dark;
        }
        return R.style.style_app_theme_light;
    }

    private void setAppTheme(int style_res) {
        setTheme(style_res);
    }

    private void setAppTheme() {
        setAppTheme(getAppTheme());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new NotificationPreferences(this);

        setAppTheme();

        NotificationFactory.startService(this);

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_toggle).setChecked(preferences.getEnabled());
        menu.findItem(R.id.menu_dark_app_theme).setChecked(preferences.getAppThemeDark());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_toggle:
                boolean enabled = !item.isChecked();
                preferences.edit().putBoolean("pref_enabled", enabled).commit();
                item.setChecked(enabled);
                if (enabled) {
                    NotificationFactory.startService(this);
                } else {
                    NotificationFactory.cancel(this);
                }
                return true;
            case R.id.menu_pref:
                startActivity(new Intent(this, ActivityPref.class));
                return true;
            case R.id.menu_dark_app_theme:
                boolean dark_theme = !item.isChecked();
                preferences.edit().putBoolean("pref_dark_app_theme", dark_theme).commit();
                item.setChecked(dark_theme);
                setAppTheme();
                recreate();
                return true;
            case R.id.menu_about:
                Uri url = Uri.parse(getResources().getString(R.string.menu_about_url));
                startActivity(new Intent(Intent.ACTION_VIEW, url));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}