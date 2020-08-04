package com.ajdeveloper.instadownloader.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    static InputStream iStream = null;
    static JSONArray jarray = null;
    static JSONObject jarrayo = null;
    static String jarrayq = null;
    static String json = "";

    public JSONArray getJSONFromUrl(String url) {
        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        builder.append(line);
                    }
                    else
                        break;
                }
                return new JSONArray(builder.toString());
            }
            Log.e("==>", "Failed to download file");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jarray = new JSONArray(builder.toString());
        } catch (JSONException e3) {
            Log.e("JSON Parser", "Error parsing data " + e3.toString());
        }
        return jarray;
    }

    public JSONObject getOJSONFromUrl(String url) {
        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        builder.append(line);
                    }
                    else
                        break;
                }
                jarrayo = new JSONObject(builder.toString());
                return jarrayo;
            }
            Log.e("==>", "Failed to download file");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jarrayo = new JSONObject(builder.toString());
        } catch (JSONException e3) {
            Log.e("JSON Parser", "Error parsing data " + e3.toString());
        }
        return jarrayo;
    }

    public String getSJSONFromUrl(String url) {
        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    builder.append(line);
                }
            } else {
                Log.e("==>", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        jarrayq = builder.toString();
        return jarrayq;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info == null) {
            return false;
        }
        for (NetworkInfo state : info) {
            if (state.getState() == State.CONNECTED) {
                return true;
            }
        }
        return false;
    }
}
