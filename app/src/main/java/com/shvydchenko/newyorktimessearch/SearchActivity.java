package com.shvydchenko.newyorktimessearch;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.shvydchenko.newyorktimessearch.EndlessRecyclerViewScrollListener.EndlessRecyclerViewScrollListener;
import com.shvydchenko.newyorktimessearch.Fragments.FilterDialogFragment;
import com.shvydchenko.newyorktimessearch.adapters.ArticleAdapter;
import com.shvydchenko.newyorktimessearch.interfaces.SearchPresenterInterface;
import com.shvydchenko.newyorktimessearch.models.Article;
import com.shvydchenko.newyorktimessearch.models.FilterOptions;
import com.shvydchenko.newyorktimessearch.presenters.SearchPresenter;

import org.parceler.Parcels;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchPresenterInterface {

    private String currentQuery;
    private SearchPresenter searchPresenter;
    private RecyclerView rvArticles;
    private ArrayList<Article> articles;
    private ArticleAdapter adapter;
    private FilterDialogFragment filterDialogFragment;
    private FilterOptions filterOptions;
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        checkIfOnline();

        filterDialogFragment = null;
        currentQuery = null;
        filterOptions = new FilterOptions();

        searchPresenter = new SearchPresenter(this);
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        articles = new ArrayList<>();

        adapter = new ArticleAdapter(this, articles);
        rvArticles.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvArticles.setLayoutManager(gridLayoutManager);
        adapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openCustomTab(view, articles.get(position));
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                searchPresenter.loadNextDataFromApi(currentQuery, filterOptions, page);
            }
        };
        rvArticles.addOnScrollListener(scrollListener);
    }

    public void updateAdapter(ArrayList<Article> articles) {
        this.articles.addAll(articles);
        adapter.notifyDataSetChanged();
    }

    public void clearAdapter() {
        adapter.clear();
        this.articles.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
        } catch (Exception e) {
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (checkIfOnline()) {
                    clearAdapter();
                    currentQuery = query;
                    searchPresenter.queryArticles(query, filterOptions);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_filter:
                FragmentManager fm = getSupportFragmentManager();
                filterDialogFragment = FilterDialogFragment.newInstance("Filter Results", filterOptions);
                filterDialogFragment.show(fm,"Filter Results");
                break;
        }
        return true;
    }

    public void onSubmit(Parcelable data) {
        filterDialogFragment.dismiss();
        filterOptions = Parcels.unwrap(data);
        if (currentQuery != null) {
            clearAdapter();
            searchPresenter.queryArticles(currentQuery, filterOptions);
        }
    }

    private void openCustomTab(View view, Article article) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());
        int requestCode = 100;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(view.getContext(), Uri.parse(article.getWebUrl()));
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private Boolean checkIfOnline() {
        if (!isOnline()) {
            Toast toast = Toast.makeText(this, "Please connect to the internet!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
        return isOnline();
    }


}
