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
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

class DialogPrefCustomTheme extends DialogPreference {

    private NotificationPreferences preferences;

    private EditText background_color_edit;
    private EditText icon_color_edit;

    DialogPrefCustomTheme(Context context, AttributeSet attrs) {
        super(context, attrs);

        preferences = new NotificationPreferences(context);

        setDialogLayoutResource(R.layout.view_dialog_pref_custom_theme);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        background_color_edit = (EditText) view.findViewById(R.id.pref_custom_theme_background_color);
        icon_color_edit = (EditText) view.findViewById(R.id.pref_custom_theme_icon_color);
        background_color_edit.setText(preferences.getCustomThemeBackgroundColor());
        icon_color_edit.setText(preferences.getCustomThemeIconColor());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            String background_color_value = background_color_edit.getText().toString();
            String icon_color_value = icon_color_edit.getText().toString();
            int background_color = preferences.getColor(background_color_value);
            int icon_color = preferences.getColor(icon_color_value);

            if (background_color != 0) {
                preferences.edit().putString("pref_custom_theme_background_color", background_color_value).apply();
            }
            if (icon_color != 0) {
                preferences.edit().putString("pref_custom_theme_icon_color", icon_color_value).apply();
            }
            if (background_color == 0 || icon_color == 0) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.pref_custom_theme_color_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }
}
