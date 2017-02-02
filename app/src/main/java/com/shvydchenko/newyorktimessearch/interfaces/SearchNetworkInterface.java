package com.shvydchenko.newyorktimessearch.interfaces;

import com.shvydchenko.newyorktimessearch.models.Article;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by shvydchenko on 1/30/17.
 */

public interface SearchNetworkInterface {
    void handleQueryResultsCallback(JSONArray queryData);
    void onTransformComplete(ArrayList<Article> articles);
}
