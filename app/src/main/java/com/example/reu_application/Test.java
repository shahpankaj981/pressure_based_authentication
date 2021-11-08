package com.example.reu_application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class Test extends AppCompatActivity {
//    private VelocityTracker velTracker = null;
//
//    //training data storage
//    String[] actions = new String[1000];
//    double[] pressures = new double[1000];
//    long[] timeStamp = new long[1000];
//    long[] endTimes = new long[1000];
//    long[] durationTimes = new long[1000];
//    int[] coordX = new int[1000];
//    int[] coordY = new int[1000];
//    double[] fingerSizes = new double[1000];
//    double[] velocityXs = new double[1000];
//    double[] velocityYs = new double[1000];
//    int[] touchIndices = new int[1000];
//    Classifier classifier = null;
//    TextView swipe = null;
//
//    int touchIndex = 0;
//
//    //test data storage
//    double[] DURATIONS_STORAGE = new double[20];
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test);
//
//        ConstraintLayout myLayout = findViewById(R.id.test_layout);
//        swipe = findViewById(R.id.swipeNumberText);
//
//        myLayout.setOnTouchListener(new View.OnTouchListener() {
//
//            //touch count
//            int moveIndex = 0;
//            int testCount = 0;
//
//            //finger size
//            double fingerSize = 0;
//
//            //Get coordinates
//            int x;
//            int y;
//
//            //Time
//            long startTime = 0;
//            long endTime = 0;
//            long durationTime;
//
//            //Pressure
//            double currPressure = 0;
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//
//                //Get coordinates
//                int[] coordinates = new int[2];
//                view.getLocationOnScreen(coordinates);
//
//                //Velocity
//                int index = event.getActionIndex();
//                int action = event.getActionMasked();
//                int pointer = event.getPointerId(index);
//                final int MAX_VELOCITY = 1000;
//                final int UNITS = 1;
//                double velocityX = 0;
//                double velocityY = 0;
//
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//
//
//                        //Size (finger dimensions)
//                        fingerSize = event.getSize();
//                        fingerSizes[moveIndex] = fingerSize;
//                        System.out.println("Finger size: " + fingerSize + " pixels");
//
//                        //Velocity
//                        if (velTracker == null){
//                            velTracker = VelocityTracker.obtain();
//                        }
//                        else{
//                            velTracker.clear();
//                        }
//                        velTracker.addMovement(event);
//
//                        //Action Check
//                        view.performClick();
//                        System.out.println("Touch Event: Action Down");
//                        actions[moveIndex] = "Down";
//
//                        //Location
//                        x = (int) event.getX();
//                        coordX[moveIndex] = x;
//                        y = (int) event.getY();
//                        coordY[moveIndex] = y;
//                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");
//
//                        //Time
////                        startTime = System.nanoTime();
////                        startTimes[touchID] = startTime / 1000000;
//                        startTime = System.currentTimeMillis();
//                        timeStamp[moveIndex] = System.currentTimeMillis();
//                        System.out.println("Start Time: " + startTime);
//
//                        //pressure
//                        currPressure = event.getPressure(event.getActionIndex());
//                        pressures[moveIndex] = currPressure;
//                        System.out.println("Pressure Action Down: " + currPressure);
//
//                        //Count
//                        touchIndices[moveIndex] = touchIndex;
//                        moveIndex++;
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//
//                        //Velocity
//                        velTracker.addMovement(event);
//
//                        //computeCurrentVelocity(units velocity will be in, MAX velocity)
//                        velTracker.computeCurrentVelocity(UNITS, MAX_VELOCITY);
//                        velocityX = velTracker.getXVelocity(pointer);
//                        velocityY = velTracker.getYVelocity(pointer);
//                        velocityXs[moveIndex] = velocityX;
//                        velocityYs[moveIndex] = velocityY;
//                        System.out.println("Velocity X: " + velocityX);
//                        System.out.println("Velocity Y: " + velocityY);
//
//                        //Action Check
//                        System.out.println("Touch Event: Action Move");
//                        actions[moveIndex] = "Move";
//
//                        //Size (finger dimensions)
//                        fingerSize = event.getSize();
//                        fingerSizes[moveIndex] = fingerSize;
//                        System.out.println("Finger size: " + fingerSize + " pixels");
//
//                        //Location
//                        x = (int) event.getX();
//                        coordX[moveIndex] = x;
//                        y = (int) event.getY();
//                        coordY[moveIndex] = y;
//                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");
//
//                        //pressure
//                        currPressure = event.getPressure(event.getActionIndex());
//                        System.out.println("Pressure Action Move: " + currPressure);
//                        pressures[moveIndex] = currPressure;
//
//                        //Start time/end time fill ins
////                        startTimes[touchID] = startTime / 1000000;
//                        timeStamp[moveIndex] = System.currentTimeMillis();
//                        System.out.println("Start Time: " + startTime);
//
//                        //Count
//                        touchIndices[moveIndex] = touchIndex;
//                        moveIndex++;
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        //Action
//                        actions[moveIndex] = "Up";
//
//                        //Location
//                        x = (int) event.getX();
//                        coordX[moveIndex] = x;
//                        y = (int) event.getY();
//                        coordY[moveIndex] = y;
//                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");
//
//                        //Action Check
//                        System.out.println("Touch Event: Action Up");
//
//                        //Time
//                        System.out.println("Start time check: " + startTime);
//
//                        timeStamp[moveIndex] = System.currentTimeMillis();
////                        durationTimes[touchIndex] = durationTime / 1000000;
////                        endTimes[touchIndex] = (durationTime + startTime) / 1000000;
////                        System.out.println("Duration of press: " + (durationTime / 1000000) + " Milliseconds");
////
////                        //Test Data Calls
////                        DURATIONS_STORAGE[testCount] = (durationTime / 1000000);
//
//
//
//                        endTime = System.currentTimeMillis();
//                        durationTime = endTime - startTime;
//                        endTimes[moveIndex] = endTime;
//                        durationTimes[moveIndex] = durationTime;
//                        System.out.println("Duration of press: " + durationTime);
//
//                        //count
//                        touchIndices[moveIndex] = touchIndex;
//
//                        testCount++;
//                        moveIndex++;
//                        touchIndex++;
//
//
//                        //Test Data Calls
//                        DURATIONS_STORAGE[testCount] = durationTime;
//
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        velTracker.recycle();
//                        break;
//                }
//                return true;
//            }
//
//        });
//
//        loadModel();
//    }
//
//    public void test(View view){
//        //save data to arff file
//        saveArffFile();
//
//        //test model
//        FileInputStream fis = null;
//        try {
//            fis = openFileInput("test_data.arff");
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader breader = new BufferedReader(isr);
//
//            Instances test = new Instances(breader);
//            test.setClassIndex(test.numAttributes()-1);
//            breader.close();
//
//            Evaluation eval = new Evaluation(test);
//            eval.crossValidateModel(classifier, test, 2, new Random(1));
//
//            System.out.println(eval.toSummaryString("\nTest Results\n=====================\n", true));
//            System.out.println(eval.fMeasure(1) + " " + eval.precision(1) + " " + eval.recall(1));
//
//            //load model
//            loadModel();
//            classifier.classifyInstance();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            Log.e("hello world", "Null");
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saveArffFile()
//    {
//        //Call Methods
//        Log.e("model generation", "process started");
//        StringBuilder data = new StringBuilder();
//
//        data.append("@RELATION gestures\n" +
//                "\n" +
//                "   @ATTRIBUTE pressure_max_1  NUMERIC\n" +
//                "   @ATTRIBUTE pressure_max_2   NUMERIC\n" +
//                "   @ATTRIBUTE class        {isPankaj,isNotPankaj}\n\n" +
//                "@DATA\n");
//        int count = 0;
//        int i = 0;
//        int testCount = 0;
//
//        //test data storage
//        double[] pressureSample = new double[150];
//        double[] fingerSizeSample = new double[150];
//        double[] coordinateXSample = new double[150];
//        double[] coordinateYSample = new double[150];
//        double[] velocityXSample = new double[150];
//        double[] velocityYSample = new double[150];
//
//        while (true){
//            if (actions[count] == "Down"){
//                while (actions[count] != "Up"){
//                    //Selecting isolated gestures or 'swipes'
//                    pressureSample[i] = pressures[count];
//                    fingerSizeSample[i] = fingerSizes[count];
//                    coordinateXSample[i] = coordX[count];
//                    coordinateYSample[i] = coordY[count];
//                    velocityXSample[i] = velocityXs[count];
//                    velocityYSample[i] = velocityYs[count];
//                    count++;
//                    i++;
//                }
//                testCount++;
//                data.append(Array_Max_1(pressureSample) +"," + Array_Max_2(pressureSample) +",isPankaj\n");
//                i = 0;
//            }
//            count++;
//            if (actions[count] == null){
//                break;
//            }
//        }
//
//        Context context = getApplicationContext();
//
//        try {
//            FileOutputStream out = openFileOutput("test_data.arff", Context.MODE_PRIVATE);
//            out.write(data.toString().getBytes());
//            out.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void loadModel()
//    {
//        InputStream is = null;
//        try {
//            is = openFileInput("j48.model");
//            ObjectInputStream is2 = new ObjectInputStream(is);
//            Object ob = is2.readObject();
//            is2.close();
//            System.out.println(is2.toString());
//            classifier = (Classifier) ob;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public double StandardDeviation(double[] array){
//        double sum = 0;
//        double STD_DEV = 0;
//        int arrayLength = array.length;
//
//        for (double num: array) {
//            sum += num;
//        }
//        double mean = sum/arrayLength;
//
//        for (double num: array){
//            STD_DEV += Math.pow(num - mean, 2);
//        }
//
//        STD_DEV = Math.sqrt(STD_DEV/arrayLength);
//
//        return STD_DEV;
//    }
//
//    public double Array_Max_1(double[] array){
//        double p_max_1 = array[0];
//
//        for (int i = 0; i < (array.length / 2); i++){
//            if (array[i] >= p_max_1){
//                p_max_1 = array[i];
//            }
//        }
//        return p_max_1;
//    }
//
//    public double Array_Max_2(double[] array){
//        double p_max_2 = array[0];
//
//        for (int i = (array.length / 2); i < array.length; i++){
//            if (array[i] >= p_max_2){
//                p_max_2 = array[i];
//            }
//        }
//        return p_max_2;
//    }
//
//    public double Array_Min(double[] array){
//        double p_min = array[0];
//
//        for (int i = 0; i < array.length; i++){
//            if (array[i] <= p_min && array[i] != 0){
//                p_min = array[i];
//            }
//        }
//        return p_min;
//    }


    ///////////single instance

    private VelocityTracker velTracker = null;

    //training data storage
    double[] pressures = new double[100];
    int[] coordX = new int[100];
    int[] coordY = new int[100];
    double[] fingerSizes = new double[100];
    double[] velocityXs = new double[100];
    double[] velocityYs = new double[100];

    Classifier classifier = null;
    TextView swipe = null;

    int touchIndex = 0;

    //test data storage
    double DURATIONS_STORAGE=0;
    Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ConstraintLayout rLayout = findViewById(R.id.test_layout);
        System.out.println("Constructor of test layout-------------------------------------------------");


        rLayout.setOnTouchListener(new View.OnTouchListener() {

            //touch count
            int moveIndex = 0;
            int testCount = 0;

            //finger size
            double fingerSize = 0;

            //Get coordinates
            int x;
            int y;

            //Time
            long startTime = 0;
            long endTime = 0;
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


                        //Size (finger dimensions)
                        fingerSize = event.getSize();
                        fingerSizes[moveIndex] = fingerSize;
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

                        //Location
                        x = (int) event.getX();
                        coordX[moveIndex] = x;
                        y = (int) event.getY();
                        coordY[moveIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //Time
//                        startTime = System.nanoTime();
//                        startTimes[touchID] = startTime / 1000000;
                        startTime = System.currentTimeMillis();
                        System.out.println("Start Time: " + startTime);

                        //pressure
                        currPressure = event.getPressure(event.getActionIndex());
                        pressures[moveIndex] = currPressure;
                        System.out.println("Pressure Action Down: " + currPressure);

                        moveIndex++;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Velocity
                        velTracker.addMovement(event);

                        //computeCurrentVelocity(units velocity will be in, MAX velocity)
                        velTracker.computeCurrentVelocity(UNITS, MAX_VELOCITY);
                        velocityX = velTracker.getXVelocity(pointer);
                        velocityY = velTracker.getYVelocity(pointer);
                        velocityXs[moveIndex] = velocityX;
                        velocityYs[moveIndex] = velocityY;
                        System.out.println("Velocity X: " + velocityX);
                        System.out.println("Velocity Y: " + velocityY);

                        //Action Check
                        System.out.println("Touch Event: Action Move");

                        //Size (finger dimensions)
                        fingerSize = event.getSize();
                        fingerSizes[moveIndex] = fingerSize;
                        System.out.println("Finger size: " + fingerSize + " pixels");

                        //Location
                        x = (int) event.getX();
                        coordX[moveIndex] = x;
                        y = (int) event.getY();
                        coordY[moveIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //pressure
                        currPressure = event.getPressure(event.getActionIndex());
                        System.out.println("Pressure Action Move: " + currPressure);
                        pressures[moveIndex] = currPressure;

                        //Start time/end time fill ins
//                        startTimes[touchID] = startTime / 1000000;
                        System.out.println("Start Time: " + startTime);

                        //Count
                        moveIndex++;

                        break;
                    case MotionEvent.ACTION_UP:
                        //Location
                        x = (int) event.getX();
                        coordX[moveIndex] = x;
                        y = (int) event.getY();
                        coordY[moveIndex] = y;
                        System.out.println("X and Y Coordinates: ( " + x + " , " + y + " )");

                        //Action Check
                        System.out.println("Touch Event: Action Up");

                        //Time
                        System.out.println("Start time check: " + startTime);

                        endTime = System.currentTimeMillis();
                        durationTime = endTime - startTime;
                        System.out.println("Duration of press: " + durationTime);

                        //count
                        moveIndex++;


                        //Test Data Calls
                        DURATIONS_STORAGE = durationTime;

                        System.out.println("pressures:" + pressures[0]);


                        break;
                    case MotionEvent.ACTION_CANCEL:
                        velTracker.recycle();
                        break;
                }
                return true;
            }

        });

        loadModel();
        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e("start", "validating");
                validateUser();
            }
        });
    }

    public void loadModel()
    {
        InputStream is = null;
        try {
            is = openFileInput("j48.model");
            ObjectInputStream is2 = new ObjectInputStream(is);
            Object ob = is2.readObject();
            is2.close();
            System.out.println(is2.toString());
            classifier = (Classifier) ob;
            System.out.println(classifier);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void validateUser()
    {
        System.out.println("Validate user here");
        Attribute pressureMax1 = new Attribute("pressure_max_1");
        Attribute pressureMax2 = new Attribute("pressure_max_2");
        Attribute aClass = new Attribute("class");

        FastVector fvWekaAttributes  = new FastVector(3);
        fvWekaAttributes.add(pressureMax1);
        fvWekaAttributes.add(pressureMax2);
        fvWekaAttributes.add(aClass);

        Instances testSet = new Instances("gestures", fvWekaAttributes,1);
        testSet.setClassIndex(2);

        Instance testExample = new DenseInstance(3);
        testExample.setValue(pressureMax1, Array_Max_1(pressures));
        testExample.setValue(pressureMax2, Array_Max_2(pressures));
        testSet.add(testExample);
        try {
            double prediction = classifier.classifyInstance(testSet.instance(0));

            System.out.println("prediction ============="+prediction);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
