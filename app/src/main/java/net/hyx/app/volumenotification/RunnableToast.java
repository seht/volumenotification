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

package net.hyx.app.volumenotification;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

class RunnableToast implements Runnable {

    private Context context;
    private String text;
    private int duration;

    private RunnableToast(Context context, String text, int duration) {
        this.context = context;
        this.text = text;
        this.duration = duration;
    }

    private static boolean deliver(Context context, String text, int duration) {
        return (new Handler(Looper.getMainLooper()))
                .post(new RunnableToast(context.getApplicationContext(), text, duration));
    }

    static boolean shortToast(Context context, String text) {
        return deliver(context, text, Toast.LENGTH_SHORT);
    }

    static boolean longToast(Context context, String text) {
        return deliver(context, text, Toast.LENGTH_LONG);
    }

    @Override
    public void run() {
        Toast.makeText(context, text, duration).show();
    }

}
