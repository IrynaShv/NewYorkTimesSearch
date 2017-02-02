package com.shvydchenko.newyorktimessearch.interfaces;

import android.os.Parcelable;

import com.shvydchenko.newyorktimessearch.models.Article;

import java.util.ArrayList;

/**
 * Created by shvydchenko on 1/30/17.
 */

public interface SearchPresenterInterface {
        void updateAdapter(ArrayList<Article> articles);
        void onSubmit(Parcelable data);
}
