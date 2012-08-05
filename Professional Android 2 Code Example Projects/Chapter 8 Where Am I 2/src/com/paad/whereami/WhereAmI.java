package com.paad.whereami;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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

    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setAltitudeRequired(false);
    criteria.setBearingRequired(false);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.POWER_LOW);
    String provider = locationManager.getBestProvider(criteria, true);

	  Location location = locationManager.getLastKnownLocation(provider);
    updateWithNewLocation(location);

    locationManager.requestLocationUpdates(provider, 2000, 10,
                                           locationListener);
  }
  
  private final LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {
      updateWithNewLocation(location);
    }
	 
    public void onProviderDisabled(String provider){
      updateWithNewLocation(null);
    }

    public void onProviderEnabled(String provider){ }
    public void onStatusChanged(String provider, int status, 
                                Bundle extras){ }
  };

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