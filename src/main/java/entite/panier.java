package entite;

public class panier {
    private int id;
    private boolean is_finalized;
    private String etat;

    public panier() {}

    public panier( int id,String etat, boolean is_finalized) {
        this.id = id;
        this.etat = etat;
        this.is_finalized = is_finalized;
    }

    public int getId() {
        return id;
    }

    public String getEtat() {
        return etat;
    }

    public boolean isIs_finalized() {
        return is_finalized;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIs_finalized(boolean is_finalized) {
        this.is_finalized = is_finalized;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "panier{" +
                "id=" + id +
                ", is_finalized=" + is_finalized +
                ", etat='" + etat + '\'' +
                '}';
    }
}
