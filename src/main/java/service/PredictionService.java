package service;

import org.json.JSONObject;

public class PredictionService {
    
    public JSONObject getPrediction(double poids, double taille, int age, String sexe, 
                                  double tourDeTaille, double tourDeHanche) throws Exception {
        
        // Calcul de l'IMC
        double imc = poids / ((taille/100) * (taille/100));
        
        // Calcul du pourcentage de graisse corporelle
        double graisseCorporelle;
        if (sexe.equalsIgnoreCase("homme")) {
            graisseCorporelle = (1.20 * imc) + (0.23 * age) - 16.2;
        } else {
            graisseCorporelle = (1.20 * imc) + (0.23 * age) - 5.4;
        }
        
        // Calcul du ratio taille/hanche
        double ratioTailleHanche = tourDeTaille / tourDeHanche;
        
        // Analyse des risques
        String analyseRisques = analyserRisques(imc, graisseCorporelle, ratioTailleHanche, sexe);
        
        // Création de l'objet JSON de réponse
        JSONObject response = new JSONObject();
        response.put("imc", imc);
        response.put("graisseCorporelle", graisseCorporelle);
        response.put("ratioTailleHanche", ratioTailleHanche);
        response.put("analyseRisques", analyseRisques);
        
        return response;
    }
    
    private String analyserRisques(double imc, double graisseCorporelle, double ratioTailleHanche, String sexe) {
        StringBuilder analyse = new StringBuilder();
        
        // Analyse IMC
        if (imc < 18.5) {
            analyse.append("Votre IMC indique une insuffisance pondérale. ");
        } else if (imc < 25) {
            analyse.append("Votre IMC est dans la norme. ");
        } else if (imc < 30) {
            analyse.append("Votre IMC indique un surpoids. ");
        } else {
            analyse.append("Votre IMC indique une obésité. ");
        }
        
        // Analyse graisse corporelle
        if (sexe.equalsIgnoreCase("homme")) {
            if (graisseCorporelle < 6) {
                analyse.append("Votre pourcentage de graisse corporelle est très bas. ");
            } else if (graisseCorporelle < 14) {
                analyse.append("Votre pourcentage de graisse corporelle est athlétique. ");
            } else if (graisseCorporelle < 24) {
                analyse.append("Votre pourcentage de graisse corporelle est normal. ");
            } else {
                analyse.append("Votre pourcentage de graisse corporelle est élevé. ");
            }
        } else {
            if (graisseCorporelle < 14) {
                analyse.append("Votre pourcentage de graisse corporelle est très bas. ");
            } else if (graisseCorporelle < 21) {
                analyse.append("Votre pourcentage de graisse corporelle est athlétique. ");
            } else if (graisseCorporelle < 31) {
                analyse.append("Votre pourcentage de graisse corporelle est normal. ");
            } else {
                analyse.append("Votre pourcentage de graisse corporelle est élevé. ");
            }
        }
        
        // Analyse ratio taille/hanche
        if (sexe.equalsIgnoreCase("homme")) {
            if (ratioTailleHanche > 0.95) {
                analyse.append("Votre ratio taille/hanche indique un risque accru de problèmes de santé. ");
            } else {
                analyse.append("Votre ratio taille/hanche est dans la norme. ");
            }
        } else {
            if (ratioTailleHanche > 0.85) {
                analyse.append("Votre ratio taille/hanche indique un risque accru de problèmes de santé. ");
            } else {
                analyse.append("Votre ratio taille/hanche est dans la norme. ");
            }
        }
        
        // Recommandations générales
        analyse.append("\n\nRecommandations : ");
        if (imc > 25 || graisseCorporelle > (sexe.equalsIgnoreCase("homme") ? 24 : 31)) {
            analyse.append("Il serait bénéfique de perdre du poids. ");
        } else if (imc < 18.5 || graisseCorporelle < (sexe.equalsIgnoreCase("homme") ? 6 : 14)) {
            analyse.append("Il serait bénéfique de prendre du poids de manière saine. ");
        } else {
            analyse.append("Maintenez vos habitudes saines actuelles. ");
        }
        
        return analyse.toString();
    }
} 