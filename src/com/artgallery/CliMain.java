package com.artgallery;

import com.artgallery.exception.ArtworkNotAvailableException;
import com.artgallery.exception.RecordNotFoundException;
import com.artgallery.model.Artwork;
import com.artgallery.model.Customer;
import com.artgallery.model.Purchase;
import com.artgallery.service.ArtGalleryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console entry point for the Art Gallery Market system.
 */
public class CliMain {

    private static final ArtGalleryService gallery = new ArtGalleryService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addArtworkFlow(); break;
                    case "2": listAllArtworks(); break;
                    case "3": listAvailableArtworks(); break;
                    case "4": searchArtworksFlow(); break;
                    case "5": addCustomerFlow(); break;
                    case "6": listAllCustomers(); break;
                    case "7": purchaseFlow(); break;
                    case "8": reserveFlow(); break;
                    case "9": customerHistoryFlow(); break;
                    case "10": salesReport(); break;
                    case "0": running = false; break;
                    default: System.out.println("Not a valid option, try again.");
                }
            } catch (RecordNotFoundException | ArtworkNotAvailableException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            System.out.println();
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("===== Art Gallery Market =====");
        System.out.println(" 1. Add artwork");
        System.out.println(" 2. List all artworks");
        System.out.println(" 3. List available artworks");
        System.out.println(" 4. Search artworks (title/artist)");
        System.out.println(" 5. Add customer");
        System.out.println(" 6. List all customers");
        System.out.println(" 7. Purchase artwork");
        System.out.println(" 8. Reserve artwork");
        System.out.println(" 9. View customer purchase history");
        System.out.println("10. Sales report");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option: ");
    }

    // ---------------------------------------------------------------
    // Artwork flows
    // ---------------------------------------------------------------

    private static void addArtworkFlow() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Artist name: ");
        String artist = scanner.nextLine().trim();
        System.out.print("Medium (e.g. Oil on canvas): ");
        String medium = scanner.nextLine().trim();
        System.out.print("Year created: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine().trim());

        Artwork artwork = gallery.addArtwork(title, artist, medium, year, price);
        System.out.println("Added: " + artwork);
    }

    private static void listAllArtworks() {
        List<Artwork> all = gallery.getAllArtworks();
        if (all.isEmpty()) {
            System.out.println("No artworks in the gallery yet.");
            return;
        }
        all.forEach(System.out::println);
    }

    private static void listAvailableArtworks() {
        List<Artwork> available = gallery.getAvailableArtworks();
        if (available.isEmpty()) {
            System.out.println("No artworks currently available.");
            return;
        }
        available.forEach(System.out::println);
    }

    private static void searchArtworksFlow() {
        System.out.print("Search keyword (title or artist): ");
        String keyword = scanner.nextLine().trim();
        List<Artwork> results = gallery.searchArtworks(keyword);
        if (results.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        results.forEach(System.out::println);
    }

    // ---------------------------------------------------------------
    // Customer flows
    // ---------------------------------------------------------------

    private static void addCustomerFlow() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        Customer customer = gallery.addCustomer(name, phone, email);
        System.out.println("Added: " + customer);
    }

    private static void listAllCustomers() {
        List<Customer> all = gallery.getAllCustomers();
        if (all.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        all.forEach(System.out::println);
    }

    // ---------------------------------------------------------------
    // Purchase / reservation flows
    // ---------------------------------------------------------------

    private static void purchaseFlow() throws RecordNotFoundException, ArtworkNotAvailableException {
        System.out.print("Artwork id: ");
        int artworkId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Customer id: ");
        int customerId = Integer.parseInt(scanner.nextLine().trim());

        Purchase purchase = gallery.purchaseArtwork(artworkId, customerId);
        System.out.println("Sale completed: " + purchase);
    }

    private static void reserveFlow() throws RecordNotFoundException, ArtworkNotAvailableException {
        System.out.print("Artwork id to reserve: ");
        int artworkId = Integer.parseInt(scanner.nextLine().trim());
        gallery.reserveArtwork(artworkId);
        System.out.println("Artwork #" + artworkId + " is now RESERVED.");
    }

    private static void customerHistoryFlow() throws RecordNotFoundException {
        System.out.print("Customer id: ");
        int customerId = Integer.parseInt(scanner.nextLine().trim());
        Customer customer = gallery.getCustomer(customerId); // validates existence
        List<Purchase> history = gallery.getPurchaseHistoryForCustomer(customerId);

        System.out.println("Purchase history for " + customer.getName() + ":");
        if (history.isEmpty()) {
            System.out.println("  (no purchases yet)");
        } else {
            history.forEach(p -> System.out.println("  " + p));
        }
    }

    private static void salesReport() {
        System.out.println("Available: " + gallery.countAvailable()
                + " | Reserved: " + gallery.countReserved()
                + " | Sold: " + gallery.countSold());
        System.out.println("Total revenue: $" + gallery.getTotalRevenue());

        Map<String, Long> byArtist = gallery.getSalesByArtist();
        if (!byArtist.isEmpty()) {
            System.out.println("Pieces sold by artist:");
            byArtist.forEach((artist, count) -> System.out.println("  " + artist + ": " + count));
        }
    }

    // ---------------------------------------------------------------
    // Sample data so the menu is immediately useful to try out
    // ---------------------------------------------------------------

    private static void seedSampleData() {
        gallery.addArtwork("Sunset Over the Bay", "Amara Chen", "Oil on canvas", 2021, new BigDecimal("1200.00"));
        gallery.addArtwork("Fractured Light", "Diego Ruiz", "Mixed media", 2019, new BigDecimal("850.00"));
        gallery.addArtwork("Silent Forest", "Amara Chen", "Watercolor", 2022, new BigDecimal("600.00"));
        gallery.addArtwork("Bronze Figure No. 3", "Helena Wolfe", "Bronze sculpture", 2018, new BigDecimal("3400.00"));

        gallery.addCustomer("Nia Osei", "555-0142", "nia.osei@example.com");
        gallery.addCustomer("Marcus Bell", "555-0198", "marcus.bell@example.com");
    }
}
