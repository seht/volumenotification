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

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

public class DialogPrefButtons extends DialogPreference {

    private Context context;
    private Resources resources;
    private NotificationPreferences preferences;
    private SparseArray<CheckBox> checkboxes;
    private SparseArray<Spinner> spinners;

    public DialogPrefButtons(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        resources = context.getResources();
        preferences = new NotificationPreferences(getContext());
        checkboxes = new SparseArray<>(NotificationFactory.buttons_selection_size);
        spinners = new SparseArray<>(NotificationFactory.buttons_selection_size);

        setPersistent(false);
        setDialogLayoutResource(R.layout.view_dialog_pref_buttons);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        for (int pos = 1; pos <= NotificationFactory.buttons_selection_size; pos++) {
            int checkbox_id = resources.getIdentifier("pref_buttons_checked_btn_" + pos, "id", context.getPackageName());
            int spinner_id = resources.getIdentifier("pref_buttons_selection_btn_" + pos, "id", context.getPackageName());

            CheckBox checkbox = (CheckBox) view.findViewById(checkbox_id);
            Spinner spinner = (Spinner) view.findViewById(spinner_id);
            checkbox.setChecked(preferences.getButtonChecked(pos));
            spinner.setSelection(preferences.getButtonSelection(pos));

            checkboxes.append(pos, checkbox);
            spinners.append(pos, spinner);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            for (int pos = 1; pos <= checkboxes.size(); pos++) {
                preferences.edit().putBoolean("pref_buttons_checked_btn_" + pos, checkboxes.get(pos).isChecked()).commit();
                preferences.edit().putInt("pref_buttons_selection_btn_" + pos, spinners.get(pos).getSelectedItemPosition()).commit();
            }
        }
    }
}
