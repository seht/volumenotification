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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.model.Settings;
import net.hyx.app.volumenotification.object.ButtonsItem;

import java.io.Serializable;

public class ButtonsItemActivity extends AppCompatActivity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //NotificationFactory.newInstance(this).startService();

        settings = new Settings(this);

        setTheme(settings.getAppTheme());
        setContentView(R.layout.activity_main);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, FragmentButtonsItem.newInstance(getIntent().getExtras().getSerializable("item")))
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        System.out.println();

    }

    public static class FragmentButtonsItem extends Fragment {

        private ButtonsItem item;

        public static final FragmentButtonsItem newInstance(Serializable item) {
            FragmentButtonsItem frag = new FragmentButtonsItem();
            Bundle args = new Bundle();
            args.putSerializable("item", item);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            item = (ButtonsItem) getArguments().getSerializable("item");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttons_item, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //intent.getExtras().getSerializable("item");

        }

    }

}
