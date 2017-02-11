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

package net.hyx.app.volumenotification.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import net.hyx.app.volumenotification.adapter.ButtonsListViewAdapter;
import net.hyx.app.volumenotification.dialog.NonceAlertDialog;
import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.helper.OnStartDragListener;
import net.hyx.app.volumenotification.helper.SimpleItemTouchHelperCallback;
import net.hyx.app.volumenotification.model.SettingsModel;

public class ButtonsListActivity extends AppCompatActivity {

    private static boolean created = false;
    private SettingsModel settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!created) {
            NotificationFactory.newInstance(this).create();
            created = true;
        }

        settings = new SettingsModel(this);

        //setTheme(settings.getAppTheme());
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new ButtonsListFragment())
                .commit();

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!settings.getDialogAlertNonceChecked(1)) {
                DialogFragment newFragment = NonceAlertDialog.newInstance(1, getResources().getString(R.string.target_api_welcome_message_N));
                newFragment.show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_dark_app_theme).setChecked(settings.getAppThemeDark());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_pref:
                startActivity(new Intent(this, PrefActivity.class));
                return true;
            case R.id.menu_dark_app_theme:
                boolean dark_theme = !item.isChecked();
                settings.edit().putBoolean("pref_dark_app_theme", dark_theme).apply();
                item.setChecked(dark_theme);
                setTheme(settings.getAppTheme());
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

    public static class ButtonsListFragment extends Fragment implements OnStartDragListener {

        private ItemTouchHelper mItemTouchHelper;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new RecyclerView(container.getContext());
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ButtonsListViewAdapter adapter = new ButtonsListViewAdapter(getActivity(), this);

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
        }

        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            mItemTouchHelper.startDrag(viewHolder);
        }

    }

}
