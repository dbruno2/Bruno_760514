package gui;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    public MainFrame(){
        setTitle("TheKnife");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));

        JButton login = new JButton("Login Cliente");
        login.addActionListener(e -> {
            new LoginClienteFrame(); // apre la nuova finestra
            dispose();        // chiude quella corrente
        });

        JButton loginRis = new JButton("Login Ristoratore");
        loginRis.addActionListener(e -> {
            new LoginRisFrame(); // apre la nuova finestra
            dispose();        // chiude quella corrente
        });

        JButton reg = new JButton("Registrati");
        reg.addActionListener(e -> {
            new RegFrame(); // apre la nuova finestra
            dispose();        // chiude quella corrente
        });

        JButton cercaRis = new JButton("Ricerca Ristorante - guest");
        cercaRis.addActionListener(e -> {
            new LoginClienteFrame(); // apre la nuova finestra
            dispose();        // chiude quella corrente
        });

        JButton esci = new JButton("Esci");
        esci.addActionListener(e -> {
            System.exit(0);
        });

        panel.add(login);
        panel.add(loginRis);
        panel.add(reg);
        panel.add(cercaRis);
        panel.add(esci);

        add(panel);
    }

}
