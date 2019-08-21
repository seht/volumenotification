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
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.SparseArray;

import com.google.gson.Gson;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VolumeControlModel {

    public static final String EXTRA_ITEM = "item";
    public static final String EXTRA_ITEM_ID = "item_id";

    private final Context context;
    private final SettingsModel settings;
    private final ArrayList<Integer> defaultOrder;
    private final SparseArray<VolumeControl> defaultControls;

    public VolumeControlModel(Context context) {
        this.context = context;
        settings = new SettingsModel(context);
        defaultOrder = new ArrayList<>();
        defaultControls = new SparseArray<>();
        setDefaultOrder();
        setDefaultControls();
    }

    private void setDefaultOrder() {
        defaultOrder.add(AudioManager.STREAM_MUSIC);
        defaultOrder.add(AudioManager.STREAM_VOICE_CALL);
        defaultOrder.add(AudioManager.STREAM_RING);
        defaultOrder.add(AudioManager.STREAM_ALARM);
        defaultOrder.add(AudioManager.STREAM_NOTIFICATION);
        defaultOrder.add(AudioManager.STREAM_SYSTEM);
        defaultOrder.add(AudioManager.STREAM_DTMF);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            defaultOrder.add(AudioManager.STREAM_ACCESSIBILITY);
        }
        //defaultOrder.put(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    private void setDefaultControls() {
        defaultControls.put(AudioManager.STREAM_MUSIC, new VolumeControl(AudioManager.STREAM_MUSIC, 0, 1, "ic_baseline_music_note_24px", getDefaultLabel(R.string.control_label_media)));
        defaultControls.put(AudioManager.STREAM_VOICE_CALL, new VolumeControl(AudioManager.STREAM_VOICE_CALL, 1, 1, "ic_baseline_phone_24px", getDefaultLabel(R.string.control_label_call)));
        defaultControls.put(AudioManager.STREAM_RING, new VolumeControl(AudioManager.STREAM_RING, 2, 1, "ic_baseline_notifications_24px", getDefaultLabel(R.string.control_label_ring)));
        defaultControls.put(AudioManager.STREAM_ALARM, new VolumeControl(AudioManager.STREAM_ALARM, 3, 0, "ic_baseline_alarm_24px", getDefaultLabel(R.string.control_label_alarm)));
        defaultControls.put(AudioManager.STREAM_NOTIFICATION, new VolumeControl(AudioManager.STREAM_NOTIFICATION, 4, 0, "ic_baseline_chat_bubble_24px", getDefaultLabel(R.string.control_label_notification)));
        defaultControls.put(AudioManager.STREAM_SYSTEM, new VolumeControl(AudioManager.STREAM_SYSTEM, 5, 0, "ic_baseline_phone_android_24px", getDefaultLabel(R.string.control_label_system)));
        defaultControls.put(AudioManager.STREAM_DTMF, new VolumeControl(AudioManager.STREAM_DTMF, 6, 0, "ic_baseline_dialpad_24px", getDefaultLabel(R.string.control_label_dial)));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            defaultControls.put(AudioManager.STREAM_ACCESSIBILITY, new VolumeControl(AudioManager.STREAM_ACCESSIBILITY, 7, 0, "ic_baseline_accessibility_new_24px", getDefaultLabel(R.string.control_label_accessibility)));
        }
        //defaultControls.put(AudioManager.USE_DEFAULT_STREAM_TYPE, new VolumeControl(AudioManager.USE_DEFAULT_STREAM_TYPE, 8, 0, "control_icon_default", getDefaultLabel(R.string.control_label_default)));
    }

    public ArrayList<Integer> getDefaultOrder() {
        return defaultOrder;
    }

    public SparseArray<VolumeControl> getDefaultControls() {
        return defaultControls;
    }

    public List<VolumeControl> getItems() {
        ArrayList<VolumeControl> items = new ArrayList<>(getDefaultControls().size());
        for (int index = 0; index < getDefaultControls().size(); index++) {
            VolumeControl item = getItem(index);
            if (item != null) {
                items.add(item);
            } else {
                VolumeControl defaultItem = defaultControls.get(getDefaultOrder().get(index));
                items.add(defaultItem);
            }
        }
        return items;
    }

    public List<String> getIconEntries() {
        return Arrays.asList(settings.getResources().getStringArray(R.array.pref_icon_entries));
    }

    public int getIconId(String drawableName) {
        return settings.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
    }

    public String getDefaultLabel(int resourceId) {
        return settings.getResources().getString(resourceId);
    }

    public VolumeControl getItem(int position) {
        String control = settings.getPreferences().getString("pref_control_list_item_" + position, null);
        if (control != null) {
            return (new Gson()).fromJson(control, VolumeControl.class);
        }
        return null;
    }

    public VolumeControl getItemByType(int streamType) {
        for (int index = 0; index < getDefaultControls().size(); index++) {
            VolumeControl item = getItem(index);
            if (item != null && item.type == streamType) {
                return item;
            }
        }
        return null;
    }

    private Editor editItem(VolumeControl item) {
        return settings.getPreferences().edit().putString("pref_control_list_item_" + item.position, (new Gson()).toJson(item));
    }

    public void saveItem(VolumeControl item) {
        editItem(item).commit();
    }

    public void saveList(List<VolumeControl> list) {
        for (int position = 0; position < list.size(); position++) {
            VolumeControl item = list.get(position);
            item.position = position;
            editItem(item).apply();
        }
    }

}
