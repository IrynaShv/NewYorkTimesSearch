package com.shvydchenko.newyorktimessearch.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by shvydchenko on 1/30/17.
 */

public class Article {

    private String headline;
    private String webUrl;
    private String thumbnail;
    private String publishDate;
    private String byline;

    public Article(String headline, String webUrl, String thumbnail, String publishDate, String byline) {
        this.headline = headline;
        this.webUrl = webUrl;
        this.thumbnail = thumbnail;
        this.publishDate = publishDate;
        this.byline= byline;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getByline() {
        return byline;
    }

    public String getHeadline() {
        return headline;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public static ArrayList<Article> fromJsonArray(JSONArray data) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject articleData = data.getJSONObject(i);
                String headline = "";
                if  (articleData.has("headline")) {
                    if (articleData.getJSONObject("headline").has("main")) {
                        headline = articleData.getJSONObject("headline").getString("main");
                    } else if (articleData.getJSONObject("headline").has("name")) {
                        headline = articleData.getJSONObject("headline").getString("name");
                    }
                }
                String byline = "";
                if  (articleData.has("byline") && articleData.getJSONObject("byline").has("original")) {
                    byline = articleData.getJSONObject("byline").getString("original");
                }
                String publishDate = "";
                if (articleData.has("pub_date")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        c.setTime(sdf.parse(articleData.getString("pub_date").substring(0, 10)));
                        publishDate = new SimpleDateFormat("EEE, d MMM yyyy").format(c.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                }
                String webUrl = articleData.has("web_url") ? articleData.getString("web_url") : "";
                String thumbnail = null;
                if (articleData.has("multimedia")) {
                    JSONArray multimedia = articleData.getJSONArray("multimedia");
                    for (int j = 0; j < multimedia.length(); j++) {
                        String subtype = multimedia.getJSONObject(j).getString("subtype");
                        if (subtype.equals("large")) {
                            thumbnail = multimedia.getJSONObject(j).getJSONObject("legacy").getString("large");
                        } else if (subtype.equals("xlarge")) {
                            thumbnail = multimedia.getJSONObject(j).getJSONObject("legacy").getString("xlarge");
                            break;
                        }
                    }
                }
                results.add(new Article(headline,webUrl, thumbnail, publishDate, byline));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public String getFormattedThumbnailUrl() {
        if (this.thumbnail != null) {
            return "http://www.nytimes.com/" + this.thumbnail;
        } else {
            return null;
        }
    }
}
