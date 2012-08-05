package com.paad.forceometer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Forceometer extends Activity {
  SensorManager sensorManager;
  TextView accelerationTextView;
  TextView maxAccelerationTextView;
  float currentAcceleration = 0;
  float maxAcceleration = 0; 
	
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);

    accelerationTextView = (TextView)findViewById(R.id.acceleration);
    maxAccelerationTextView = (TextView)findViewById(R.id.maxAcceleration);
    sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

    Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(sensorEventListener,
                                   accelerometer,
                                   SensorManager.SENSOR_DELAY_FASTEST);
    
    Timer updateTimer = new Timer("gForceUpdate");
    updateTimer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        updateGUI();
      }
    }, 0, 100);
  }
  
  private final SensorEventListener sensorEventListener = new SensorEventListener() {
    double calibration = SensorManager.STANDARD_GRAVITY;

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void onSensorChanged(SensorEvent event) {
      double x = event.values[0];
      double y = event.values[1];
      double z = event.values[2];

      double a = Math.round(Math.sqrt(Math.pow(x, 2) + 
                                      Math.pow(y, 2) + 
                                      Math.pow(z, 2)));
      currentAcceleration = Math.abs((float)(a-calibration));

      if (currentAcceleration > maxAcceleration)
        maxAcceleration = currentAcceleration;
    }
  };
  
  private void updateGUI() {
    runOnUiThread(new Runnable() {
      public void run() {
        String currentG = currentAcceleration/SensorManager.STANDARD_GRAVITY
                          + "Gs";
        accelerationTextView.setText(currentG);
        accelerationTextView.invalidate();

        String maxG = maxAcceleration/SensorManager.STANDARD_GRAVITY + "Gs";
        maxAccelerationTextView.setText(maxG);
        maxAccelerationTextView.invalidate();
      }
    });
  };
}