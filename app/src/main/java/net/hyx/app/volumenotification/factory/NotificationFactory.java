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

package net.hyx.app.volumenotification.factory;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.receiver.CreateNotificationReceiver;
import net.hyx.app.volumenotification.receiver.SetVolumeReceiver;

import java.util.List;

public class NotificationFactory {

    public static final String EXTRA_ITEM_ID = "item_id";

    private static final int STREAM_BLUETOOTH = 6;
    private static final int[] STREAM_TYPES = {
            AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_VOICE_CALL,
            AudioManager.STREAM_RING,
            AudioManager.STREAM_ALARM,
            AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_SYSTEM,
            STREAM_BLUETOOTH
    };
    private static String _package;
    private final Context context;
    private final NotificationManager manager;
    private final AudioManager audio;
    private final SettingsModel settings;
    private final VolumeControlModel model;
    private List<VolumeControl> items;

    private NotificationFactory(Context context) {
        this.context = context;
        manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        audio = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        settings = new SettingsModel(this.context);
        model = new VolumeControlModel(this.context);
        items = model.getList();
        _package = this.context.getPackageName();
    }

    public static NotificationFactory newInstance(Context context) {
        return new NotificationFactory(context);
    }

    public void setVolume(int id) {
        int type = getStreamType(id);
        audio.adjustStreamVolume(type, getStreamFlag(type), AudioManager.FLAG_SHOW_UI);
    }

    public void create() {
        int id = 1;
        cancel();
        if (settings.getEnabled()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setOngoing(true)
                    .setPriority(getPriority())
                    .setVisibility(getVisibility())
                    .setCustomContentView(getCustomContentView())
                    .setSmallIcon((settings.getHideStatus()) ? android.R.color.transparent : R.drawable.ic_launcher)
                    .setColor(Color.TRANSPARENT);

            PendingIntent deleteIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                    new Intent(context, CreateNotificationReceiver.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setDeleteIntent(deleteIntent);
            manager.notify(id, builder.build());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestListeningTiles();
        }
    }

    public void cancel() {
        manager.cancelAll();
    }


    @TargetApi(Build.VERSION_CODES.N)
    public void requestListeningTiles() {
        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = items.get(pos);
            TileService.requestListeningState(context, new ComponentName(context, _package + ".service.TileService" + item.id));
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void updateTile(Tile tile, int id) {
        VolumeControl item = model.parseItem(model.getItemById(id));
        if (item != null && tile != null) {
            tile.setIcon(Icon.createWithResource(context, model.getIconDrawable(item.icon)));
            tile.setLabel(item.label);
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }

    private int getStreamType(int id) {
        int index = id - 1;
        if (index >= 0 && index < STREAM_TYPES.length) {
            return STREAM_TYPES[index];
        }
        return AudioManager.USE_DEFAULT_STREAM_TYPE;
    }

    private int getStreamFlag(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((type == AudioManager.STREAM_MUSIC && settings.getToggleMute()) || (type == AudioManager.STREAM_RING && settings.getToggleSilent())) {
                return AudioManager.ADJUST_TOGGLE_MUTE;
            }
        }
        return AudioManager.ADJUST_SAME;
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
        int style = settings.getResources().getIdentifier("style_" + settings.getTheme(), "style", _package);
        int backgroundColor;
        int iconColor;

        if (style != 0) {
            backgroundColor = settings.getStyleAttributeColor(style, android.R.attr.colorBackground);
            iconColor = settings.getStyleAttributeColor(style, android.R.attr.colorForeground);
        } else {
            backgroundColor = settings.getColor(settings.getCustomThemeBackgroundColor());
            iconColor = settings.getColor(settings.getCustomThemeIconColor());
        }
        if (settings.getTranslucent()) {
            backgroundColor = android.R.color.transparent;
        }
        view.setInt(R.id.notification_layout, "setBackgroundColor", backgroundColor);
        view.removeAllViews(R.id.volume_control_wrapper);

        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = model.parseItem(items.get(pos));
            if (item.status == 0) {
                continue;
            }
            RemoteViews btn = new RemoteViews(_package, R.layout.view_widget_volume_control);
            PendingIntent event = PendingIntent.getBroadcast(context.getApplicationContext(), item.id,
                    new Intent(context, SetVolumeReceiver.class).putExtra(EXTRA_ITEM_ID, item.id),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            btn.setOnClickPendingIntent(R.id.btn_volume_control, event);
            btn.setInt(R.id.btn_volume_control, "setImageResource", model.getIconDrawable(item.icon));
            btn.setInt(R.id.btn_volume_control, "setColorFilter", iconColor);
            view.addView(R.id.volume_control_wrapper, btn);
        }

        return view;
    }

}
