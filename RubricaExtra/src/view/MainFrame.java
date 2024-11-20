package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.RubricaDao;
import entità.Persona;
import entità.RubricaTelefonica;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private RubricaTelefonica controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public MainFrame() {
        controller = new RubricaTelefonica();

        try {
            // Connessione al database
            RubricaDao.connect();
            
            controller.setContatti(RubricaDao.caricaPersone()); // Carica i contatti dal file
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nella connessione al database");
        }

        // Configurazione della finestra
        setTitle("Rubrica Telefonica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Configurazione della finestra per il salvataggio dei contatti al momento della chiusura
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Salva i contatti prima di chiudere la finestra
                RubricaDao.salvaPersone(controller.getContatti());
                try {
                    RubricaDao.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Configurazione JTable
        String[] columnNames = {"Nome", "Cognome", "Telefono"};
        tableModel = new DefaultTableModel(columnNames, 0); // Inizializzazione della tabella
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Barra degli strumenti (JToolBar)
        JToolBar toolBar = creaToolBar();
        add(toolBar, BorderLayout.NORTH);

        // Carica i contatti dal file e aggiorna la tabella
        aggiornaTabella();
    }

    private JToolBar creaToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // Impedisce di spostare la barra
        toolBar.setLayout(new GridLayout(1, 3, 10, 0)); // Centra i bottoni in orizzontale con spaziatura

        JButton nuovoButton = creaBottone("Nuovo", "icons/nuovo.png");
        JButton modificaButton = creaBottone("Modifica", "icons/modifica.png");
        JButton eliminaButton = creaBottone("Elimina", "icons/elimina.png");

        toolBar.add(nuovoButton);
        toolBar.add(modificaButton);
        toolBar.add(eliminaButton);

        // Azioni Bottoni
        nuovoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriEditorPersona(null, -1);
            }
        });

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificaPersona();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminaPersona();
            }
        });

        return toolBar;
    }

    private JButton creaBottone(String testo, String percorsoIcona) {
        JButton bottone = new JButton(testo);
        try {
            // Carica e ridimensiona l'icona
            ImageIcon iconaOriginale = new ImageIcon(percorsoIcona);
            Image iconaRidimensionata = iconaOriginale.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            bottone.setIcon(new ImageIcon(iconaRidimensionata));
            bottone.setHorizontalTextPosition(SwingConstants.CENTER); // Testo centrato sotto l'icona
            bottone.setVerticalTextPosition(SwingConstants.BOTTOM);
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'icona: " + percorsoIcona);
        }
        return bottone;
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0); // Reset della tabella
        for (Persona p : controller.getContatti()) {
            tableModel.addRow(new Object[]{p.getNome(), p.getCognome(), p.getTelefono()});
        }
    }

    private void apriEditorPersona(Persona persona, int index) {
        EditorPersona editor = new EditorPersona(this, persona, new EditorPersona.PersonaListener() {
            @Override
            public void onPersonaSalvata(Persona personaSalvata) {
                if (index == -1) {
                    controller.aggiungiPersona(personaSalvata);
                } else {
                    controller.modificaPersona(index, personaSalvata);
                }
                aggiornaTabella();
            }
        });
        editor.setVisible(true);
    }

    private void modificaPersona() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una persona da modificare.");
            return;
        }
        Persona persona = controller.getContatti().get(selectedRow);
        apriEditorPersona(persona, selectedRow);
    }

    private void eliminaPersona() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una persona da eliminare.");
            return;
        }

        // Chiedi conferma prima di eliminare
        Persona persona = controller.getContatti().get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Eliminare la persona " + persona.getNome() + " " + persona.getCognome() + "?",
                "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.eliminaPersona(selectedRow);
            aggiornaTabella();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
    }
}
