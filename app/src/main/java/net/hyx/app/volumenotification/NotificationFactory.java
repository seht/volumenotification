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

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class NotificationFactory {

    static final int BUTTONS_SELECTION_SIZE = 6;
    private static final int[] STREAM_TYPES = {
            AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_VOICE_CALL,
            AudioManager.STREAM_RING,
            AudioManager.STREAM_ALARM,
            AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_SYSTEM
    };
    private static boolean _mute = false;
    private static boolean _silent = false;
    private static String _package;
    private Context context;
    private Resources resources;
    private NotificationManager manager;
    private AudioManager audio;
    private PrefSettings settings;

    private NotificationFactory(Context context) {
        this.context = context;
        resources = context.getResources();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        settings = new PrefSettings(context);
        _package = context.getPackageName();
    }

    public static NotificationFactory newInstance(Context context) {
        return new NotificationFactory(context);
    }

    void startService() {
        context.startService(new Intent(context, ServiceNotification.class));
    }

    void setVolume(int position) {
        int selection = settings.getButtonSelection(position);
        int direction = AudioManager.ADJUST_SAME;
        int type = STREAM_TYPES[selection];

        if (type == AudioManager.STREAM_MUSIC && settings.getToggleMute()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                direction = AudioManager.ADJUST_TOGGLE_MUTE;
            } else {
                _mute = !_mute;
                audio.setStreamMute(type, _mute);
            }
        } else if (type == AudioManager.STREAM_RING && settings.getToggleSilent()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                direction = AudioManager.ADJUST_TOGGLE_MUTE;
            } else {
                _silent = !_silent;
                audio.setStreamMute(type, _silent);
            }
        }
        audio.adjustStreamVolume(type, direction, AudioManager.FLAG_SHOW_UI);
    }

    void create() {
        cancel();
        if (settings.getEnabled()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setOngoing(true)
                    .setPriority(getPriority())
                    .setVisibility(getVisibility())
                    .setCustomContentView(getCustomContentView())
                    .setSmallIcon((settings.getHideStatus()) ? android.R.color.transparent : R.drawable.ic_launcher);
            manager.notify(1, builder.build());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (int pos = 1; pos <= BUTTONS_SELECTION_SIZE; pos++) {
                TileService.requestListeningState(context, new ComponentName(_package, _package + ".ServiceTile" + pos));
            }
        }
    }

    void cancel() {
        manager.cancelAll();
    }

    @TargetApi(Build.VERSION_CODES.N)
    void updateTile(Tile tile, int position) {
        //tile.setIcon(Icon.createWithResource(context, settings.getDrawable(context, R.array.pref_buttons_icons_entries, settings.getButtonIcon(position))));
        //tile.setLabel(settings.getButtonLabel(position));
        if (settings.getButtonChecked(position)) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }

    private int getPriority() {
        if (settings.getTopPriority()) {
            return NotificationCompat.PRIORITY_MAX;
        }
        return NotificationCompat.PRIORITY_MIN;
    }

    private int getVisibility() {
        if (settings.getHideLocked()) {
            return NotificationCompat.VISIBILITY_SECRET;
        }
        return NotificationCompat.VISIBILITY_PUBLIC;
    }

    private RemoteViews getCustomContentView() {

        RemoteViews view = new RemoteViews(_package, R.layout.view_layout_notification);
        List<Integer> buttons = new ArrayList<>();
        int theme = resources.getIdentifier("style_" + settings.getTheme(), "style", _package);
        int background_color;
        int icon_color;

        if (theme != 0) {
            TypedArray attrs = context.obtainStyledAttributes(theme, R.styleable.styleable);
            background_color = attrs.getColor(R.styleable.styleable_background_color, 0);
            icon_color = attrs.getColor(R.styleable.styleable_icon_color, 0);
            attrs.recycle();
        } else {
            background_color = settings.getColor(settings.getCustomThemeBackgroundColor());
            icon_color = settings.getColor(settings.getCustomThemeIconColor());
        }
        if (settings.getTransparent()) {
            background_color = android.R.color.transparent;
        }
        view.setInt(R.id.layout_background, "setBackgroundColor", background_color);
        view.removeAllViews(R.id.layout_buttons);

        for (int pos = 1; pos <= BUTTONS_SELECTION_SIZE; pos++) {
            int sel = settings.getButtonSelection(pos);
            if (!settings.getButtonChecked(pos) || buttons.contains(sel) || sel >= BUTTONS_SELECTION_SIZE) {
                continue;
            }
            buttons.add(sel);
            int btn_sel = sel + 1;
            int btn_id = resources.getIdentifier("btn_sel_" + btn_sel, "id", _package);
            RemoteViews btn = new RemoteViews(_package, resources.getIdentifier("view_btn_sel_" + btn_sel, "layout", _package));
            Intent intent = new Intent(context, ReceiverSetVolume.class).putExtra("position", pos);
            PendingIntent event = PendingIntent.getBroadcast(context, btn_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            btn.setOnClickPendingIntent(btn_id, event);
            //btn.setInt(btn_id, "setImageResource", settings.getDrawable(context, R.array.pref_buttons_icons_entries, settings.getButtonIcon(pos)));
            btn.setInt(btn_id, "setColorFilter", icon_color);
            view.addView(R.id.layout_buttons, btn);
        }
        return view;
    }

}
