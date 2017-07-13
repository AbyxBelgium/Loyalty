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

import java.util.List;

/**
 * This interface should be implemented by all Activities (or other entities) that try to interact
 * with a fragment that provides the view of a List (multiple items) of data.
 *
 * @param <T> The type of data that's shown in the Fragment's list.
 *
 * @author Pieter Verschaffelt
 */
public interface ListInteractor<T> {
    /**
     * This method will be invoked by the Fragment when it requests for new data. The Activity
     * should then send the most recent dataset available.
     *
     * @return The most recent dataset available in the Activity.
     */
    public List<T> requestData();

    /**
     * This method should be invoked by the Activity when it receives an updated dataset and wants
     * to bring this new data under the attention of the Fragment.
     *
     * @param updated Newly generated dataset.
     */
    public void dataChanged(List<T> updated);

    /**
     * This method is invoked by the Fragment when the user clicks an item in the list.
     *
     * @param dataPos The position of the clicked item inside of the most recently received list.
     * @param item The item that was clicked.
     */
    public void onItemClick(int dataPos, T item);

    /**
     * Set a Filter that is used for determining which items are shown by the Fragment and which
     * not.
     *
     * @param filter A Filter that decides which items should be visible.
     */
    public void filter(Filter<T> filter);
}