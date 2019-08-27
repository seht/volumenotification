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
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.receiver.AdjustVolumeReceiver;
import net.hyx.app.volumenotification.receiver.StartServiceReceiver;

import java.util.List;

public class NotificationFactory {

    private final String packageName;
    private final Context context;
    private final NotificationManager manager;
    private final SettingsModel settings;
    private final VolumeControlModel volumeControlModel;
    private final List<VolumeControl> items;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "net.hyx.app.volumenotification.channel.DEFAULT";

    public NotificationFactory(Context context) {
        this.context = context;
        packageName = context.getPackageName();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        settings = new SettingsModel(context);
        volumeControlModel = new VolumeControlModel(context);
        items = volumeControlModel.getList();
    }

    public int getNotificationId() {
        return NotificationFactory.NOTIFICATION_ID;
    }

    public Notification getNotification() {
        return getNotificationBuilder().build();
    }

    public void startNotification() {
        manager.notify(NOTIFICATION_ID, getNotification());
    }

    public void cancelNotification() {
        manager.cancel(NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, packageName, getImportance());
            manager.createNotificationChannel(notificationChannel);
        }

        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(context, StartServiceReceiver.class),
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setDeleteIntent(deleteIntent)
                .setOngoing(true)
                .setPriority(getPriority())
                .setVisibility(getVisibility())
                .setCustomContentView(getCustomContentView())
                .setColor(Color.TRANSPARENT)
                .setSmallIcon(settings.getStatusIcon());

        return builder;
    }

    @TargetApi(Build.VERSION_CODES.O)
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

        RemoteViews view = new RemoteViews(packageName, R.layout.notification_layout);
        view.removeAllViews(R.id.notification_layout);

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

        for (int position = 0; position < items.size(); position++) {
            VolumeControl item = items.get(position);
            if (item.status != 1) {
                continue;
            }
            RemoteViews imageButton = new RemoteViews(packageName, R.layout.widget_volume_control);
            PendingIntent clickEvent = PendingIntent.getBroadcast(context, item.type,
                    new Intent(context, AdjustVolumeReceiver.class).putExtra(VolumeControlModel.STREAM_TYPE_FIELD, item.type),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            imageButton.setOnClickPendingIntent(R.id.btn_volume_control, clickEvent);
            imageButton.setInt(R.id.btn_volume_control, "setImageResource", volumeControlModel.getIconId(item.icon));
            imageButton.setInt(R.id.btn_volume_control, "setColorFilter", iconColor);
            imageButton.setCharSequence(R.id.btn_volume_control, "setContentDescription", item.label);

            view.addView(R.id.notification_layout, imageButton);
        }

        return view;
    }

}

