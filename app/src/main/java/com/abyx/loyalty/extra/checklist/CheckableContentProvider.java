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

package com.abyx.loyalty.extra.checklist;

/**
 * @author Pieter Verschaffelt
 */
public interface CheckableContentProvider<T> {
    /**
     * Returns the title that should be shown by the CheckListDialog for the given input object.
     *
     * @return A String representing the input's unique name or title.
     */
    public String getCheckableContent(T input);

    /**
     * Returns whether the given object should be marked as active or inactive.
     *
     * @param input Object that should be judged.
     * @return True when given object should be marked as being active in the list.
     */
    public boolean isActivated(T input);
}
