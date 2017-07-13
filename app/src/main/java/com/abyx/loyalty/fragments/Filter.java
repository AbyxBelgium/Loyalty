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

package com.abyx.loyalty.fragments;

/**
 * Functional interface that can be used for filtering large datasets.
 *
 * @param <T> Type of type that should be filtered.
 *
 * @author Pieter Verschaffelt
 */
public interface Filter<T> {

    /**
     * Returns true when the given item should be retained.
     *
     * @param item The item that should be judged.
     * @return True when the given item should be retained, false otherwise.
     */
    public boolean retain(T item);
}
