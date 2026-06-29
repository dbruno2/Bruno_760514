package gui;

import dao.GestioneTheKnife;
import sicurezzaPassword.Criptazione;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Calendar;

import static dao.GestioneTheKnife.parseDataNascita;


public class RegFrame extends JFrame {

    public RegFrame() {

        setTitle("Registrazione");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.BOLD, 30);
        Dimension dim = new Dimension(250, 30);

        // Bottoni
        JButton indietro = new JButton("Indietro");
        JButton registrazione = new JButton("Conferma");

        // Username
        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();
        txtUsername.setPreferredSize(dim);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(dim);

        // nome
        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField();
        txtNome.setPreferredSize(dim);

        // cognome
        JLabel lblCognome = new JLabel("Cognome:");
        JTextField txtCognome = new JTextField();
        txtCognome.setPreferredSize(dim);

        // domicilio
        JLabel lblDomicilio = new JLabel("Domicilio:");
        JTextField txtDomicilio = new JTextField();
        txtDomicilio.setPreferredSize(dim);

        // Ruolo
        JComboBox<String> cmbRuolo = new JComboBox<>(new String[]{"cliente", "ristoratore"});

        // DataNascita
        JLabel lblDataNascita = new JLabel("Data di nascita (facoltativo) - (usa dd/MM/yyyy):");
        JTextField txtDataNascita = new JTextField();
        txtDataNascita.setPreferredSize(dim);

        // azioni pulsanti
        indietro.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        registrazione.addActionListener(e -> {

            String nome = txtNome.getText().trim();
            String cognome = txtCognome.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String domicilio = txtDomicilio.getText().trim();
            String ruolo = (String) cmbRuolo.getSelectedItem();
            String dataNascita = txtDataNascita.getText().trim();

            Calendar dataNascitaCal;

            try {
                dataNascitaCal = parseDataNascita(dataNascita);
            } catch (IllegalArgumentException i) {
                JOptionPane.showMessageDialog(
                        this,
                        "Formato data non valido (usa dd/MM/yyyy)"
                );
                return;
            }

            password = Criptazione.critta(password);

            boolean registrato = GestioneTheKnife.registraUtente(nome, cognome, username, password, dataNascita, domicilio, ruolo, "");

            if(registrato){
                JOptionPane.showMessageDialog(this,
                        "Registrazione completata!");

                new MainFrame();
                dispose();
            }else{
                JOptionPane.showMessageDialog(this,
                        "Errore durante la registrazione.");
            }

        });

        // PANNELLO NORD

        JPanel northPanel = new JPanel();
        JLabel titolo = new JLabel("Registrazione");
        titolo.setFont(font);
        northPanel.add(titolo);

        // PANNELLO CENTRALE

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 15));

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);
        formPanel.add(lblNome);
        formPanel.add(txtNome);
        formPanel.add(lblCognome);
        formPanel.add(txtCognome);
        formPanel.add(lblDomicilio);
        formPanel.add(txtDomicilio);
        formPanel.add(new JLabel("Ruolo:"));
        formPanel.add(cmbRuolo);
        formPanel.add(lblDataNascita);
        formPanel.add(txtDataNascita);

        centerPanel.add(formPanel);
        // PANNELLO SUD

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(indietro);
        southPanel.add(registrazione);

        // AGGIUNTA PANNELLI

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}