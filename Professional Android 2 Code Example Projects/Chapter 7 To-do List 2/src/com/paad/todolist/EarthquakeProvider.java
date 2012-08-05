package com.paad.earthquake;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EarthquakeProvider extends ContentProvider {

  @Override
  public boolean onCreate() {
  }

  @Override
  public String getType(Uri url) {
  }
    
  @Override
  public Cursor query(Uri url, String[] projection, String selection,
                      String[] selectionArgs, String sort) {
  }

  @Override
  public Uri insert(Uri _url, ContentValues _initialValues) {
  }

  @Override
  public int delete(Uri url, String where, String[] whereArgs) {
  }

  @Override
  public int update(Uri url, ContentValues values, String where, String[] wArgs) {
  }
}