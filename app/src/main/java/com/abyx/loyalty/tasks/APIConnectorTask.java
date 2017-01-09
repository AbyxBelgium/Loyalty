package com.abyx.loyalty.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.abyx.loyalty.R;

import org.json.JSONException;

import java.io.IOException;

/**
 * Async task which handles the connection with the Loyalty API. This Async task makes sure that
 * the UI doesn't freeze while connecting to the API.
 *
 * @author Pieter Verschaffelt
 */
public class APIConnectorTask extends AsyncTask<String, Void, String> {
    private String exceptionTitle;
    private String exceptionMessage;
    private Context context;
    private APIConnectorCallback apiConnector;

    /**
     * Default constructor that accepts one APIConnectorCallback-object and one Context
     *
     * @param apiConnector The hostactivity that has to display all error messages to the user
     * @param context The hostactivity's context
     */
    public APIConnectorTask(APIConnectorCallback apiConnector, Context context){
        this.apiConnector = apiConnector;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            ApiConnector temp = new ApiConnector();
            return temp.getStoreLogo(params[0]);
        } catch (IOException e){
            //An unexpected exception occurred while connecting to the Loyalty API. We will inform
            //the user that he has to provide one manually
            exceptionTitle = context.getString(R.string.connect_error_title);
            exceptionMessage = context.getString(R.string.connect_error_message);
        } catch (JSONException e){
            //A JSON-exception points at the fact that the API didn't find the correct logo for
            //this store
            exceptionTitle = context.getString(R.string.not_found_error_title);
            exceptionMessage = context.getString(R.string.not_found_error_message);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result){
        if (result != null){
            //API returned a usefull URL
            apiConnector.onAPIReady(result);
        } else {
            //Store is not available in the API or something went wrong while connecting to Loyalty
            apiConnector.onAPIException(exceptionTitle, exceptionMessage);
        }
    }
}
