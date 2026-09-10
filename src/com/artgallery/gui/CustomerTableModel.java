package com.artgallery.gui;

import com.artgallery.model.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CustomerTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Name", "Phone", "Email"};
    private List<Customer> customers = new ArrayList<>();

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        fireTableDataChanged();
    }

    public Customer getCustomerAt(int row) {
        return customers.get(row);
    }

    @Override
    public int getRowCount() {
        return customers.size();
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
        Customer c = customers.get(row);
        switch (col) {
            case 0: return c.getId();
            case 1: return c.getName();
            case 2: return c.getPhone();
            case 3: return c.getEmail();
            default: return "";
        }
    }
}
