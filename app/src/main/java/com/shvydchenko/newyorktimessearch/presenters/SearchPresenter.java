package com.shvydchenko.newyorktimessearch.presenters;

import com.shvydchenko.newyorktimessearch.NetworkManager;
import com.shvydchenko.newyorktimessearch.interfaces.SearchNetworkInterface;
import com.shvydchenko.newyorktimessearch.interfaces.SearchPresenterInterface;
import com.shvydchenko.newyorktimessearch.models.Article;
import com.shvydchenko.newyorktimessearch.models.FilterOptions;
import com.shvydchenko.newyorktimessearch.transforms.NetworkTransform;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;

/**
 * Created by shvydchenko on 1/30/17.
 */

public class SearchPresenter implements SearchNetworkInterface {

    private String API_KEY = "2a1e3002007241528df2d4fcd0c2528e";
    String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    private NetworkManager networkManager;
    private SearchPresenterInterface searchPresenterInterface;
    private NetworkTransform networkTransform;

    public SearchPresenter(SearchPresenterInterface searchPresenterInterface) {
        this.searchPresenterInterface = searchPresenterInterface;
        networkManager = new NetworkManager(this);
        networkTransform = new NetworkTransform(this);

    }

    public void queryArticles(String queryString, FilterOptions filterOptions) {
        networkManager.queryArticles(createUrl(queryString, filterOptions, "0"), 0, 1);
    }

    public void loadNextDataFromApi(String queryString, FilterOptions filterOptions, int offset) {
        networkManager.queryArticles(createUrl(queryString, filterOptions, Integer.toString(offset)), 0, 1);
    }

    public void handleQueryResultsCallback(JSONArray queryData) {
        networkTransform.JSONtoArticleList(queryData);
    }

    public void onTransformComplete(ArrayList<Article> articles) {
        searchPresenterInterface.updateAdapter(articles);
    }

    private String createUrl(String queryString, FilterOptions filterOptions, String page) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder().addQueryParameter("api-key", API_KEY);
        if (queryString != null) {
            urlBuilder.addQueryParameter("q", queryString.replaceAll(" ", "+"));
        }
        if (filterOptions.getSortOrder() != null ) {
            urlBuilder.addQueryParameter("sort", filterOptions.getSortOrder().toLowerCase());
        }
        if (filterOptions.getFormattedDate() != null) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            urlBuilder.addQueryParameter("begin_date", filterOptions.getFormattedDate())
                    .addQueryParameter("end_date", sdf.format(date));
        }
        if (filterOptions.getCategories() != null) {
            String categoriesString = "(";
            LinkedHashMap<String, Boolean> categories = filterOptions.getCategories();
            Set set = categories.entrySet();
            Iterator i = set.iterator();
            while(i.hasNext()) {
                Map.Entry cat = (Map.Entry)i.next();
                if ((Boolean)cat.getValue() == true) {
                    categoriesString += "\"" + cat.getKey().toString() + "\",";
                }
            }
            categoriesString = categoriesString.substring(0, categoriesString.length()-1) + ")";
            urlBuilder.addQueryParameter("news_desk", categoriesString);
        }

        urlBuilder.addQueryParameter("page", page);
        return urlBuilder.build().toString();
    }
}