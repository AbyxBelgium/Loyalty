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

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * @author Pieter Verschaffelt
 */
public abstract class ListFragment<T> extends Fragment {
    /**
     * This method should be invoked by the Activity when it receives an updated dataset and wants
     * to bring this new data under the attention of the Fragment.
     *
     * @param updated Newly generated dataset.
     */
    public abstract void dataChanged(List<T> updated);

    /**
     * Set a Filter that is used for determining which items are shown by the Fragment and which
     * not.
     *
     * @param filter A Filter that decides which items should be visible.
     */
    public abstract void filter(Filter<T> filter);

    /**
     * This method returns whether the RecyclerView inside of this fragment is currently in multi
     * mode selection state or not.
     *
     * @return True when multi-mode is enabled for the RecyclerView inside the fragment
     */
    public abstract boolean multiModeEnabled();

    public abstract void disableMultiMode();
}
