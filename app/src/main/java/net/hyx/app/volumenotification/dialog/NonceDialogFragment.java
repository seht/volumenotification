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
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import net.hyx.app.volumenotification.model.SettingsModel;

public class NonceDialogFragment extends DialogFragment {

    private static final String DIALOG_ID_FIELD = "id";
    private static final String MESSAGE_FIELD = "message";
    private static final String TITLE_FIELD = "title";

    public static NonceDialogFragment newInstance(int id, String message, String title) {
        NonceDialogFragment dialog = new NonceDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_FIELD, id);
        args.putString(MESSAGE_FIELD, message);
        args.putString(TITLE_FIELD, title);
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

        final int dialogId = getArguments().getInt(DIALOG_ID_FIELD);
        final String message = getArguments().getString(MESSAGE_FIELD);
        final String title = getArguments().getString(TITLE_FIELD);
        final SettingsModel settings = new SettingsModel(getActivity());

        return new AlertDialog.Builder(getActivity()).setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        settings.setNonceDialogCancelled(dialogId, true);
                    }
                }).create();
    }

}