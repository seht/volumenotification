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
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.service.TileServiceAccessibilityVolume;
import net.hyx.app.volumenotification.service.TileServiceAlarmVolume;
import net.hyx.app.volumenotification.service.TileServiceCallVolume;
import net.hyx.app.volumenotification.service.TileServiceDialVolume;
import net.hyx.app.volumenotification.service.TileServiceMediaVolume;
import net.hyx.app.volumenotification.service.TileServiceNotificationVolume;
import net.hyx.app.volumenotification.service.TileServiceRingVolume;
import net.hyx.app.volumenotification.service.TileServiceSystemVolume;

@TargetApi(Build.VERSION_CODES.N)
public class TileServiceController {

    private final Context context;
    private final VolumeControlModel volumeControlModel;

    public TileServiceController(Context context) {
        this.context = context;
        volumeControlModel = new VolumeControlModel(context);
    }

    public static TileServiceController newInstance(Context context) {
        return new TileServiceController(context);
    }

    public void requestListening() {
        requestListeningTiles();
    }


    private void requestListeningTiles() {
        Class<?>[] tileServices = {
                TileServiceMediaVolume.class,
                TileServiceCallVolume.class,
                TileServiceRingVolume.class,
                TileServiceAlarmVolume.class,
                TileServiceNotificationVolume.class,
                TileServiceSystemVolume.class,
                TileServiceDialVolume.class,
                TileServiceAccessibilityVolume.class,
        };
        for (Class<?> service : tileServices) {
            TileService.requestListeningState(context, new ComponentName(context, service));
        }
    }

    public void updateTile(Tile tile, int streamType) {
        VolumeControl item = volumeControlModel.getItemByType(streamType);
        if (item == null) {
            item = volumeControlModel.getDefaultControls().get(streamType);
        }
        if (item == null) {
            return;
        }
        int iconId = volumeControlModel.getIconId(item.icon);
        tile.setIcon(Icon.createWithResource(context, iconId != 0 ? iconId : R.drawable.ic_outline_volume_up_24px));
        tile.setLabel(item.label);
        if (item.status == 1) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }

}
