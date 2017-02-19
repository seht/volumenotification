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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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
import net.hyx.app.volumenotification.adapter.ListViewAdapter;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;

import java.io.Serializable;

public class ItemViewActivity extends AppCompatActivity {

    private ButtonsItemFragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VolumeControl item = (VolumeControl) getIntent().getExtras().getSerializable(ListViewAdapter.EXTRA_ITEM);
        if (item == null) {
            finish();
            return;
        }
        SettingsModel settings = new SettingsModel(this);
        frag = ButtonsItemFragment.newInstance(item);

        setTheme(settings.getAppTheme());
        setTitle(item.label);
        setContentView(R.layout.activity_frame_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, frag)
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buttons_item, menu);
        LinearLayout actionLayout = (LinearLayout) menu.findItem(R.id.item_btn_checked_layout).getActionView();
        Switch statusInput = (Switch) actionLayout.findViewById(R.id.menu_item_switch);
        statusInput.setChecked((frag.item.status == 1));
        statusInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                frag.item.status = (isChecked) ? 1 : 0;
                frag.model.saveItem(frag.item);

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                frag.model.saveItem(frag.item, false);
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ButtonsItemFragment extends Fragment {

        private static final String ARG_ITEM = "item";

        private VolumeControl item;
        private VolumeControlModel model;

        public static ButtonsItemFragment newInstance(Serializable item) {
            ButtonsItemFragment frag = new ButtonsItemFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_ITEM, item);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            model = new VolumeControlModel(getActivity());
            item = (VolumeControl) getArguments().getSerializable(ARG_ITEM);
            item = model.getParseItem(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_item_view, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            EditText labelInput = (EditText) view.findViewById(R.id.pref_label_input);
            Spinner iconInput = (Spinner) view.findViewById(R.id.pref_icon_input);

            iconInput.setAdapter(new IconSpinnerAdapter(getContext(),
                    R.array.pref_icon_entries,
                    model.getIconEntries()));

            labelInput.setText(item.label);
            labelInput.setHint(model.getDefaultLabel(item.id));
            iconInput.setSelection(item.icon);

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
                    item.icon = position;
                    model.saveItem(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

}
