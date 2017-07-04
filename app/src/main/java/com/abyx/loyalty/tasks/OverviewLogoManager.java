/*
 * Copyright 2017 Abyx (https://abyx.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abyx.loyalty.tasks;

import android.content.Context;
import android.widget.ImageView;

/**
 * Class that retrieves the correct logo for a store and generates an appropriate thumbnail for
 * it by using AsyncTasks.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewLogoManager {
    private ImageView view;
    private Context context;

    public OverviewLogoManager(Context context, ImageView view) {
        this.view = view;
        this.context = context;
    }
}
