package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private String url = "jdbc:mysql://localhost:3306/fitnutripathint";
    private String username = "root";
    private String password = "";
    private Connection connection;
    public static DataSource instance;

    private DataSource() {
        try {
            System.out.println("Tentative de connexion à la base de données...");
            System.out.println("URL: " + url);
            System.out.println("Username: " + username);
            
            // Charger explicitement le pilote MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Pilote MySQL chargé avec succès");
            
            // Tenter la connexion
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion à la base de données établie avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Le pilote MySQL n'a pas pu être chargé");
            throw new RuntimeException("Le pilote MySQL n'a pas pu être chargé. Vérifiez que la dépendance mysql-connector-j est bien présente dans le pom.xml", e);
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            System.err.println("État SQL: " + e.getSQLState());
            System.err.println("Code d'erreur: " + e.getErrorCode());
            
            throw new RuntimeException("Erreur lors de la connexion à la base de données. Vérifiez que :\n" +
                    "1. MySQL est en cours d'exécution (services.msc)\n" +
                    "2. La base de données 'fitnutripathint' existe\n" +
                    "3. Les identifiants sont corrects (username: " + username + ")\n" +
                    "4. Le port 3306 est accessible\n" +
                    "Message d'erreur détaillé: " + e.getMessage(), e);
        }
    }

    public static DataSource getInstance() {
        if (instance == null)
            instance = new DataSource();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }
}
