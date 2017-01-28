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

    private Context context;
    private Resources resources;
    private NotificationManager manager;
    private NotificationPreferences preferences;
    private RemoteViews view;

    private int theme_color_background;
    private int theme_color_foreground;

    private NotificationFactory(Context context) {
        this.context = context;
        resources = context.getResources();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = new NotificationPreferences(context);
    }

    static void notify(Context context) {
        NotificationFactory self = new NotificationFactory(context);
        if (self.preferences.getEnabled()) {
            self.create();
        }
    }

    static void cancel(Context context) {
        NotificationFactory notification_factory = new NotificationFactory(context);
        notification_factory.manager.cancelAll();
    }

    static void startService(Context context) {
        context.startService(new Intent(context, NotificationService.class));
    }

    private void create() {

        view = new RemoteViews(context.getPackageName(), R.layout.view_layout_background);

        parseLayout();
        parseVolButtons();

        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setVisibility(preferences.getVisibility())
                .setPriority(preferences.getPriority())
                .setContent(view);

        if (preferences.getHideStatusIcon()) {
            notification_builder.setSmallIcon(android.R.color.transparent);
        } else {
            notification_builder.setSmallIcon(R.drawable.ic_launcher);
        }
        manager.cancelAll();
        manager.notify(1, notification_builder.build());
    }

    private void parseLayout() {

        String theme = preferences.getTheme();
        if (!theme.equals("theme_custom")) {
            int style_res = resources.getIdentifier("style_" + theme, "style", context.getPackageName());
            TypedArray style_attrs = context.obtainStyledAttributes(style_res, R.styleable.styleable);
            theme_color_background = style_attrs.getColor(R.styleable.styleable_color_background, Color.TRANSPARENT);
            theme_color_foreground = style_attrs.getColor(R.styleable.styleable_color_foreground, Color.TRANSPARENT);
            style_attrs.recycle();
        } else {
            theme_color_background = preferences.getThemeCustomBackgroundColor();
            theme_color_foreground = preferences.getThemeCustomForegroundColor();
        }
        if (!preferences.getTransparent()) {
            view.setInt(R.id.layout_background, "setBackgroundColor", theme_color_background);
        } else {
            view.setInt(R.id.layout_background, "setBackgroundColor", android.R.color.transparent);
        }
    }

    private void parseVolButtons() {

        view.removeAllViews(R.layout.view_layout_background);

        List<Integer> buttons = new ArrayList<>();

        for (int pos = 1; pos <= 6; pos++) {
            if (preferences.getButtonChecked(pos)) {
                int selection = preferences.getButtonSelection(pos);
                if (selection > 0 && !buttons.contains(selection)) {
                    buttons.add(selection);

                    int res = resources.getIdentifier("view_btn_" + selection, "layout", context.getPackageName());
                    int id = resources.getIdentifier("btn_" + selection, "id", context.getPackageName());
                    RemoteViews btn = new RemoteViews(context.getPackageName(), res);

                    int stream_type = volStreamType(selection);
                    int direction = volStreamDirection(stream_type);
                    Intent intent = new Intent(context, ReceiverAudioManager.class);
                    intent.putExtra("stream_type", stream_type);
                    intent.putExtra("direction", direction);

                    btn.setOnClickPendingIntent(id, PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    btn.setInt(id, "setColorFilter", theme_color_foreground);

                    view.addView(R.id.layout_background, btn);
                }
            }
        }
    }

    private int volStreamType(int button) {
        switch (button) {
            case 1:
                return AudioManager.STREAM_MUSIC;
            case 2:
                return AudioManager.STREAM_VOICE_CALL;
            case 3:
                return AudioManager.STREAM_NOTIFICATION;
            case 4:
                return AudioManager.STREAM_RING;
            case 5:
                return AudioManager.STREAM_SYSTEM;
            case 6:
                return AudioManager.STREAM_ALARM;
            default:
                return AudioManager.USE_DEFAULT_STREAM_TYPE;
        }
    }

    private int volStreamDirection(int stream_type) {
        if (preferences.getToggleMute() && stream_type == AudioManager.STREAM_MUSIC) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AudioManager.ADJUST_TOGGLE_MUTE;
            }
        }
        return AudioManager.ADJUST_SAME;
    }

}
