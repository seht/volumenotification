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

package net.hyx.app.volumenotification.model;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

public class AudioManagerModel {

    private final AudioManager audio;
    private final SettingsModel settings;

    public AudioManagerModel(Context context) {
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        settings = new SettingsModel(context);
    }

    public void adjustVolume(int streamType) {
        audio.adjustStreamVolume(streamType, getStreamFlag(streamType), AudioManager.FLAG_SHOW_UI);
    }

    private int getStreamFlag(int streamType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((streamType == AudioManager.STREAM_MUSIC && settings.getToggleMute()) || (streamType == AudioManager.STREAM_RING && settings.getToggleSilent())) {
                return AudioManager.ADJUST_TOGGLE_MUTE;
            }
        }
        return AudioManager.ADJUST_SAME;
    }

}
