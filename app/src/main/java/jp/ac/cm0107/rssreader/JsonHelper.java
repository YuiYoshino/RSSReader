package jp.ac.cm0107.rssreader;

import android.util.Log;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class JsonHelper {

    static String pubDate;
    public static ArrayList<RssItem> parseJson(String strJson){
        ArrayList<RssItem> list = new ArrayList<>();
        try{
            JSONObject json = new JSONObject(strJson);
            JSONObject feed = json.getJSONObject("channel");

            pubDate = feed.getString("pubDate");


            JSONArray entries = feed.getJSONArray("item");
            for(int i = 0; i < entries.length();i++) {
                JSONObject entry = entries.getJSONObject(i);
                list.add(parseToItem(entry));
            }
        }catch (Exception e) {
            Log.e("JsonHelper", e.getMessage());
        }
        return list;
    }
    public static RssItem parseToItem (JSONObject json) throws JSONException {
        RssItem item = new RssItem();
        item.setTitle(json.getString("title"));
        item.setLink(json.getString("link"));
        item.setPubDate(json.getString("pubDate"));
        return item;
    }
}