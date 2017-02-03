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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class NotificationFactory {

    static final int BUTTONS_SELECTION_SIZE = 6;
    private static boolean _mute = false;
    private Context context;
    private Resources resources;
    private NotificationManager manager;
    private NotificationPreferences preferences;

    private NotificationFactory(Context context) {
        this.context = context;
        resources = context.getResources();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = new NotificationPreferences(context);
    }

    public static NotificationFactory newInstance(Context context) {
        return new NotificationFactory(context);
    }

    void startService() {
        context.startService(new Intent(context, ServiceNotification.class));
    }

    void setVolume(int selection) {

        AudioManager audio_manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int direction = AudioManager.ADJUST_SAME;
        int stream_type = getStreamType(selection);

        if (preferences.getToggleMute() && stream_type == AudioManager.STREAM_MUSIC) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                direction = AudioManager.ADJUST_TOGGLE_MUTE;
            } else {
                _mute = !_mute;
                audio_manager.setStreamMute(stream_type, _mute);
            }
        }
        if (preferences.getToggleSilent() && stream_type == AudioManager.STREAM_RING) {
            if (audio_manager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                audio_manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                audio_manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }
        audio_manager.adjustStreamVolume(stream_type, direction, AudioManager.FLAG_SHOW_UI);
    }

    void cancel() {
        manager.cancelAll();
    }

    void create() {

        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setPriority(getPriority())
                .setVisibility(getVisibility())
                .setCustomContentView(getCustomContentView());

        if (preferences.getHideStatus()) {
            notification_builder.setSmallIcon(android.R.color.transparent);
        } else {
            notification_builder.setSmallIcon(R.drawable.ic_launcher);
        }

        manager.cancelAll();
        manager.notify(1, notification_builder.build());
    }

    private int getPriority() {
        if (preferences.getTopPriority()) {
            return NotificationCompat.PRIORITY_MAX;
        }
        return NotificationCompat.PRIORITY_MIN;
    }

    private int getVisibility() {
        if (preferences.getHideLocked()) {
            return NotificationCompat.VISIBILITY_SECRET;
        }
        return NotificationCompat.VISIBILITY_PUBLIC;
    }

    private RemoteViews getCustomContentView() {

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.view_layout_notification);
        String theme = preferences.getTheme();
        List<Integer> buttons = new ArrayList<>();
        int background_color;
        int icon_color;

        if (!theme.equals("theme_custom")) {
            int theme_res = resources.getIdentifier("style_" + theme, "style", context.getPackageName());
            TypedArray attrs = context.obtainStyledAttributes(theme_res, R.styleable.styleable);
            background_color = attrs.getColor(R.styleable.styleable_background_color, Color.TRANSPARENT);
            icon_color = attrs.getColor(R.styleable.styleable_icon_color, Color.TRANSPARENT);
            attrs.recycle();
        } else {
            background_color = preferences.getColor(preferences.getCustomThemeBackgroundColor());
            icon_color = preferences.getColor(preferences.getCustomThemeIconColor());
        }

        if (!preferences.getTransparent()) {
            view.setInt(R.id.layout_background, "setBackgroundColor", background_color);
        } else {
            view.setInt(R.id.layout_background, "setBackgroundColor", android.R.color.transparent);
        }

        view.removeAllViews(R.id.layout_buttons);

        for (int pos = 1; pos <= BUTTONS_SELECTION_SIZE; pos++) {
            int sel = preferences.getButtonSelection(pos);
            if (!preferences.getButtonChecked(pos) || sel == 0 || buttons.contains(sel)) {
                continue;
            }
            buttons.add(sel);
            int btn_id = resources.getIdentifier("btn_sel_" + sel, "id", context.getPackageName());
            RemoteViews btn = new RemoteViews(context.getPackageName(), resources.getIdentifier("view_btn_sel_" + sel, "layout", context.getPackageName()));

            Intent intent = new Intent(context, ReceiverSetVolume.class);
            intent.putExtra("selection", sel);

            PendingIntent event = PendingIntent.getBroadcast(context, btn_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            btn.setOnClickPendingIntent(btn_id, event);
            btn.setInt(btn_id, "setColorFilter", icon_color);

            view.addView(R.id.layout_buttons, btn);
        }
        return view;
    }

    private int getStreamType(int selection) {
        switch (selection) {
            case 1:
                return AudioManager.STREAM_MUSIC;
            case 2:
                return AudioManager.STREAM_VOICE_CALL;
            case 3:
                return AudioManager.STREAM_RING;
            case 4:
                return AudioManager.STREAM_NOTIFICATION;
            case 5:
                return AudioManager.STREAM_ALARM;
            case 6:
                return AudioManager.STREAM_SYSTEM;
            default:
                return AudioManager.USE_DEFAULT_STREAM_TYPE;
        }
    }

}
