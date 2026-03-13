package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class BadWordsDetector {
    private static final String API_KEY = "W+g44kxc9zvgErMHUklZzA==TTTzYtcyMbzCmKru"; // À remplacer par votre clé API
    private static final String API_URL = "https://api.api-ninjas.com/v1/profanityfilter";
    private static String lastDetectedWord = "";
    private static String lastOriginalText = "";
    private static int lastSeverityScore = 0;
    private static String lastDetectionType = "";
    private static int totalDetections = 0;
    private static Map<String, Integer> wordFrequency = new HashMap<>();
    private static Random random = new Random();

    // Mapping pour le leetspeak
    private static final Map<Character, String> LEET_MAPPING = new HashMap<>();
    static {
        LEET_MAPPING.put('a', "[a4@]");
        LEET_MAPPING.put('b', "[b8]");
        LEET_MAPPING.put('e', "[e3]");
        LEET_MAPPING.put('g', "[g9]");
        LEET_MAPPING.put('i', "[i1!]");
        LEET_MAPPING.put('l', "[l1]");
        LEET_MAPPING.put('o', "[o0]");
        LEET_MAPPING.put('s', "[s5$]");
        LEET_MAPPING.put('t', "[t7]");
        LEET_MAPPING.put('z', "[z2]");
    }

    // Niveaux de gravité des mots
    private static final Map<String, Integer> WORD_SEVERITY = new HashMap<>();
    static {
        // Mots très graves
        WORD_SEVERITY.put("fuck", 5);
        WORD_SEVERITY.put("shit", 5);
        WORD_SEVERITY.put("bitch", 5);
        WORD_SEVERITY.put("asshole", 5);
        WORD_SEVERITY.put("merde", 5);
        WORD_SEVERITY.put("putain", 5);
        WORD_SEVERITY.put("connard", 5);
        WORD_SEVERITY.put("salope", 5);
        
        // Mots modérément graves
        WORD_SEVERITY.put("damn", 3);
        WORD_SEVERITY.put("crap", 3);
        WORD_SEVERITY.put("darn", 3);
        WORD_SEVERITY.put("con", 3);
        WORD_SEVERITY.put("idiot", 3);
        
        // Mots légers
        WORD_SEVERITY.put("stupid", 1);
        WORD_SEVERITY.put("suck", 1);
    }

    // Suggestions de remplacement
    private static final Map<String, String[]> WORD_SUGGESTIONS = new HashMap<>();
    static {
        WORD_SUGGESTIONS.put("fuck", new String[]{"darn", "heck", "fudge", "shoot"});
        WORD_SUGGESTIONS.put("shit", new String[]{"crap", "darn", "shoot", "dang"});
        WORD_SUGGESTIONS.put("bitch", new String[]{"jerk", "mean person", "rude person"});
        WORD_SUGGESTIONS.put("asshole", new String[]{"jerk", "mean person", "rude person"});
        WORD_SUGGESTIONS.put("merde", new String[]{"mince", "zut", "flûte"});
        WORD_SUGGESTIONS.put("putain", new String[]{"mince", "zut", "flûte"});
        WORD_SUGGESTIONS.put("damn", new String[]{"darn", "dang", "shoot"});
        WORD_SUGGESTIONS.put("stupid", new String[]{"silly", "foolish", "unwise"});
    }

    // Conseils pour éviter les mots inappropriés
    private static String[] TIPS = {
        "Essayez d'exprimer votre frustration de manière constructive",
        "Utilisez des mots plus appropriés pour décrire vos émotions",
        "Prenez une pause et respirez profondément avant de répondre",
        "Pensez à l'impact de vos mots sur les autres",
        "Utilisez l'humour de manière positive plutôt que négative"
    };

    public static String getRandomSuggestion(String word) {
        String[] suggestions = WORD_SUGGESTIONS.get(word);
        if (suggestions != null && suggestions.length > 0) {
            return suggestions[random.nextInt(suggestions.length)];
        }
        return "mot plus approprié";
    }

    private static String getRandomTip() {
        return TIPS[random.nextInt(TIPS.length)];
    }

    private static void updateStatistics(String word) {
        totalDetections++;
        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
    }

    private static void displayAlert(String word, String text, String normalizedText, String detectionType, int severity) {
        updateStatistics(word);
        
        System.out.println("\n🚨 ALERTE DE MOT INAPPROPRIÉ 🚨");
        System.out.println("════════════════════════════════════");
        System.out.println("📝 Mot détecté: '" + word + "'");
        System.out.println("🔍 Type de détection: " + detectionType);
        System.out.println("🔴 Niveau de gravité: " + severity + "/5");
        System.out.println("📊 Statistiques:");
        System.out.println("   - Détections totales: " + totalDetections);
        System.out.println("   - Fréquence de ce mot: " + wordFrequency.get(word));
        System.out.println("\n💡 Suggestions de remplacement:");
        System.out.println("   - " + getRandomSuggestion(word));
        System.out.println("\n✨ Conseil du jour:");
        System.out.println("   - " + getRandomTip());
        System.out.println("\n📋 Détails:");
        System.out.println("   - Texte original: '" + text + "'");
        System.out.println("   - Texte normalisé: '" + normalizedText + "'");
        System.out.println("════════════════════════════════════\n");
    }

    private static String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder normalized = new StringBuilder();
        char prevChar = '\0';
        
        for (int i = 0; i < text.length(); i++) {
            char currentChar = Character.toLowerCase(text.charAt(i));
            if (currentChar != prevChar) {
                normalized.append(currentChar);
                prevChar = currentChar;
            }
        }
        
        return normalized.toString();
    }

    private static String convertToLeetRegex(String word) {
        StringBuilder regex = new StringBuilder();
        for (char c : word.toCharArray()) {
            String leetChars = LEET_MAPPING.getOrDefault(c, String.valueOf(c));
            regex.append(leetChars);
        }
        return regex.toString();
    }

    private static boolean containsMaskedWord(String text, String word) {
        String maskedPattern = word.charAt(0) + "\\*+" + word.charAt(word.length() - 1);
        return Pattern.compile(maskedPattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    public static String getLastDetectedWord() {
        return lastDetectedWord;
    }

    public static String getLastOriginalText() {
        return lastOriginalText;
    }

    public static int getLastSeverityScore() {
        return lastSeverityScore;
    }

    public static String getLastDetectionType() {
        return lastDetectionType;
    }

    public static int getTotalDetections() {
        return totalDetections;
    }

    public static Map<String, Integer> getWordFrequency() {
        return new HashMap<>(wordFrequency);
    }

    public static boolean containsBadWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        lastOriginalText = text;
        String normalizedText = normalizeText(text);
        String lowerText = normalizedText.toLowerCase();

        for (Map.Entry<String, Integer> entry : WORD_SEVERITY.entrySet()) {
            String word = entry.getKey();
            
            if (containsMaskedWord(text, word)) {
                lastDetectedWord = word;
                lastSeverityScore = entry.getValue();
                lastDetectionType = "MASKED";
                displayAlert(word, text, normalizedText, "Mot masqué", lastSeverityScore);
                return true;
            }

            String leetRegex = convertToLeetRegex(word);
            if (Pattern.compile(leetRegex, Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                lastDetectedWord = word;
                lastSeverityScore = entry.getValue();
                lastDetectionType = "LEETSPEAK";
                displayAlert(word, text, normalizedText, "Leetspeak", lastSeverityScore);
                return true;
            }

            if (lowerText.contains(word)) {
                lastDetectedWord = word;
                lastSeverityScore = entry.getValue();
                lastDetectionType = "STANDARD";
                displayAlert(word, text, normalizedText, "Standard", lastSeverityScore);
                return true;
            }
        }

        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            URL url = new URL(API_URL + "?text=" + encodedText);
            
            System.out.println("🌐 Envoi de la requête à l'API pour le texte: " + text);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", API_KEY);
            
            int responseCode = connection.getResponseCode();
            System.out.println("📡 Code de réponse de l'API: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                System.out.println("📥 Réponse de l'API: " + response.toString());
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean hasProfanity = jsonResponse.getBoolean("has_profanity");
                if (hasProfanity) {
                    lastDetectionType = "API";
                    lastSeverityScore = 3;
                    displayAlert("Mot inapproprié", text, normalizedText, "API", lastSeverityScore);
                }
                return hasProfanity;
            } else {
                System.err.println("❌ Erreur API: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la détection des mots inappropriés: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 