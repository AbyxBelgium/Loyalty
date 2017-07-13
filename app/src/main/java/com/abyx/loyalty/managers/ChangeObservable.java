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

package com.abyx.loyalty.managers;

import java.util.List;

/**
 * A ChangeObservable is some kind of entity to which other entities can subscribe and that should
 * be able to notify it's listeners of important changes of it's internal state.
 *
 * @author Pieter Verschaffelt
 */
public abstract class ChangeObservable<T> {
    private List<ChangeListener<T>> listeners;

    public void subscribe(ChangeListener<T> listener) {
        this.listeners.add(listener);
    }

    public void unsubscribe(ChangeListener<T> listener) {
        this.listeners.remove(listener);
    }

    protected void notifyListeners(T res) {
        for (ChangeListener<T> listener: listeners) {
            listener.change(res);
        }
    }
}
