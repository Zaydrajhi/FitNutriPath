package service;

import entite.Abonnement;
import entite.SalleDeSport;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements IService<Abonnement> {

    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;
    private ResultSet rs;

    public AbonnementService() {
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Abonnement abonnement) {
        String requete = "insert into abonnement (salle_de_sport_id, nom, prenom, numero_tlfn, email, date_deb, date_fin) " +
                "values (" + abonnement.getSalleDeSport().getId() + ", '" + abonnement.getNom() + "', '" +
                abonnement.getPrenom() + "', '" + abonnement.getNumeroTlfn() + "', '" + abonnement.getEmail() +
                "', '" + abonnement.getDateDeb() + "', '" + abonnement.getDateFin() + "')";
        try {
            ste = cnx.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPst(Abonnement abonnement) {
        String requete = "insert into abonnement (salle_de_sport_id, nom, prenom, numero_tlfn, email, date_deb, date_fin) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, abonnement.getSalleDeSport().getId());
            pst.setString(2, abonnement.getNom());
            pst.setString(3, abonnement.getPrenom());
            pst.setString(4, abonnement.getNumeroTlfn());
            pst.setString(5, abonnement.getEmail());
            pst.setDate(6, Date.valueOf(abonnement.getDateDeb()));
            pst.setDate(7, Date.valueOf(abonnement.getDateFin()));
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Abonnement abonnement) {
        String requete = "update abonnement set salle_de_sport_id = ?, nom = ?, prenom = ?, numero_tlfn = ?, " +
                "email = ?, date_deb = ?, date_fin = ? where id = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, abonnement.getSalleDeSport().getId());
            pst.setString(2, abonnement.getNom());
            pst.setString(3, abonnement.getPrenom());
            pst.setString(4, abonnement.getNumeroTlfn());
            pst.setString(5, abonnement.getEmail());
            pst.setDate(6, Date.valueOf(abonnement.getDateDeb()));
            pst.setDate(7, Date.valueOf(abonnement.getDateFin()));
            pst.setInt(8, abonnement.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Abonnement abonnement) {
        String requete = "delete from abonnement where id = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, abonnement.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Abonnement> readAll() {
        String requete = "select a.*, s.nom as salle_nom, s.ville as salle_ville from abonnement a " +
                "join salle_de_sport s on a.salle_de_sport_id = s.id";
        List<Abonnement> list = new ArrayList<>();
        try {
            ste = cnx.createStatement();
            rs = ste.executeQuery(requete);
            while (rs.next()) {
                SalleDeSport salle = new SalleDeSport();
                salle.setId(rs.getInt("salle_de_sport_id"));
                salle.setNom(rs.getString("salle_nom"));
                salle.setVille(rs.getString("salle_ville"));

                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setSalleDeSport(salle);
                abonnement.setNom(rs.getString("nom"));
                abonnement.setPrenom(rs.getString("prenom"));
                abonnement.setNumeroTlfn(rs.getString("numero_tlfn"));
                abonnement.setEmail(rs.getString("email"));
                abonnement.setDateDeb(rs.getDate("date_deb").toLocalDate());
                abonnement.setDateFin(rs.getDate("date_fin").toLocalDate());

                list.add(abonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Abonnement readById(int id) {
        String requete = "select a.*, s.nom as salle_nom, s.ville as salle_ville from abonnement a " +
                "join salle_de_sport s on a.salle_de_sport_id = s.id where a.id = ?";
        Abonnement abonnement = null;
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                SalleDeSport salle = new SalleDeSport();
                salle.setId(rs.getInt("salle_de_sport_id"));
                salle.setNom(rs.getString("salle_nom"));
                salle.setVille(rs.getString("salle_ville"));

                abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setSalleDeSport(salle);
                abonnement.setNom(rs.getString("nom"));
                abonnement.setPrenom(rs.getString("prenom"));
                abonnement.setNumeroTlfn(rs.getString("numero_tlfn"));
                abonnement.setEmail(rs.getString("email"));
                abonnement.setDateDeb(rs.getDate("date_deb").toLocalDate());
                abonnement.setDateFin(rs.getDate("date_fin").toLocalDate());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return abonnement;
    }

    // Méthode supplémentaire pour récupérer les abonnements d'une salle spécifique
    public List<Abonnement> getBySalleDeSport(int salleId) {
        String requete = "select * from abonnement where salle_de_sport_id = ?";
        List<Abonnement> list = new ArrayList<>();
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, salleId);
            rs = pst.executeQuery();
            while (rs.next()) {
                SalleDeSport salle = new SalleDeSport();
                salle.setId(salleId);

                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setSalleDeSport(salle);
                abonnement.setNom(rs.getString("nom"));
                abonnement.setPrenom(rs.getString("prenom"));
                abonnement.setNumeroTlfn(rs.getString("numero_tlfn"));
                abonnement.setEmail(rs.getString("email"));
                abonnement.setDateDeb(rs.getDate("date_deb").toLocalDate());
                abonnement.setDateFin(rs.getDate("date_fin").toLocalDate());

                list.add(abonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}