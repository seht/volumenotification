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

package net.hyx.app.volumenotification.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.Spinner;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.adapter.ButtonsIconSpinnerAdapter;
import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.model.Settings;

public class PrefIconsDialog extends DialogPreference {

    private Resources resources;
    private Settings settings;

    private SparseArray<Spinner> icons;

    public PrefIconsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        resources = context.getResources();
        settings = new Settings(context);

        icons = new SparseArray<>(NotificationFactory.BUTTONS_SELECTION_SIZE);

        setDialogLayoutResource(R.layout.view_dialog_pref_icons);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        String _package = getContext().getPackageName();
        String[] _icons = resources.getStringArray(R.array.pref_buttons_icon_entries);

        for (int pos = 1; pos <= NotificationFactory.BUTTONS_SELECTION_SIZE; pos++) {
            Spinner _icon = (Spinner) view.findViewById(resources.getIdentifier("pref_buttons_icon_btn_" + pos, "id", _package));
            _icon.setAdapter(new ButtonsIconSpinnerAdapter(getContext(), R.array.pref_buttons_icon_entries, _icons));

            if (settings.getButtonIcon(pos) < _icon.getAdapter().getCount()) {
                _icon.setSelection(settings.getButtonIcon(pos));
            }
            icons.append(pos, _icon);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            for (int pos = 1; pos <= icons.size(); pos++) {
                settings.edit().putInt("pref_buttons_icon_btn_" + pos, icons.get(pos).getSelectedItemPosition()).apply();
            }
        }
    }

}
