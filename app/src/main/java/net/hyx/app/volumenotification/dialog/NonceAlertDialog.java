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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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

    private int id;
    private String message;
    private SettingsModel settings;

    /*static DialogAlertNonce newInstance(String message) {
        return newInstance(0, message);
    }*/

    public static NonceAlertDialog newInstance(int id, String message) {
        NonceAlertDialog frag = new NonceAlertDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(ARG_ID);
        message = getArguments().getString(ARG_MESSAGE);
        settings = new SettingsModel(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_alert_nonce, null);
        TextView messageView = (TextView) view.findViewById(R.id.pref_dialog_alert_message);
        messageView.setText(message);

        if (id != 0) {
            CheckBox nonceInput = (CheckBox) view.findViewById(R.id.pref_dialog_alert_nonce_checked);
            nonceInput.setChecked(settings.getDialogAlertNonceChecked(id));
            nonceInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settings.edit().putBoolean("pref_dialog_alert_nonce_checked_" + id, isChecked).commit();
                }
            });
        } else {
            LinearLayout nonceLayer = (LinearLayout) view.findViewById(R.id.pref_dialog_alert_nonce);
            nonceLayer.setVisibility(View.GONE);
        }

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}