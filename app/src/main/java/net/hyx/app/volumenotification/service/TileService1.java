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

package net.hyx.app.volumenotification.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import net.hyx.app.volumenotification.factory.NotificationFactory;

@TargetApi(Build.VERSION_CODES.N)
public class TileService1 extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        NotificationFactory.newInstance(this).updateTile(getQsTile(), 1);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        NotificationFactory.newInstance(this).updateTile(getQsTile(), 1);
    }

    @Override
    public void onClick() {
        super.onClick();
        if (getQsTile().getState() == Tile.STATE_ACTIVE) {
            NotificationFactory.newInstance(this).setVolume(1);
        }
    }

}
