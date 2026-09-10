package com.artgallery.gui;

import com.artgallery.service.ArtGalleryService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Top-level Swing window for the Art Gallery Market.
 * Holds one shared ArtGalleryService and wires each tab so that a change
 * in one (e.g. a purchase) refreshes every other tab that shows related data.
 */
public class MainFrame extends JFrame implements DataChangeListener {

    private final ArtGalleryService gallery = new ArtGalleryService();

    private ArtworkPanel artworkPanel;
    private CustomerPanel customerPanel;
    private PurchasePanel purchasePanel;
    private ReportsPanel reportsPanel;

    public MainFrame() {
        super("Art Gallery Market");
        seedSampleData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 640);
        setMinimumSize(new Dimension(760, 480));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        artworkPanel = new ArtworkPanel(gallery, this);
        customerPanel = new CustomerPanel(gallery, this);
        purchasePanel = new PurchasePanel(gallery, this);
        reportsPanel = new ReportsPanel(gallery);

        tabs.addTab("Artworks", artworkPanel);
        tabs.addTab("Customers", customerPanel);
        tabs.addTab("Sales", purchasePanel);
        tabs.addTab("Dashboard", reportsPanel);

        setContentPane(tabs);
    }

    /** Called by any tab after it changes data, so every tab stays in sync. */
    @Override
    public void onDataChanged() {
        artworkPanel.refresh();
        customerPanel.refresh();
        purchasePanel.refresh();
        reportsPanel.refresh();
    }

    private void seedSampleData() {
        gallery.addArtwork("Sunset Over the Bay", "Amara Chen", "Oil on canvas", 2021, new BigDecimal("1200.00"));
        gallery.addArtwork("Fractured Light", "Diego Ruiz", "Mixed media", 2019, new BigDecimal("850.00"));
        gallery.addArtwork("Silent Forest", "Amara Chen", "Watercolor", 2022, new BigDecimal("600.00"));
        gallery.addArtwork("Bronze Figure No. 3", "Helena Wolfe", "Bronze sculpture", 2018, new BigDecimal("3400.00"));

        gallery.addCustomer("Nia Osei", "555-0142", "nia.osei@example.com");
        gallery.addCustomer("Marcus Bell", "555-0198", "marcus.bell@example.com");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default look and feel
            }
            new MainFrame().setVisible(true);
        });
    }
}
