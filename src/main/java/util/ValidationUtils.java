package util;

import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import java.util.regex.Pattern;

public class ValidationUtils {
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$"); // exactement 8 chiffres
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s-]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{1,9}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean validateField(TextField field, String fieldName, ValidationRule rule) {
        String value = field.getText().trim();
        boolean isValid = true;
        String errorMessage = "";

        switch (rule) {
            case REQUIRED:
                isValid = isNotEmpty(value);
                errorMessage = fieldName + " est requis";
                break;
            case EMAIL:
                isValid = isValidEmail(value);
                errorMessage = "Format d'email invalide";
                break;
            case PHONE:
                isValid = isValidPhone(value);
                errorMessage = "Numéro invalide (exactement 8 chiffres)";
                break;
            case NAME:
                isValid = isValidName(value);
                errorMessage = fieldName + " doit contenir uniquement des lettres (min 2 caractères)";
                break;
            case PASSWORD:
                isValid = isValidPassword(value);
                errorMessage = "Mot de passe doit contenir lettres et chiffres (max 9 caractères)";
                break;
        }

        if (!isValid) {
            field.setStyle("-fx-border-color: red;");
            field.setPromptText(errorMessage);
        } else {
            field.setStyle("");
            field.setPromptText("");
        }

        return isValid;
    }

    public static boolean validateDatePicker(DatePicker datePicker, String fieldName) {
        boolean isValid = datePicker.getValue() != null;

        if (!isValid) {
            datePicker.setStyle("-fx-border-color: red;");
            datePicker.setPromptText(fieldName + " est requis");
        } else {
            datePicker.setStyle("");
            datePicker.setPromptText("");
        }

        return isValid;
    }

    public enum ValidationRule {
        REQUIRED,
        EMAIL,
        PHONE,
        NAME,
        PASSWORD
    }
}
