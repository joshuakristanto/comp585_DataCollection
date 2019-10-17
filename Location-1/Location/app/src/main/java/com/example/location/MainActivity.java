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
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
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
    Button button;
    String one = "Location";
    String two = "Average Accelration";
    String three = "Distance";
    String four = "Location History";
    String output="";
    double distance = 0;
    double distanceRange = 3;
    double latitude = 0;
    double longitude = 0;
    float sumX = 0;
    float sumY = 0;
    float sumZ = 0;
    float averageX = 0;
    float averageY = 0;
    float averageZ = 0;
    String locationList[];
    SimpleDateFormat simpleDateFormat;
    String averageValues = "";
    boolean trigger = false;
    boolean trigger2 = false;
    boolean firstRun = true;
    boolean reset = true;
    int iteration = 0;
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
        button = (Button)findViewById(R.id.button);

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
        simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String format = simpleDateFormat.format(new Date());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh_mm_ss");
                // Perform action on click
//               writeToFile(output);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/LocationData", "text");
                if (!file.exists()) {
                    Log.i("HELLO WORLD", ""+file);
                    file.mkdirs();
                }
                try {
                    File gpxfile = new File(file, simpleDateFormat2.format(new Date())+"sample.csv");
                    Log.i("HELLO WORLD2 " ,""+gpxfile);
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(output);
                    writer.flush();
                    writer.close();
                    Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
                } catch (Exception e) { }
            }

        });



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            // Permission has already been granted
            trigger2 = true;
        }
        ;        if (ContextCompat.checkSelfPermission(this,
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
                Log.i("sensor",reset+" "+var1.values[0] +" "+var1.values[1]+" " +var1.values[2]);
//                Toast.makeText(MainActivity.this, var1.values[0] +" "+var1.values[1]+" " +var1.values[2], Toast.LENGTH_LONG).show();
                if (reset) {
                    iteration = 0;
                    resets();
                   // averageValues = average(var1.values[0], var1.values[1], var1.values[2], iteration);
                    averageValues = average(0, 0, 0, 1);
                    reset = false;
                    Log.i("False Reached3",sumX+" " +sumY + " "+ sumZ);
                    Log.i("False Reached",averageX+" " +averageY + " "+ averageZ);
                    two = averageValues;
                    Log.i("False Reached2",averageValues);
                    text2.setText(averageValues);
                   // Toast.makeText(MainActivity.this, "Reset"+ averageValues, Toast.LENGTH_LONG).show();
                } else {
                    iteration++;
                    averageValues = average(var1.values[0], var1.values[1], var1.values[2], iteration);
                    Log.i("False Reached5",sumX+" " +sumY + " "+ sumZ);
                    Log.i("False Reached6",averageX+" " +averageY + " "+ averageZ);
                    two = averageValues;
                    Log.i("False Reached7",averageValues);
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
                    output = ""+simpleDateFormat.format(new Date())+", " +location.getLatitude() +", "+ location.getLongitude() +", " +location.getAltitude() +", "+String.format( "%.2f" ,max()) +", "+address.get(0).getAddressLine(0)  ;
                    locationList[0] = first;
                    printIteration++;
                    firstRun = false;
                    reset = true;
                    iteration = 1;
                    sumX = 0;
                    sumX = 0;
                    sumZ = 0;
                    averageX = 0;
                    averageY = 0;
                    averageZ = 0;
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
                                "\n\n Average Max Absolute Acceleration: \n"+String.format( "%.2f" ,max())  ;
                        one = first+ "\n\nDistance Range: " + distanceRange + " m";
                        text.setText("Current Location\n\n"+first+ "\n\nDistance Range: " + distanceRange + " m");
                        text3.setText("Distance: " + String.format("%.2f ",distance) +" m");
                        output = output +"\n"+simpleDateFormat.format(new Date())+", " +location.getLatitude() +", "+ location.getLongitude() +", " +location.getAltitude()+ ", " + String.format( "%.2f" ,max()) +", "+address.get(0).getAddressLine(0) ;
                        reset = true;
                        float empty = max();
                        firstRun = false;
                        distance = 0;
                        three = "Distance: " + distance +" m" ;
                        iteration = 1;
                        resets();
                        mSensorManager.unregisterListener(listener2);
                        mSensorManager.registerListener(listener2, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);

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
                        text3.setText("Distance: " + String.format("%.2f ",distance) +" m");
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

    public void resets()
    {
        this.averageX = 0;
        this.averageY = 0;
        this.averageZ =0;
        this.sumX = 0;
        this.sumY = 0;
        this.sumZ = 0;
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


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            // Permission has already been granted
            trigger2 = true;
        }
        ;
        if (trigger) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            // manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            mSensorManager.registerListener(listener2, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
        }
        iteration = 1;
        sumX = 0;
        sumX = 0;
        sumZ = 0;
        averageX = 0;
        averageY = 0;
        averageZ = 0;
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        manager.removeUpdates(listener);
        mSensorManager.unregisterListener(listener2);
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState)
//    {
//
//
//        super.onSaveInstanceState(outState);
//        outState.putString("one",one);
//        outState.putString("two", two);
//        outState.putString("three", three);
//        outState.putString("four", four);
//        outState.putFloat("sumX",sumX);
//        outState.putFloat("sumY",sumY);
//        outState.putFloat("sumZ",sumZ);
//        outState.putFloat("averageX",averageX);
//        outState.putFloat("averageY",averageY);
//        outState.putFloat("averageZ", averageZ);
//        // outState.putString(locationList,"LocationList");
//
//    }
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState)
//    {
//
//        super.onRestoreInstanceState(savedInstanceState);
//        one = savedInstanceState.getString("one");
//        two = savedInstanceState.getString("two");
//        three = savedInstanceState.getString("three");
//        four = savedInstanceState.getString("four");
//        sumX = savedInstanceState.getFloat("sumX");
//        sumY= savedInstanceState.getFloat("sumY");
//        sumZ = savedInstanceState.getFloat("sumZ");
//        averageX = savedInstanceState.getFloat("averageX");
//        averageY = savedInstanceState.getFloat("averageY");
//        averageZ = savedInstanceState.getFloat("averageZ");
//
//
//
//    }
    public double distanceFormula(double x1, double x2, double y1, double y2) {
        double distance = 0;
        distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    public String average(float numberX, float numberY, float numberZ, int iteration) {
        if(reset)
        {
            sumX = 0;
            sumY = 0;
            sumZ = 0;
            averageX = 0;
            averageY = 0;
            averageZ =0;
        }
        sumX = sumX + numberX;
        sumY = sumY + numberY;
        sumZ = sumZ + numberZ;
        averageX = sumX / iteration;
        averageY = sumY / iteration;
        averageZ = sumZ / iteration;
//        if(reset)
//        Toast.makeText(MainActivity.this, "Sum of Acelerometer\n\n"+String.format("X: %.2f m/s^2" , sumX )+ String.format("\nY: %.2f m/s^2" , sumY )+ String.format("\nZ: %.2f m/s^2" , sumZ), Toast.LENGTH_LONG).show();
        return "Averages of Acelerometer\n\n"+String.format("X: %.2f m/s^2" , averageX )+ String.format("\nY: %.2f m/s^2" , averageY )+ String.format("\nZ: %.2f m/s^2" , averageZ );
    }
    public float max()
    {
        if(reset)
        {
            averageX = 0;
            averageY = 0;
            averageY = 0;
            averageZ =0;
        }
        float a = Math.max(Math.abs(averageX),Math.abs(averageY));
        float b = Math.max(Math.abs(averageZ),a);
        return b;
    }
//    private void writeToFile(String data) {
//
//            File file = new File("/LocationData", "text");
//            if (!file.exists()) {
//                Log.i("HELLO WORLD", ""+file);
//                file.mkdir();
//            }
//            try {
//                File gpxfile = new File(file, "sample.csv");
//                Log.i("HELLO WORLD2 " ,""+gpxfile);
//                FileWriter writer = new FileWriter(gpxfile);
//                writer.append(data);
//                writer.flush();
//                writer.close();
//                Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
//            } catch (Exception e) { }
//    }
}
