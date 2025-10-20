import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryGUI extends JFrame {
    private JTextField productIdField, productNameField, stockField, priceField, updateProductIdField, updateStockField;
    private JButton insertButton, updateButton;
    private JLabel statusLabel;

    // Update these with your DB credentials
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Aleem007";

    public InventoryGUI() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 2, 5, 5));

        // Insert Product components
        add(new JLabel("Insert New Product", SwingConstants.CENTER));
        add(new JLabel(""));

        add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        add(productIdField);

        add(new JLabel("Product Name:"));
        productNameField = new JTextField();
        add(productNameField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        insertButton = new JButton("Insert Product");
        add(insertButton);
        add(new JLabel(""));

        // Update Stock components
        add(new JLabel("Update Stock", SwingConstants.CENTER));
        add(new JLabel(""));

        add(new JLabel("Product ID:"));
        updateProductIdField = new JTextField();
        add(updateProductIdField);

        add(new JLabel("New Stock:"));
        updateStockField = new JTextField();
        add(updateStockField);

        updateButton = new JButton("Update Stock");
        add(updateButton);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        add(statusLabel);

        // Button Listeners
        insertButton.addActionListener(e -> insertProduct());
        updateButton.addActionListener(e -> updateStock());

        setVisible(true);
    }

    private void insertProduct() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int productId = Integer.parseInt(productIdField.getText());
            String name = productNameField.getText();
            int stock = Integer.parseInt(stockField.getText());
            double price = Double.parseDouble(priceField.getText());

            String sql = "INSERT INTO Inventory (product_id, product_name, stock, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, productId);
                pst.setString(2, name);
                pst.setInt(3, stock);
                pst.setDouble(4, price);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    statusLabel.setText("Product inserted successfully.");
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                statusLabel.setText("Error: Product ID already exists.");
            }
        } catch (Exception ex) {
            statusLabel.setText("Insert failed: " + ex.getMessage());
        }
    }

    private void updateStock() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int productId = Integer.parseInt(updateProductIdField.getText());
            int newStock = Integer.parseInt(updateStockField.getText());

            String sql = "UPDATE Inventory SET stock = ? WHERE product_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, newStock);
                pst.setInt(2, productId);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    statusLabel.setText("Stock updated successfully.");
                } else {
                    statusLabel.setText("Product ID not found.");
                }
            }
        } catch (Exception ex) {
            statusLabel.setText("Update failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Optional: load driver explicitly
        try {
            Class.forName( "com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
            return;
        }

        SwingUtilities.invokeLater(() -> new InventoryGUI());
    }
}
