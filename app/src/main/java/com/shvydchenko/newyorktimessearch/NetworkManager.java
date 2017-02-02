package com.shvydchenko.newyorktimessearch;

import android.os.Handler;
import android.os.Looper;

import com.shvydchenko.newyorktimessearch.interfaces.SearchNetworkInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shvydchenko on 1/30/17.
 */

public class NetworkManager {

    private SearchNetworkInterface searchNetworkInterface;
    private OkHttpClient okHttpClient;
    private Handler mHandler;

    public NetworkManager(SearchNetworkInterface searchNetworkInterface) {
        okHttpClient = new OkHttpClient();
        this.searchNetworkInterface = searchNetworkInterface;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void queryArticles(final String url, final Integer delay, final Integer attemptNumber) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (response.code() == 429 && attemptNumber <= 3) {
                        queryArticles(url,2000, attemptNumber + 1);
                    }
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        final String responseData = response.body().string();
                        final JSONArray articlesQueryResults = new JSONObject(responseData).getJSONObject("response").getJSONArray("docs");

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                searchNetworkInterface.handleQueryResultsCallback(articlesQueryResults);
                            }
                        }, delay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
