package com.artgallery.gui;

import com.artgallery.exception.ArtworkNotAvailableException;
import com.artgallery.exception.RecordNotFoundException;
import com.artgallery.model.Artwork;
import com.artgallery.service.ArtGalleryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * "Artworks" tab: a form on top to insert a new artwork, and a table below
 * that displays the gallery's inventory. Supports searching by title/artist
 * and toggling between "all" and "available only".
 */
public class ArtworkPanel extends JPanel {

    private final ArtGalleryService gallery;
    private final DataChangeListener listener;

    private final JTextField titleField = new JTextField(16);
    private final JTextField artistField = new JTextField(14);
    private final JTextField mediumField = new JTextField(14);
    private final JTextField yearField = new JTextField(5);
    private final JTextField priceField = new JTextField(8);

    private final JTextField searchField = new JTextField(16);
    private final JCheckBox availableOnlyBox = new JCheckBox("Available only");

    private final ArtworkTableModel tableModel = new ArtworkTableModel();
    private final JTable table = new JTable(tableModel);

    public ArtworkPanel(ArtGalleryService gallery, DataChangeListener listener) {
        this.gallery = gallery;
        this.listener = listener;

        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildInsertForm(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActionBar(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildInsertForm() {
        // Two explicit rows (rather than one auto-wrapping FlowLayout row) so the
        // panel's preferred height is always correct and nothing gets clipped
        // when the window is narrower than the full field set.
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.setBorder(new TitledBorder("Insert New Artwork"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row1.add(new JLabel("Title:"));
        row1.add(titleField);
        row1.add(new JLabel("Artist:"));
        row1.add(artistField);
        row1.add(new JLabel("Medium:"));
        row1.add(mediumField);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row2.add(new JLabel("Year:"));
        row2.add(yearField);
        row2.add(new JLabel("Price ($):"));
        row2.add(priceField);

        JButton addButton = new JButton("Add Artwork");
        addButton.addActionListener(e -> addArtwork());
        row2.add(addButton);

        form.add(row1);
        form.add(row2);

        return form;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new TitledBorder("Gallery Inventory"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        searchBar.add(new JLabel("Search (title/artist):"));
        searchBar.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> refresh());
        searchBar.add(searchButton);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refresh();
        });
        searchBar.add(clearButton);
        availableOnlyBox.addActionListener(e -> refresh());
        searchBar.add(availableOnlyBox);

        panel.add(searchBar, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton reserveButton = new JButton("Reserve Selected");
        reserveButton.addActionListener(e -> reserveSelected());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelected());
        bar.add(reserveButton);
        bar.add(deleteButton);
        return bar;
    }

    private void addArtwork() {
        String title = titleField.getText().trim();
        String artist = artistField.getText().trim();
        String medium = mediumField.getText().trim();
        String yearText = yearField.getText().trim();
        String priceText = priceField.getText().trim();

        if (title.isEmpty() || artist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and artist are required.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        BigDecimal price;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year must be a whole number.",
                    "Invalid year", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number (e.g. 1200.00).",
                    "Invalid price", JOptionPane.WARNING_MESSAGE);
            return;
        }

        gallery.addArtwork(title, artist, medium.isEmpty() ? "Unspecified" : medium, year, price);

        titleField.setText("");
        artistField.setText("");
        mediumField.setText("");
        yearField.setText("");
        priceField.setText("");
        titleField.requestFocus();

        notifyChanged();
    }

    private void reserveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an artwork first.",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Artwork artwork = tableModel.getArtworkAt(row);
        try {
            gallery.reserveArtwork(artwork.getId());
            notifyChanged();
        } catch (RecordNotFoundException | ArtworkNotAvailableException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot reserve", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an artwork first.",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Artwork artwork = tableModel.getArtworkAt(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + artwork.getTitle() + "\" from the gallery?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gallery.removeArtwork(artwork.getId());
            notifyChanged();
        }
    }

    private void notifyChanged() {
        if (listener != null) {
            listener.onDataChanged();
        } else {
            refresh();
        }
    }

    /** Reloads the table according to the current search text and filter checkbox. */
    public void refresh() {
        String keyword = searchField.getText().trim();
        List<Artwork> results = keyword.isEmpty()
                ? gallery.getAllArtworks()
                : gallery.searchArtworks(keyword);

        if (availableOnlyBox.isSelected()) {
            results.removeIf(a -> !a.isAvailable());
        }

        tableModel.setArtworks(results);
    }
}
