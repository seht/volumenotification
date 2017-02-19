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

package net.hyx.app.volumenotification.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.adapter.ListViewAdapter;
import net.hyx.app.volumenotification.dialog.NonceAlertDialog;
import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.helper.ItemTouchHelperCallback;
import net.hyx.app.volumenotification.helper.OnStartDragListener;
import net.hyx.app.volumenotification.model.SettingsModel;

public class ListViewActivity extends AppCompatActivity {

    private static boolean _created = false;
    private SettingsModel settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationFactory.newInstance(this).create();

        settings = new SettingsModel(this);

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
        _created = true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!_created && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!settings.getDialogAlertNonceChecked(1)) {
                DialogFragment dialogFragment = NonceAlertDialog.newInstance(1,
                        settings.getResources().getString(R.string.target_api_welcome_message_N));
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
                settings.edit().putBoolean("pref_dark_app_theme", darkTheme).commit();
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

    public static class ListViewFragment extends Fragment implements OnStartDragListener {

        private ItemTouchHelper itemTouchHelper;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new RecyclerView(container.getContext());
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ListViewAdapter adapter = new ListViewAdapter(getContext(), this);

            RecyclerView _view = (RecyclerView) view;
            _view.setHasFixedSize(true);
            _view.setAdapter(adapter);
            _view.setLayoutManager(new LinearLayoutManager(getContext()));

            itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
            itemTouchHelper.attachToRecyclerView(_view);
        }

        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            itemTouchHelper.startDrag(viewHolder);
        }

    }

}
