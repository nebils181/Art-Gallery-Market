package com.artgallery.exception;

/**
 * Thrown when a lookup by id (artwork, customer, purchase) fails.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
