package src.gui;

import javax.swing.*;
import java.awt.*;

public class RegFrame extends JFrame {
    public RegFrame(){
        setTitle("Registrazione");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JButton indietro = new JButton("Indietro");

        indietro.addActionListener(e -> {
            new MainFrame(); // riapre la finestra precedente
            dispose();       // chiude la finestra corrente
        });

        JPanel panel= new JPanel(new GridLayout(1,1,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));


        panel.add(indietro);

        add(panel);
    }
}
