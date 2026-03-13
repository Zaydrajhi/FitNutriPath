package entite;

import entite.SalleDeSport;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Entity
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "salle_de_sport_id", nullable = false) // Spécifie la clé étrangère et l'option "non nullable"
    @NotNull(message = "La salle de sport est obligatoire")
    private SalleDeSport salleDeSport;


    @NotBlank(message = "Le nom est obligatoire.")
    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caractères.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(min = 3, max = 50, message = "Le prénom doit contenir entre 3 et 50 caractères.")
    private String prenom;

    @NotBlank(message = "Le numéro de téléphone est obligatoire.")
    @Pattern(regexp = "^\\d{8}$", message = "Le numéro de téléphone doit contenir exactement 8 chiffres.")
    private String numeroTlfn;

    @NotBlank(message = "L'adresse email est obligatoire.")
    @Email(message = "L'adresse email n'est pas valide.")
    private String email;

    @Column(name = "date_deb", nullable = false)
    private LocalDate dateDeb;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;




    // Constructeurs
    public Abonnement() {
    }




    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SalleDeSport getSalleDeSport() {
        return salleDeSport;
    }

    public void setSalleDeSport(SalleDeSport salleDeSport) {
        this.salleDeSport = salleDeSport;
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

    public String getNumeroTlfn() {
        return numeroTlfn;
    }

    public void setNumeroTlfn(String numeroTlfn) {
        this.numeroTlfn = numeroTlfn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateDeb() {
        return dateDeb;
    }

    public void setDateDeb(LocalDate dateDeb) {
        this.dateDeb = dateDeb;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

}
