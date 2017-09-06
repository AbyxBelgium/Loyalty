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

package com.abyx.loyalty.managers.memory;

/**
 * Provides methods for figuring out how much memory is available and how much memory can be used.
 *
 * @author Pieter Verschaffelt
 */
public class MemoryManager {
    private static int simultaneousTasks;

    private MemoryManager() {}

    /**
     * @return The amount of memory that's available to our app in MiB (floored to nearest integer).
     */
    public static int getFreeMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();

        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long availableMemory = maxMemory-usedMemory;

        // Convert bytes to MiB.
        return (int) (availableMemory / (1024 * 1024));
    }

    /**
     * @return The maximum amount of memory that's available for the heap to be used (in MiB). The
     * result is floored to the nearest integer.
     */
    public static int maxHeapSize() {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        return (int) (maxMemory / (1024 * 1024));
    }

    public static int getTotalMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();

        // Convert bytes to MiB.
        return (int) (maxMemory / (1024 * 1024));
    }

    public static int getMemoryTreshold() {
        int totalMemory = getTotalMemory();
        if (totalMemory >= 190) {
            return 100;
        } else if (totalMemory >= 100) {
            return 50;
        } else {
            return 20;
        }
    }
}
