package service;

import entite.Evenement;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements iservice1<Evenement> {

    private Connection connection;

    public EvenementService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        String req = "INSERT INTO evenement (titre, description, date, lieu, statut, type, nbrplace, image, prix) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, evenement.getTitre());
        ps.setString(2, evenement.getDescription());
        ps.setString(3, evenement.getDate());
        ps.setString(4, evenement.getLieu());
        ps.setString(5, evenement.getStatut());
        ps.setString(6, evenement.getType());
        ps.setInt(7, evenement.getNbrplace());
        ps.setString(8, evenement.getImage());
        ps.setInt(9, evenement.getPrix());

        ps.executeUpdate();
        System.out.println("✅ Événement ajouté !");
    }

    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String req = "UPDATE evenement SET titre=?, description=?, date=?, lieu=?, statut=?, type=?, nbrplace=?, image=?, prix=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, evenement.getTitre());
        ps.setString(2, evenement.getDescription());
        ps.setString(3, evenement.getDate());
        ps.setString(4, evenement.getLieu());
        ps.setString(5, evenement.getStatut());
        ps.setString(6, evenement.getType());
        ps.setInt(7, evenement.getNbrplace());
        ps.setString(8, evenement.getImage());
        ps.setInt(9, evenement.getPrix());
        ps.setInt(10, evenement.getId());

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Aucune modification : ID non trouvé.");
        }
        System.out.println("✅ Événement modifié avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenement WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Suppression échouée : ID non trouvé.");
        }
        System.out.println("🗑️ Événement supprimé !");
    }

    @Override
    public List<Evenement> afficher() throws SQLException {
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Evenement e = new Evenement(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("date"),
                    rs.getString("lieu"),
                    rs.getString("statut"),
                    rs.getString("type"),
                    rs.getInt("nbrplace"),
                    rs.getString("image"),
                    rs.getInt("prix")
            );
            list.add(e);
        }

        return list;
    }
}
