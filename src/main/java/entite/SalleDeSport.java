package entite;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class SalleDeSport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotBlank(message = "Le nom de la salle est obligatoire.")
    @Size(max = 25, message = "Le nom ne peut pas dépasser 25 caractères.")
    private String nom;

    @NotBlank(message = "La ville est obligatoire.")
    @Size(max = 12, message = "La ville ne peut pas dépasser 12 caractères.")
    private String ville;

    @NotBlank(message = "La rue est obligatoire.")
    @Size(max = 20, message = "La rue ne peut pas dépasser {max} caractères.")
    private String rue;

    @NotBlank(message = "Le code postal est obligatoire.")
    @Pattern(regexp = "^\\d{4}$", message = "Le code postal doit contenir exactement 4 chiffres.")
    private String codePostal;

    @NotBlank(message = "L'adresse email est obligatoire.")
    @Email(message = "L'adresse email n'est pas valide.")
    private String email;

    @NotNull(message = "Le prix de l'abonnement est obligatoire.")
    @Positive(message = "Le prix de l'abonnement doit être un nombre positif.")
    private int prixAbonnement;

    // Constructeur avec paramètres pour initialiser tous les champs
    public SalleDeSport(int id, String nom, String ville, String rue, String codePostal, String email, int prixAbonnement) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
        this.rue = rue;
        this.codePostal = codePostal;
        this.email = email;
        this.prixAbonnement = prixAbonnement;
        this.abonnements = new ArrayList<>();
    }

    public SalleDeSport(String nom, String ville, String rue, String codePostal, String email, int prixAbonnement) {
        this.nom = nom;
        this.ville = ville;
        this.rue = rue;
        this.codePostal = codePostal;
        this.email = email;
        this.prixAbonnement = prixAbonnement;
    }

    @OneToMany(mappedBy = "salleDeSport", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Abonnement> abonnements;

    public SalleDeSport() {
        this.abonnements = new ArrayList<>();
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void addAbonnement(Abonnement abonnement) {
        if (!this.abonnements.contains(abonnement)) {
            this.abonnements.add(abonnement);
            abonnement.setSalleDeSport(this);
        }
    }

    public void removeAbonnement(Abonnement abonnement) {
        if (this.abonnements.remove(abonnement)) {
            if (abonnement.getSalleDeSport() == this) {
                abonnement.setSalleDeSport(null);
            }
        }
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPrixAbonnement() {
        return prixAbonnement;
    }

    public void setPrixAbonnement(int prixAbonnement) {
        this.prixAbonnement = prixAbonnement;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", nom='" + nom + '\'' +
                ", ville='" + ville + '\'' ;
    }

}
