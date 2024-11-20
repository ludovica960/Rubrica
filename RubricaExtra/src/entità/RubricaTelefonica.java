package entità;

import java.util.Vector;
import dao.RubricaDao;

public class RubricaTelefonica {
	private Vector<Persona> contatti = new Vector<>();

    public Vector<Persona> getContatti() {
        return contatti;
    }

    public void aggiungiPersona(Persona persona)  {
        contatti.add(persona);
        RubricaDao.salvaPersone(contatti); // Salva il file ogni volta che viene aggiunta una persona

    }
    
    

    public void modificaPersona(int index, Persona persona) {
    	
    	Persona personaOld = contatti.get(index);
    	System.out.println("old: " + personaOld);
    	System.out.println("new: " + persona);
    	contatti.set(index, persona);
        RubricaDao.modificaPersona(personaOld, persona);
        

    }

    public void eliminaPersona(int index) {
        Persona persona = contatti.get(index);
        contatti.remove(index); // Rimuovi dalla lista
        RubricaDao.eliminaPersona(persona.getNome(), persona.getCognome()); // Rimuovi dal database
    }

    
    public void setContatti(Vector<Persona> contatti) {
        this.contatti = contatti;
    }

}