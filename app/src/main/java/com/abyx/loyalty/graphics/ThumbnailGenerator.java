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
        // Logo does not need to be cropped as it is placed onto a white background!
        ImageUtils utils = new ImageUtils(this.context);
        Bitmap cropped = utils.magicCrop(logo, Color.WHITE, 0.2f);

        ShapeFactory factory = new ParallelShapeFactory();
        return factory.createShape(new CircleShape(this.context), cropped, Constants.BACKGROUND_COLOUR, 50);
    }
}
