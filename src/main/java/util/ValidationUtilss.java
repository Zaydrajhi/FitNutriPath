package util;

import javafx.scene.control.TextField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ValidationUtilss {

    // Validation pour le nom de catégorie
    public static boolean validateCategorieNom(String nom) {
        if (nom == null || nom.length() < 3) {
            showError("Le nom de la catégorie doit contenir au moins 3 caractères.");
            return false;
        }

        if (!nom.matches("^[a-zA-Z\\s]+$")) {
            showError("Le nom de la catégorie ne doit contenir que des lettres.");
            return false;
        }

        if (nom.contains("  ")) {
            showError("Le nom de la catégorie ne doit pas contenir plusieurs espaces consécutifs.");
            return false;
        }

        return true;
    }

    // Validation pour la description de catégorie
    public static boolean validateCategorieDescription(String description) {
        if (description == null || description.length() < 15) {
            showError("La description doit contenir au moins 15 caractères.");
            return false;
        }

        if (description.contains("  ")) {
            showError("La description ne doit pas contenir plusieurs espaces consécutifs.");
            return false;
        }

        return true;
    }

    // Validation pour le nom de produit
    public static boolean validateProduitNom(String nom) {
        if (nom == null || nom.length() < 3) {
            showError("Le nom du produit doit contenir au moins 3 caractères.");
            return false;
        }

        if (!nom.matches("^[a-zA-Z\\s]+$")) {
            showError("Le nom du produit ne doit contenir que des lettres.");
            return false;
        }

        if (nom.contains("  ")) {
            showError("Le nom du produit ne doit pas contenir plusieurs espaces consécutifs.");
            return false;
        }

        return true;
    }

    // Validation pour le prix
    public static boolean validatePrix(String prix) {
        try {
            double prixValue = Double.parseDouble(prix);
            if (prixValue <= 0) {
                showError("Le prix doit être supérieur à 0.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError("Le prix doit être un nombre valide.");
            return false;
        }
    }

    // Validation pour la description de produit
    public static boolean validateProduitDescription(String description) {
        if (description == null || description.length() < 15) {
            showError("La description doit contenir au moins 15 caractères.");
            return false;
        }

        if (description.contains("  ")) {
            showError("La description ne doit pas contenir plusieurs espaces consécutifs.");
            return false;
        }

        return true;
    }

    // Validation pour le lieu de livraison
    public static boolean validateLieu(String lieu) {
        if (lieu == null || lieu.trim().isEmpty()) {
            showAlert("Erreur", "Le lieu de livraison ne peut pas être vide");
            return false;
        }

        // Vérifier la longueur minimale (3 caractères)
        if (lieu.trim().length() < 3) {
            showAlert("Erreur", "Le lieu de livraison doit contenir au moins 3 caractères");
            return false;
        }

        // Vérifier que le lieu ne contient que des lettres et des espaces
        if (!lieu.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            showAlert("Erreur", "Le lieu de livraison ne doit contenir que des lettres");
            return false;
        }

        // Vérifier qu'il n'y a pas plusieurs espaces consécutifs
        if (lieu.contains("  ")) {
            showAlert("Erreur", "Le lieu de livraison ne doit pas contenir plusieurs espaces consécutifs");
            return false;
        }

        return true;
    }

    // Validation pour le numéro de téléphone
    public static boolean validateNumTele(String numTele) {
        if (numTele == null || numTele.trim().isEmpty()) {
            showAlert("Erreur", "Le numéro de téléphone ne peut pas être vide");
            return false;
        }

        // Vérifier que le numéro contient exactement 8 chiffres
        if (!numTele.matches("^\\d{8}$")) {
            showAlert("Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres");
            return false;
        }

        return true;
    }

    // Configuration du Spinner pour la quantité
    public static void configureQuantiteSpinner(Spinner<Integer> spinner, int min, int max, int initialValue) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);

        // Ajouter un listener pour valider la saisie
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                if (newValue != null && !newValue.isEmpty()) {
                    int value = Integer.parseInt(newValue);
                    if (value < min) {
                        spinner.getValueFactory().setValue(min);
                    } else if (value > max) {
                        spinner.getValueFactory().setValue(max);
                    }
                }
            } catch (NumberFormatException e) {
                spinner.getValueFactory().setValue(min);
            }
        });
    }

    // Configuration du Spinner pour le stock
    public static void configureStockSpinner(Spinner<Integer> spinner, int min, int max, int initialValue) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
    }

    // Affichage des erreurs
    private static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
