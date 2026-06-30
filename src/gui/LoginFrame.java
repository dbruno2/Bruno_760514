package gui;

import dto.Richiesta;
import dto.Risposta;
import dto.TipoOperazione;
import theknife.ClientTK;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final ClientTK client;

    public LoginFrame() {
        this(null);
    }

    public LoginFrame(ClientTK client) {
        this.client = client;

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
            new MainFrame(client);
            dispose();
        });

        login.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (client == null) {
                JOptionPane.showMessageDialog(this, "Connessione al server non disponibile");
                return;
            }

            String risultato;
            try {
                Risposta risposta = client.inviaRichiesta(
                        new Richiesta(TipoOperazione.LOGIN, username, password)
                );
                if (risposta != null) {
                    if (risposta.getMessaggio() != null) {
                        risultato = risposta.getMessaggio();
                    } else if (risposta.getArgomenti() != null && risposta.getArgomenti().length > 0) {
                        risultato = String.valueOf(risposta.getArgomenti()[0]);
                    } else {
                        risultato = "false";
                    }
                } else {
                    risultato = "false";
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore di comunicazione con il server");
                ex.printStackTrace();
                return;
            }

            String esito = risultato;
            if (risultato != null && risultato.startsWith("true,")) {
                String[] parti = risultato.split(",");
                if (parti.length >= 2) {
                    esito = parti[0] + "," + parti[1];
                }
            }

            if (esito.equals("true,cliente")) {
                new ClienteFrame(client);
                dispose();
            } else if (esito.equals("true,ristoratore")) {
                new RistoratoreFrame(client);
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
