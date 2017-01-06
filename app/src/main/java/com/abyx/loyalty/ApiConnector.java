package com.abyx.loyalty;

import android.os.StrictMode;
import android.support.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This class connects to the Loyalty API and returns all data available for a
 * certain store.
 *
 * Usage: http://abyx.be/loyalty/public/api.php?store=STORENAME_HERE
 *
 * The Loyalty API returns a JSON-object containing the stores known information.
 *
 * @author Pieter Verschaffelt
 */
public class ApiConnector {
    public ApiConnector(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * This function connects to the Loyalty API and returns a specific URL that matches best
     * with the given store name.
     *
     * @param store The store name for which the logo has to be found
     * @return The JSON-object as a String returned by the Loyalty-server. When something goes
     * wrong during connection to the API, null is returned.
     * @throws IOException
     */
    @Nullable
    public String getJSON(String store) throws IOException {
        store = URLEncoder.encode(store, "UTF-8");
        String response;
        URL url = new URL("http://abyx.be/loyalty/public/api.php?store=" + store);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int statusCode = connection.getResponseCode();
        if (statusCode == 200) {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = IOUtils.toString(in, "UTF-8");
        } else {
            throw new IOException("Unable to connect to Loyalty API!");
        }
        return response;
    }

    /**
     * This method returns the url for an appropriate logo for this store
     *
     * @param store The store name for which the logo has to be found
     * @return String containing the URL to the logo
     */
    public String getStoreLogo(String store) throws IOException, JSONException {
        JSONObject json = new JSONObject(getJSON(store));
        return json.getString("logo");
    }
}
