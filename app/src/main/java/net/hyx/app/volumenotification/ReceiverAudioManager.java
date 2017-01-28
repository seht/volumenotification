/*
 * Copyright (C) 2017 Seht (R) HyX
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class ReceiverAudioManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int stream_type = intent.getExtras().getInt("stream_type");
        int direction = intent.getExtras().getInt("direction");

        AudioManager audio_manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        audio_manager.adjustStreamVolume(stream_type, direction, AudioManager.FLAG_SHOW_UI);
    }

}