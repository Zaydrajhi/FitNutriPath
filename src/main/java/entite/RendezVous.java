package entite;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class RendezVous {
    private int id;
    private String nom;
    private String prenom;
    private String description;
    private String etat = "en attente";
    private String type;
    private LocalDateTime dateEtHeure;
    private String numTel;
    private int taille;
    private int poids;
    private String objectif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String priorite;
    private Collection<Regime> regimes = new ArrayList<>();


    // Constructor
    public RendezVous() {
        this.dateEtHeure = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateEtHeure() {
        return dateEtHeure;
    }

    public void setDateEtHeure(LocalDateTime dateEtHeure) {
        this.dateEtHeure = dateEtHeure;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Collection<Regime> getRegimes() {
        return regimes;
    }

    public void setRegimes(Collection<Regime> regimes) {
        this.regimes = regimes;
    }


    // toString() method
    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", description='" + description + '\'' +
                ", etat='" + etat + '\'' +
                ", type='" + type + '\'' +
                ", dateEtHeure=" + dateEtHeure +
                ", numTel='" + numTel + '\'' +
                ", taille=" + taille +
                ", poids=" + poids +
                ", objectif='" + objectif + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", priorite='" + priorite + '\'' +
                ", regimes=" + regimes +
                '}';
    }
}