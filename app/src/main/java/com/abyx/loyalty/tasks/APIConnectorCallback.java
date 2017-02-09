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
package com.abyx.loyalty.tasks;

/**
 * Interface used for connecting to the Loyalty API in a parallel thread.
 *
 * @author Pieter Verschaffelt
 */
public interface APIConnectorCallback {
    /**
     * Method called when connecting to the API completed succesfully.
     *
     * @param url The url for the store's image logo returned by the API
     */
    void onAPIReady(String url);

    /**
     * Method called whenever something goes wrong while connecting to the API.
     *
     * @param title The exception's title
     * @param message The exception's extended message (a detailed description of what went wrong)
     */
    void onAPIException(String title, String message);
}
