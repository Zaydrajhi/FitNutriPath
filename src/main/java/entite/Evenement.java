package entite;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private String date; // ou LocalDate si tu préfères
    private String lieu;
    private String statut;
    private String type;
    private int nbrplace;
    private String image;
    private int prix;

    public Evenement() {
    }

    public Evenement(int id, String titre, String description, String date, String lieu,
                     String statut, String type, int nbrplace, String image, int prix) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
        this.type = type;
        this.nbrplace = nbrplace;
        this.image = image;
        this.prix = prix;
    }

    public Evenement( String titre, String description, String date, String lieu,
                     String statut, String type, int nbrplace, String image, int prix) {

        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
        this.type = type;
        this.nbrplace = nbrplace;
        this.image = image;
        this.prix = prix;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getNbrplace() { return nbrplace; }
    public void setNbrplace(int nbrplace) { this.nbrplace = nbrplace; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getPrix() { return prix; }
    public void setPrix(int prix) { this.prix = prix; }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", lieu='" + lieu + '\'' +
                ", statut='" + statut + '\'' +
                ", type='" + type + '\'' +
                ", nbrplace=" + nbrplace +
                ", image='" + image + '\'' +
                ", prix=" + prix +
                '}';
    }
}
