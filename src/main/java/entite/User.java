package entite;

import javafx.beans.property.*;
import java.util.Date;

public class User {
    private IntegerProperty id;
    private StringProperty login;
    private StringProperty nom;
    private StringProperty prenom;
    private ObjectProperty<Date> datedenaissance;
    private StringProperty numTel;
    private StringProperty email;
    private StringProperty motDePasse;
    private StringProperty image;
    private StringProperty role;
    private BooleanProperty isPending;
    private StringProperty statut;

    public User() {
        this(0, "", "", "", new Date(), "", "", "", "", "", false, "en attente");
    }

    public User(int id, String login, String nom, String prenom, Date datedenaissance, String numTel, String email,
                String motDePasse, String image, String role, boolean isPending, String statut) {
        this.id = new SimpleIntegerProperty(id);
        this.login = new SimpleStringProperty(login);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.datedenaissance = new SimpleObjectProperty<>(datedenaissance);
        this.numTel = new SimpleStringProperty(numTel);
        this.email = new SimpleStringProperty(email);
        this.motDePasse = new SimpleStringProperty(motDePasse);
        this.image = new SimpleStringProperty(image);
        this.role = new SimpleStringProperty(role);
        this.isPending = new SimpleBooleanProperty(isPending);
        this.statut = new SimpleStringProperty(statut);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getLogin() { return login.get(); }
    public void setLogin(String login) { this.login.set(login); }
    public StringProperty loginProperty() { return login; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public StringProperty prenomProperty() { return prenom; }

    public Date getDatedenaissance() { return datedenaissance.get(); }
    public void setDatedenaissance(Date date) { this.datedenaissance.set(date); }
    public ObjectProperty<Date> datedenaissanceProperty() { return datedenaissance; }

    public String getNumTel() { return numTel.get(); }
    public void setNumTel(String numTel) { this.numTel.set(numTel); }
    public StringProperty numTelProperty() { return numTel; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getMotDePasse() { return motDePasse.get(); }
    public void setMotDePasse(String motDePasse) { this.motDePasse.set(motDePasse); }
    public StringProperty motDePasseProperty() { return motDePasse; }

    public String getImage() { return image.get(); }
    public void setImage(String image) { this.image.set(image); }
    public StringProperty imageProperty() { return image; }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }

    public boolean isPending() { return isPending.get(); }
    public void setPending(boolean isPending) { this.isPending.set(isPending); }
    public BooleanProperty isPendingProperty() { return isPending; }

    public String getStatut() { return statut.get(); }
    public void setStatut(String statut) { this.statut.set(statut); }
    public StringProperty statutProperty() { return statut; }
}
