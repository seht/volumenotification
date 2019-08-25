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
import net.hyx.app.volumenotification.service.NotificationForegroundService;

import java.util.List;

public class NotificationFactory {

    private final String packageName;
    private final Context context;
    private final NotificationManager manager;
    private final SettingsModel settings;
    private final VolumeControlModel volumeControlModel;
    //private final AudioManagerModel audioManagerModel;
    private final List<VolumeControl> items;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = NotificationForegroundService.class.getSimpleName();

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
            notificationChannel.setShowBadge(!settings.getHideStatus());
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

        RemoteViews wrapperLayout = new RemoteViews(packageName, getWrapperLayout());
        wrapperLayout.removeAllViews(R.id.notification_wrapper);

        for (int pos = 0; pos < items.size(); pos++) {
            VolumeControl item = items.get(pos);
            if (item.status != 1) {
                continue;
            }
            RemoteViews btn = new RemoteViews(packageName, R.layout.view_widget_volume_control);
            PendingIntent event = PendingIntent.getBroadcast(context, item.type,
                    new Intent(context, AdjustVolumeReceiver.class).putExtra(VolumeControlModel.STREAM_TYPE_FIELD, item.type),
                    PendingIntent.FLAG_CANCEL_CURRENT);

            btn.setOnClickPendingIntent(R.id.btn_volume_control, event);
            btn.setInt(R.id.btn_volume_control, "setImageResource", volumeControlModel.getIconId(item.icon));
            btn.setInt(R.id.btn_volume_control, "setColorFilter", iconColor);
            wrapperLayout.addView(R.id.notification_wrapper, btn);
        }

        RemoteViews view = new RemoteViews(packageName, R.layout.view_layout_notification);
        view.setInt(R.id.notification_layout, "setBackgroundColor", backgroundColor);

        view.removeAllViews(R.id.notification_layout);
        view.addView(R.id.notification_layout, wrapperLayout);

        return view;
    }

    private int getWrapperLayout() {
        switch (settings.getNotificationHeight()) {
            default:
            case "match_parent":
                return R.layout.view_layout_notification_wrapper_match_parent;
            case "wrap_content":
                return R.layout.view_layout_notification_wrapper_wrap_content;
            case "32dp":
                return R.layout.view_layout_notification_wrapper_32dp;
            case "40dp":
                return R.layout.view_layout_notification_wrapper_40dp;
            case "64dp":
                return R.layout.view_layout_notification_wrapper_64dp;
            case "70dp":
                return R.layout.view_layout_notification_wrapper_70dp;
            case "92dp":
                return R.layout.view_layout_notification_wrapper_92dp;
        }
    }

}

