/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.phantombot.phantomupdater.api;


import javax.net.ssl.HttpsURLConnection;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author ScaniaTV
 */
public final class GitHubAPI {
    // The base API url.
    private static final String API_URL = "https://api.github.com/repos/";
    // The repo of which to get the results.
    private static final String REPO_NAME = "PhantomBot/PhantomBot";
    
    /**
     * Class constructor.
     */
    private GitHubAPI() {
        
    }
    
    /**
     * Method that converts a reader into a string.
     * 
     * @param reader
     * @return 
     */
    private static String getDataFromReader(final Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        
        return sb.toString();
    }
    
    /**
     * Method that gets data from a URL.
     * 
     * @param url URL to get the data from.
     * @return 
     */
    private static JSONObject getData(final String url) {
        JSONObject obj = new JSONObject();
        InputStream inputStream = null;
        HttpsURLConnection connection;
        URL u;
        
        try {
            u = new URL(url);
            // Open a new connection.
            connection = (HttpsURLConnection) u.openConnection();
            // Input type.
            connection.setDoInput(true);
            // Request method.
            connection.setRequestMethod("GET");
            // Content type.
            connection.addRequestProperty("Content-Type", "application/json");
            // Accept type.
            connection.addRequestProperty("Accept", "application/vnd.github.v3+json");
            
            // Connect
            connection.connect();
            
            // Get out input stream
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
            }
            
            // If we have an input stream, read it.
            if (inputStream != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                
                // Get the data and convert it into an object,
                obj = new JSONObject(getDataFromReader(rd));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return obj;
    }
    
    /**
     * Method that gets the latest download URL.
     * 
     * @return 
     */
    public static String getLatestDownloadUrl() {
        // Get the latest release.
        JSONObject obj = getData(API_URL + REPO_NAME + "/releases/latest");
        // Download URL string.
        String url = null;
        
        if (obj.has("assets")) {
            JSONArray assetsArray = obj.getJSONArray("assets");
            
            // In case we ever have multiple files, find the PhantomBot one.
            for (int i = 0; i < assetsArray.length(); i++) {
                if (assetsArray.getJSONObject(i).getString("name").startsWith("PhantomBot")) {
                    url = assetsArray.getJSONObject(i).getString("browser_download_url");
                    break;
                }
            }
        }
        
        return url;
    }
    
    /**
     * Method that returns the latest release object.
     * 
     * @return 
     */
    public static JSONObject getLatestRelease() {
        return getData(API_URL + REPO_NAME + "/releases/latest");
    }
}
