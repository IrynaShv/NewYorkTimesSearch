package com.shvydchenko.newyorktimessearch.models;

import org.parceler.Parcel;

import java.util.LinkedHashMap;

/**
 * Created by shvydchenko on 1/31/17.
 */

@Parcel
public class FilterOptions {
    String sortOrder;
    String formattedDate;
    LinkedHashMap<String, Boolean> categories;

    public FilterOptions() {
        this.sortOrder = "newest";
        this.formattedDate = null;
        this.categories = null;
    }

    public FilterOptions(String sortOrder, String formattedDate, LinkedHashMap <String, Boolean> categories) {
        this.sortOrder = sortOrder;
        this.formattedDate = formattedDate;
        this.categories = categories;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public LinkedHashMap <String, Boolean> getCategories() {
        return categories;
    }
}
