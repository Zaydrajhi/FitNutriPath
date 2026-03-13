package service;

import java.text.DecimalFormat;

public class HealthMetricsService {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Calcule l'Indice de Masse Corporelle (IMC)
     * @param poids en kg
     * @param taille en cm
     * @return IMC arrondi à 2 décimales
     */
    public double calculerIMC(double poids, double taille) {
        if (poids <= 0 || taille <= 0) {
            throw new IllegalArgumentException("Le poids et la taille doivent être positifs");
        }
        return poids / (taille * taille);
    }

    /**
     * Interprète l'IMC et retourne la catégorie
     * @param imc
     * @return Catégorie de poids
     */
    public String interpreterIMC(double imc) {
        if (imc < 16.5) return "Dénutrition";
        if (imc < 18.5) return "Maigreur";
        if (imc < 25) return "Corpulence normale";
        if (imc < 30) return "Surpoids";
        if (imc < 35) return "Obésité modérée";
        if (imc < 40) return "Obésité sévère";
        return "Obésité morbide";
    }

    /**
     * Calcule le pourcentage de graisse corporelle selon la formule de Deurenberg
     * @param imc
     * @param age en années
     * @param sexe "M" pour masculin, "F" pour féminin
     * @return Pourcentage de graisse corporelle
     */
    public double calculerPourcentageGraisse(double imc, int age, String sexe) {
        if (age <= 0) {
            throw new IllegalArgumentException("L'âge doit être positif");
        }
        
        double pourcentage;
        if (sexe.equals("M")) {
            pourcentage = (1.20 * imc) + (0.23 * age) - 16.2;
        } else {
            pourcentage = (1.20 * imc) + (0.23 * age) - 5.4;
        }
        
        return Math.max(0, Math.min(100, pourcentage));
    }

    /**
     * Interprète le pourcentage de graisse corporelle
     * @param pourcentageGraisse
     * @param sexe "M" pour masculin, "F" pour féminin
     * @return Catégorie de graisse corporelle
     */
    public String interpreterPourcentageGraisse(double pourcentageGraisse, String sexe) {
        if (sexe.equals("M")) {
            if (pourcentageGraisse < 6) return "Graisse essentielle";
            if (pourcentageGraisse < 14) return "Athlète";
            if (pourcentageGraisse < 18) return "Fitness";
            if (pourcentageGraisse < 25) return "Acceptable";
            return "Obésité";
        } else {
            if (pourcentageGraisse < 14) return "Graisse essentielle";
            if (pourcentageGraisse < 21) return "Athlète";
            if (pourcentageGraisse < 25) return "Fitness";
            if (pourcentageGraisse < 32) return "Acceptable";
            return "Obésité";
        }
    }

    /**
     * Calcule le ratio taille/hanche
     * @param tourDeTaille en cm
     * @param tourDeHanche en cm
     * @return Ratio taille/hanche
     */
    public double calculerRatioTailleHanche(double tourDeTaille, double tourDeHanche) {
        if (tourDeTaille <= 0 || tourDeHanche <= 0) {
            throw new IllegalArgumentException("Les mesures doivent être positives");
        }
        return tourDeTaille / tourDeHanche;
    }

    /**
     * Interprète le ratio taille/hanche
     * @param ratio
     * @param sexe "M" pour masculin, "F" pour féminin
     * @return Évaluation du risque
     */
    public String interpreterRatioTailleHanche(double ratio, String sexe) {
        if (sexe.equals("M")) {
            if (ratio < 0.85) return "Risque faible";
            if (ratio < 0.90) return "Risque modéré";
            return "Risque élevé";
        } else {
            if (ratio < 0.75) return "Risque faible";
            if (ratio < 0.80) return "Risque modéré";
            return "Risque élevé";
        }
    }

    /**
     * Calcule le poids idéal selon la formule de Lorentz
     * @param taille en cm
     * @param sexe "M" pour masculin, "F" pour féminin
     * @return Poids idéal en kg
     */
    public double calculerPoidsIdeal(double taille, String sexe) {
        if (taille <= 0) {
            throw new IllegalArgumentException("La taille doit être positive");
        }
        
        if (sexe.equals("M")) {
            return 50 + 2.3 * ((taille * 100) - 152.4);
        } else {
            return 45.5 + 2.3 * ((taille * 100) - 152.4);
        }
    }
} 