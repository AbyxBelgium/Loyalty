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

import java.util.Collection;
import java.util.List;

/**
 * @author Pieter Verschaffelt
 */
public interface CheckListListener<T> {
    /**
     * This method will be called when the user indicates that his selection is definitive.
     * @param selectedItems A Collection containing all items that were selected by the user.
     */
    public void selected(Collection<T> selectedItems);
}
