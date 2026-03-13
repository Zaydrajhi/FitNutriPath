package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class PoseEstimationService {
    private static final String API_URL = "https://api.deepai.org/api/pose-detection";
    private static final String API_KEY = "24611e3e-530f-4e8e-9d14-927fe3bd3707"; // Mets ta clé DeepAI ici

    public String analyzePosture(File imageFile) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Api-Key", API_KEY);
        conn.setDoOutput(true);

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        OutputStream output = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // Détecte le type MIME réel de l'image
        String mimeType = Files.probeContentType(imageFile.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";

        // Envoi du fichier image
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"\r\n");
        writer.append("Content-Type: " + mimeType + "\r\n\r\n").flush();

        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        inputStream.close();
        writer.append("\r\n").flush();
        writer.append("--" + boundary + "--\r\n").flush();

        // Lecture de la réponse ou de l'erreur
        int responseCode = conn.getResponseCode();
        InputStream responseStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) response.append(line);
        in.close();

        if (responseCode != 200) {
            throw new IOException("Erreur HTTP " + responseCode + " : " + response.toString());
        }

        return response.toString(); // JSON avec les points de posture
    }
}