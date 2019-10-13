package com.example.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    LocationManager manager;
    LocationListener listener;
    Geocoder gcd;
    List<Address> address;
    TextView text;
    TextView text2;
    TextView text3;
    TextView text4;
    Location last;
    String one = "Location";
    String two = "Average Accelration";
    String three = "Distance";
    String four = "Location History";
    double distance = 0;
    double distanceRange = 20;
    double latitude = 0;
    double longitude = 0;
    float sumX;
    float sumY;
    float sumZ;
    float averageX;
    float averageY;
    float averageZ;
    String locationList[];

    String averageValues = "";
    boolean trigger = false;
    boolean firstRun = true;
    boolean reset = true;
    int iteration;
    int printIteration;
    SensorManager mSensorManager;
    SensorEventListener listener2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gcd = new Geocoder(this, Locale.getDefault());
        address = new ArrayList<Address>();
        text = (TextView) findViewById(R.id.textView);
        text2 = (TextView) findViewById(R.id.textView3);
        text3 = (TextView) findViewById(R.id.textView2);
        text4 = (TextView) findViewById(R.id.textView4);
        text.setText(one);
        text2.setText(two);
        text3.setText(three);
        text4.setText(four);
        locationList= new String[4];
        locationList[0] = "";
        locationList[1] ="";
        locationList[2] = "";
        locationList[3] = "";
        text3.setText("Distance: 0.00 m");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            trigger = true;
        };


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        listener2 = new SensorEventListener() {
            public void onSensorChanged(SensorEvent var1) {
                Log.i("sensor",var1.values[0] +" "+var1.values[1]+" " +var1.values[2]);
                if (reset) {
                    iteration = 1;
                    sumX = 0;
                    sumX = 0;
                    sumZ = 0;
                    averageValues = average(var1.values[0], var1.values[1], var1.values[2], iteration);
                    reset = false;
                    two = averageValues;
                    text2.setText(averageValues);
                } else {
                    iteration++;
                    averageValues = average(var1.values[0], var1.values[1], var1.values[2], iteration);
                    two = averageValues;
                    text2.setText(averageValues);
                }
            }

            public void onAccuracyChanged(Sensor var1, int var2) {
            }

        };
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i("loc", location.getLatitude() + " " + location.getLongitude());
                try {
                    address = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (firstRun) {
                    String first;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    last = location;
                    first = address.get(0).getAddressLine(0) + "\nLongitude: " + location.getLatitude() + "\nLatitude: " + location.getLongitude() + "\n" + "Altitude: " + location.getAltitude()+
                            "\n\n Average Max Absolute Acceleration: \n"+String.format( "%.2f m/s^2" ,max())   ;
                    one = first+ "\n\nDistance Range: " + distanceRange + " m" ;
                    text.setText("Current Location\n\n"+first+ "\n\nDistance Range: " + distanceRange + " m");
                    locationList[0] = first;
                    printIteration++;
                    firstRun = false;
                    reset = true;
                } else {
                  //  distance = distanceFormula(latitude, location.getLatitude(), longitude, location.getLatitude());
                    distance = location.distanceTo(last);

                    reset = false;
                    firstRun = false;
                    Log.i("loc First Run False", location.getLatitude() + " " + location.getLongitude());
                    if (distanceRange < distance) {
                        String first;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        last = location;
                        first = address.get(0).getAddressLine(0) + "\nLongitude: " + location.getLatitude() + "\nLatitude: " + location.getLongitude() + "\n" + "Altitude: " + location.getAltitude() +
                                "\n\n Average Max Absolute Acceleration: \n"+String.format( "%.2f m/s^2" ,max())  ;
                        one = first+ "\n\nDistance Range: " + distanceRange + " m";
                        text.setText("Current Location\n\n"+first+ "\n\nDistance Range: " + distanceRange + " m");
                        reset = true;
                        firstRun = false;
                        distance = 0;
                        three = "Distance: " + distance +" m" ;
                        text3.setText("Distance: " + String.format("%.2f m/s^2",distance) +" m");
                        if(printIteration>3)
                        {
                            String a = locationList[1];
                            String b = locationList[2];
                            String c = locationList[3];

                            locationList[0] = a;
                            locationList[1] = b;
                            locationList[2] = c;
                            locationList[3] = first;
                            printIteration = 4;

                        }
                        else
                        {
                            locationList[printIteration] = first ;
                            printIteration++;
                        }


                    }
                    else
                    {
                        three =  "Distance: " + distance +" m";
                        text3.setText("Distance: " + String.format("%.2f m/s^2",distance) +" m");
                    }
                }
                if(locationList[0].equals(""))
                {
                    four = "Location History";
                    text4.setText("Location History");
                }

               else if(locationList[1].equals(""))
                {
                    four = "Location History\n\n#1\n" +locationList[0]+"\n";
                    text4.setText("Location History\n\n#1\n" +locationList[0]+"\n");
                }
                else if(locationList[2].equals(""))
                {
                    four = "Location History\n\n#1\n" +locationList[1]+"\n\n#2\n"+ locationList[0]+"\n" ;
                    text4.setText("Location History\n\n#1\n" +locationList[1]+"\n\n#2\n"+ locationList[0]+"\n");
                }

                else if(locationList[3].equals("")) {
                    four = "Location History\n\n#1\n" + locationList[2] + "\n\n#2\n" + locationList[1] + "\n" + "\n#3\n" + locationList[0] + "\n";
                    text4.setText("Location History\n\n#1\n" + locationList[2] + "\n\n#2\n" + locationList[1] + "\n" + "\n#3\n" + locationList[0] + "\n");
                }
                else {
                    four = "Location History\n\n#1\n" + locationList[3] + "\n" + "\n#2\n" + locationList[2] + "\n#3\n" + locationList[1] + "\n\n#4\n" + locationList[0] + "\n";
                    text4.setText("Location History\n\n#1\n" + locationList[3] + "\n" + "\n#2\n" + locationList[2] + "\n\n#3\n" + locationList[1] + "\n\n#4\n" + locationList[0] + "\n");
                }



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
            }


        };

        if (trigger) {
            System.out.println("GPS PROVIDER TRIGGER");
            mSensorManager.registerListener(listener2, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
           // manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            // Permission has already been granted
            trigger = true;
        }
        ;
        if (trigger) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
           // manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            mSensorManager.registerListener(listener2, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        manager.removeUpdates(listener);
        mSensorManager.unregisterListener(listener2);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {


        super.onSaveInstanceState(outState);
        outState.putString("one",one);
        outState.putString("two", two);
        outState.putString("three", three);
        outState.putString("four", four);
        outState.putFloat("sumX",sumX);
        outState.putFloat("sumY",sumY);
        outState.putFloat("sumZ",sumZ);
        outState.putFloat("averageX",averageX);
        outState.putFloat("averageY",averageY);
        outState.putFloat("averageZ", averageZ);
       // outState.putString(locationList,"LocationList");

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {

        super.onRestoreInstanceState(savedInstanceState);
       one = savedInstanceState.getString("one");
       two = savedInstanceState.getString("two");
       three = savedInstanceState.getString("three");
       four = savedInstanceState.getString("four");
       sumX = savedInstanceState.getFloat("sumX");
       sumY= savedInstanceState.getFloat("sumY");
       sumZ = savedInstanceState.getFloat("sumZ");
       averageX = savedInstanceState.getFloat("averageX");
       averageY = savedInstanceState.getFloat("averageY");
       averageZ = savedInstanceState.getFloat("averageZ");



    }
    public double distanceFormula(double x1, double x2, double y1, double y2) {
        double distance = 0;
        distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    public String average(float numberX, float numberY, float numberZ, int iteration) {
        sumX = sumX + numberX;
        sumY = sumY + numberY;
        sumZ = sumZ + numberZ;
        averageX = sumX / iteration;
        averageY = sumY / iteration;
        averageZ = sumZ / iteration;
        return "Averages of Acelerometer\n\n"+String.format("X: %.2f m/s^2" , averageX )+ String.format("\nY: %.2f m/s^2" , averageY )+ String.format("\nZ: %.2f m/s^2" , averageZ );
    }
    public float max()
    {
        float a = Math.max(Math.abs(averageX),Math.abs(averageY));
        float b = Math.max(Math.abs(averageZ),a);
        return b;
    }
}