package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class AdviceService {
    private static final String API_URL = "https://api.adviceslip.com/advice";

    public String getAdvice() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) response.append(line);
        in.close();

        JSONObject obj = new JSONObject(response.toString());
        return obj.has("slip") ? obj.getJSONObject("slip").getString("advice") : "No advice found.";
    }
}