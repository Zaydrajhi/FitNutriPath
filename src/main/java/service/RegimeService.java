package service;

import entite.Regime;
import util.DataSource;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegimeService implements iservice2<Regime> {

    private Connection connection;

    public RegimeService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Regime regime) throws SQLException {
        String query = "INSERT INTO regime (titre, description, objectif, duree, calories_cible, niveau_activite, rendez_vous_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, regime.getTitre());
            statement.setString(2, regime.getDescription());
            statement.setString(3, regime.getObjectif());
            statement.setInt(4, regime.getDuree());
            statement.setInt(5, regime.getCaloriesCible());
            statement.setString(6, regime.getNiveauActivite());
            statement.setInt(7, regime.getRendezVousId());

            statement.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    regime.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Regime regime) throws SQLException {
        String query = "UPDATE regime SET titre=?, description=?, objectif=?, duree=?, " +
                "calories_cible=?, niveau_activite=?, rendez_vous_id=? WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, regime.getTitre());
            statement.setString(2, regime.getDescription());
            statement.setString(3, regime.getObjectif());
            statement.setInt(4, regime.getDuree());
            statement.setInt(5, regime.getCaloriesCible());
            statement.setString(6, regime.getNiveauActivite());
            statement.setInt(7, regime.getRendezVousId());
            statement.setInt(8, regime.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Regime regime) throws SQLException {
        String query = "DELETE FROM regime WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, regime.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Regime> readAll() throws SQLException {
        List<Regime> regimes = new ArrayList<>();
        String query = "SELECT * FROM regime";

        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Regime regime = new Regime();
                regime.setId(rs.getInt("id"));
                regime.setTitre(rs.getString("titre"));
                regime.setDescription(rs.getString("description"));
                regime.setObjectif(rs.getString("objectif"));
                regime.setDuree(rs.getInt("duree"));
                regime.setCaloriesCible(rs.getInt("calories_cible"));
                regime.setNiveauActivite(rs.getString("niveau_activite"));
                regime.setRendezVousId(rs.getInt("rendez_vous_id"));
                regimes.add(regime);
            }
        }
        return regimes;
    }

    @Override
    public Regime readById(int id) throws SQLException {
        String query = "SELECT * FROM regime WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Regime(
                            resultSet.getInt("id"),
                            resultSet.getString("titre"),
                            resultSet.getString("description"),
                            resultSet.getString("objectif"),
                            resultSet.getInt("duree"),
                            resultSet.getInt("calories_cible"),
                            resultSet.getString("niveau_activite"),
                            resultSet.getInt("rendez_vous_id")
                    );
                }
            }
        }
        return null;
    }

    public Regime getByRendezVousId(int rendezVousId) throws SQLException {
        String query = "SELECT r.* FROM regime r " +
                "JOIN rendezvous_regime rr ON r.id = rr.regime_id " +
                "WHERE rr.rendezvous_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, rendezVousId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Regime(
                            resultSet.getInt("id"),
                            resultSet.getString("titre"),
                            resultSet.getString("description"),
                            resultSet.getString("objectif"),
                            resultSet.getInt("duree"),
                            resultSet.getInt("calories_cible"),
                            resultSet.getString("niveau_activite"),
                            resultSet.getInt("rendez_vous_id")
                    );
                }
            }
        }
        return null;
    }

    public void associateWithRendezVous(int regimeId, int rendezVousId) throws SQLException {
        String query = "INSERT INTO rendezvous_regime (rendezvous_id, regime_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, rendezVousId);
            statement.setInt(2, regimeId);
            statement.executeUpdate();
        }
    }

    public void dissociateFromRendezVous(int regimeId, int rendezVousId) throws SQLException {
        String query = "DELETE FROM rendezvous_regime WHERE rendezvous_id = ? AND regime_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, rendezVousId);
            statement.setInt(2, regimeId);
            statement.executeUpdate();
        }
    }
    public List<Regime> getRegimesByRendezVous(int rdvId) throws SQLException {
        List<Regime> regimes = new ArrayList<>();
        String sql = "SELECT * FROM regime WHERE rendez_vous_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, rdvId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Regime regime = new Regime();
                regime.setId(rs.getInt("id"));
                regime.setTitre(rs.getString("titre"));
                regime.setDescription(rs.getString("description"));
                regime.setObjectif(rs.getString("objectif"));
                regime.setDuree(rs.getInt("duree"));
                regime.setCaloriesCible(rs.getInt("calories_cible"));
                regime.setNiveauActivite(rs.getString("niveau_activite"));
                // + tous les autres champs que tu as en base.

                regimes.add(regime);
            }
        }
        return regimes;
    }



}