/*
 * Copyright (C) 2017 Seht (R) HyX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    private SparseArray<CheckBox> checkboxes = new SparseArray<>(6);
    private SparseArray<Spinner> spinners = new SparseArray<>(6);

    public DialogPrefButtons(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        resources = context.getResources();
        preferences = new NotificationPreferences(getContext());

        setPersistent(false);
        setDialogLayoutResource(R.layout.view_dialog_pref_buttons);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        for (int pos = 1; pos <= 6; pos++) {
            int checkbox_id = resources.getIdentifier("pref_buttons_btn_" + pos + "_checked", "id", context.getPackageName());
            int spinner_id = resources.getIdentifier("pref_buttons_btn_" + pos + "_selection", "id", context.getPackageName());

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
            for (int pos = 1; pos <= 6; pos++) {
                preferences.edit().putBoolean("pref_buttons_btn_" + pos + "_checked", checkboxes.get(pos).isChecked()).commit();
                preferences.edit().putInt("pref_buttons_btn_" + pos + "_selection", spinners.get(pos).getSelectedItemPosition()).commit();
            }
        }
    }
}
