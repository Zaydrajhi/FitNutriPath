package entite;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Regime {
    private int id;
    private String titre;
    private StringProperty description;
    private String objectif;
    private int duree;
    private int caloriesCible;
    private String niveauActivite;
    private int rendezVousId;  // Ajout de l'ID du RendezVous

    // Constructeurs
    public Regime() {
        this.description = new SimpleStringProperty();
    }

    public Regime(int id, String titre, String description, String objectif, int duree, int caloriesCible, String niveauActivite, int rendezVousId) {
        this.id = id;
        this.titre = titre;
        this.description = new SimpleStringProperty(description);
        this.objectif = objectif;
        this.duree = duree;
        this.caloriesCible = caloriesCible;
        this.niveauActivite = niveauActivite;
        this.rendezVousId = rendezVousId;  // Initialisation de l'ID
    }

    public Regime(String titre, String description, String objectif, int duree, int caloriesCible, String niveauActivite, int rendezVousId) {
        this.titre = titre;
        this.description = new SimpleStringProperty(description);
        this.objectif = objectif;
        this.duree = duree;
        this.caloriesCible = caloriesCible;
        this.niveauActivite = niveauActivite;
        this.rendezVousId = rendezVousId;  // Initialisation de l'ID
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description.get();
    }

    public String getObjectif() {
        return objectif;
    }

    public int getDuree() {
        return duree;
    }

    public int getCaloriesCible() {
        return caloriesCible;
    }

    public String getNiveauActivite() {
        return niveauActivite;
    }

    public int getRendezVousId() {  // Getter pour l'ID du RendezVous
        return rendezVousId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public void setCaloriesCible(int caloriesCible) {
        this.caloriesCible = caloriesCible;
    }

    public void setNiveauActivite(String niveauActivite) {
        this.niveauActivite = niveauActivite;
    }

    public void setRendezVousId(int rendezVousId) {  // Setter pour l'ID du RendezVous
        this.rendezVousId = rendezVousId;
    }

    @Override
    public String toString() {
        return "Regime{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description.get() + '\'' +
                ", objectif='" + objectif + '\'' +
                ", duree=" + duree +
                ", caloriesCible=" + caloriesCible +
                ", niveauActivite='" + niveauActivite + '\'' +
                ", rendezVousId=" + rendezVousId +  // Affichage de l'ID du RendezVous
                '}';
    }
}
