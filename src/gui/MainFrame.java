package gui;

import theknife.ClientTK;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;


public class MainFrame extends JFrame {
    private final ClientTK client;

    public MainFrame() {
        this(null);
    }

    public MainFrame(ClientTK client){
        this.client = client;
        setTitle("TheKnife");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));

        JLabel titolo = new JLabel("The Knife");
        Font font = new Font("Arial", Font.BOLD, 30);
        titolo.setFont(font);

        JButton login = new JButton("Login");
        login.addActionListener(e -> {
            new LoginFrame(client);
            dispose();
        });

        JButton reg = new JButton("Registrati");
        reg.addActionListener(e -> {
            new RegFrame(client);
            dispose();
        });

        JButton cercaRis = new JButton("Ricerca Ristorante - guest");
        cercaRis.addActionListener(e -> {
            new GuestFrame(client);
            dispose();
        });

        JButton esci = new JButton("Esci");
        esci.addActionListener(e -> {
            System.exit(0);
        });


        panel.add(titolo);
        panel.add(login);
        panel.add(reg);
        panel.add(cercaRis);
        panel.add(esci);

        add(panel);
    }

}
