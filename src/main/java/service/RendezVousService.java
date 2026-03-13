package service;

import entite.RendezVous;
import util.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.lang.StringBuilder;

public class RendezVousService implements iservice2<RendezVous> {

    private Connection conn;
    private PreparedStatement pst;

    public RendezVousService() {
        conn = DataSource.getInstance().getConnection();
    }

    /**
     * Récupère les créneaux disponibles pour une date donnée
     * @param date La date pour laquelle on veut les créneaux
     * @return Liste des créneaux disponibles
     */
    public List<LocalDateTime> getCreneauxDisponibles(LocalDate date) throws SQLException {
        List<LocalDateTime> creneauxDisponibles = new ArrayList<>();
        List<LocalDateTime> creneauxPris = new ArrayList<>();

        // Récupérer les créneaux déjà pris pour cette date
        String query = "SELECT date_et_heure FROM rendez_vous WHERE DATE(date_et_heure) = ?";
        pst = conn.prepareStatement(query);
        pst.setDate(1, Date.valueOf(date));

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                creneauxPris.add(rs.getTimestamp("date_et_heure").toLocalDateTime());
            }
        }

        // Générer tous les créneaux possibles pour la journée
        LocalDateTime debutJournee = date.atTime(9, 0);
        LocalDateTime finJournee = date.atTime(16, 30);

        for (LocalDateTime creneau = debutJournee; 
             !creneau.isAfter(finJournee); 
             creneau = creneau.plusMinutes(30)) {
            
            boolean estDisponible = true;
            for (LocalDateTime creneauPris : creneauxPris) {
                if (Math.abs(ChronoUnit.MINUTES.between(creneau, creneauPris)) < 30) {
                    estDisponible = false;
                    break;
                }
            }
            
            if (estDisponible) {
                creneauxDisponibles.add(creneau);
            }
        }

        return creneauxDisponibles;
    }

    /**
     * Vérifie si un créneau horaire est disponible
     * @param dateTime Le créneau à vérifier
     * @return true si le créneau est disponible, false sinon
     */
    public boolean isCreneauDisponible(LocalDateTime dateTime) throws SQLException {
        // Arrondir à l'intervalle de 30 minutes le plus proche
        LocalDateTime creneauArrondi = dateTime.truncatedTo(ChronoUnit.MINUTES)
                .withMinute((dateTime.getMinute() / 30) * 30);

        // Vérifier si le créneau est dans les horaires d'ouverture (9h-17h)
        if (creneauArrondi.getHour() < 9 || creneauArrondi.getHour() >= 17) {
            return false;
        }

        // Vérifier les rendez-vous existants
        String query = "SELECT date_et_heure FROM rendez_vous WHERE date_et_heure BETWEEN ? AND ?";
        pst = conn.prepareStatement(query);
        
        // Vérifier 30 minutes avant et après le créneau
        pst.setTimestamp(1, Timestamp.valueOf(creneauArrondi.minusMinutes(30)));
        pst.setTimestamp(2, Timestamp.valueOf(creneauArrondi.plusMinutes(30)));

        try (ResultSet rs = pst.executeQuery()) {
            return !rs.next(); // Si aucun résultat, le créneau est disponible
        }
    }

    @Override
    public void create(RendezVous rendezVous) throws SQLException {
        // Vérifier la disponibilité du créneau
        if (!isCreneauDisponible(rendezVous.getDateEtHeure())) {
            // Récupérer les créneaux disponibles pour cette date
            List<LocalDateTime> creneauxDisponibles = getCreneauxDisponibles(rendezVous.getDateEtHeure().toLocalDate());
            
            StringBuilder message = new StringBuilder("Ce créneau horaire n'est pas disponible.\n\n");
            message.append("Créneaux disponibles pour cette date :\n");
            
            for (LocalDateTime creneau : creneauxDisponibles) {
                message.append("- ").append(creneau.format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
            }
            
            throw new SQLException(message.toString());
        }

        // Vérifier les dates de début et de fin
        if (rendezVous.getDateDebut() != null && rendezVous.getDateDebut().isBefore(rendezVous.getDateEtHeure().toLocalDate())) {
            throw new SQLException("La date de début ne peut pas être avant la date du rendez-vous");
        }

        if (rendezVous.getDateFin() != null && 
            (rendezVous.getDateFin().isBefore(rendezVous.getDateDebut()) || 
             rendezVous.getDateFin().isBefore(rendezVous.getDateEtHeure().toLocalDate()))) {
            throw new SQLException("La date de fin doit être après la date de début et la date du rendez-vous");
        }

        String query = "INSERT INTO rendez_vous (nom, prenom, description, etat, type, date_et_heure, num_tel, " +
                "taille, poids, objectif, date_debut, date_fin, priorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, rendezVous.getNom());
            pst.setString(2, rendezVous.getPrenom());
            pst.setString(3, rendezVous.getDescription());
            pst.setString(4, rendezVous.getEtat());
            pst.setString(5, rendezVous.getType());
            pst.setTimestamp(6, Timestamp.valueOf(rendezVous.getDateEtHeure()));
            pst.setString(7, rendezVous.getNumTel());
            pst.setInt(8, rendezVous.getTaille());
            pst.setInt(9, rendezVous.getPoids());
            pst.setString(10, rendezVous.getObjectif());
            
            // Gestion sécurisée des dates nulles
            if (rendezVous.getDateDebut() != null) {
                pst.setDate(11, Date.valueOf(rendezVous.getDateDebut()));
            } else {
                pst.setNull(11, Types.DATE);
            }
            
            if (rendezVous.getDateFin() != null) {
                pst.setDate(12, Date.valueOf(rendezVous.getDateFin()));
            } else {
                pst.setNull(12, Types.DATE);
            }
            
            pst.setString(13, rendezVous.getPriorite());

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rendezVous.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(RendezVous rendezVous) throws SQLException {
        String query = "UPDATE rendez_vous SET nom=?, prenom=?, description=?, etat=?, type=?, " +
                "date_et_heure=?, num_tel=?, taille=?, poids=?, objectif=?, date_debut=?, " +
                "date_fin=?, priorite=? WHERE id=?";

        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, rendezVous.getNom());
            pst.setString(2, rendezVous.getPrenom());
            pst.setString(3, rendezVous.getDescription());
            pst.setString(4, rendezVous.getEtat());
            pst.setString(5, rendezVous.getType());
            pst.setTimestamp(6, Timestamp.valueOf(rendezVous.getDateEtHeure()));
            pst.setString(7, rendezVous.getNumTel());
            pst.setInt(8, rendezVous.getTaille());
            pst.setInt(9, rendezVous.getPoids());
            pst.setString(10, rendezVous.getObjectif());
            pst.setDate(11, Date.valueOf(rendezVous.getDateDebut()));
            pst.setDate(12, Date.valueOf(rendezVous.getDateFin()));
            pst.setString(13, rendezVous.getPriorite());
            pst.setInt(14, rendezVous.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(RendezVous rendezVous) throws SQLException {
        String query = "DELETE FROM rendez_vous WHERE id=?";

        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, rendezVous.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RendezVous> readAll() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT * FROM rendez_vous";

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                RendezVous rendezVous = new RendezVous();
                rendezVous.setId(resultSet.getInt("id"));
                rendezVous.setNom(resultSet.getString("nom"));
                rendezVous.setPrenom(resultSet.getString("prenom"));
                rendezVous.setDescription(resultSet.getString("description"));
                rendezVous.setEtat(resultSet.getString("etat"));
                rendezVous.setType(resultSet.getString("type"));
                
                // Gestion sécurisée de la date et heure
                Timestamp dateEtHeure = resultSet.getTimestamp("date_et_heure");
                if (dateEtHeure != null) {
                    rendezVous.setDateEtHeure(dateEtHeure.toLocalDateTime());
                }
                
                rendezVous.setNumTel(resultSet.getString("num_tel"));
                rendezVous.setTaille(resultSet.getInt("taille"));
                rendezVous.setPoids(resultSet.getInt("poids"));
                rendezVous.setObjectif(resultSet.getString("objectif"));
                
                // Gestion sécurisée de la date de début
                Date dateDebut = resultSet.getDate("date_debut");
                if (dateDebut != null) {
                    rendezVous.setDateDebut(dateDebut.toLocalDate());
                }
                
                // Gestion sécurisée de la date de fin
                Date dateFin = resultSet.getDate("date_fin");
                if (dateFin != null) {
                    rendezVous.setDateFin(dateFin.toLocalDate());
                }
                
                rendezVous.setPriorite(resultSet.getString("priorite"));

                rendezVousList.add(rendezVous);
            }
        } catch (SQLException e) {
            System.out.println("Error reading all rendez-vous: " + e.getMessage());
            throw e;
        }
        return rendezVousList;
    }

    @Override
    public RendezVous readById(int id) throws SQLException {
        String query = "SELECT * FROM rendez_vous WHERE id=?";

        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);

            try (ResultSet resultSet = pst.executeQuery()) {
                if (resultSet.next()) {
                    RendezVous rendezVous = new RendezVous();
                    rendezVous.setId(resultSet.getInt("id"));
                    rendezVous.setNom(resultSet.getString("nom"));
                    rendezVous.setPrenom(resultSet.getString("prenom"));
                    rendezVous.setDescription(resultSet.getString("description"));
                    rendezVous.setEtat(resultSet.getString("etat"));
                    rendezVous.setType(resultSet.getString("type"));
                    rendezVous.setDateEtHeure(resultSet.getTimestamp("date_et_heure").toLocalDateTime());
                    rendezVous.setNumTel(resultSet.getString("num_tel"));
                    rendezVous.setTaille(resultSet.getInt("taille"));
                    rendezVous.setPoids(resultSet.getInt("poids"));
                    rendezVous.setObjectif(resultSet.getString("objectif"));
                    rendezVous.setDateDebut(resultSet.getDate("date_debut").toLocalDate());

                    Date dateFin = resultSet.getDate("date_fin");
                    if (dateFin != null) {
                        rendezVous.setDateFin(dateFin.toLocalDate());
                    }

                    rendezVous.setPriorite(resultSet.getString("priorite"));

                    return rendezVous;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading rendez-vous by id: " + e.getMessage());
            throw e;
        }
        return null;

    }
    public void deleteRendezVousWithRegimes(int rendezVousId) throws SQLException {
        // 1. Supprimer les régimes associés au rendez-vous
        String deleteRegimesQuery = "DELETE FROM regime WHERE rendez_vous_id = ?";

        // Utiliser l'instance de connection
        try (PreparedStatement statement = conn.prepareStatement(deleteRegimesQuery)) {
            statement.setInt(1, rendezVousId);
            statement.executeUpdate();
        }

        // 2. Supprimer le rendez-vous
        String deleteRendezVousQuery = "DELETE FROM rendez_vous WHERE id = ?";

        // Utiliser l'instance de connection
        try (PreparedStatement statement = conn.prepareStatement(deleteRendezVousQuery)) {
            statement.setInt(1, rendezVousId);
            statement.executeUpdate();
        }
    }


}