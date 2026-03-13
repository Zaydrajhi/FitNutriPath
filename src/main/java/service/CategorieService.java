package service;

import entite.categorie;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IService<categorie> {
    private Connection cnx = DataSource.getInstance().getConnection();
    private Statement ste;
    private PreparedStatement pst;
    private ResultSet rs;

    @Override
    public void create(categorie cat) {
        String requete = "INSERT INTO categorie (nom, description, image) VALUES (?, ?, ?)";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setString(1, cat.getNom());
            pst.setString(2, cat.getDescription());
            pst.setString(3, cat.getImage());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(categorie cat) {
        String requete = "UPDATE categorie SET nom = ?, description = ?, image = ? WHERE id = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setString(1, cat.getNom());
            pst.setString(2, cat.getDescription());
            pst.setString(3, cat.getImage());
            pst.setInt(4, cat.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(categorie cat) {
        // D'abord, supprimer tous les produits associés à cette catégorie
        String deleteProduitsQuery = "DELETE FROM produit WHERE categorie_id = ?";
        try {
            // Désactiver l'auto-commit pour permettre une transaction
            cnx.setAutoCommit(false);
            
            // Supprimer les produits associés
            pst = cnx.prepareStatement(deleteProduitsQuery);
            pst.setInt(1, cat.getId());
            int produitsSupprimes = pst.executeUpdate();
            System.out.println("Nombre de produits supprimés: " + produitsSupprimes);
            
            // Ensuite, supprimer la catégorie
            String deleteCategorieQuery = "DELETE FROM categorie WHERE id = ?";
            pst = cnx.prepareStatement(deleteCategorieQuery);
            pst.setInt(1, cat.getId());
            int resultat = pst.executeUpdate();
            
            // Valider la transaction
            cnx.commit();
            
            if (resultat == 0) {
                throw new RuntimeException("Aucune catégorie n'a été supprimée avec l'ID: " + cat.getId());
            }
            
            System.out.println("Catégorie supprimée avec succès: " + cat.getNom() + " (ID: " + cat.getId() + ")");
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            try {
                cnx.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erreur lors de la suppression de la catégorie et de ses produits associés: " + e.getMessage(), e);
        } finally {
            // Réactiver l'auto-commit
            try {
                cnx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<categorie> readAll() {
        List<categorie> list = new ArrayList<>();
        String requete = "SELECT * FROM categorie";
        try {
            ste = cnx.createStatement();
            rs = ste.executeQuery(requete);
            while (rs.next()) {
                categorie cat = new categorie();
                cat.setId(rs.getInt("id"));
                cat.setNom(rs.getString("nom"));
                cat.setDescription(rs.getString("description"));
                cat.setImage(rs.getString("image"));
                list.add(cat);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public categorie readById(int id) {
        String requete = "SELECT * FROM categorie WHERE id = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                categorie cat = new categorie();
                cat.setId(rs.getInt("id"));
                cat.setNom(rs.getString("nom"));
                cat.setDescription(rs.getString("description"));
                cat.setImage(rs.getString("image"));
                return cat;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}