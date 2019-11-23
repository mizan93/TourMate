package com.pro.firebasepro;

import android.content.SearchRecentSuggestionsProvider;

public class MySuggestionsProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.pro.firebasepro.MySuggestionsProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionsProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }
}
