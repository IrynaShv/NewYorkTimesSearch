package com.shvydchenko.newyorktimessearch.transforms;

import android.os.AsyncTask;

import com.shvydchenko.newyorktimessearch.interfaces.SearchNetworkInterface;
import com.shvydchenko.newyorktimessearch.models.Article;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by shvydchenko on 1/30/17.
 */

public class NetworkTransform {

    private SearchNetworkInterface searchNetworkInterface;

    public NetworkTransform(SearchNetworkInterface searchNetworkInterface) {
        this.searchNetworkInterface = searchNetworkInterface;
    }

    public void JSONtoArticleList(JSONArray jsonArray) {
        new TransformArticles(this.searchNetworkInterface).execute(jsonArray);
    }
}

class TransformArticles extends AsyncTask<JSONArray, Object, ArrayList<Article>>{
    private SearchNetworkInterface searchNetworkInterface;

    public TransformArticles(SearchNetworkInterface searchNetworkInterface) {
        this.searchNetworkInterface = searchNetworkInterface;
    }

    @Override
    protected ArrayList<Article> doInBackground(JSONArray...params){
        try{
            return Article.fromJsonArray(params[0]);
        }catch(Exception e){
            return null;
        }
    }

    protected void onPostExecute(ArrayList<Article> result) {
        searchNetworkInterface.onTransformComplete(result);
    }
}