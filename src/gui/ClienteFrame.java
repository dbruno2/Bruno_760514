package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
public class ClienteFrame extends JFrame {

    public ClienteFrame() {
        setTitle("ClienteFrame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);



        JButton indietro = new JButton("Indietro");



        // PANNELLO NORD

        JPanel northPanel = new JPanel();
        JLabel titolo = new JLabel("BENVENUTO");
        titolo.setFont(getFont());
        northPanel.add(titolo);

        // PANNELLO CENTRALE

        JPanel centerPanel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));


        centerPanel.add(formPanel);

        // PANNELLO SUD

        JPanel southPanel = new JPanel(new FlowLayout());

        southPanel.add(indietro);

        // funzionamento bottoni

        indietro.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        // aggiunta pannelli

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}