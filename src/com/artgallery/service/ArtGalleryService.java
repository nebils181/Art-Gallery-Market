package com.artgallery.service;

import com.artgallery.exception.ArtworkNotAvailableException;
import com.artgallery.exception.RecordNotFoundException;
import com.artgallery.model.Artwork;
import com.artgallery.model.ArtworkStatus;
import com.artgallery.model.Customer;
import com.artgallery.model.Purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central service that owns all gallery data and business rules:
 *  - artwork inventory (CRUD + availability)
 *  - customer records (CRUD)
 *  - purchases (sale processing, customer-artwork relationship)
 *
 * Storage is in-memory (LinkedHashMap keeps insertion order for nicer listings).
 * Swapping this for a database later would only mean changing this class'
 * internals, since Main only talks to this service.
 */
public class ArtGalleryService {

    private final Map<Integer, Artwork> artworks = new LinkedHashMap<>();
    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final Map<Integer, Purchase> purchases = new LinkedHashMap<>();

    private int nextArtworkId = 1;
    private int nextCustomerId = 1;
    private int nextPurchaseId = 1;

    // ---------------------------------------------------------------
    // Artwork management
    // ---------------------------------------------------------------

    public Artwork addArtwork(String title, String artistName, String medium,
                               int yearCreated, BigDecimal price) {
        Artwork artwork = new Artwork(nextArtworkId++, title, artistName, medium, yearCreated, price);
        artworks.put(artwork.getId(), artwork);
        return artwork;
    }

    public Artwork getArtwork(int id) throws RecordNotFoundException {
        Artwork artwork = artworks.get(id);
        if (artwork == null) {
            throw new RecordNotFoundException("No artwork found with id " + id);
        }
        return artwork;
    }

    public boolean removeArtwork(int id) {
        return artworks.remove(id) != null;
    }

    public List<Artwork> getAllArtworks() {
        return new ArrayList<>(artworks.values());
    }

    public List<Artwork> getAvailableArtworks() {
        return artworks.values().stream()
                .filter(Artwork::isAvailable)
                .collect(Collectors.toList());
    }

    /** Case-insensitive search across title and artist name. */
    public List<Artwork> searchArtworks(String keyword) {
        String needle = keyword.toLowerCase();
        return artworks.values().stream()
                .filter(a -> a.getTitle().toLowerCase().contains(needle)
                        || a.getArtistName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    public List<Artwork> getArtworksInPriceRange(BigDecimal min, BigDecimal max) {
        return artworks.values().stream()
                .filter(a -> a.getPrice().compareTo(min) >= 0 && a.getPrice().compareTo(max) <= 0)
                .collect(Collectors.toList());
    }

    public void reserveArtwork(int artworkId) throws RecordNotFoundException, ArtworkNotAvailableException {
        Artwork artwork = getArtwork(artworkId);
        if (!artwork.isAvailable()) {
            throw new ArtworkNotAvailableException(
                    "Artwork #" + artworkId + " is " + artwork.getStatus() + ", cannot reserve.");
        }
        artwork.setStatus(ArtworkStatus.RESERVED);
    }

    public void cancelReservation(int artworkId) throws RecordNotFoundException {
        Artwork artwork = getArtwork(artworkId);
        if (artwork.getStatus() == ArtworkStatus.RESERVED) {
            artwork.setStatus(ArtworkStatus.AVAILABLE);
        }
    }

    // ---------------------------------------------------------------
    // Customer management
    // ---------------------------------------------------------------

    public Customer addCustomer(String name, String phone, String email) {
        Customer customer = new Customer(nextCustomerId++, name, phone, email);
        customers.put(customer.getId(), customer);
        return customer;
    }

    public Customer getCustomer(int id) throws RecordNotFoundException {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new RecordNotFoundException("No customer found with id " + id);
        }
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public List<Customer> searchCustomersByName(String keyword) {
        String needle = keyword.toLowerCase();
        return customers.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------
    // Purchase processing (the core "market" transaction)
    // ---------------------------------------------------------------

    /**
     * Processes the purchase of an artwork by a customer:
     *  - validates both records exist
     *  - validates the artwork is still available (AVAILABLE or RESERVED by no one else)
     *  - records the sale (customer-artwork relationship)
     *  - flips the artwork's status to SOLD
     */
    public Purchase purchaseArtwork(int artworkId, int customerId)
            throws RecordNotFoundException, ArtworkNotAvailableException {

        Artwork artwork = getArtwork(artworkId);
        Customer customer = getCustomer(customerId);

        if (artwork.getStatus() == ArtworkStatus.SOLD) {
            throw new ArtworkNotAvailableException(
                    "Artwork #" + artworkId + " (\"" + artwork.getTitle() + "\") is already sold.");
        }
        // AVAILABLE or RESERVED are both purchasable (reserved = held for this sale)

        Purchase purchase = new Purchase(nextPurchaseId++, artwork, customer, artwork.getPrice());
        purchases.put(purchase.getId(), purchase);

        artwork.setStatus(ArtworkStatus.SOLD);

        return purchase;
    }

    public List<Purchase> getAllPurchases() {
        return new ArrayList<>(purchases.values());
    }

    public List<Purchase> getPurchaseHistoryForCustomer(int customerId) {
        return purchases.values().stream()
                .filter(p -> p.getCustomer().getId() == customerId)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------
    // Reporting
    // ---------------------------------------------------------------

    public BigDecimal getTotalRevenue() {
        return purchases.values().stream()
                .map(Purchase::getPriceAtSale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countAvailable() {
        return artworks.values().stream().filter(a -> a.getStatus() == ArtworkStatus.AVAILABLE).count();
    }

    public long countReserved() {
        return artworks.values().stream().filter(a -> a.getStatus() == ArtworkStatus.RESERVED).count();
    }

    public long countSold() {
        return artworks.values().stream().filter(a -> a.getStatus() == ArtworkStatus.SOLD).count();
    }

    /** Best-selling artists by number of pieces sold, descending. */
    public Map<String, Long> getSalesByArtist() {
        return purchases.values().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getArtwork().getArtistName(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<Artwork> getAllArtworksSortedByPrice() {
        return artworks.values().stream()
                .sorted(Comparator.comparing(Artwork::getPrice))
                .collect(Collectors.toList());
    }
}
