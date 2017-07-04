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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.abyx.loyalty.contents.Card;

/**
 * Retrieves the logo corresponding to this store that has been processed with a magic crop and that
 * is placed above a circular, semi-transparent, background. This task will automatically check
 * if a logo has already been found for this store and will download it once found. The Aurora
 * library is used for editing these images on the fly.
 *
 * @author Pieter Verschaffelt
 */
public class DetailedLogoTask extends AsyncTask<Card, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;

    public DetailedLogoTask(Context context, TaskListener<Bitmap> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    @Nullable
    protected Bitmap doInBackground(Card... params) {
        return null;
    }
}
