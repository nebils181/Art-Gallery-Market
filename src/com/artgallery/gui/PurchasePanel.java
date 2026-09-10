package com.artgallery.gui;

import com.artgallery.exception.ArtworkNotAvailableException;
import com.artgallery.exception.RecordNotFoundException;
import com.artgallery.model.Artwork;
import com.artgallery.model.Customer;
import com.artgallery.model.Purchase;
import com.artgallery.service.ArtGalleryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PurchasePanel extends JPanel {

    private final ArtGalleryService gallery;
    private final DataChangeListener listener;

    private final JComboBox<Artwork> artworkCombo = new JComboBox<>();
    private final JComboBox<Customer> customerCombo = new JComboBox<>();

    private final PurchaseTableModel tableModel = new PurchaseTableModel();
    private final JTable table = new JTable(tableModel);

    public PurchasePanel(ArtGalleryService gallery, DataChangeListener listener) {
        this.gallery = gallery;
        this.listener = listener;

        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildPurchaseForm(), BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Sale History"));
        table.setRowHeight(22);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildPurchaseForm() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        form.setBorder(new TitledBorder("Process a Purchase"));

        form.add(new JLabel("Artwork:"));
        artworkCombo.setPreferredSize(new Dimension(320, artworkCombo.getPreferredSize().height));
        form.add(artworkCombo);

        form.add(new JLabel("Customer:"));
        customerCombo.setPreferredSize(new Dimension(220, customerCombo.getPreferredSize().height));
        form.add(customerCombo);

        JButton purchaseButton = new JButton("Complete Purchase");
        purchaseButton.addActionListener(e -> completePurchase());
        form.add(purchaseButton);

        JButton refreshButton = new JButton("Refresh Lists");
        refreshButton.addActionListener(e -> refresh());
        form.add(refreshButton);

        return form;
    }

    private void completePurchase() {
        Artwork artwork = (Artwork) artworkCombo.getSelectedItem();
        Customer customer = (Customer) customerCombo.getSelectedItem();

        if (artwork == null || customer == null) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one artwork and one customer first.",
                    "Nothing to purchase", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Purchase purchase = gallery.purchaseArtwork(artwork.getId(), customer.getId());
            JOptionPane.showMessageDialog(this,
                    "Sale completed: \"" + purchase.getArtwork().getTitle() + "\" to "
                            + purchase.getCustomer().getName() + " for $" + purchase.getPriceAtSale(),
                    "Purchase successful", JOptionPane.INFORMATION_MESSAGE);
            if (listener != null) {
                listener.onDataChanged();
            } else {
                refresh();
            }
        } catch (RecordNotFoundException | ArtworkNotAvailableException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot complete purchase",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        Artwork previouslySelectedArtwork = (Artwork) artworkCombo.getSelectedItem();
        Customer previouslySelectedCustomer = (Customer) customerCombo.getSelectedItem();

        artworkCombo.removeAllItems();
        for (Artwork a : gallery.getAvailableArtworks()) {
            artworkCombo.addItem(a);
        }
        customerCombo.removeAllItems();
        for (Customer c : gallery.getAllCustomers()) {
            customerCombo.addItem(c);
        }

        if (previouslySelectedArtwork != null) {
            artworkCombo.setSelectedItem(previouslySelectedArtwork);
        }
        if (previouslySelectedCustomer != null) {
            customerCombo.setSelectedItem(previouslySelectedCustomer);
        }

        tableModel.setPurchases(gallery.getAllPurchases());
    }
}
