package com.paad.whereami;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class WhereAmI extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    LocationManager locationManager;
    String context = Context.LOCATION_SERVICE;
    locationManager = (LocationManager)getSystemService(context);

    String provider = LocationManager.GPS_PROVIDER;
    Location location = 
      locationManager.getLastKnownLocation(provider);

    updateWithNewLocation(location);
  }

  private void updateWithNewLocation(Location location) {
    String latLongString;
    TextView myLocationText; 
    myLocationText = (TextView)findViewById(R.id.myLocationText);
    if (location != null) {
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      latLongString = "Lat:" + lat + "\nLong:" + lng;
    } else {
      latLongString = "No location found"; 
    }
    myLocationText.setText("Your Current Position is:\n" + 
                           latLongString);
  }
}