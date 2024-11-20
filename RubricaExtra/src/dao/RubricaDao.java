package dao;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

import entità.Persona;

public class RubricaDao {
    private static Connection connection;

    public static void connect() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("credenziali_database.properties")) {
            props.load(fis);
        }

        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String ip = props.getProperty("ip-server-mysql");
        String port = props.getProperty("porta");
        String database = props.getProperty("database");

        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?serverTimezone=Europe/Rome";

        connection = DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    public static Vector<Persona> caricaPersone() {
        Vector<Persona> persone = new Vector<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM persone")) {

            while (rs.next()) {
                Persona p = new Persona(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("indirizzo"),
                        rs.getString("telefono"),
                        rs.getInt("eta")
                );
                persone.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persone;
    }

    public static void salvaPersone(Vector<Persona> persone) {
      
           
                try (Statement stmt = connection.createStatement()) {
                    for (Persona p : persone) {
                        String query = "REPLACE INTO persone (nome, cognome, indirizzo, telefono, eta) VALUES ('"
                                + p.getNome() + "', '"
                                + p.getCognome() + "', '"
                                + p.getIndirizzo() + "', '"
                                + p.getTelefono() + "', "
                                + p.getEta() + ")";
                        stmt.execute(query);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
          
            
    }


    
    public static void eliminaPersona(String nome, String cognome) {
      
            String query = "DELETE FROM persone WHERE nome = \"" + nome + "\" AND cognome = \"" + cognome+"\"";
            
            try {
    			Statement statement = connection.createStatement();
    			statement.execute(query);
    		
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}

    	}
    
    public static void modificaPersona(Persona personaOld, Persona persona) {
        // Query SQL per aggiornare una persona
        String query = "UPDATE persone SET nome = ?, cognome = ?, indirizzo = ?, telefono = ?, eta = ? WHERE nome = ? AND cognome = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Imposta i valori nel PreparedStatement
            stmt.setString(1, persona.getNome());
            stmt.setString(2, persona.getCognome());
            stmt.setString(3, persona.getIndirizzo());
            stmt.setString(4, persona.getTelefono());
            stmt.setInt(5, persona.getEta());
            stmt.setString(6, personaOld.getNome());
            stmt.setString(7, personaOld.getCognome());

            // Esegui l'UPDATE
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Numero di righe aggiornate: " + rowsUpdated);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
