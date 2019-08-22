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

package net.hyx.app.volumenotification.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.model.SettingsModel;

public class NonceAlertDialog extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_TITLE = "title";

    public static NonceAlertDialog newInstance(int id, String message, String title) {
        NonceAlertDialog dialog = new NonceAlertDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null || getActivity() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        final int id = getArguments().getInt(ARG_ID);
        String message = getArguments().getString(ARG_MESSAGE);
        String title = getArguments().getString(ARG_TITLE);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        final SettingsModel settings = new SettingsModel(getActivity());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_alert_nonce, null);
        TextView messageView = view.findViewById(R.id.pref_dialog_alert_message);
        messageView.setText(message);

        if (id != 0) {
            CheckBox nonceInput = view.findViewById(R.id.pref_nonce_input);
            nonceInput.setChecked(settings.getDialogAlertNonceChecked(id));
            nonceInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settings.getPreferences().edit().putBoolean("pref_dialog_alert_nonce_checked_" + id, isChecked).apply();
                }
            });
        } else {
            LinearLayout nonceLayer = view.findViewById(R.id.pref_nonce_wrapper);
            nonceLayer.setVisibility(View.GONE);
        }

        dialog.setView(view);
        dialog.setTitle(title);
        dialog.setPositiveButton(android.R.string.ok, null);
        return dialog.create();
    }

}