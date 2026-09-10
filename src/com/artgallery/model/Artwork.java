package com.artgallery.model;

import java.math.BigDecimal;

/**
 * Represents a single piece of art tracked by the gallery.
 */
public class Artwork {

    private final int id;
    private String title;
    private String artistName;
    private String medium;      // e.g. "Oil on canvas", "Bronze sculpture"
    private int yearCreated;
    private BigDecimal price;
    private ArtworkStatus status;

    public Artwork(int id, String title, String artistName, String medium,
                   int yearCreated, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.artistName = artistName;
        this.medium = medium;
        this.yearCreated = yearCreated;
        this.price = price;
        this.status = ArtworkStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public int getYearCreated() {
        return yearCreated;
    }

    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ArtworkStatus getStatus() {
        return status;
    }

    public void setStatus(ArtworkStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == ArtworkStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return String.format("[#%d] \"%s\" by %s (%d) - %s - $%s - %s",
                id, title, artistName, yearCreated, medium, price, status);
    }
}
