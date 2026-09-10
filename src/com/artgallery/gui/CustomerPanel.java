package com.artgallery.gui;

import com.artgallery.service.ArtGalleryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private final ArtGalleryService gallery;
    private final DataChangeListener listener;

    private final JTextField nameField = new JTextField(14);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField emailField = new JTextField(16);

    private final CustomerTableModel tableModel = new CustomerTableModel();
    private final JTable table = new JTable(tableModel);

    public CustomerPanel(ArtGalleryService gallery, DataChangeListener listener) {
        this.gallery = gallery;
        this.listener = listener;

        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildInsertForm(), BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Customers"));
        table.setRowHeight(22);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildInsertForm() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        form.setBorder(new TitledBorder("Add New Customer"));

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Email:"));
        form.add(emailField);

        JButton addButton = new JButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        form.add(addButton);

        return form;
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and phone are required.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        gallery.addCustomer(name, phone, email);

        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        nameField.requestFocus();

        if (listener != null) {
            listener.onDataChanged();
        } else {
            refresh();
        }
    }

    public void refresh() {
        tableModel.setCustomers(gallery.getAllCustomers());
    }
}
