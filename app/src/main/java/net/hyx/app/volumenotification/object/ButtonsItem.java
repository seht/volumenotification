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

package net.hyx.app.volumenotification.object;

import java.io.Serializable;

public class ButtonsItem implements Serializable {

    public final int id;
    public int position;
    public int status = 1;
    public int icon = 0;
    public String label = "";

    public ButtonsItem(int id, int position, int status, int icon, String label) {
        this.id = id;
        this.position = position;
        this.status = status;
        this.icon = icon;
        this.label = label;
    }

}
