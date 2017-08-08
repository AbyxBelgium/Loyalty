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

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;

import java.io.File;

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
        String logoFileName = cache.getCacheLocation(Integer.toString(card.getName().hashCode()) + "." + Constants.IMAGE_FORMAT);
        File file = context.getFileStreamPath(logoFileName);
        return file != null && file.exists();
    }

    private void deleteRecursive(File file){
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }
}