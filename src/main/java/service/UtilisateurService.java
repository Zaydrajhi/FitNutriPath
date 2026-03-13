package service;

import entite.User;
import util.DataSource;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilisateurService {

    private final Connection cnx = DataSource.getInstance().getConnection(); // Connexion via DataSource

    // Méthode pour créer un utilisateur
    public void create(User user) {
        String query = "INSERT INTO user (login, nom, prenom, datedenaissance, num_tel, email, mot_de_passe, image, role, is_pending, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {

            // Générer un login automatique
            String loginGenere = (int)(Math.random() * 900 + 100) + "USR" + (int)(Math.random() * 9000 + 1000);

            pst.setString(1, loginGenere);
            pst.setString(2, user.getNom());
            pst.setString(3, user.getPrenom());
            pst.setDate(4, new java.sql.Date(user.getDatedenaissance().getTime()));
            pst.setString(5, user.getNumTel());
            pst.setString(6, user.getEmail());

            // Hachage du mot de passe avec BCrypt
            String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());
            pst.setString(7, hashedPassword);

            pst.setString(8, user.getImage());
            pst.setString(9, user.getRole());
            pst.setBoolean(10, user.isPending());
            pst.setString(11, user.getStatut());

            pst.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    // Méthode pour hasher un mot de passe (SHA-256)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage : " + e.getMessage());
        }
    }

    // Méthode pour récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("datedenaissance"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("image"),
                        rs.getString("role"),
                        rs.getBoolean("is_pending"),
                        rs.getString("statut")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return users;
    }

    // Méthode pour supprimer un utilisateur
    public void delete(User user) {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, user.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour un utilisateur
    public boolean updateUser(int id, String login, String nom, String prenom, String numTel, String email, String role, String image) {
        String updateQuery = "UPDATE user SET login = ?, nom = ?, prenom = ?, num_tel = ?, email = ?, role = ?, image = ? WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(updateQuery)) {
            stmt.setString(1, login);
            stmt.setString(2, nom);
            stmt.setString(3, prenom);
            stmt.setString(4, numTel);
            stmt.setString(5, email);
            stmt.setString(6, role);
            stmt.setString(7, image);
            stmt.setInt(8, id);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Retourner true si au moins une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // En cas d'erreur, retourner false
        }
    }
    public boolean updateUtilisateur(User user) {
        String sql = "UPDATE user SET nom = ?, prenom = ?, num_tel = ?, email = ?, image = ? WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getNumTel());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getImage());
            stmt.setInt(6, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si au moins une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // En cas d'erreur, retourne false
        }
    }

    public List<User> getUsersByRole(String role) {
        List<User> liste = new ArrayList<>();
        try {
            String req = "SELECT * FROM user WHERE role=?";
            PreparedStatement pst = cnx.prepareStatement(req);

            pst.setString(1, role);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("datedenaissance"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("image"),
                        rs.getString("role"),
                        rs.getBoolean("is_pending"),
                        rs.getString("statut")
                );
                liste.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    public List<User> getAllNutritionnistes() {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'Nutritionniste'";

        try (Connection conn = DataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setNumTel(rs.getString("num_tel"));  // Assurez-vous que les noms de colonnes sont corrects
                user.setEmail(rs.getString("email"));
                user.setImage(rs.getString("image"));
                list.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public void supprimerUtilisateur(int id) {
        String query = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateUserStatus(int userId, boolean isPending, String statut) {
        String query = "UPDATE user SET is_pending = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setBoolean(1, isPending);
            pst.setString(2, statut);
            pst.setInt(3, userId);
            
            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                
                // Vérifier si le hash est au format BCrypt (commence par $2a$)
                if (hashedPassword.startsWith("$2a$")) {
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return createUserFromResultSet(rs);
                    }
                } else {
                    // Si ce n'est pas un hash BCrypt, vérifier avec SHA-256
                    String inputHash = hashPassword(password);
                    if (inputHash.equals(hashedPassword)) {
                        return createUserFromResultSet(rs);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'authentification : " + e.getMessage());
            return null;
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getDate("datedenaissance"),
            rs.getString("num_tel"),
            rs.getString("email"),
            rs.getString("mot_de_passe"),
            rs.getString("image"),
            rs.getString("role"),
            rs.getBoolean("is_pending"),
            rs.getString("statut")
        );
    }
}
