package entite;

public class categorie {
    private int id;
    private String nom;
    private String description;
    private String image;

    public categorie() {
    }

    public categorie(String nom, String description, String image) {
        this.nom = nom;
        this.description = description;
        this.image = image;
    }

    // Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}