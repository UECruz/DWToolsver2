package com.java1.fullsail.dwtools.api;

import com.semantics3.errors.Semantics3Exception;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class Semantic3API {

    final static private String API_DOMAIN = "api.semantics3.com";
    final static private String API_BASE   = "https://" + API_DOMAIN + "/v1/";

    private String apiKey;
    private String apiSecret;
    private String endpoint;
    private String endpointURL;
    private OAuthConsumer consumer;
    private HashMap<String,JSONObject> query = new HashMap<String, JSONObject>();
    private JSONObject queryResult;
    private StringBuffer urlBuilder;

    public Semantic3API(String apiKey, String apiSecret, String endpoint){
        if (apiKey == null) {
            throw new Semantics3Exception(
                    "API Credentials Missing",
                    "You did not supply an apiKey. Please sign up at https://semantics3.com/ to obtain your api_key."
            );
        }
        if (apiSecret == null) {
            throw new Semantics3Exception(
                    "API Credentials Missing",
                    "You did not supply an apiSecret. Please sign up at https://semantics3.com/ to obtain your api_key."
            );
        }

        this.apiKey    = apiKey;
        this.apiSecret = apiSecret;
        this.endpoint  = endpoint;
        this.consumer = new DefaultOAuthConsumer(apiKey, apiSecret);
        consumer.setTokenWithSecret("", "");
    }

    public Semantic3API(String apiKey, String apiSecret) {
        if (apiKey == null) {
            throw new Semantics3Exception(
                    "API Credentials Missing",
                    "You did not supply an apiKey. Please sign up at https://semantics3.com/ to obtain your api_key."
            );
        }
        if (apiSecret == null) {
            throw new Semantics3Exception(
                    "API Credentials Missing",
                    "You did not supply an apiSecret. Please sign up at https://semantics3.com/ to obtain your api_key."
            );
        }

        this.apiKey    = apiKey;
        this.apiSecret = apiSecret;
        this.consumer = new DefaultOAuthConsumer(apiKey, apiSecret);
        consumer.setTokenWithSecret("", "");
    }

    private JSONObject fetch(String endpoint, String params) throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException, JSONException {
        String req = API_BASE +
                endpoint +
                "?q=" +
                URLEncoder.encode(params, "UTF-8");
        URL url = new URL(req);
        url = url.toURI().normalize().toURL();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestProperty("User-Agent", "Semantics3 Java Library");
        consumer.sign(request);
        request.connect();
        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getInputStream().toString()));
            if (!json.has("code")){
                json.put("code", "OK");
            }
            return json;
        }
        catch (IOException e) {
            InputStream error = ((HttpURLConnection) request).getErrorStream();
            JSONObject json = new JSONObject(new JSONTokener(error.toString()));
            if (!json.has("code")) {
                json.put("code", "Error");
            }
            else {
                Object value = json.get("code");
                json.put("code", value.toString());
            }
            return json;
        }
    }

    private JSONObject fetch(String endpoint, String method, HashMap<String, Object> params) throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException, JSONException {
        String req = API_BASE +
                endpoint;
        URL url = new URL(req);
        url = url.toURI().normalize().toURL();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestProperty("User-Agent", "Semantics3 Java Library");
        if(method.equals("POST")) {
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestMethod(method);
            request.setRequestProperty("Content-Type", "application/json");
            request.setRequestProperty("Accept", "application/json");
            consumer.sign(request);
            JSONObject jsonParams = new JSONObject(params);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(request.getOutputStream());
            outputStreamWriter.write(jsonParams.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        else {
            request.setRequestMethod(method);
            consumer.sign(request);
        }
        request.connect();
        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getInputStream().toString()));
            if (!json.has("code")){
                json.put("code", "OK");
            }
            return json;
        }
        catch (IOException e) {
            InputStream error = ((HttpURLConnection) request).getErrorStream();
            JSONObject json = new JSONObject(new JSONTokener(error.toString()));
            if (!json.has("code")) {
                json.put("code", "Error");
            }
            else {
                Object value = json.get("code");
                json.put("code", value.toString());
            }
            return json;
        }
    }

    public Semantic3API add(String endpoint, Object... fields) throws JSONException {
        JSONObject endpointQuery;
        if ((endpointQuery = query.get(endpoint)) == null) {
            endpointQuery = new JSONObject();
            query.put(endpoint,endpointQuery);
        }
        JSONObject sq = endpointQuery;
        for (int i=0;i<fields.length-2;i++) {
            JSONObject tmp = sq;
            if(sq.has((String)fields[i])){
                try {
                    sq = sq.getJSONObject((String)fields[i]);
                } catch(Exception e) {
                    throw new Semantics3Exception(
                            "Invalid constraint",
                            "Cannot add this constraint, '" + fields[i] +"' is already a value."
                    );
                }
            } else {
                sq = new JSONObject();
                tmp.put((String)fields[i], sq);
            }
        }
        sq.put((String)fields[fields.length-2], fields[fields.length-1]);
        return this;
    }

    public void remove(String endpoint, String... fields) {
        _remove(this.query.get(endpoint),0,fields);
    }

    public void _remove(JSONObject subquery,int i, String[] fields){
        if (i == fields.length-1) {
            subquery.remove(fields[i]);
        } else {
            JSONObject child = null;
            try {
                child = (JSONObject) subquery.get(fields[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            _remove(child,i+1,fields);
            assert child != null;
            if (child.length() == 0) {
                subquery.remove(fields[i]);
            }
        }
    }

    public Semantic3API field(Object...fields) throws JSONException {
        return add(this.endpoint,fields);
    }

    protected void runQuery() throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException, JSONException {
        runQuery(this.endpoint);
    }

    private void runQuery(String endpoint) throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException, JSONException {
        JSONObject q = this.query.get(endpoint);
        if (q==null) {
            throw new Semantics3Exception(
                    "No query built",
                    "You need to first create a query using the add() method."
            );

        }
        this.queryResult = fetch(endpoint,q.toString());
        if (!this.queryResult.getString("code").equals("OK")) {
            throw new Semantics3Exception(
                    this.queryResult.getString("code"),
                    String.format("Error Code: %s %s", this.queryResult.getString("code"),this.queryResult.getString("message"))
            );

        }
    }

    public JSONObject runQuery(String endpoint, String method, HashMap<String, Object> params) throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException {
        if(method.equals("GET")){
            JSONObject jsonObject = new JSONObject(params);
            this.query.put(endpoint, jsonObject);
            try {
                this.runQuery(endpoint);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            try {
                this.queryResult = fetch(endpoint, method, params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            if (!this.queryResult.getString("code").equals("OK")) {
                throw new Semantics3Exception(
                        this.queryResult.getString("code"),
                        this.queryResult.getString("message")
                );

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.queryResult;
    }

    public JSONObject get(String endpoint) throws
            OAuthMessageSignerException,
            OAuthExpectationFailedException,
            OAuthCommunicationException,
            IOException, URISyntaxException {
        try {
            this.runQuery(endpoint);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.queryResult;
    }
}
