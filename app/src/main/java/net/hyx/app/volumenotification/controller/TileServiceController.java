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

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import net.hyx.app.volumenotification.entity.VolumeControl;
import net.hyx.app.volumenotification.model.VolumeControlModel;
import net.hyx.app.volumenotification.service.tile.AccessibilityVolume;
import net.hyx.app.volumenotification.service.tile.AlarmVolume;
import net.hyx.app.volumenotification.service.tile.CallVolume;
import net.hyx.app.volumenotification.service.tile.DialVolume;
import net.hyx.app.volumenotification.service.tile.MediaVolume;
import net.hyx.app.volumenotification.service.tile.NotificationVolume;
import net.hyx.app.volumenotification.service.tile.RingVolume;
import net.hyx.app.volumenotification.service.tile.SystemVolume;

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
        String[] tileServices = {
                MediaVolume.class.getName(),
                CallVolume.class.getName(),
                RingVolume.class.getName(),
                AlarmVolume.class.getName(),
                NotificationVolume.class.getName(),
                SystemVolume.class.getName(),
                DialVolume.class.getName(),
                AccessibilityVolume.class.getName(),
                //TileServiceDefaultVolume.class.getName(),
        };
        for (String service : tileServices) {
            TileService.requestListeningState(context, new ComponentName(context, service));
        }
    }

    public void updateTile(Tile tile, int streamType) {
        VolumeControl item = volumeControlModel.getItemByType(streamType);
        if (item == null) {
            item = volumeControlModel.getDefaultControls().get(streamType);
        }
        tile.setIcon(Icon.createWithResource(context, volumeControlModel.getIconId(item.icon)));
        tile.setLabel(item.label);
        if (item.status == 1) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }

}
