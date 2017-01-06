package com.abyx.loyalty;

/**
 * Functional interface that has to be implemented to define a task that should be executed whenever
 * a permission is granted.
 *
 * @author Pieter Verschaffelt
 */
public interface ReceivedPermission {
    public void onPermissionGranted();
}
