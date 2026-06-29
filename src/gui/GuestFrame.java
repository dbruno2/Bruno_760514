package gui;

import com.sun.tools.javac.Main;
import dao.GestioneTheKnife;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

public class GuestFrame extends JFrame {

    public GuestFrame() {
        setTitle("GuestFrame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.BOLD, 30);

        // PANNELLO NORD

        JPanel northPanel = new JPanel();
        JLabel titolo = new JLabel("Cerca ristoranti");
        titolo.setFont(font);
        northPanel.add(titolo);

        // zona
        JLabel lblZona = new JLabel("Zona geografica (obbligatorio):");
        JTextField txtZona = new JTextField();

        // tipo di cucina
        JLabel lblCucina = new JLabel("Tipo di cucina (facoltativo):");
        JTextField txtCucina = new JTextField();

        // prezzo minimo
        JLabel lblPrezzoMin = new JLabel("Prezzo minimo (facoltativo):");
        JTextField txtPrezzoMin = new JTextField();

        // prezzo max
        JLabel lblPrezzoMax = new JLabel("Prezzo massimo (facoltativo):");
        JTextField txtPrezzoMax = new JTextField();

        // delivery
        JComboBox<String> cmbDelivery = new JComboBox<>(new String[]{" ","true", "false"});

        // prenotazione online
        JComboBox<String> cmbOnline = new JComboBox<>(new String[]{" ","true", "false"});

        //valutazione media
        JLabel lblStelle = new JLabel("Valutazione media 1...5 (facoltativo):");
        JTextField txtStelle = new JTextField();

        // PANNELLO CENTRALE

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 15));

        formPanel.add(lblZona);
        formPanel.add(txtZona);
        formPanel.add(lblCucina);
        formPanel.add(txtCucina);
        formPanel.add(lblPrezzoMin);
        formPanel.add(txtPrezzoMin);
        formPanel.add(lblPrezzoMax);
        formPanel.add(txtPrezzoMax);
        formPanel.add(new JLabel("Delivery (facoltativo):"));
        formPanel.add(cmbDelivery);
        formPanel.add(new JLabel("Prenotazione online (facoltativo):"));
        formPanel.add(cmbOnline);
        formPanel.add(lblStelle);
        formPanel.add(txtStelle);

        centerPanel.add(formPanel);

        // PANNELLO SUD
        JButton indietro = new JButton("Indietro");
        JButton conferma = new JButton("Conferma");

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(indietro);
        southPanel.add(conferma);

        // funzionamento bottoni

        indietro.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        conferma.addActionListener(e -> {

            String zona = txtZona.getText().trim();

            if (zona.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La zona geografica è obbligatoria.");
                return;
            }

            String cucina = txtCucina.getText().trim();

            Integer prezzoMin = txtPrezzoMin.getText().isBlank()
                    ? null
                    : Integer.valueOf(txtPrezzoMin.getText());

            Integer prezzoMax = txtPrezzoMax.getText().isBlank()
                    ? null
                    : Integer.valueOf(txtPrezzoMax.getText());

            Boolean delivery = cmbDelivery.getSelectedItem().equals("Nessun filtro")
                    ? null
                    : cmbDelivery.getSelectedItem().equals("true");

            Boolean prenotazione = cmbOnline.getSelectedItem().equals("Nessun filtro")
                    ? null
                    : cmbOnline.getSelectedItem().equals("true");

            Double stelleMin = txtStelle.getText().isBlank()
                    ? null
                    : Double.valueOf(txtStelle.getText());

            List<String> risultati = List.of();  //= GestioneTheKnife.cercaRistorantiAvanzata(zona, cucina, prezzoMin, prezzoMax, delivery, prenotazione, stelleMin);

            if (risultati.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nessun ristorante trovato.");
            } else {
                if (risultati.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Nessun ristorante trovato.");
                } else {
                    new RisultatiRicercaFrame(risultati);
                    dispose();
                }
            }

        });

        // aggiunta pannelli

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}