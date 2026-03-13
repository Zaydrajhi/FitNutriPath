package entite;


public class produit {
    private int id;
    private String nom;
    private double prix;
    private int stock;
    private String description;
    private String image;
    private categorie categorie;

    public produit() {}

    public produit(String nom, double prix, int stock, String description,
                   String image, categorie categorie) {
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
        this.description = description;
        this.image = image;
        this.categorie = categorie;
    }

    public categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(categorie categorie) {
        this.categorie = categorie;
    }

    public double getPrix() {
        return prix;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", categorie=" + (categorie != null ? categorie.getNom() : "null") +
                '}';
    }

}

