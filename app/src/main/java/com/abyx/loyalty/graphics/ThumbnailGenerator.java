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

package com.abyx.loyalty.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.abyx.loyalty.extra.Constants;

import be.abyx.aurora.CircleShape;
import be.abyx.aurora.ImageUtils;
import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.ShapeFactory;

/**
 * @author Pieter Verschaffelt
 */
public class ThumbnailGenerator {
    private Context context;

    public ThumbnailGenerator(Context context) {
        this.context = context;
    }

    /**
     * This function will draw a circular thumbnail with a white background.
     *
     * @param logo The logo that should be transformed to a thumbnail.
     * @return A new Bitmap that represents a circular thumbnail with the given logo at the center.
     */
    public Bitmap generateThumbnail(Bitmap logo) {
        ImageUtils utils = new ImageUtils(this.context);
        Bitmap cropped = utils.magicCrop(logo, Color.WHITE, 0.2f);

        ShapeFactory factory = new ParallelShapeFactory();
        Bitmap circle = factory.createShape(new CircleShape(this.context), cropped, Color.BLACK, 100);

        return factory.createShape(new CircleShape(this.context), circle, Color.WHITE, 10);
    }
}
