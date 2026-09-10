package com.artgallery.gui;

import com.artgallery.service.ArtGalleryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

/**
 * "Dashboard" tab: quick at-a-glance stats about the gallery -
 * inventory counts by status, total revenue, and top-selling artists.
 */
public class ReportsPanel extends JPanel {

    private final ArtGalleryService gallery;

    private final JLabel availableLabel = new JLabel();
    private final JLabel reservedLabel = new JLabel();
    private final JLabel soldLabel = new JLabel();
    private final JLabel revenueLabel = new JLabel();
    private final JTextArea artistBreakdown = new JTextArea();

    public ReportsPanel(ArtGalleryService gallery) {
        this.gallery = gallery;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel stats = new JPanel(new GridLayout(2, 2, 12, 12));
        stats.setBorder(new TitledBorder("Inventory Snapshot"));

        Font big = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        for (JLabel label : new JLabel[]{availableLabel, reservedLabel, soldLabel, revenueLabel}) {
            label.setFont(big);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }
        stats.add(wrapWithCaption(availableLabel, "Available"));
        stats.add(wrapWithCaption(reservedLabel, "Reserved"));
        stats.add(wrapWithCaption(soldLabel, "Sold"));
        stats.add(wrapWithCaption(revenueLabel, "Total Revenue"));

        add(stats, BorderLayout.NORTH);

        artistBreakdown.setEditable(false);
        artistBreakdown.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JPanel artistPanel = new JPanel(new BorderLayout());
        artistPanel.setBorder(new TitledBorder("Pieces Sold by Artist"));
        artistPanel.add(new JScrollPane(artistBreakdown), BorderLayout.CENTER);
        add(artistPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refresh());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refreshButton);
        add(south, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel wrapWithCaption(JLabel valueLabel, String caption) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(valueLabel, BorderLayout.CENTER);
        JLabel captionLabel = new JLabel(caption, SwingConstants.CENTER);
        captionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(captionLabel, BorderLayout.SOUTH);
        return panel;
    }

    public void refresh() {
        availableLabel.setText(String.valueOf(gallery.countAvailable()));
        reservedLabel.setText(String.valueOf(gallery.countReserved()));
        soldLabel.setText(String.valueOf(gallery.countSold()));
        revenueLabel.setText("$" + gallery.getTotalRevenue());

        Map<String, Long> byArtist = gallery.getSalesByArtist();
        if (byArtist.isEmpty()) {
            artistBreakdown.setText("No sales yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            byArtist.forEach((artist, count) -> sb.append(artist).append(" - ").append(count).append(" piece(s) sold\n"));
            artistBreakdown.setText(sb.toString());
        }
    }
}
