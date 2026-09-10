package com.artgallery.gui;

/**
 * Implemented by MainFrame; called by any panel after it adds/modifies data
 * so every tab can refresh its table (e.g. selling an artwork must update
 * the Artworks tab's status column and the Purchases tab's history).
 */
public interface DataChangeListener {
    void onDataChanged();
}
