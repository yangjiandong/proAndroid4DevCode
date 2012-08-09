package com.paad.compass;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class CompassActivity extends Activity {
  float[] aValues = new float[3];
  float[] mValues = new float[3];
  CompassView compassView;
  SensorManager sensorManager;

  TextView tx1;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);

    tx1 = (TextView) this.findViewById(R.id.tx1);
    compassView = (CompassView) this.findViewById(R.id.compassView);
    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    showSensors();

    updateOrientation(new float[] { 0, 0, 0 });
  }

  private void showSensors() {
    // get all
    List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

    // 显示每个传感器的具体信息
    for (Sensor s : allSensors) {

      String tempString = "\n" + "  设备名称：" + s.getName() + "\n" + "  设备版本："
          + s.getVersion() + "\n" + "  供应商：" + s.getVendor() + "\n";

      switch (s.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 加速度传感器accelerometer" + tempString);
        break;
      case Sensor.TYPE_GYROSCOPE:
        tx1.setText(tx1.getText().toString() + s.getType() + " 陀螺仪传感器gyroscope"
            + tempString);
        break;
      case Sensor.TYPE_LIGHT:
        tx1.setText(tx1.getText().toString() + s.getType() + " 环境光线传感器light"
            + tempString);
        break;
      case Sensor.TYPE_MAGNETIC_FIELD:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 电磁场传感器magnetic field" + tempString);
        break;
      case Sensor.TYPE_ORIENTATION:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 方向传感器orientation" + tempString);
        break;
      case Sensor.TYPE_PRESSURE:
        tx1.setText(tx1.getText().toString() + s.getType() + " 压力传感器pressure"
            + tempString);
        break;
      case Sensor.TYPE_PROXIMITY:
        tx1.setText(tx1.getText().toString() + s.getType() + " 距离传感器proximity"
            + tempString);
        break;
      case Sensor.TYPE_TEMPERATURE:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 温度传感器temperature" + tempString);
        break;
      case Sensor.TYPE_LINEAR_ACCELERATION:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 线性传感器LINEAR_ACCELERATION" + tempString);
        break;
      case Sensor.TYPE_GRAVITY:
        tx1.setText(tx1.getText().toString() + s.getType() + " 重力传感器GRAVITY"
            + tempString);
        break;
      case Sensor.TYPE_ROTATION_VECTOR:
        tx1.setText(tx1.getText().toString() + s.getType()
            + " 旋转传感器ROTATION_VECTOR" + tempString);
        break;
      default:
        tx1.setText(tx1.getText().toString() + s.getType() + " 未知传感器"
            + tempString);
        break;
      }
    }

    // 在title上显示重力传感器的变化
    Sensor sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    // 注册listener，第三个参数是检测的精确度
    //sensorManager.registerListener(lsn, sensor2,
    //    SensorManager.SENSOR_DELAY_GAME);
  }

  SensorEventListener lsn = new SensorEventListener() {
    float x, y, z;

    public void onSensorChanged(SensorEvent e) {
      x = e.values[SensorManager.DATA_X];
      y = e.values[SensorManager.DATA_Y];
      z = e.values[SensorManager.DATA_Z];
      // String s = getTitle().toString();
      setTitle("x=" + (int) x + "," + "y=" + (int) y + "," + "z=" + (int) z);
    }

    public void onAccuracyChanged(Sensor s, int accuracy) {
    }
  };

  private void updateOrientation(float[] values) {
    if (compassView != null) {
      compassView.setBearing(values[0]);
      compassView.setPitch(values[1]);
      compassView.setRoll(-values[2]);
      compassView.invalidate();
    }
  }

  private float[] calculateOrientation() {
    float[] values = new float[3];
    float[] R = new float[9];
    float[] outR = new float[9];

    SensorManager.getRotationMatrix(R, null, aValues, mValues);
    SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,
        SensorManager.AXIS_Z, outR);

    SensorManager.getOrientation(outR, values);

    // Convert from Radians to Degrees.
    values[0] = (float) Math.toDegrees(values[0]);
    //values[0] = 234;//
    values[1] = (float) Math.toDegrees(values[1]);
    values[2] = (float) Math.toDegrees(values[2]);

    return values;
  }

  private final SensorEventListener sensorEventListener = new SensorEventListener() {
    public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        float x, y, z;

        x = event.values[SensorManager.DATA_X];
        y = event.values[SensorManager.DATA_Y];
        z = event.values[SensorManager.DATA_Z];
        // String s = getTitle().toString();
        setTitle("x=" + (int) x + "," + "y=" + (int) y + "," + "z=" + (int) z);

        aValues = event.values;
      }
      if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        mValues = event.values;

      updateOrientation(calculateOrientation());
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  @Override
  protected void onResume() {
    super.onResume();

    Sensor accelerometer = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    Sensor magField = sensorManager
        .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    sensorManager.registerListener(sensorEventListener, accelerometer,
        SensorManager.SENSOR_DELAY_GAME);//SENSOR_DELAY_FASTEST);
    sensorManager.registerListener(sensorEventListener, magField,
        SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  protected void onStop() {
    sensorManager.unregisterListener(sensorEventListener);
    // sensorManager.unregisterListener(lsn);
    super.onStop();
  }
}