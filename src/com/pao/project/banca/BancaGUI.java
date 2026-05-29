package com.pao.project.banca;

import com.pao.project.banca.models.Client;
import com.pao.project.banca.models.Cont;
import com.pao.project.banca.service.ClientService;
import com.pao.project.banca.service.ContService;
import com.pao.project.banca.utils.DatabaseInitializer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BancaGUI extends JFrame {

    private final ClientService clientService = ClientService.getInstance();
    private final ContService contService = ContService.getInstance();

    private JTable clientTable;
    private DefaultTableModel clientModel;

    private JTable contTable;
    private DefaultTableModel contModel;

    public BancaGUI() {
        // Initialize Database
        DatabaseInitializer.initialize();

        setTitle("Sistem Management Bancar - PAO 2026");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Gestionare Clienti", createClientPanel());
        tabs.addTab("Gestionare Conturi", createContPanel());

        add(tabs);
        
        refreshData();
    }

    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for Clients
        String[] columns = {"ID", "Nume", "Prenume", "CNP", "Email"};
        clientModel = new DefaultTableModel(columns, 0);
        clientTable = new JTable(clientModel);
        panel.add(new JScrollPane(clientTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());

        JButton addClientBtn = new JButton("Adauga Client");
        addClientBtn.addActionListener(e -> handleAddClient());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addClientBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createContPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for Accounts
        String[] columns = {"IBAN", "Client ID", "Tip", "Moneda", "Sold", "Banca"};
        contModel = new DefaultTableModel(columns, 0);
        contTable = new JTable(contModel);
        panel.add(new JScrollPane(contTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        
        JButton addContBtn = new JButton("Deschide Cont");
        addContBtn.addActionListener(e -> handleAddCont());

        JButton depuneBtn = new JButton("Depunere");
        depuneBtn.addActionListener(e -> handleDepunere());

        JButton transferBtn = new JButton("Transfer");
        transferBtn.addActionListener(e -> handleTransfer());

        buttonPanel.add(addContBtn);
        buttonPanel.add(depuneBtn);
        buttonPanel.add(transferBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleAddClient() {
        JTextField numeField = new JTextField();
        JTextField prenumeField = new JTextField();
        JTextField cnpField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
            "Nume:", numeField,
            "Prenume:", prenumeField,
            "CNP:", cnpField,
            "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Adauga Client Nou", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // For simplicity, we create a default address
                com.pao.project.banca.models.Adresa adresa = new com.pao.project.banca.models.Adresa("Strada Generica", "1", "Oras", "Judet", "000000", "Romania");
                clientService.inregistreazaClient(numeField.getText(), prenumeField.getText(), cnpField.getText(), emailField.getText(), "0700000000", adresa);
                refreshData();
                JOptionPane.showMessageDialog(this, "Client adaugat cu succes!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
            }
        }
    }

    private void handleAddCont() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un client din tabelul de Clienti mai intai!");
            return;
        }

        String clientId = (String) clientModel.getValueAt(selectedRow, 0);
        
        String[] tips = {"CURENT", "ECONOMII"};
        String tip = (String) JOptionPane.showInputDialog(this, "Tip Cont:", "Deschide Cont", JOptionPane.QUESTION_MESSAGE, null, tips, tips[0]);
        
        if (tip != null) {
            String banca = JOptionPane.showInputDialog(this, "Prefix Banca (ex: BCR, BT, ING):");
            if (banca != null && !banca.isEmpty()) {
                try {
                    contService.deschideCont(clientId, tip, com.pao.project.banca.models.Moneda.RON, banca);
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Cont deschis cu succes!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
                }
            }
        }
    }

    private void handleTransfer() {
        int selectedRow = contTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectati contul SURSA din tabel!");
            return;
        }

        String ibanSursa = (String) contModel.getValueAt(selectedRow, 0);
        String ibanDest = JOptionPane.showInputDialog(this, "IBAN Destinatie:");
        
        if (ibanDest != null && !ibanDest.isEmpty()) {
            String sumaStr = JOptionPane.showInputDialog(this, "Suma de transferat:");
            if (sumaStr != null && !sumaStr.isEmpty()) {
                try {
                    double suma = Double.parseDouble(sumaStr);
                    contService.transfera(ibanSursa, ibanDest, suma);
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Transfer realizat cu succes!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
                }
            }
        }
    }

    private void refreshData() {
        // Refresh Clients
        clientModel.setRowCount(0);
        List<Client> clienti = clientService.listeazaToti();
        for (Client c : clienti) {
            clientModel.addRow(new Object[]{c.getId(), c.getNume(), c.getPrenume(), c.getCnp(), c.getEmail()});
        }

        // Refresh Conturi
        contModel.setRowCount(0);
        List<Cont> conturi = contService.listeazaToate();
        for (Cont cont : conturi) {
            contModel.addRow(new Object[]{
                cont.getIban(), 
                cont.getIdClient(), 
                cont.getTipCont(), 
                cont.getMoneda(), 
                cont.getSold(), 
                cont.getNumeBanca()
            });
        }
    }

    private void handleDepunere() {
        int selectedRow = contTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un cont din tabel!");
            return;
        }

        String iban = (String) contModel.getValueAt(selectedRow, 0);
        String sumaStr = JOptionPane.showInputDialog(this, "Suma de depus:");
        
        if (sumaStr != null && !sumaStr.isEmpty()) {
            try {
                double suma = Double.parseDouble(sumaStr);
                contService.depune(iban, suma);
                refreshData();
                JOptionPane.showMessageDialog(this, "Depunere reusita!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Set System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new BancaGUI().setVisible(true));
    }
}
