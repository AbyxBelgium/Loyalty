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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.CardAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class that retrieves the correct logo for a store and generates an appropriate thumbnail for
 * it by using AsyncTasks.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewLogoManager {
    private ImageView view;
    private Context context;
    private Card card;
    private ThreadPoolExecutor executor;


    public OverviewLogoManager(Context context, ImageView view, Card card, ThreadPoolExecutor executor) {
        this.view = view;
        this.context = context;
        this.card = card;
        this.executor = executor;
    }

    /**
     * Generate the desired thumbnail and apply it to the ImageView given in the constructor. The
     * generation process will run completely asynchronously.
     */
    public void start() {
        LogoTask logoTask = new LogoTask(this.context, new LogoTaskListener());
        logoTask.executeOnExecutor(executor, card);
    }

    /**
     * This Listener handles all the callback-methods for the LogoTask class and can be used for
     * retrieving a Bitmap of the desired logo.
     */
    private class LogoTaskListener implements TaskListener<Bitmap> {
        @Override
        public void onProgressUpdate(double progress) {
            // Nothing to do here
        }

        @Override
        public void onFailed(Throwable exception) {
            // TODO: handle exceptions
        }

        @Override
        public void onDone(Bitmap result) {
            // Start the OverviewLogoTask now that we have the raw logo.
            OverviewLogoTask task = new OverviewLogoTask(context, new OverviewLogoTaskListener(), card);
            task.executeOnExecutor(executor, result);
        }
    }

    private class OverviewLogoTaskListener implements TaskListener<Drawable> {
        @Override
        public void onProgressUpdate(double progress) {
            // Nothing to do here
        }

        @Override
        public void onFailed(Throwable exception) {
            // TODO: handle exceptions
        }

        @Override
        public void onDone(Drawable result) {
            view.setImageDrawable(result);
        }
    }
}
