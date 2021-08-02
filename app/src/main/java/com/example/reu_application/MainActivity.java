package com.example.reu_application;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity<CSVLoader> extends AppCompatActivity {

    private VelocityTracker velTracker = null;

    //training data storage
    String[] actions = new String[1000];
    double[] pressures = new double[1000];
    long[] startTimes = new long[1000];
    long[] endTimes = new long[1000];
    long[] durationTimes = new long[1000];
    int[] coordX = new int[1000];
    int[] coordY = new int[1000];
    double[] fingerSizes = new double[1000];
    double[] velocityXs = new double[1000];
    double[] velocityYs = new double[1000];

    //test data storage
    double[] DURATIONS_STORAGE = new double[20];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout myLayout = findViewById(R.id.main_layout);

        myLayout.setOnTouchListener(new View.OnTouchListener() {

            //touch count
            int touchID = 0;
            int touchIndex = 0;
            int testCount = 0;

            //finger size
            double fingerSize = 0;

            //Get coordinates
            int x;
            int y;

            //Time
            long startTime = 0;
            long durationTime;

            //Pressure
            double currPressure = 0;

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //Get coordinates
                int[] coordinates = new int[2];
                view.getLocationOnScreen(coordinates);

                //Velocity
                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointer = event.getPointerId(index);
                final int MAX_VELOCITY = 1000;
                final int UNITS = 1;
                double velocityX = 0;
                double velocityY = 0;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //Count
                        touchID++;
                        touchIndex++;

                        //Size (finger dimensions)
                        fingerSize = event.getSize();
                        fingerSizes[touchIndex] = fingerSize;
                        System.out.println("Finger size: " + fingerSize + " pixels");

                        //Velocity
                        if (velTracker == null){
                            velTracker = VelocityTracker.obtain();
                        }
                        else{
                            velTracker.clear();
                        }
                        velTracker.addMovement(event);

                        //Action Check
                        view.performClick();
                        System.out.println("Touch Event: Action Down");
                        actions[touchIndex] = "Down";

                        //Location
                        x = (int) event.getX();
                        coordX[touchIndex] = x;
                        y = (int) event.getY();
                        coordY[touchIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //Time
                        startTime = System.nanoTime();
                        startTimes[touchID] = startTime / 1000000;
                        System.out.println("Start Time: " + startTime);

                        //pressure
                        currPressure = event.getPressure(event.getActionIndex());
                        pressures[touchIndex] = currPressure;
                        System.out.println("Pressure Action Down: " + currPressure);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Count
                        touchID++;
                        touchIndex++;

                        //Velocity
                        velTracker.addMovement(event);

                        //computeCurrentVelocity(units velocity will be in, MAX velocity)
                        velTracker.computeCurrentVelocity(UNITS, MAX_VELOCITY);
                        velocityX = velTracker.getXVelocity(pointer);
                        velocityY = velTracker.getYVelocity(pointer);
                        velocityXs[touchIndex] = velocityX;
                        velocityYs[touchIndex] = velocityY;
                        System.out.println("Velocity X: " + velocityX);
                        System.out.println("Velocity Y: " + velocityY);

                        //Action Check
                        System.out.println("Touch Event: Action Move");
                        actions[touchIndex] = "Move";

                        //Size (finger dimensions)
                        fingerSize = event.getSize();
                        fingerSizes[touchIndex] = fingerSize;
                        System.out.println("Finger size: " + fingerSize + " pixels");

                        //Location
                        x = (int) event.getX();
                        coordX[touchIndex] = x;
                        y = (int) event.getY();
                        coordY[touchIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //pressure
                        currPressure = event.getPressure(event.getActionIndex());
                        System.out.println("Pressure Action Move: " + currPressure);
                        pressures[touchIndex] = currPressure;

                        //Start time/end time fill ins
                        startTimes[touchID] = startTime / 1000000;
                        System.out.println("Start Time: " + startTime);

                        break;
                    case MotionEvent.ACTION_UP:
                        //count
                        testCount++;
                        touchIndex++;
                        touchID++;

                        //Action
                        actions[touchIndex] = "Up";

                        //Location
                        x = (int) event.getX();
                        coordX[touchIndex] = x;
                        y = (int) event.getY();
                        coordY[touchIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //Action Check
                        System.out.println("Touch Event: Action Up");

                        //Time
                        System.out.println("Start time check: " + startTime);
                        durationTime = System.nanoTime() - startTime;
                        startTimes[touchIndex] = startTime;
                        durationTimes[touchIndex] = durationTime / 1000000;
                        endTimes[touchIndex] = (durationTime + startTime) / 1000000;
                        System.out.println("Duration of press: " + (durationTime / 1000000) + " Milliseconds");

                        //Test Data Calls
                        DURATIONS_STORAGE[testCount] = (durationTime / 1000000);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        velTracker.recycle();
                        break;
                }
                return true;
            }
        });
    }

    public void Export2CSV(View view){
        StringBuilder data = new StringBuilder();
        int touchID = 0;
        int touchIndex = 0;

        data.append("TouchID, TouchIndex, Action, startTime(Milliseconds), " +
                "endTime(Milliseconds), durationTime(Milliseconds), pressure, " +
                "coordinates, touchSize(pixels), Velocity(X), Velocity(Y)\n");

        while (true){
            data.append(touchID + "," + touchIndex + "," + actions[touchIndex] + "," + startTimes[touchIndex] + ","
                    + endTimes[touchIndex] + "," + durationTimes[touchIndex] + " ," + pressures[touchIndex]
                    + "," + "[" + coordX[touchIndex] + ":" + coordY[touchIndex] + "]" + "," + fingerSizes[touchIndex] + ","
                    + velocityXs[touchIndex] + "," + velocityYs[touchIndex] + "\n");
            touchID++;
            touchIndex++;
            if (actions[touchIndex] == null){
                break;
            }
        }

        try{
            //Saving data to file
            FileOutputStream out = openFileOutput("trainingData.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //Exporting
            Context context = getApplicationContext();
            File fileLocation = new File(getFilesDir(), "trainingData.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.REU_Application.fileProvider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "trainingData");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(fileIntent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void ExportTestData(View view){
        //Call Methods
        StringBuilder data = new StringBuilder();
        data.append("P_MAX_1(pressure), P_MAX_2(pressure), P_MIN(pressure), DURATION(ms)," +
                " STD_DEV(pressure), P_MAX_1(fingerSize), P_MAX_2(fingerSize), P_MIN(fingerSize), " +
                "STD_DEV(fingerSize), P_MAX_1(coordinateXSample), P_MAX_2(coordinateXSample), P_MIN(coordinateXSample)," +
                "STD_DEV(coordinateXSample), P_MAX_1(coordinateYSample), P_MAX_2(coordinateYSample), P_MIN(coordinateYSample), " +
                "STD_DEV(coordinateYSample), P_MAX_1(velocityXSample), P_MAX_2(velocityXSample), P_MIN(velocityXSample), " +
                "STD_DEV(velocityXSample), P_MAX_1(velocityYSample), P_MAX_2(velocityYSample), P_MIN(velocityYSample), " +
                "STD_DEV(velocityYSample)\n");
        int touchID = 0;
        int touchIndex = 0;
        int i = 0;
        int testCount = 0;

        //test data storage
        double[] pressureSample = new double[150];
        double[] fingerSizeSample = new double[150];
        double[] coordinateXSample = new double[150];
        double[] coordinateYSample = new double[150];
        double[] velocityXSample = new double[150];
        double[] velocityYSample = new double[150];

        while (true){
            if (actions[touchIndex] == "Down"){
                while (actions[touchIndex] != "Up"){
                    //Selecting isolated gestures or 'swipes'
                    pressureSample[i] = pressures[touchIndex];
                    fingerSizeSample[i] = fingerSizes[touchIndex];
                    coordinateXSample[i] = coordX[touchIndex];
                    coordinateYSample[i] = coordY[touchIndex];
                    velocityXSample[i] = velocityXs[touchIndex];
                    velocityYSample[i] = velocityYs[touchIndex];
                    touchID++;
                    touchIndex++;
                    i++;
                }
                testCount++;
                //Adding information to .csv document
                data.append(Array_Max_1(pressureSample) + "," + Array_Max_2(pressureSample) + "," +
                        Array_Min(pressureSample) + "," + DURATIONS_STORAGE[testCount] + "," +
                        StandardDeviation(pressureSample) + "," + Array_Max_1(fingerSizeSample) + "," +
                        Array_Max_2(fingerSizeSample) + "," + Array_Min(fingerSizeSample) + "," +
                        StandardDeviation(fingerSizeSample) + "," + Array_Max_1(coordinateXSample) + "," +
                                Array_Max_2(coordinateXSample) + "," + Array_Min(coordinateXSample) + "," +
                                StandardDeviation(coordinateXSample) + "," + Array_Max_1(coordinateYSample) + "," +
                                Array_Max_2(coordinateYSample) + "," + Array_Min(coordinateYSample) + "," +
                                StandardDeviation(coordinateYSample) + "," + Array_Max_1(velocityXSample) + "," +
                                Array_Max_2(velocityXSample) + "," + Array_Min(velocityXSample) + "," +
                                StandardDeviation(velocityXSample)+ "," + Array_Max_1(velocityYSample) + "," +
                                Array_Max_2(velocityYSample) + "," + Array_Min(velocityYSample) + "," +
                                StandardDeviation(velocityYSample) + "\n");
                i = 0;
            }
            touchID++;
            touchIndex++;
            if (actions[touchIndex] == null){
                break;
            }
        }

        try{
            //Saving data to file
            FileOutputStream out = openFileOutput("testData.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //Exporting
            Context context = getApplicationContext();
            File fileLocation = new File(getFilesDir(), "testData.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.REU_Application.fileProvider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "testData");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(fileIntent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public double StandardDeviation(double[] array){
        double sum = 0;
        double STD_DEV = 0;
        int arrayLength = array.length;

        for (double num: array) {
            sum += num;
        }
        double mean = sum/arrayLength;

        for (double num: array){
            STD_DEV += Math.pow(num - mean, 2);
        }

        STD_DEV = Math.sqrt(STD_DEV/arrayLength);

        return STD_DEV;
    }

    public double Array_Max_1(double[] array){
        double p_max_1 = array[0];

        for (int i = 0; i < (array.length / 2); i++){
            if (array[i] >= p_max_1){
                p_max_1 = array[i];
            }
        }
        return p_max_1;
    }

    public double Array_Max_2(double[] array){
        double p_max_2 = array[0];

        for (int i = (array.length / 2); i < array.length; i++){
            if (array[i] >= p_max_2){
                p_max_2 = array[i];
            }
        }
        return p_max_2;
    }

    public double Array_Min(double[] array){
        double p_min = array[0];

        for (int i = 0; i < array.length; i++){
            if (array[i] <= p_min && array[i] != 0){
                p_min = array[i];
            }
        }
        return p_min;
    }
}