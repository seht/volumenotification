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

package net.hyx.app.volumenotification.controller;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.AudioManagerModel;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.receiver.CreateNotificationReceiver;
import net.hyx.app.volumenotification.receiver.SetVolumeReceiver;

import java.util.List;

public class NotificationController {

    private final String packageName;
    private final Context context;
    private final NotificationManager manager;
    private final SettingsModel settings;
    private final VolumeControlModel volumeControlModel;
    private final AudioManagerModel audioManagerModel;
    private final List<VolumeControl> items;

    public NotificationController(Context context) {
        this.context = context;
        packageName = context.getPackageName();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        settings = new SettingsModel(context);
        volumeControlModel = new VolumeControlModel(context);
        audioManagerModel = new AudioManagerModel(context);
        items = volumeControlModel.getList();
    }

    public static NotificationController newInstance(Context context) {
        return new NotificationController(context);
    }

    public AudioManagerModel audioManagerModel() {
        return audioManagerModel;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void requestListeningTiles() {
        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = items.get(pos);
            TileService.requestListeningState(context, new ComponentName(context, packageName + ".service.TileService" + item.id));
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void updateTile(Tile tile, int id) {
        VolumeControl item = volumeControlModel.parseItem(volumeControlModel.getItemById(id));
        if (item != null && tile != null) {
            tile.setIcon(Icon.createWithResource(context, volumeControlModel.getIconDrawable(item.icon)));
            tile.setLabel(item.label);
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }

    public void create() {
        manager.cancelAll();
        if (settings.getEnabled()) {
            int id = 1;
            String channelId = packageName + id;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, packageName, getImportance());
                notificationChannel.setShowBadge(settings.getHideStatus());
                manager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setOngoing(true)
                    .setPriority(getPriority())
                    .setVisibility(getVisibility())
                    .setCustomContentView(getCustomContentView())
                    .setSmallIcon(settings.getStatusIcon())
                    .setColor(Color.TRANSPARENT);

            PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, CreateNotificationReceiver.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setDeleteIntent(deleteIntent);
            manager.notify(id, builder.build());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestListeningTiles();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private int getImportance() {
        if (settings.getTopPriority()) {
            return NotificationManager.IMPORTANCE_MAX;
        }
        return NotificationManager.IMPORTANCE_MIN;
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
        RemoteViews view = new RemoteViews(packageName, R.layout.view_layout_notification);
        int style = settings.getResources().getIdentifier("style_" + settings.getTheme(), "style", packageName);
        int backgroundColor;
        int iconColor;

        if (style != 0) {
            backgroundColor = settings.getStyleAttributeColor(context.getTheme(), style, android.R.attr.colorBackground);
            iconColor = settings.getStyleAttributeColor(context.getTheme(), style, android.R.attr.colorForeground);
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
            VolumeControl item = volumeControlModel.parseItem(items.get(pos));
            if (item.status == 0) {
                continue;
            }
            RemoteViews btn = new RemoteViews(packageName, R.layout.view_widget_volume_control);
            PendingIntent event = PendingIntent.getBroadcast(context, item.id,
                    new Intent(context, SetVolumeReceiver.class).putExtra(VolumeControlModel.EXTRA_ITEM_ID, item.id),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            btn.setOnClickPendingIntent(R.id.btn_volume_control, event);
            btn.setInt(R.id.btn_volume_control, "setImageResource", volumeControlModel.getIconDrawable(item.icon));
            btn.setInt(R.id.btn_volume_control, "setColorFilter", iconColor);
            view.addView(R.id.volume_control_wrapper, btn);
        }

        return view;
    }

}
