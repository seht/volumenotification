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
import net.hyx.app.volumenotification.adapter.ButtonsIconSpinnerAdapter;
import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.model.ButtonsModel;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.object.ButtonsItem;

import java.io.Serializable;

public class ButtonsItemActivity extends AppCompatActivity {

    private ButtonsItemFragment frag;
    private ButtonsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ButtonsModel(this);
        ButtonsItem item = (ButtonsItem) getIntent().getExtras().getSerializable("item");
        frag = ButtonsItemFragment.newInstance(item);

        //setTheme(settings.getAppTheme());
        setContentView(R.layout.activity_main);
        setTitle(model.getDefaultButtonLabel(item.id));

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
        LinearLayout checked_layout = (LinearLayout) menu.findItem(R.id.item_btn_checked_layout).getActionView();
        Switch checked = (Switch) checked_layout.findViewById(R.id.menu_item_switch);
        checked.setChecked((frag.item.status > 0));
        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                frag.item.status = (isChecked) ? 1 : 0;
                model.saveButtonItem(frag.item);

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                model.saveButtonItem(frag.item);
                NotificationFactory.newInstance(this).startService();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            /*case R.id.menu_buttons_item_save:
                model.saveButtonItem(frag.item.position, frag.item);
                //NotificationFactory.newInstance(this).startService();
                return true;*/
            case R.id.menu_item_switch:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class ButtonsItemFragment extends Fragment {

        private ButtonsItem item;
        private ButtonsModel model;

        public static ButtonsItemFragment newInstance(Serializable item) {
            ButtonsItemFragment frag = new ButtonsItemFragment();
            Bundle args = new Bundle();
            args.putSerializable("item", item);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            item = (ButtonsItem) getArguments().getSerializable("item");
            model = new ButtonsModel(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttons_item, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            SettingsModel settings = new SettingsModel(getActivity());

            EditText label = (EditText) view.findViewById(R.id.item_btn_label);
            Spinner icon = (Spinner) view.findViewById(R.id.item_btn_icon);

            if (item.icon >= icon.getAdapter().getCount()) {
                // Defensive versioning icon list.
                item.icon = 0;
            }
            if (item.icon == 0) {
                item.icon = model.getDefaultButtonIcon(item.id);
            }

            //model.saveButtonItem(item.position, item);

            icon.setAdapter(new ButtonsIconSpinnerAdapter(getContext(),
                    R.array.pref_buttons_icon_entries,
                    settings.resources.getStringArray(R.array.pref_buttons_icon_entries)));

            label.setText(item.label);
            label.setHint(model.getDefaultButtonLabel(item.id));
            icon.setSelection(item.icon);

            label.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.label = s.toString();
                    model.saveButtonItem(item);
                }
            });

            icon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item.icon = position;
                    model.saveButtonItem(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

}
