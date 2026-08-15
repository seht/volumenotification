package net.hyx.app.volumenotification.activity;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.hyx.app.volumenotification.model.AudioManagerModel;
import net.hyx.app.volumenotification.model.VolumeControlModel;

public class AdjustVolumeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        super.onCreate(savedInstanceState);
        int streamType = getIntent().getIntExtra(
                VolumeControlModel.STREAM_TYPE_FIELD,
                VolumeControlModel.DEFAULT_STREAM_TYPE);
        new AudioManagerModel(getApplicationContext()).adjustVolume(streamType);
        finish();
    }

}
