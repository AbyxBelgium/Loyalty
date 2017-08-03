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
 * This interface specifies the contract that must be followed by all specific MemoryGovernor
 * implementations. A MemoryGovernor decides whether a specific action can be fulfilled with the
 * amount of memory that's currently available for the app to use. The amount of memory that is
 * considered needed for a specific operation depends on the type of device. A governor will
 * typically allocate more free memory when executed on a high-end device in comparison to a low-end
 * device.
 *
 * @author Pieter Verschaffelt
 */
public interface MemoryGovernor {
    public boolean vote(Runnable task);
}
