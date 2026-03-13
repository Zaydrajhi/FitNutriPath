package service;

import entite.Regime;
import entite.RendezVous;
import java.time.LocalDateTime;
import java.util.*;

public class IAService {

    // Constantes pour les scores
    private static final int SCORE_OBJECTIF_MATCH = 30;
    private static final int SCORE_ACTIVITE_MATCH = 25;
    private static final int SCORE_CALORIES_MATCH = 20;
    private static final int SCORE_DUREE_MATCH = 15;



    /**
     * Prédit le taux de réussite d'un régime pour un utilisateur
     */
    public double predireReussiteRegime(
            Regime regime,
            String niveauMotivation,
            boolean aDesAllergies,
            boolean aDesRestrictions) {
        
        double tauxReussite = 100.0;
        
        if (regime.getDuree() > 30) {
            tauxReussite *= 0.9;
        }

        switch (niveauMotivation.toLowerCase()) {
            case "faible" -> tauxReussite *= 0.7;
            case "moyen" -> tauxReussite *= 0.85;
            case "eleve" -> tauxReussite *= 1.0;
        }

        if (aDesAllergies) tauxReussite *= 0.85;
        if (aDesRestrictions) tauxReussite *= 0.9;
        
        switch (regime.getNiveauActivite().toLowerCase()) {
            case "intense" -> tauxReussite *= 0.85;
            case "modere" -> tauxReussite *= 0.95;
            case "leger" -> tauxReussite *= 1.0;
        }
        
        return Math.round(tauxReussite * 100.0) / 100.0;
    }

    public double calculerScoreEfficacite(Regime regime) {
        double score = 70.0; // Score de base
        
        // Ajustements basés sur les caractéristiques du régime
        if (regime.getDuree() >= 14 && regime.getDuree() <= 30) score += 15;
        if (regime.getCaloriesCible() >= 1500 && regime.getCaloriesCible() <= 2500) score += 15;
        
        return Math.min(score, 100.0);
    }

    public double calculerScoreAdaptabilite(Regime regime) {
        double score = 60.0;
        
        if (regime.getNiveauActivite().equalsIgnoreCase("modere")) score += 20;
        if (regime.getDuree() <= 21) score += 20;
        
        return Math.min(score, 100.0);
    }

    public List<String> genererRecommandations(Regime regime) {
        List<String> recommandations = new ArrayList<>();
        String objectif = regime.getObjectif().toLowerCase();
        
        if (objectif.contains("perte")) {
            recommandations.addAll(Arrays.asList(
                "🏃 Pratiquer une activité physique régulière",
                "🥗 Privilégier les repas riches en protéines",
                "❌ Éviter les sucres raffinés",
                "⏰ Respecter les horaires des repas"
            ));
        } else if (objectif.contains("masse")) {
            recommandations.addAll(Arrays.asList(
                "💪 Se concentrer sur les exercices de force",
                "🍗 Augmenter les portions de protéines",
                "🥜 Ajouter des collations nutritives",
                "😴 Optimiser la récupération"
            ));
        } else {
            recommandations.addAll(Arrays.asList(
                "🥗 Maintenir une alimentation équilibrée",
                "🔄 Varier les sources de nutriments",
                "⚖️ Garder des portions modérées",
                "💧 S'hydrater régulièrement"
            ));
        }
        
        return recommandations;
    }
} 