package com.abyx.loyalty;

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
