package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import entità.Persona;


@SuppressWarnings("serial")
public class EditorPersona extends JDialog {
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField indirizzoField;
    private JTextField telefonoField;
    private JTextField etaField;
    private Persona persona;

    public interface PersonaListener {
        void onPersonaSalvata(Persona persona);
    }

    public EditorPersona(JFrame parent, Persona persona, PersonaListener listener) {
        super(parent, "Editor Persona", true);
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));

        this.setPersona(persona);

        // Campi per l'input
        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField(persona != null ? persona.getNome() : "");
        JLabel cognomeLabel = new JLabel("Cognome:");
        cognomeField = new JTextField(persona != null ? persona.getCognome() : "");
        JLabel indirizzoLabel = new JLabel("Indirizzo:");
        indirizzoField = new JTextField(persona != null ? persona.getIndirizzo() : "");
        JLabel telefonoLabel = new JLabel("Telefono:");
        telefonoField = new JTextField(persona != null ? persona.getTelefono() : "");
        JLabel etaLabel = new JLabel("Età:");
        etaField = new JTextField(persona != null ? String.valueOf(persona.getEta()) : "");

        // Bottoni
        JButton salvaButton = new JButton("Salva");
        JButton annullaButton = new JButton("Annulla");

        add(nomeLabel);
        add(nomeField);
        add(cognomeLabel);
        add(cognomeField);
        add(indirizzoLabel);
        add(indirizzoField);
        add(telefonoLabel);
        add(telefonoField);
        add(etaLabel);
        add(etaField);
        add(salvaButton);
        add(annullaButton);

        salvaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = nomeField.getText();
                    String cognome = cognomeField.getText();
                    String indirizzo = indirizzoField.getText();
                    String telefono = telefonoField.getText();
                    int eta = Integer.parseInt(etaField.getText());

                    listener.onPersonaSalvata(new Persona(nome, cognome, indirizzo, telefono, eta));
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(EditorPersona.this, "Errore: età deve essere un numero intero.");
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}
}