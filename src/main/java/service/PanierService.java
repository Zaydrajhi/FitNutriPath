package service;

import entite.panier;
import entite.panier_produit;
import entite.produit;
import entite.categorie;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierService {

    private final Connection connection;

    public PanierService() {
        connection = DataSource.getInstance().getConnection();
        System.out.println("Connexion à la base de données établie: " + (connection != null));
    }

    // Méthode principale modifiée
    public panier getOrCreateCurrentPanier() {
        String query = "SELECT * FROM panier WHERE is_finalized = false LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                panier p = new panier(
                        rs.getInt("id"),
                        rs.getString("etat"),
                        rs.getBoolean("is_finalized")
                );
                System.out.println("Panier existant trouvé: " + p.getId());
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche panier: " + e.getMessage());
        }
        return createNewPanier();
    }

    private panier createNewPanier() {
        String query = "INSERT INTO panier (etat, is_finalized) VALUES ('En cours', false)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec création panier, aucune ligne affectée");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    panier newPanier = new panier(
                            rs.getInt(1),
                            "En cours",
                            false
                    );
                    System.out.println("Nouveau panier créé avec ID: " + newPanier.getId());
                    return newPanier;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur création panier: " + e.getMessage());
        }
        return null;
    }

    public void ajouterProduitAuPanier(panier_produit panierProduit) {
        // Vérification pour le débogage
        if (panierProduit == null || panierProduit.getProduit() == null) {
            System.err.println("Erreur: panierProduit ou produit est null");
            throw new IllegalArgumentException("Le panierProduit ou le produit ne peut pas être null");
        }

        if (panierProduit.getProduit().getCategorie() == null || panierProduit.getProduit().getCategorie().getId() <= 0) {
            System.err.println("AVERTISSEMENT: Produit sans catégorie valide - ID produit: " + panierProduit.getProduit().getId());
            // Ne pas bloquer l'ajout, mais loguer pour débogage
        }

        String query = "INSERT INTO panier_produit (quantite, total, lieu, num_tele, statut, panier_id, produit_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, panierProduit.getQuantite());
            stmt.setDouble(2, panierProduit.getTotal());
            stmt.setString(3, panierProduit.getLieu());
            stmt.setInt(4, panierProduit.getNum_tele());
            stmt.setString(5, panierProduit.getStatut());
            stmt.setInt(6, panierProduit.getPanier().getId());
            stmt.setInt(7, panierProduit.getProduit().getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout du produit au panier, aucune ligne affectée");
            }

            // Récupérer l'ID généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    panierProduit.setId(rs.getInt(1));
                    System.out.println("Produit ajouté au panier avec ID panier_produit: " + panierProduit.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout au panier: " + e.getMessage());
            throw new RuntimeException("Échec de l'ajout au panier", e);
        }
    }

    // Méthodes restant inchangées (mais vérifiées) :

    public List<panier_produit> getProduitsDuPanier(int panierId) {
        List<panier_produit> produits = new ArrayList<>();
        String query = "SELECT pp.*, p.*, c.id as categorie_id, c.nom as categorie_nom " +
                "FROM panier_produit pp " +
                "JOIN produit p ON pp.produit_id = p.id " +
                "LEFT JOIN categorie c ON p.categorie_id = c.id " +
                "WHERE pp.panier_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panierId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Création de la catégorie
                categorie cat = new categorie();
                cat.setId(rs.getInt("categorie_id"));
                cat.setNom(rs.getString("categorie_nom"));

                // Création du produit
                produit p = new produit();
                p.setId(rs.getInt("produit_id"));
                p.setNom(rs.getString("nom"));
                p.setPrix(rs.getDouble("prix"));
                p.setStock(rs.getInt("stock"));
                p.setDescription(rs.getString("description"));
                p.setImage(rs.getString("image"));
                p.setCategorie(cat); // Assignation de la catégorie

                // Création du panier
                panier pan = new panier(panierId, "", false);

                // Création du panier_produit
                panier_produit pp = new panier_produit();
                pp.setId(rs.getInt("id"));
                pp.setQuantite(rs.getInt("quantite"));
                pp.setTotal(rs.getDouble("total"));
                pp.setLieu(rs.getString("lieu"));
                pp.setNum_tele(rs.getInt("num_tele"));
                pp.setStatut(rs.getString("statut"));
                pp.setProduit(p);
                pp.setPanier(pan);

                produits.add(pp);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération panier: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return produits;
    }

    public void updateQuantiteProduit(int panierProduitId, int nouvelleQuantite) {
        String query = "UPDATE panier_produit SET quantite = ?, total = (SELECT prix FROM produit p " +
                "JOIN panier_produit pp ON p.id = pp.produit_id WHERE pp.id = ?) * ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, panierProduitId);
            stmt.setInt(3, nouvelleQuantite);
            stmt.setInt(4, panierProduitId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour quantité: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void supprimerProduitDuPanier(int panierProduitId) {
        String query = "DELETE FROM panier_produit WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panierProduitId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression produit: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void finaliserPanier(int panierId) {
        String query = "UPDATE panier SET is_finalized = true, etat = 'Validé' WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur finalisation panier: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public double calculerTotalPanier(int panierId) {
        String query = "SELECT SUM(total) as total FROM panier_produit WHERE panier_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panierId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur calcul total: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    public List<panier> getAllPaniers() {
        List<panier> paniers = new ArrayList<>();
        String query = "SELECT * FROM panier";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                panier p = new panier(
                        rs.getInt("id"),
                        rs.getString("etat"),
                        rs.getBoolean("is_finalized")
                );
                paniers.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération paniers: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return paniers;
    }

    public void updatePanierProduit(panier_produit pp) {
        String query = "UPDATE panier_produit SET quantite = ?, lieu = ?, num_tele = ?, total = (SELECT prix FROM produit WHERE id = ?) * ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pp.getQuantite());
            stmt.setString(2, pp.getLieu());
            stmt.setInt(3, pp.getNum_tele());
            stmt.setInt(4, pp.getProduit().getId());
            stmt.setInt(5, pp.getQuantite());
            stmt.setInt(6, pp.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour a échoué, aucune ligne n'a été modifiée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du panier_produit: " + e.getMessage());
            throw new RuntimeException("Échec de la mise à jour du panier_produit", e);
        }
    }

    public void deletePanierProduit(int panierProduitId) {
        String query = "DELETE FROM panier_produit WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panierProduitId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression a échoué, aucune ligne n'a été supprimée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du panier_produit: " + e.getMessage());
            throw new RuntimeException("Échec de la suppression du panier_produit", e);
        }
    }
}