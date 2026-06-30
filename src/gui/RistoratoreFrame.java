package gui;

import theknife.ClientTK;

import javax.swing.*;

public class RistoratoreFrame extends JFrame {
    private final ClientTK client;

    public RistoratoreFrame() {
        this(null);
    }

    public RistoratoreFrame(ClientTK client) {
        this.client = client;
        setTitle("RistoratoreFrame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton indietro = new JButton("Indietro");
        indietro.addActionListener(e -> {
            new LoginFrame(client);
            dispose();
        });

        add(indietro);
        setVisible(true);
    }

}
