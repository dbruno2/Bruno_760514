package gui;

import dao.GestioneTheKnife;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.BOLD, 30);
        Dimension dim = new Dimension(250, 30);

        // Bottoni
        JButton indietro = new JButton("Indietro");
        JButton login = new JButton("Login");

        // Username
        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();
        txtUsername.setPreferredSize(dim);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(dim);

        // azioni pulsanti
        indietro.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        login.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            String risultato = GestioneTheKnife.login(username, password);

            if (risultato.equals("true,cliente")) {
                new ClienteFrame();
                dispose();
            } else if (risultato.equals("true,ristoratore")) {
                new RistoratoreFrame();
                dispose();
            }

            else {
                JOptionPane.showMessageDialog(
                        this,
                        "Username o password errati"
                );
            }
        });

        // PANNELLO NORD

        JPanel northPanel = new JPanel();
        JLabel titolo = new JLabel("Login");
        titolo.setFont(font);
        northPanel.add(titolo);

        // PANNELLO CENTRALE

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        centerPanel.add(formPanel);

        // PANNELLO SUD

        JPanel southPanel = new JPanel(new FlowLayout());

        southPanel.add(indietro);
        southPanel.add(login);

        // AGGIUNTA PANNELLI

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}