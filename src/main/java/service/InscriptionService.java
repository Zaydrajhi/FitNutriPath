package service;

import entite.Inscription;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscriptionService implements iservice1<Inscription> {

    private Connection connection;

    public InscriptionService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Inscription inscription) throws SQLException {
        // La requête d'ajout d'une inscription avec l'ID de l'événement
        String req = "INSERT INTO inscription (evenement_id, user_id, date, commentaire, nbrPlaceReserve, type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);

        // On associe l'ID de l'événement à l'inscription
        ps.setInt(1, inscription.getEvenementId());  // ID de l'événement
        ps.setObject(2, inscription.getUserId(), Types.INTEGER);  // userId, peut être NULL
        ps.setString(3, inscription.getDate());  // Date de l'inscription
        ps.setString(4, inscription.getCommentaire());  // Commentaire
        ps.setInt(5, inscription.getNbrPlaceReserve());  // Nombre de places réservées
        ps.setString(6, inscription.getType());  // Type d'inscription (par exemple, "standard", "VIP", etc.)

        ps.executeUpdate();
        System.out.println("✅ Inscription ajoutée !");
    }

    @Override
    public void modifier(Inscription inscription) throws SQLException {
        String req = "UPDATE inscription SET evenement_id=?, user_id=?, date=?, commentaire=?, nbrPlaceReserve=?, type=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, inscription.getEvenementId());  // ID de l'événement
        ps.setObject(2, inscription.getUserId(), Types.INTEGER);  // userId, peut être NULL
        ps.setString(3, inscription.getDate());
        ps.setString(4, inscription.getCommentaire());
        ps.setInt(5, inscription.getNbrPlaceReserve());
        ps.setString(6, inscription.getType());
        ps.setInt(7, inscription.getId());

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Aucune modification : ID non trouvé.");
        }
        System.out.println("✅ Inscription modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM inscription WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Suppression échouée : ID non trouvé.");
        }
        System.out.println("🗑️ Inscription supprimée !");
    }

    @Override
    public List<Inscription> afficher() throws SQLException {
        List<Inscription> list = new ArrayList<>();
        String req = "SELECT * FROM inscription";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Inscription i = new Inscription(
                    rs.getInt("id"),
                    rs.getInt("evenement_id"),  // Récupérer l'ID de l'événement
                    rs.getString("date"),
                    rs.getString("commentaire"),
                    rs.getInt("nbrPlaceReserve"),
                    rs.getString("type")
            );

            // On récupère l'ID de l'événement associé à cette inscription
            System.out.println("Inscription pour l'événement ID : " + i.getEvenementId());

            list.add(i);
        }

        return list;
    }
}
