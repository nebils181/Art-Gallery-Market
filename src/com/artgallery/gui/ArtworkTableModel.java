package com.artgallery.gui;

import com.artgallery.model.Artwork;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a List<Artwork> to Swing's JTable.
 */
public class ArtworkTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Title", "Artist", "Medium", "Year", "Price", "Status"};
    private List<Artwork> artworks = new ArrayList<>();

    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
        fireTableDataChanged();
    }

    public Artwork getArtworkAt(int row) {
        return artworks.get(row);
    }

    @Override
    public int getRowCount() {
        return artworks.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col == 0 || col == 4) return Integer.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Artwork a = artworks.get(row);
        switch (col) {
            case 0: return a.getId();
            case 1: return a.getTitle();
            case 2: return a.getArtistName();
            case 3: return a.getMedium();
            case 4: return a.getYearCreated();
            case 5: return "$" + a.getPrice();
            case 6: return a.getStatus().toString();
            default: return "";
        }
    }
}
