package gui;

import com.sun.tools.javac.Main;
import dao.GestioneTheKnife;
import javax.swing.*;
import java.awt.*;
import java.util.List;


public class RisultatiRicercaFrame extends JFrame {
    RisultatiRicercaFrame(List<String> risultati){
        setTitle("RisultatiRicercaFrame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.BOLD, 30);

        // pannello nord
        JPanel northPanel = new JPanel();
        JLabel titolo = new JLabel("Lista ristoranti trovati");
        titolo.setFont(font);
        northPanel.add(titolo);

        // pannello centrale
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        for (String ristorante : risultati) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(Color.GRAY)
            ));

            JTextArea testo = new JTextArea(ristorante);
            testo.setEditable(false);
            testo.setLineWrap(true);
            testo.setWrapStyleWord(true);
            JButton dettagli = new JButton("Visualizza");
            card.add(testo, BorderLayout.CENTER);
            card.add(dettagli, BorderLayout.SOUTH);
            formPanel.add(card);
            formPanel.add(Box.createVerticalStrut(10)); // spazio tra una card e l'altra
        }
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        centerPanel.add(scrollPane);

        //pannello south

        JButton indietro = new JButton("Indietro");
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(indietro);

        // funzionamento tasti
        indietro.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        // aggiunta pannelli
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

}
