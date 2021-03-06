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

package com.abyx.loyalty.contents;

/**
 * Contract-class containing the Database-contract for this application.
 *
 * @author Pieter Verschaffelt
 */
public class DatabaseContract {
    public static final String TABLE_CARD = "CARD";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_BARCODE = "BARCODE";
    public static final String COLUMN_BARCODE_FORMAT = "BARCODE_FORMAT";
    public static final String COLUMN_IMAGE_URL = "IMAGE_URL";
    // This column will contain the epoch time (in seconds) when the last logo search was performed.
    public static final String COLUMN_LAST_SEARCHED = "LAST_SEARCHED";
    // How many times has a row been retrieved from the database.
    public static final String COLUMN_HIT_COUNT = "HIT_COUNT";
}
