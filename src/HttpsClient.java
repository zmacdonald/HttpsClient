import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * Created by zmacdonald on 2016-08-18.
 */
public class HttpsClient {

    private static HttpsClient instance;
    private HttpsURLConnection connection;
    private String authorization;

    private HttpsClient() {
    }

    /**
     * @return the singleton instance of the HttpsClient
     */
    public static HttpsClient getInstance() {
        if (instance == null)
            instance = new HttpsClient();
        return instance;
    }

    /**
     * HTTP GET method accepting a json string of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a string of parameters in json format
     * @return  a json object of the http response message
     */
    public JSONObject get(String stringUrl, String params) {
        JSONObject json = getJson(params);
        return get(stringUrl, json);
    }

    /**
     * HTTP GET method accepting an array of value pairs as parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    one or more pairs of strings to be used as parameters
     * @return  a json object of the http response message
     */
    public JSONObject get(String stringUrl, ValuePair... params) {
        JSONObject json = getJson(params);
        return get(stringUrl, json);
    }

    /**
     * HTTP GET method accepting a json object of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a json object of parameters
     * @return  a json object of the http response message
     */
    public JSONObject get(String stringUrl, JSONObject params) {
        openConnection(stringUrl);

        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        if (params != null) {
            addParams(params);
        }
        return getResponse();
    }

    /**
     * HTTP PUT method accepting a json string of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a string of parameters in json format
     * @return  a json object of the http response message
     */
    public JSONObject put(String stringUrl, String params) {
        JSONObject json = getJson(params);
        return put(stringUrl, json);
    }

    /**
     * HTTP PUT method accepting an array of value pairs as parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    one or more pairs of strings to be used as parameters
     * @return  a json object of the http response message
     */
    public JSONObject put(String stringUrl, ValuePair... params) {
        JSONObject json = getJson(params);
        return put(stringUrl, json);
    }

    /**
     * HTTP PUT method accepting a json object of parameters
     * @param stringUrl the url to be connected to
     * @param params    a json object of parameters
     * @return  a json object of the http response message
     */
    public JSONObject put(String stringUrl, JSONObject params) {

        openConnection(stringUrl);

        try {
            connection.setRequestMethod("PUT");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        if (params != null) {
            addParams(params);
        }
        return getResponse();
    }

    /**
     * HTTP POST method accepting a json string of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a string of parameters in json format
     * @return  a json object of the http response message
     */
    public JSONObject post(String stringUrl, String params) {
        JSONObject json = getJson(params);
        return post(stringUrl, json);
    }

    /**
     * HTTP POST method accepting an array of value pairs as parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    one or more pairs of strings to be used as parameters
     * @return  a json object of the http response message
     */
    public JSONObject post(String stringUrl, ValuePair... params) {
        JSONObject json = getJson(params);
        return post(stringUrl, json);
    }

    /**
     * HTTP POST method accepting a json object of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a json object of parameters
     * @return  a json object of the http response message
     */
    public JSONObject post(String stringUrl, JSONObject params) {
        openConnection(stringUrl);

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        if (params != null) {
            addParams(params);
        }
        return getResponse();
    }

    /**
     * HTTP DELETE method accepting a json string of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a string of parameters in json format
     * @return  a json object of the http response message
     */
    public JSONObject delete(String stringUrl, String params) {
        JSONObject json = getJson(params);
        return delete(stringUrl, json);
    }

    /**
     * HTTP DELETE method accepting an array of value pairs as parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    one or more pairs of strings to be used as parameters
     * @return  a json object of the http response message
     */
    public JSONObject delete(String stringUrl, ValuePair... params) {
        JSONObject json = getJson(params);
        return delete(stringUrl, json);
    }

    /**
     * HTTP DELETE method accepting a json object of parameters
     *
     * @param stringUrl the url to be connected to
     * @param params    a json object of parameters
     * @return  a json object of the http response message
     */
    public JSONObject delete(String stringUrl, JSONObject params) {
        openConnection(stringUrl);

        try {
            connection.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        if (params != null) {
            addParams(params);
        }

        return getResponse();
    }

    /**
     * sets the authorization string used in the Http header
     *
     * @param username web server username
     * @param password web server password
     */
    public void setAuthorization(String username, String password) {
        byte[] data = new byte[0];
        try {
            data = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        authorization = "Basic " + Base64.encode(data);
        System.out.println(authorization);
    }

    private void openConnection(String stringUrl) {
        try {
            connection = (HttpsURLConnection) new URL(stringUrl).openConnection();
            trustAllHosts();

            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            if (authorization != null && !authorization.isEmpty())
                connection.setRequestProperty("Authorization", authorization);
            connection.getDoInput();
            connection.setRequestProperty("Accept", "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addParams(JSONObject json) {

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(json.toString().length()));
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            System.out.println("parameters out: " + json.toString());
            wr.writeBytes(json.toString());
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getJson(ValuePair[] params) {
        if (params == null)
            return null;

        JSONObject json = new JSONObject();

        for (ValuePair param : params) {
            try {
                json.put(param.first, param.second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

    private JSONObject getJson(String params) {
        if (params == null || params.isEmpty())
            return null;

        JSONObject json = new JSONObject();

        try {
            json = new JSONObject(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private JSONObject getResponse() {
        JSONObject jsonObject = null;
        try {
            System.out.println("Response code: " + connection.getResponseCode());
            System.out.println("msg: " + connection.getResponseMessage());
            // read the response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            jsonObject = new JSONObject(result);
            System.out.println("message back: " + result);

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        connection.disconnect();

        return jsonObject;
    }

    private void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        connection.setHostnameVerifier(allHostsValid);
    }
}
