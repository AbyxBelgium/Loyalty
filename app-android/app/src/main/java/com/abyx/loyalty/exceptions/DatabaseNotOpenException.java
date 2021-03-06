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

package com.abyx.loyalty.exceptions;

/**
 * This exception is used to indicate that a database is trying to be manipulated that isn't open.
 *
 * @author Pieter Verschaffelt
 */
public class DatabaseNotOpenException extends RuntimeException {
    public DatabaseNotOpenException() {
        super();
    }

    public DatabaseNotOpenException(String detailMessage) {
        super(detailMessage);
    }

    public DatabaseNotOpenException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DatabaseNotOpenException(Throwable throwable) {
        super(throwable);
    }
}
