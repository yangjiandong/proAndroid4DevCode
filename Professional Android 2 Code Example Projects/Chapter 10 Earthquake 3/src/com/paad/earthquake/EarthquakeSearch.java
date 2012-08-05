package com.paad.earthquake;

import android.app.ListActivity; 
import android.app.SearchManager; 
import android.database.Cursor; 
import android.net.Uri; 
import android.os.Bundle; 
import android.widget.SimpleCursorAdapter; 

public class EarthquakeSearch extends ListActivity { 

  @Override 
  public void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState); 
    
    String searchTerm = getIntent().getStringExtra(SearchManager.USER_QUERY); 
    Uri searchQuery = Uri.withAppendedPath(EarthquakeProvider.SEARCH_URI, searchTerm);
    
    Cursor c = getContentResolver().query(searchQuery, null, null, null, null); 
    startManagingCursor(c); 

    String[] from = new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1}; 
    int[] to = new int[] {android.R.id.text1}; 
    
    SimpleCursorAdapter searchResults = new SimpleCursorAdapter(this, 
    android.R.layout.simple_list_item_1, c, from, to); 
    setListAdapter(searchResults); 
  } 
} 
