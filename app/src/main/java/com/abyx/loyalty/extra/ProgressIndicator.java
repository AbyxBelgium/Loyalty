package com.abyx.loyalty.extra;

/**
 * Interface that contains some methods to set the progress of some task.
 *
 * @author Pieter Verschaffelt
 */
public interface ProgressIndicator {
    void setDone(boolean done);
    void setProgress(double percentage);
}
