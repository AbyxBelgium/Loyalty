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

import java.io.IOException;

/**
 * This exception is thrown when it's not possible for the system to create a new directory to save
 * files.
 *
 * @author Pieter Verschaffelt
 */
public class MakeDirException extends IOException {
    public MakeDirException(){
        super();
    }

    public MakeDirException(String message){
        super(message);
    }
}
