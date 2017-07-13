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

/**
 * This listener can be registered by some kind of manager (such as the DataManager) and will then
 * get notified upon changes of this manager.
 *
 * @author Pieter Verschaffelt
 */
public interface ChangeListener<T> {
    /**
     * This method will be called when something changes inside the manager and that should be
     * propagated to it's listeners.
     *
     * @param resource An updated version of the resource to which the listener is subscribed.
     */
    public void change(T resource);
}
