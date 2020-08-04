package com.ajdeveloper.instadownloader.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ajdeveloper.instadownloader.R;

public class GeneratingDownloadLinks implements iConstants {
    public static Context Mcontext;
    public static Dialog dialog;
    static int error = 1;
    public static ProgressDialog pd;

    public static class GetUrls extends AsyncTask<String, Void, JSONObject> {
        JSONParser FJson = new JSONParser();

        protected JSONObject doInBackground(String... urls) {
//            return this.FJson.getOJSONFromUrl(urls[0]);
            return new CustomJSONParser().getJSONObject(urls[0]);
        }

        protected void onPostExecute(JSONObject result) {
            String error = "";
            if (result == null){
                iUtils.ShowToast(GeneratingDownloadLinks.Mcontext, iConstants.URL_NOT_SUPPORTED);
            }
            else {
                Log.e("ERROR", result.toString());
                GeneratingDownloadLinks.pd.dismiss();
                try {
                    error = result.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error.contains("not-supported") || error.contains("no_media_found") || error.contains("miss")) {
                    iUtils.ShowToast(GeneratingDownloadLinks.Mcontext, iConstants.URL_NOT_SUPPORTED);
                    return;
                }
                try {
                    GeneratingDownloadLinks.GenerateUI(result);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static void Start(Context context, String url, String title) {
        error = 1;
        for (CharSequence contains : DISABLE_DOWNLOADING) {
            if (url.contains(contains)) {
                error = 0;
            }
        }
        Mcontext = context;
        if (error == 1) {
            pd = new ProgressDialog(context);
            pd.setMessage(iConstants.DOWNLOADING_MSG);
            pd.show();
            GetUrls getUrls = new GetUrls();
            String[] strArr = new String[1];
            strArr[0] = String.format(iConstants.API_URL, url);
            getUrls.execute(strArr);
            return;
        }
        iUtils.ShowToast(Mcontext, iConstants.WEB_DISABLE);
    }

    public static void GenerateUI(JSONObject result) throws JSONException {
        String thumbnail = result.getString("thumbnail");
        final String title = result.getString("title");
        final JSONArray urls = result.getJSONArray("urls");
        dialog = new Dialog(Mcontext);
        dialog.setContentView(R.layout.download_dialogdown_);
        dialog.setTitle("Title...");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        ListView LV = (ListView) dialog.findViewById(R.id.ListView);
        String[] listItems = new String[urls.length()];
        ((TextView) dialog.findViewById(R.id.text)).setText(title);
        if (!thumbnail.equals("")) {
            Picasso.get().load(thumbnail).resize(100, 100).centerCrop().into(image);
        }
        String label = "";
        for (int i = 0; i < urls.length(); i++) {
            label = urls.getJSONObject(i).getString("label");
            if (label.contains("(audio - no video) webm")) {
                label = label.replace("(audio - no video) webm", "mp3");
            }
            listItems[i] = label;
        }
        LV.setAdapter(new ArrayAdapter(Mcontext, android.R.layout.simple_list_item_1, android.R.id.text1, listItems));
        LV.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                String ext = "";
                try {
                    JSONObject m = urls.getJSONObject(position);
                    if (m.getString("label").contains(" mp4")) {
                        ext = ".mp4";
                    } else if (m.getString("label").contains(" mp3")) {
                        ext = ".mp3";
                    } else if (m.getString("label").contains(" 360p - webm")) {
                        ext = ".webm";
                    } else if (m.getString("label").contains(" webm")) {
                        ext = ".mp3";
                    } else if (m.getString("label").contains(" m4a")) {
                        ext = ".m4a";
                    } else if (m.getString("label").contains(" 3gp")) {
                        ext = ".3gp";
                    } else if (m.getString("label").contains(" flv")) {
                        ext = ".flv";
                    } else {
                        ext = ".mp4";
                    }
                    DownloadFile.Downloading(GeneratingDownloadLinks.Mcontext, m.getString("id"), title, ext);
                    GeneratingDownloadLinks.dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }
}
