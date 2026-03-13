package entite;

public class panier_produit {
    private int id;
    private double total;
    private int quantite;
    private String lieu;
    private int num_tele;
    private String statut;
    private produit produit;
    private panier panier;
    public panier_produit() {}

    public panier_produit(double total , int quantite, String lieu, int num_tele, String statut, panier panier,produit produit) {
        this.panier = panier;
        this.produit = produit;
        this.statut = statut;
        this.num_tele = num_tele;
        this.lieu = lieu;
        this.quantite = quantite;
        this.total = total;

    }

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getLieu() {
        return lieu;
    }

    public int getNum_tele() {
        return num_tele;
    }

    public String getStatut() {
        return statut;
    }

    public entite.produit getProduit() {
        return produit;
    }

    public entite.panier getPanier() {
        return panier;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setNum_tele(int num_tele) {
        this.num_tele = num_tele;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setProduit(entite.produit produit) {
        this.produit = produit;
    }

    public void setPanier(entite.panier panier) {
        this.panier = panier;
    }

    @Override
    public String toString() {
        return "panier_produit{" +
                "id=" + id +
                ", total=" + total +
                ", quantite=" + quantite +
                ", lieu='" + lieu + '\'' +
                ", num_tele=" + num_tele +
                ", statut='" + statut + '\'' +
                ", produit=" + produit +
                ", panier=" + panier +
                '}';
    }
}
