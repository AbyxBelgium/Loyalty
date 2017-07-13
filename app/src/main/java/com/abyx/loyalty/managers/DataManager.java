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

import android.content.Context;

import com.abyx.loyalty.contents.Card;

import java.util.List;

/**
 * The DataManager is responsible for updating, modifying and adding new data to persistent storage.
 * Other entities can subscribe to the DataManager and be notified upon data changes.
 *
 * @author Pieter Verschaffelt
 */
public class DataManager extends ChangeObservable<List<Card>> {
    private List<Card> data;
    private Context context;

    public DataManager(Context context) {
        this.context = context;
    }


}
