package com.paad.earthquake; 

import android.widget.RemoteViews; 
import android.appwidget.AppWidgetManager;  
import android.appwidget.AppWidgetProvider; 
import android.content.ComponentName; 
import android.content.ContentResolver;
import android.content.Context; 
import android.content.Intent; 
import android.database.Cursor; 
  
public class EarthquakeWidget extends AppWidgetProvider { 
  public void updateQuake(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { 
    Cursor lastEarthquake; 
    
    ContentResolver cr = context.getContentResolver(); 
    lastEarthquake = cr.query(EarthquakeProvider.CONTENT_URI, null, null, null, null); 
    
    String magnitude = "--"; 
    String details = "-- None --"; 
    
    if (lastEarthquake != null) { 
      try { 
        if (lastEarthquake.moveToFirst()) { 
          magnitude = lastEarthquake.getString(EarthquakeProvider.MAGNITUDE_COLUMN); 
          details = lastEarthquake.getString(EarthquakeProvider.DETAILS_COLUMN); 
        } 
      } 
      finally { 
        lastEarthquake.close(); 
      } 
    }
    
    final int N = appWidgetIds.length; 
    for (int i = 0; i < N; i++) { 
      int appWidgetId = appWidgetIds[i]; 
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quake_widget); 
      views.setTextViewText(R.id.widget_magnitude, magnitude); 
      views.setTextViewText(R.id.widget_details, details); 
      appWidgetManager.updateAppWidget(appWidgetId,views); 
    }
  } 
  
  public void updateQuake(Context context) { 
    ComponentName thisWidget = new ComponentName(context, 
    EarthquakeWidget.class); 
    AppWidgetManager appWidgetManager = 
    AppWidgetManager.getInstance(context); 
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget); 
    updateQuake(context, appWidgetManager, appWidgetIds); 
  } 
  
  @Override 
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { 
    updateQuake(context, appWidgetManager, appWidgetIds); 
  } 

  @Override 
  public void onReceive(Context context, Intent intent){ 
    super.onReceive(context, intent); 
    if (intent.getAction().equals(EarthquakeService.QUAKES_REFRESHED)) 
      updateQuake(context); 
  } 
} 