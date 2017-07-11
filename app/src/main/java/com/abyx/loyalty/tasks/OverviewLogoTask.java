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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;

import com.abyx.loyalty.contents.Card;
import com.amulyakhare.textdrawable.TextDrawable;

import be.abyx.aurora.ColourPalette;
import be.abyx.aurora.ColourPaletteFactory;

/**
 * This Task generates all small thumbnails for store logos that are shown in an overview or summary
 * list. One of the parameters for this Task is an ImageView. This is needed as an Adapter always
 * has to return the View immediately and is not able to wait for an async thread to complete.
 *
 * No checked exceptions are thrown by this Task.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewLogoTask extends AsyncTask<Bitmap, Void, Integer> {
    private Context context;
    private TaskListener<Drawable> listener;
    private Card card;

    /**
     * @param context A valid ApplicationContext.
     * @param listener A listener that should respond to state changes of this task.
     * @param card The loyalty Card for which a new thumbnail should be generated.
     */
    public OverviewLogoTask(Context context, TaskListener<Drawable> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    @Override
    protected Integer doInBackground(Bitmap... params) {
        Palette palette = Palette.from(params[0]).generate();
        int dominant = palette.getDominantColor(Color.GRAY);

        // Convert to closest material design colour
        ColourPaletteFactory colourPaletteFactory = new ColourPaletteFactory(context);
        ColourPalette materialPalette = colourPaletteFactory.getMaterialColourPalette();
        return materialPalette.matchToClosestColour(dominant);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.onProgressUpdate(0.0);
    }

    @Override
    protected void onPostExecute(Integer colour) {
        super.onPostExecute(colour);
        TextDrawable drawable = TextDrawable.builder().buildRound(card.getName().substring(0,1).toUpperCase(), colour);
        this.listener.onProgressUpdate(1.0);
        this.listener.onDone(drawable);
    }
}
