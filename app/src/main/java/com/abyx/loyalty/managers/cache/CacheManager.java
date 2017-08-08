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

package com.abyx.loyalty.managers.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the cache and all it's related actions.
 *
 * @author Pieter Verschaffelt
 */
public class CacheManager {
    private Context context;

    public CacheManager(Context context) {
        this.context = context;
    }

    public void clearCache() {
        deleteRecursive(context.getFilesDir());
    }

    public boolean inCache(Card card, Cache cache) {
        String logoFileName = cache.getCacheLocation(getFileName(card));
        File file = context.getFileStreamPath(logoFileName);
        return file != null && file.exists();
    }

    public void addToCache(Bitmap bitmap, Card card, Cache cache) throws FileNotFoundException {
        String fileName = cache.getCacheLocation(getFileName(card));
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        bitmap.compress(Constants.IMAGE_COMPRESS_FORMAT, Constants.IMAGE_QUALITY, fos);
    }

    /**
     * Remove the entry of the given card from the given cache.
     *
     * @param card Card whose entries in the given cache should be removed.
     * @param cache Cache in which the entries of the given card should be removed.
     */
    public void removeFromCache(Card card, Cache cache) {
        context.deleteFile(cache.getCacheLocation(getFileName(card)));
    }

    /**
     * Remove all cache entries from the given card.
     *
     * @param card The card whose entries should be removed from all caches.
     */
    public void removeFromCache(Card card) {
        Cache[] caches = {new RawCache(), new DetailedCache(), new OverviewCache(), new AuroraCache()};
        for (Cache cache: caches) {
            removeFromCache(card, cache);
        }
    }

    public Bitmap restoreFromCache(Card card, Cache cache) throws IOException {
        String fileName = cache.getCacheLocation(getFileName(card));
        FileInputStream in = context.openFileInput(fileName);
        Bitmap out = BitmapFactory.decodeStream(in);
        in.close();
        return out;
    }

    private void deleteRecursive(File file){
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }

    private String getFileName(Card card) {
        return Integer.toString(card.getName().hashCode()) + "." + Constants.IMAGE_FORMAT;
    }
}
