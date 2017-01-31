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

class NotificationFactory {

    static final int buttons_selection_size = 6;
    private Resources resources;
    private NotificationManager manager;
    private NotificationPreferences preferences;

    private NotificationFactory(Context context) {
        resources = context.getResources();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = new NotificationPreferences(context);
    }

    static void notify(Context context) {
        NotificationFactory self = new NotificationFactory(context);
        if (self.preferences.getEnabled()) {
            self.create(context);
        }
    }

    static void cancel(Context context) {
        NotificationFactory self = new NotificationFactory(context);
        self.manager.cancelAll();
    }

    static void startService(Context context) {
        context.startService(new Intent(context, ServiceNotification.class));
    }

    static void setVolume(Context context, int selection) {
        NotificationFactory self = new NotificationFactory(context);
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .adjustStreamVolume(self.getVolStreamType(selection), self.getVolDirection(selection),
                        AudioManager.FLAG_SHOW_UI);
    }

    private void create(Context context) {

        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setPriority(getPriority())
                .setVisibility(getVisibility())
                .setCustomContentView(getCustomContentView(context));

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

    private RemoteViews getCustomContentView(Context context) {

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

        for (int pos = 1; pos <= buttons_selection_size; pos++) {
            if (preferences.getButtonChecked(pos)) {
                int sel = preferences.getButtonSelection(pos);
                if (sel > 0 && !buttons.contains(sel)) {
                    buttons.add(sel);

                    int btn_res = resources.getIdentifier("view_btn_sel_" + sel, "layout", context.getPackageName());
                    int btn_id = resources.getIdentifier("btn_sel_" + sel, "id", context.getPackageName());
                    RemoteViews btn = new RemoteViews(context.getPackageName(), btn_res);

                    Intent intent = new Intent(context, ReceiverSetVolume.class);
                    intent.putExtra("selection", sel);

                    PendingIntent event = PendingIntent.getBroadcast(context, btn_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    btn.setOnClickPendingIntent(btn_id, event);
                    btn.setInt(btn_id, "setColorFilter", icon_color);

                    view.addView(R.id.layout_buttons, btn);
                }
            }
        }
        return view;
    }

    private int getVolStreamType(int selection) {
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

    private int getVolDirection(int selection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (preferences.getToggleMute() && getVolStreamType(selection) == AudioManager.STREAM_MUSIC) {
                return AudioManager.ADJUST_TOGGLE_MUTE;
            }
        }
        return AudioManager.ADJUST_SAME;
    }

}
