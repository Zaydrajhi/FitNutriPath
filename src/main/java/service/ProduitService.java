package service;

import entite.produit;
import entite.categorie;
import util.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IService<produit> {
    private Connection cnx = DataSource.getInstance().getConnection();
    private PreparedStatement pst;
    private Statement ste;
    private ResultSet rs;

    @Override
    public void create(produit p) {
        String query = "INSERT INTO produit (nom, prix, stock, description, image, categorie_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, p.getNom());
            pst.setDouble(2, p.getPrix());
            pst.setInt(3, p.getStock());
            pst.setString(4, p.getDescription());
            pst.setString(5, p.getImage());
            pst.setInt(6, p.getCategorie().getId());

            pst.executeUpdate();

            // Récupérer l'ID généré
            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du produit", e);
        }
    }

    @Override
    public void update(produit produit) {
        String query = "UPDATE produit SET nom = ?, prix = ?, stock = ?, description = ?, image = ?, categorie_id = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setInt(3, produit.getStock());
            stmt.setString(4, produit.getDescription());
            stmt.setString(5, produit.getImage());
            // Vérifier que categorie_id est valide
            if (produit.getCategorie() == null) {
                throw new IllegalStateException("La catégorie du produit ne peut pas être null lors de la mise à jour");
            }
            stmt.setInt(6, produit.getCategorie().getId());
            stmt.setInt(7, produit.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun produit mis à jour pour l'ID: " + produit.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour produit: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(produit p) {
        String query = "DELETE FROM produit WHERE id=?";
        try {
            System.out.println("Exécution de la requête SQL: " + query + " avec l'ID: " + p.getId());
            pst = cnx.prepareStatement(query);
            pst.setInt(1, p.getId());
            int rowsAffected = pst.executeUpdate();
            System.out.println("Nombre de lignes affectées: " + rowsAffected);
            
            if (rowsAffected == 0) {
                System.err.println("Aucun produit n'a été supprimé avec l'ID: " + p.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du produit: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression du produit: " + e.getMessage(), e);
        }
    }

    @Override
    public List<produit> readAll() {
        String query = "SELECT p.*, c.nom as categorie_nom, c.description as categorie_desc, c.image as categorie_img " +
                "FROM produit p JOIN categorie c ON p.categorie_id = c.id";
        List<produit> list = new ArrayList<>();

        try {
            ste = cnx.createStatement();
            rs = ste.executeQuery(query);

            while (rs.next()) {
                // Création de la catégorie associée
                categorie cat = new categorie(

                        rs.getString("categorie_nom"),
                        rs.getString("categorie_desc"),
                        rs.getString("categorie_img")
                );

                // Création du produit
                produit p = new produit(
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getString("image"),
                        cat
                );
                p.setId(rs.getInt("id"));

                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des produits", e);
        }
        return list;
    }

    @Override
    public produit readById(int id) {
        String query = "SELECT p.*, c.nom as categorie_nom FROM produit p " +
                "JOIN categorie c ON p.categorie_id = c.id WHERE p.id=?";
        produit p = null;

        try {
            pst = cnx.prepareStatement(query);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                categorie cat = new categorie();
                cat.setId(rs.getInt("categorie_id"));
                cat.setNom(rs.getString("categorie_nom"));

                p = new produit();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setPrix(rs.getDouble("prix"));
                p.setStock(rs.getInt("stock"));
                p.setDescription(rs.getString("description"));
                p.setImage(rs.getString("image"));
                p.setCategorie(cat);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du produit par ID", e);
        }
        return p;
    }

    // Méthode supplémentaire pour rechercher par catégorie
    public List<produit> getByCategorie(int categorieId) {
        String query = "SELECT * FROM produit WHERE categorie_id=?";
        List<produit> list = new ArrayList<>();

        try {
            pst = cnx.prepareStatement(query);
            pst.setInt(1, categorieId);
            rs = pst.executeQuery();

            while (rs.next()) {
                produit p = new produit();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setPrix(rs.getDouble("prix"));
                p.setStock(rs.getInt("stock"));
                p.setDescription(rs.getString("description"));
                p.setImage(rs.getString("image"));

                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par catégorie", e);
        }
        return list;
    }
}