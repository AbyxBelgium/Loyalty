/**
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

package com.abyx.loyalty.extra;

import android.graphics.Color;

/**
 * Class containing some contracts and constants that can be used throughout the application.
 *
 * @author Pieter Verschaffelt
 */
public class Constants {
    // This constant is used for passing on one Card-object within an intent
    public static final String INTENT_CARD_ARG = "CARD";
    // This constant is used for passing the id of a Card, so that it can be retrieved from the
    // database later on
    public static final String INTENT_CARD_ID_ARG = "CARD_ID";
    // This constant is used for passing on a list of Card-objects
    public static final String INTENT_LIST_ARG = "LIST";

    public static final int BACKGROUND_COLOUR = Color.argb(143, 175, 175, 175);
}
