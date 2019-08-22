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

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.adapter.RecyclerViewAdapter;
import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.dialog.NonceDialogFragment;
import net.hyx.app.volumenotification.adapter.ItemTouchAdapter;
import net.hyx.app.volumenotification.helper.DragHandleListener;
import net.hyx.app.volumenotification.model.SettingsModel;

public class RecyclerViewActivity extends AppCompatActivity {

    private SettingsModel settings;

    private final static int DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new SettingsModel(getApplicationContext());

        setTheme(settings.getAppTheme());
        setContentView(R.layout.activity_frame_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new ListViewFragment())
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationServiceController.newInstance(getApplicationContext()).startService();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!settings.getNonceDialogCancelled(DIALOG_ID)) {
                DialogFragment dialogFragment = NonceDialogFragment.newInstance(DIALOG_ID,
                        settings.getResources().getString(R.string.target_api_welcome_message_N),
                        settings.getResources().getString(R.string.target_api_welcome_title_N));
                dialogFragment.show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_dark_app_theme).setChecked(settings.getAppThemeDark());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.menu_pref:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_dark_app_theme:
                boolean darkTheme = !item.isChecked();
                settings.getPreferences().edit().putBoolean("pref_dark_app_theme", darkTheme).commit();
                item.setChecked(darkTheme);
                setTheme(settings.getAppTheme());
                recreate();
                break;
            case R.id.menu_about:
                Snackbar.make(findViewById(android.R.id.content), R.string.hint_about_message, Snackbar.LENGTH_LONG)
                        .setAction(R.string.hint_about_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri url = Uri.parse(getResources().getString(R.string.menu_about_url));
                                startActivity(new Intent(Intent.ACTION_VIEW, url));
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ListViewFragment extends Fragment implements DragHandleListener {

        private ItemTouchHelper itemTouchHelper;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new RecyclerView(container.getContext());
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(view.getContext(), this);

            RecyclerView listView = (RecyclerView) view;
            listView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            listView.setAdapter(adapter);
            listView.setHasFixedSize(true);

            itemTouchHelper = new ItemTouchHelper(new ItemTouchAdapter(adapter));
            itemTouchHelper.attachToRecyclerView(listView);
        }

        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            itemTouchHelper.startDrag(viewHolder);
        }

    }

}
