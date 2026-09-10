package com.artgallery.gui;

import com.artgallery.model.Purchase;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PurchaseTableModel extends AbstractTableModel {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String[] columns = {"Sale ID", "Artwork", "Customer", "Price Paid", "Date"};
    private List<Purchase> purchases = new ArrayList<>();

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return purchases.size();
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
    public Object getValueAt(int row, int col) {
        Purchase p = purchases.get(row);
        switch (col) {
            case 0: return p.getId();
            case 1: return p.getArtwork().getTitle();
            case 2: return p.getCustomer().getName();
            case 3: return "$" + p.getPriceAtSale();
            case 4: return p.getPurchaseDate().format(FORMAT);
            default: return "";
        }
    }
}
