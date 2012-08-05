package com.paad.earthquake;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class EarthquakeMap extends MapActivity {

  NotificationManager notificationManager;
  Cursor earthquakeCursor; 
  EarthquakeReceiver receiver;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.earthquake_map);

    Uri earthquakeURI = EarthquakeProvider.CONTENT_URI;
    earthquakeCursor = getContentResolver().query(earthquakeURI,
                                                  null, null, null, null); 

    MapView earthquakeMap = (MapView)findViewById(R.id.map_view);
    EarthquakeOverlay eo = new EarthquakeOverlay(earthquakeCursor);
    earthquakeMap.getOverlays().add(eo);
    
    String svcName = Context.NOTIFICATION_SERVICE;
    notificationManager = (NotificationManager)getSystemService(svcName);
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  @Override 
  public void onResume() {
    notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
	  
    earthquakeCursor.requery();

    IntentFilter filter;
    filter = new IntentFilter(EarthquakeService.NEW_EARTHQUAKE_FOUND);
    receiver = new EarthquakeReceiver();
    registerReceiver(receiver, filter);

    super.onResume();
  }

  @Override
  public void onPause() {
    earthquakeCursor.deactivate();
    super.onPause();
  }

  public class EarthquakeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
      notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
      earthquakeCursor.requery();
      MapView earthquakeMap = (MapView)findViewById(R.id.map_view);
      earthquakeMap.invalidate();
    }
  }

  @Override 
  public void onDestroy() {
    earthquakeCursor.close();
    super.onDestroy();
  }
}