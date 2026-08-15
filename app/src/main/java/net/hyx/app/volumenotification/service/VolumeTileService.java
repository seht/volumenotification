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

package net.hyx.app.volumenotification.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import net.hyx.app.volumenotification.activity.AdjustVolumeActivity;
import net.hyx.app.volumenotification.controller.TileServiceController;
import net.hyx.app.volumenotification.model.AudioManagerModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;

@TargetApi(Build.VERSION_CODES.N)
abstract public class VolumeTileService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(PendingIntent.getActivity(this, getStreamType(),
                    new Intent(this, AdjustVolumeActivity.class)
                            .putExtra(VolumeControlModel.STREAM_TYPE_FIELD, getStreamType())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        } else {
            adjustVolume();
        }
    }

    protected abstract int getStreamType();

    private void updateTile() {
        TileServiceController tileServiceController = TileServiceController.newInstance(getApplicationContext());
        tileServiceController.updateTile(getQsTile(), getStreamType());
    }

    private void adjustVolume() {
        AudioManagerModel audioManagerModel = new AudioManagerModel(getApplicationContext());
        audioManagerModel.adjustVolume(getStreamType());
    }

}
