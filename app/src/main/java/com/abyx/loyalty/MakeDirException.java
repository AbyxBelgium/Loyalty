package com.abyx.loyalty;

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
