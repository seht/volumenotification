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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class DialogAlertNonce extends DialogFragment {

    private NotificationPreferences preferences;
    private int pref_key;
    private String message;

    static DialogAlertNonce newInstance(int pref_key, String message) {

        DialogAlertNonce frag = new DialogAlertNonce();
        Bundle args = new Bundle();
        args.putInt("pref_key", pref_key);
        args.putString("message", message);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new NotificationPreferences(getContext());

        pref_key = getArguments().getInt("pref_key");
        message = getArguments().getString("message");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_alert_nonce, null);
        TextView message_view = (TextView) view.findViewById(R.id.pref_dialog_alert_message);
        CheckBox nonce_checked = (CheckBox) view.findViewById(R.id.pref_dialog_alert_nonce_checked);

        message_view.setText(message);
        nonce_checked.setChecked(preferences.getDialogAlertNonceChecked(pref_key));
        nonce_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("pref_dialog_alert_nonce_checked_" + pref_key, isChecked).apply();
            }
        });

        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        return builder.create();
    }

}