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

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.adapter.IconSpinnerAdapter;
import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;

import java.io.Serializable;

public class ItemViewActivity extends AppCompatActivity {

    private ItemFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsModel settings = new SettingsModel(getApplicationContext());
        VolumeControl item = (VolumeControl) getIntent().getSerializableExtra(VolumeControlModel.ITEM_FIELD);

        fragment = ItemFragment.newInstance(item);

        setTheme(settings.getAppTheme());
        setTitle(item.label);
        setContentView(R.layout.activity_frame_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_check_24px);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buttons_item, menu);
        LinearLayout actionLayout = (LinearLayout) menu.findItem(R.id.item_btn_checked_layout).getActionView();
        Switch statusInput = actionLayout.findViewById(R.id.menu_item_switch);
        statusInput.setChecked((fragment.item.status == 1));
        statusInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fragment.item.status = (isChecked) ? 1 : 0;
                fragment.model.saveItem(fragment.item);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        fragment.model.saveItem(fragment.item);
        NotificationServiceController.newInstance(getApplicationContext()).startService();
        finish();
        return true;
    }

    public static class ItemFragment extends Fragment {

        private VolumeControl item;
        private VolumeControlModel model;
        private SettingsModel settings;

        public static ItemFragment newInstance(Serializable item) {
            ItemFragment fragment = new ItemFragment();
            Bundle args = new Bundle();
            args.putSerializable(VolumeControlModel.ITEM_FIELD, item);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null && getContext() != null) {
                model = new VolumeControlModel(getContext());
                settings = new SettingsModel(getContext());
                item = (VolumeControl) getArguments().getSerializable(VolumeControlModel.ITEM_FIELD);
            }
        }

        @Override
        public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_item_view, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Spinner iconInput = view.findViewById(R.id.pref_icon_input);
            EditText labelInput = view.findViewById(R.id.pref_label_input);
            VolumeControl defaultItem = model.getDefaultControls().get(item.type);

            IconSpinnerAdapter adapter = new IconSpinnerAdapter(getContext(), settings.getIconEntries(), model);
            iconInput.setAdapter(adapter);

            iconInput.setSelection(adapter.getPosition(item.icon));
            labelInput.setText(item.label);
            labelInput.setHint(defaultItem.label);

            labelInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.label = s.toString();
                    model.saveItem(item);
                }
            });

            iconInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item.icon = parent.getItemAtPosition(position).toString();
                    model.saveItem(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

    }

}
