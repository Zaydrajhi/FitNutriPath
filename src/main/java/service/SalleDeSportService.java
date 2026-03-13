package service;

import entite.SalleDeSport;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDeSportService implements IService<SalleDeSport> {

    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;
    private ResultSet rs;

    public SalleDeSportService(){
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(SalleDeSport salle) {
        String requete = "insert into salle_de_sport (nom, ville, rue, code_postal, email, prix_abonnement) " +
                "values ('" + salle.getNom() + "', '" + salle.getVille() + "', '" + salle.getRue() +
                "', '" + salle.getCodePostal() + "', '" + salle.getEmail() + "', " + salle.getPrixAbonnement() + ")";
        try {
            ste = cnx.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPst(SalleDeSport salle) {
        String requete = "insert into salle_de_sport (nom, ville, rue, code_postal, email, prix_abonnement) " +
                "values (?, ?, ?, ?, ?, ?)";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setString(1, salle.getNom());
            pst.setString(2, salle.getVille());
            pst.setString(3, salle.getRue());
            pst.setString(4, salle.getCodePostal());
            pst.setString(5, salle.getEmail());
            pst.setInt(6, salle.getPrixAbonnement());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(SalleDeSport salle) {
        String requete = "update salle_de_sport set nom = ?, ville = ?, rue = ?, code_postal = ?, email = ?, prix_abonnement = ? " +
                "where id = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setString(1, salle.getNom());
            pst.setString(2, salle.getVille());
            pst.setString(3, salle.getRue());
            pst.setString(4, salle.getCodePostal());
            pst.setString(5, salle.getEmail());
            pst.setInt(6, salle.getPrixAbonnement());
            pst.setInt(7, salle.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(SalleDeSport salle) {
        // Désactiver l'auto-commit pour gérer la transaction manuellement
        try {
            cnx.setAutoCommit(false);

            // 1. D'abord supprimer tous les abonnements associés
            String deleteAbonnements = "DELETE FROM abonnement WHERE salle_de_sport_id = ?";
            pst = cnx.prepareStatement(deleteAbonnements);
            pst.setInt(1, salle.getId());
            pst.executeUpdate();

            // 2. Ensuite supprimer la salle
            String deleteSalle = "DELETE FROM salle_de_sport WHERE id = ?";
            pst = cnx.prepareStatement(deleteSalle);
            pst.setInt(1, salle.getId());
            pst.executeUpdate();

            // Valider la transaction
            cnx.commit();
        } catch (SQLException e) {
            try {
                // En cas d'erreur, annuler la transaction
                if (cnx != null) {
                    cnx.rollback();
                }
                throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
            } catch (SQLException ex) {
                throw new RuntimeException("Erreur lors du rollback: " + ex.getMessage(), ex);
            }
        } finally {
            try {
                // Rétablir l'auto-commit et fermer les ressources
                if (cnx != null) {
                    cnx.setAutoCommit(true);
                }
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture des ressources: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<SalleDeSport> readAll() {
        String requete = "select * from salle_de_sport";
        List<SalleDeSport> list = new ArrayList<>();
        try {
            ste = cnx.createStatement();
            rs = ste.executeQuery(requete);
            while (rs.next()) {
                list.add(new SalleDeSport(rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("ville"),
                        rs.getString("rue"),
                        rs.getString("code_postal"),
                        rs.getString("email"),
                        rs.getInt("prix_abonnement")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public SalleDeSport readById(int id) {
        String requete = "select * from salle_de_sport where id = ?";
        SalleDeSport salle = null;
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                salle = new SalleDeSport(rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("ville"),
                        rs.getString("rue"),
                        rs.getString("code_postal"),
                        rs.getString("email"),
                        rs.getInt("prix_abonnement"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salle;
    }
}
