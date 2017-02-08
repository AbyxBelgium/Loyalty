package com.abyx.loyalty.extra;

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
}
