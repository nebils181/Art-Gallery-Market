package com.artgallery.exception;

/**
 * Thrown when an operation requires an artwork to be AVAILABLE
 * (e.g. purchase, reserve) but it is not.
 */
public class ArtworkNotAvailableException extends Exception {
    public ArtworkNotAvailableException(String message) {
        super(message);
    }
}
