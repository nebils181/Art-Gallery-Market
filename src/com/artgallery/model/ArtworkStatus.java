package com.artgallery.model;

/**
 * Represents the lifecycle state of an artwork in the gallery.
 *
 * AVAILABLE - can be purchased or reserved
 * RESERVED  - a customer has put a hold on it (not yet purchased)
 * SOLD      - purchase has been finalized
 */
public enum ArtworkStatus {
    AVAILABLE,
    RESERVED,
    SOLD
}
