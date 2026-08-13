package net.hyx.app.volumenotification.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.hyx.app.volumenotification.model.SettingsModel;

abstract class BaseActivity extends AppCompatActivity {

    private SettingsModel settingsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settingsModel = SettingsModel.getInstance(getApplicationContext());
        setTheme(settingsModel.getAppTheme());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
    }

    protected final SettingsModel getSettingsModel() {
        return settingsModel;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        EdgeToEdgeInsets.apply(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        EdgeToEdgeInsets.apply(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        EdgeToEdgeInsets.apply(this);
    }
}
