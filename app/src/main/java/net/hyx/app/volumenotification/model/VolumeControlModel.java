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
import android.media.AudioManager;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.hyx.app.volumenotification.R;
import net.hyx.app.volumenotification.entity.VolumeControl;

import java.util.ArrayList;
import java.util.List;

public class VolumeControlModel {
    public static final String STREAM_TYPE_FIELD = "item_type";
    public static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final Gson GSON = new Gson();

    private static final ArrayList<Integer> DEFAULT_ORDER = buildDefaultOrder();

    private final SettingsModel settings;
    private final SparseArray<VolumeControl> defaultControls;

    private static ArrayList<Integer> buildDefaultOrder() {
        ArrayList<Integer> order = new ArrayList<>();
        order.add(AudioManager.STREAM_MUSIC);
        order.add(AudioManager.STREAM_VOICE_CALL);
        order.add(AudioManager.STREAM_RING);
        order.add(AudioManager.STREAM_ALARM);
        order.add(AudioManager.STREAM_NOTIFICATION);
        order.add(AudioManager.STREAM_SYSTEM);
        order.add(AudioManager.STREAM_DTMF);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            order.add(AudioManager.STREAM_ACCESSIBILITY);
        }
        return order;
    }

    public VolumeControlModel(Context context) {
        settings = SettingsModel.getInstance(context);
        defaultControls = new SparseArray<>();
        setDefaultControls();
    }

    private void setDefaultControls() {
        defaultControls.put(AudioManager.STREAM_MUSIC,
                new VolumeControl(AudioManager.STREAM_MUSIC, 0, 1, "ic_outline_music_note_24px", getDefaultLabel(R.string.control_label_media)));
        defaultControls.put(AudioManager.STREAM_VOICE_CALL,
                new VolumeControl(AudioManager.STREAM_VOICE_CALL, 1, 1, "ic_outline_phone_24px", getDefaultLabel(R.string.control_label_call)));
        defaultControls.put(AudioManager.STREAM_RING,
                new VolumeControl(AudioManager.STREAM_RING, 2, 1, "ic_outline_notifications_24px", getDefaultLabel(R.string.control_label_ring)));
        defaultControls.put(AudioManager.STREAM_ALARM,
                new VolumeControl(AudioManager.STREAM_ALARM, 3, 0, "ic_outline_alarm_24px", getDefaultLabel(R.string.control_label_alarm)));
        defaultControls.put(AudioManager.STREAM_NOTIFICATION,
                new VolumeControl(AudioManager.STREAM_NOTIFICATION, 4, 0, "ic_outline_chat_bubble_outline_24px", getDefaultLabel(R.string.control_label_notification)));
        defaultControls.put(AudioManager.STREAM_SYSTEM,
                new VolumeControl(AudioManager.STREAM_SYSTEM, 5, 0, "ic_outline_phone_android_24px", getDefaultLabel(R.string.control_label_system)));
        defaultControls.put(AudioManager.STREAM_DTMF,
                new VolumeControl(AudioManager.STREAM_DTMF, 6, 0, "ic_outline_dialpad_24px", getDefaultLabel(R.string.control_label_dial)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultControls.put(AudioManager.STREAM_ACCESSIBILITY,
                    new VolumeControl(AudioManager.STREAM_ACCESSIBILITY, 7, 0, "ic_outline_accessibility_new_24px", getDefaultLabel(R.string.control_label_accessibility)));
        }
    }

    public ArrayList<Integer> getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    public SparseArray<VolumeControl> getDefaultControls() {
        return defaultControls;
    }

    public List<VolumeControl> getList() {
        ArrayList<VolumeControl> items = new ArrayList<>(getDefaultControls().size());
        for (int index = 0; index < getDefaultControls().size(); index++) {
            VolumeControl item = getStorageItem(index);
            if (item != null) {
                item = sanitizeItem(item);
                if (item != null) {
                    items.add(item);
                }
            } else {
                VolumeControl defaultItem = defaultControls.get(getDefaultOrder().get(index));
                items.add(defaultItem);
            }
        }
        return items;
    }

    public VolumeControl getItemByType(int streamType) {
        for (int index = 0; index < getDefaultControls().size(); index++) {
            VolumeControl item = getStorageItem(index);
            if (item != null && item.type == streamType) {
                return item;
            }
        }
        return getDefaultControls().get(streamType);
    }

    public void saveItem(VolumeControl item) {
        item = sanitizeItem(item);
        if (item != null) {
            editItem(item).apply();
        }
    }

    public void saveList(List<VolumeControl> list) {
        if (list == null) {
            return;
        }
        for (int position = 0; position < list.size(); position++) {
            VolumeControl item = list.get(position);
            if (item == null) {
                continue;
            }
            item.position = position;
            saveItem(item);
        }
    }

    private Editor editItem(VolumeControl item) {
        return settings.getPreferences().edit().putString(SettingsModel.PREF_CONTROL_LIST_ITEM_PREFIX + item.position, GSON.toJson(item));
    }

    private String getDefaultLabel(int resourceId) {
        return settings.getResources().getString(resourceId);
    }

    private VolumeControl getStorageItem(int position) {
        String control = settings.getPreferences().getString(SettingsModel.PREF_CONTROL_LIST_ITEM_PREFIX + position, null);
        if (control != null) {
            try {
                return GSON.fromJson(control, VolumeControl.class);
            } catch (JsonSyntaxException ex) {
                return null;
            }
        }
        return null;
    }

    private VolumeControl sanitizeItem(@NonNull VolumeControl item) {
        if (item.position < 0 || item.position >= DEFAULT_ORDER.size()) {
            return null;
        }
        if (!DEFAULT_ORDER.contains(item.type)) {
            item.type = DEFAULT_ORDER.get(item.position);
        }
        if (getIconId(item.icon) == 0) {
            item.icon = defaultControls.get(item.type).icon;
        }
        return item;
    }

    public int getIconId(String iconName) {
        if (iconName == null || iconName.trim().isEmpty()) {
            return 0;
        }
        switch (iconName) {
            case "ic_baseline_music_note_24px":
                return R.drawable.ic_baseline_music_note_24px;
            case "ic_baseline_phone_24px":
                return R.drawable.ic_baseline_phone_24px;
            case "ic_baseline_notifications_24px":
                return R.drawable.ic_baseline_notifications_24px;
            case "ic_baseline_alarm_24px":
                return R.drawable.ic_baseline_alarm_24px;
            case "ic_baseline_chat_bubble_24px":
                return R.drawable.ic_baseline_chat_bubble_24px;
            case "ic_baseline_phone_android_24px":
                return R.drawable.ic_baseline_phone_android_24px;
            case "ic_baseline_dialpad_24px":
                return R.drawable.ic_baseline_dialpad_24px;
            case "ic_baseline_volume_up_24px":
                return R.drawable.ic_baseline_volume_up_24px;
            case "ic_baseline_accessibility_new_24px":
                return R.drawable.ic_baseline_accessibility_new_24px;
            case "ic_baseline_headset_24px":
                return R.drawable.ic_baseline_headset_24px;
            case "ic_baseline_speaker_24px":
                return R.drawable.ic_baseline_speaker_24px;
            case "ic_baseline_notifications_active_24px":
                return R.drawable.ic_baseline_notifications_active_24px;
            case "ic_baseline_notification_important_24px":
                return R.drawable.ic_baseline_notification_important_24px;
            case "ic_baseline_phone_in_talk_24px":
                return R.drawable.ic_baseline_phone_in_talk_24px;
            case "ic_baseline_ring_volume_24px":
                return R.drawable.ic_baseline_ring_volume_24px;
            case "ic_baseline_phonelink_ring_24px":
                return R.drawable.ic_baseline_phonelink_ring_24px;
            case "ic_baseline_smartphone_24px":
                return R.drawable.ic_baseline_smartphone_24px;
            case "ic_baseline_tune_24px":
                return R.drawable.ic_baseline_tune_24px;
            case "ic_outline_music_note_24px":
                return R.drawable.ic_outline_music_note_24px;
            case "ic_outline_phone_24px":
                return R.drawable.ic_outline_phone_24px;
            case "ic_outline_notifications_24px":
                return R.drawable.ic_outline_notifications_24px;
            case "ic_outline_alarm_24px":
                return R.drawable.ic_outline_alarm_24px;
            case "ic_outline_chat_bubble_outline_24px":
                return R.drawable.ic_outline_chat_bubble_outline_24px;
            case "ic_outline_phone_android_24px":
                return R.drawable.ic_outline_phone_android_24px;
            case "ic_outline_dialpad_24px":
                return R.drawable.ic_outline_dialpad_24px;
            case "ic_outline_accessibility_new_24px":
                return R.drawable.ic_outline_accessibility_new_24px;
            case "ic_outline_volume_up_24px":
                return R.drawable.ic_outline_volume_up_24px;
            case "ic_outline_headset_24px":
                return R.drawable.ic_outline_headset_24px;
            case "ic_outline_speaker_24px":
                return R.drawable.ic_outline_speaker_24px;
            case "ic_outline_notifications_active_24px":
                return R.drawable.ic_outline_notifications_active_24px;
            case "ic_outline_notification_important_24px":
                return R.drawable.ic_outline_notification_important_24px;
            case "ic_outline_phone_in_talk_24px":
                return R.drawable.ic_outline_phone_in_talk_24px;
            case "ic_outline_ring_volume_24px":
                return R.drawable.ic_outline_ring_volume_24px;
            case "ic_outline_phonelink_ring_24px":
                return R.drawable.ic_outline_phonelink_ring_24px;
            case "ic_outline_smartphone_24px":
                return R.drawable.ic_outline_smartphone_24px;
            case "ic_outline_tune_24px":
                return R.drawable.ic_outline_tune_24px;
            default:
                return 0;
        }
    }

}
