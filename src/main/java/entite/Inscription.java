package entite;

public class Inscription {
    private int id;
    private int evenementId;
    private Integer userId; // Peut être null selon ta BDD
    private String date; // ou LocalDateTime si tu préfères
    private String commentaire;
    private int nbrPlaceReserve;
    private String type;

    public Inscription() {
    }

    public Inscription(int id, int evenementId, String date, String commentaire, int nbrPlaceReserve, String type) {
        this.id = id;
        this.evenementId = evenementId;
        this.date = date;
        this.commentaire = commentaire;
        this.nbrPlaceReserve = nbrPlaceReserve;
        this.type = type;
    }
    public Inscription(int evenementId, String date, String commentaire, int nbrPlaceReserve, String type) {
        this.evenementId = evenementId;
        this.date = date;
        this.commentaire = commentaire;
        this.nbrPlaceReserve = nbrPlaceReserve;
        this.type = type;
    }

    // Getters et Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEvenementId() { return evenementId; }
    public void setEvenementId(int evenementId) { this.evenementId = evenementId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getNbrPlaceReserve() { return nbrPlaceReserve; }
    public void setNbrPlaceReserve(int nbrPlaceReserve) { this.nbrPlaceReserve = nbrPlaceReserve; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + id +
                ", evenementId=" + evenementId +
                ", userId=" + userId +
                ", date='" + date + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", nbrPlaceReserve=" + nbrPlaceReserve +
                ", type='" + type + '\'' +
                '}';
    }
}