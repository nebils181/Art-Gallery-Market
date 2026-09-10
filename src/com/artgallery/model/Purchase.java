package com.artgallery.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a finalized sale: the link between a Customer and an Artwork,
 * including the price actually paid and when the sale happened.
 * Keeping priceAtSale separate from Artwork.price preserves history even
 * if the gallery's price list changes later.
 */
public class Purchase {

    private final int id;
    private final Artwork artwork;
    private final Customer customer;
    private final BigDecimal priceAtSale;
    private final LocalDateTime purchaseDate;

    public Purchase(int id, Artwork artwork, Customer customer, BigDecimal priceAtSale) {
        this.id = id;
        this.artwork = artwork;
        this.customer = customer;
        this.priceAtSale = priceAtSale;
        this.purchaseDate = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getPriceAtSale() {
        return priceAtSale;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    @Override
    public String toString() {
        return String.format("[Sale #%d] \"%s\" -> %s for $%s on %s",
                id, artwork.getTitle(), customer.getName(), priceAtSale, purchaseDate);
    }
}
