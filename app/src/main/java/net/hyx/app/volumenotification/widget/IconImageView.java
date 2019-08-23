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

package net.hyx.app.volumenotification.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

import net.hyx.app.volumenotification.model.SettingsModel;

public class IconImageView extends AppCompatImageView {

    private SettingsModel settings;

    public IconImageView(Context context) {
        super(context);
        construct(context);
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct(context);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        construct(context);
    }

    private void construct(final Context context) {
        settings = new SettingsModel(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setColorFilter(settings.getThemeAttributeColor(getContext().getTheme(), android.R.attr.colorForeground));
    }

}
