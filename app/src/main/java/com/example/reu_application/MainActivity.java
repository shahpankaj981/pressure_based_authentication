package com.example.reu_application;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
//import  weka.classifiers.;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.experiment.InstanceQuery;
import weka.filters.unsupervised.attribute.Remove;

public class MainActivity extends AppCompatActivity {
    private VelocityTracker velTracker = null;

    //training data storage
    String[] actions = new String[1000];
    double[] pressures = new double[1000];
    long[] timeStamp = new long[1000];
    long[] endTimes = new long[1000];
    long[] durationTimes = new long[1000];
    int[] coordX = new int[1000];
    int[] coordY = new int[1000];
    double[] fingerSizes = new double[1000];
    double[] velocityXs = new double[1000];
    double[] velocityYs = new double[1000];
    int[] touchIndices = new int[1000];
    Classifier classifier = null;
    TextView swipe = null;

    int touchIndex = 0;

    //test data storage
    double[] DURATIONS_STORAGE = new double[20];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout myLayout = findViewById(R.id.main_layout);
        swipe = findViewById(R.id.swipeNumberText);

        myLayout.setOnTouchListener(new View.OnTouchListener() {

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
                        actions[moveIndex] = "Down";

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
                        timeStamp[moveIndex] = System.currentTimeMillis();
                        System.out.println("Start Time: " + startTime);

                        //pressure
                        currPressure = event.getPressure(event.getActionIndex());
                        pressures[moveIndex] = currPressure;
                        System.out.println("Pressure Action Down: " + currPressure);

                        //Count
                        touchIndices[moveIndex] = touchIndex;
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
                        actions[moveIndex] = "Move";

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
                        timeStamp[moveIndex] = System.currentTimeMillis();
                        System.out.println("Start Time: " + startTime);

                        //Count
                        touchIndices[moveIndex] = touchIndex;
                        moveIndex++;

                        break;
                    case MotionEvent.ACTION_UP:
                        //Action
                        actions[moveIndex] = "Up";

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

                        timeStamp[moveIndex] = System.currentTimeMillis();
//                        durationTimes[touchIndex] = durationTime / 1000000;
//                        endTimes[touchIndex] = (durationTime + startTime) / 1000000;
//                        System.out.println("Duration of press: " + (durationTime / 1000000) + " Milliseconds");
//
//                        //Test Data Calls
//                        DURATIONS_STORAGE[testCount] = (durationTime / 1000000);



                        endTime = System.currentTimeMillis();
                        durationTime = endTime - startTime;
                        endTimes[moveIndex] = endTime;
                        durationTimes[moveIndex] = durationTime;
                        System.out.println("Duration of press: " + durationTime);

                        //count
                        touchIndices[moveIndex] = touchIndex;

                        testCount++;
                        moveIndex++;
                        touchIndex++;


                        //Test Data Calls
                        DURATIONS_STORAGE[testCount] = durationTime;

                        swipe.setText("Swipes Completed: "+ testCount);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        velTracker.recycle();
                        break;
                }
                return true;
            }

        });
    }

    public void resetData()
    {
        //training data storage
        actions = new String[1000];
        pressures = new double[1000];
        timeStamp = new long[1000];
        endTimes = new long[1000];
        durationTimes = new long[1000];
        coordX = new int[1000];
        coordY = new int[1000];
        fingerSizes = new double[1000];
        velocityXs = new double[1000];
        velocityYs = new double[1000];
        touchIndices = new int[1000];
    }

    public void Export2CSV(View view){
        StringBuilder data = new StringBuilder();
        int count = 0;

        data.append("TouchIndex, MoveIndex, Action, timeStamp(Milliseconds), " +
                "endTime(Milliseconds), durationTime(Milliseconds), pressure, " +
                "coordinates, touchSize(pixels), Velocity(X), Velocity(Y)\n");

        while (true){
            System.out.println(count);
            data.append(touchIndices[count] + "," + count + "," + actions[count] + "," + timeStamp[count] + ","
                    + endTimes[count] + "," + durationTimes[count] + " ," + pressures[count]
                    + "," + "[" + coordX[count] + ":" + coordY[count] + "]" + "," + fingerSizes[count] + ","
                    + velocityXs[count] + "," + velocityYs[count] + "\n");
            count++;

            if (actions[count] == null){
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
        Log.e("model generation", "process started");
        StringBuilder data = new StringBuilder();
        data.append("P_MAX_1(pressure), P_MAX_2(pressure), P_MIN(pressure), DURATION(ms)," +
                " STD_DEV(pressure), P_MAX_1(fingerSize), P_MAX_2(fingerSize), P_MIN(fingerSize), " +
                "STD_DEV(fingerSize), P_MAX_1(coordinateXSample), P_MAX_2(coordinateXSample), P_MIN(coordinateXSample)," +
                "STD_DEV(coordinateXSample), P_MAX_1(coordinateYSample), P_MAX_2(coordinateYSample), P_MIN(coordinateYSample), " +
                "STD_DEV(coordinateYSample), P_MAX_1(velocityXSample), P_MAX_2(velocityXSample), P_MIN(velocityXSample), " +
                "STD_DEV(velocityXSample), P_MAX_1(velocityYSample), P_MAX_2(velocityYSample), P_MIN(velocityYSample), " +
                "STD_DEV(velocityYSample), Person\n");
        data.append("P_MAX_1(pressure), Person\n");

        int count = 0;
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
            if (actions[count] == "Down"){
                while (actions[count] != "Up"){
                    //Selecting isolated gestures or 'swipes'
                    pressureSample[i] = pressures[count];
                    fingerSizeSample[i] = fingerSizes[count];
                    coordinateXSample[i] = coordX[count];
                    coordinateYSample[i] = coordY[count];
                    velocityXSample[i] = velocityXs[count];
                    velocityYSample[i] = velocityYs[count];
                    count++;
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
                                StandardDeviation(velocityYSample) + ", Pankaj\n");

                i = 0;
            }
            count++;
            if (actions[count] == null){
                break;
            }
        }

        Context context = getApplicationContext();

        try {
            FileOutputStream out = openFileOutput("trainData.arff", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        ConverterUtils.DataSource source = null;
        FileInputStream fis = null;


        try{
            //Saving data to file
            FileOutputStream out = openFileOutput("testData.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //Exporting
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

    public void train(View view){
        //save data to arff file
        saveArffFile();

        //train model
        FileInputStream fis = null;
        try {
            fis = openFileInput("trainData.arff");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader breader = new BufferedReader(isr);

            Instances train = new Instances(breader);
            train.setClassIndex(train.numAttributes()-1);
            breader.close();

            LibSVM model = new LibSVM();
            String options = ( "-S 2 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1" );
            String[] optionsArray = options.split( " " );
            model.setOptions( optionsArray );

            model.buildClassifier(train);
            OutputStream os = openFileOutput("j48.model", Context.MODE_PRIVATE);
            ObjectOutputStream out2 = new ObjectOutputStream(os);
            out2.writeObject(model);
            out2.close();

            Evaluation eval = new Evaluation(train);
            eval.crossValidateModel(model, train, 2, new Random(1));

            System.out.println(eval.toSummaryString("\nResults\n========\n", true));
            System.out.println(eval.fMeasure(0) + " " + eval.precision(0) + " " + eval.recall(0));

            //load model
//            loadModel();
            swipe.setText("Training Completed. Please Swipe now to test.");
//            resetData();
            Intent intent = new Intent(this, Test.class);
            startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e("hello world", "Null");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        readDataArffFile();
    }

    public void saveArffFile()
    {
        //Call Methods
        Log.e("model generation", "process started");
        StringBuilder data = new StringBuilder();

        data.append("@RELATION gestures\n" +
                "\n" +
                "   @ATTRIBUTE pressure_max_1  NUMERIC\n" +
                "   @ATTRIBUTE pressure_max_2   NUMERIC\n" +
                "   @ATTRIBUTE class        {isPankaj}\n\n" +
                "@DATA\n");
        int count = 0;
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
            if (actions[count] == "Down"){
                while (actions[count] != "Up"){
                    //Selecting isolated gestures or 'swipes'
                    pressureSample[i] = pressures[count];
                    fingerSizeSample[i] = fingerSizes[count];
                    coordinateXSample[i] = coordX[count];
                    coordinateYSample[i] = coordY[count];
                    velocityXSample[i] = velocityXs[count];
                    velocityYSample[i] = velocityYs[count];
                    count++;
                    i++;
                }
                testCount++;
                data.append(Array_Max_1(pressureSample) +"," + Array_Max_2(pressureSample) +",isPankaj\n");
                i = 0;
            }
            count++;
            if (actions[count] == null){
                break;
            }
        }

        Context context = getApplicationContext();

        try {
            FileOutputStream out = openFileOutput("trainData.arff", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
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

    public void readDataArffFile()
    {
        FileInputStream fis2 = null;
        try {
            fis2 = openFileInput("trainData.arff");
            InputStreamReader isr = new InputStreamReader(fis2);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text;
            Log.e("reading", "from file");

            while((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            EditText mEditText = findViewById(R.id.editTextTextPersonName);
            mEditText.setText(sb.toString());
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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


    public void storeInDatabase() {
        SQLiteDatabase myDatabase = openOrCreateDatabase("reu_app", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS gestures(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, max_pressure_1 double , max_pressure_2 double, " +
                "min_pressure double, duration int, pressure_std_dev double, max_finger_size_1 double, max_finger_size_2 double, min_finger_size, " +
                "finger_size_std_dev double, max_velocityX_1 double, max_velocityX_2 double, min_velocityX double, velocityX_std_dev double, max_velocityY_1 double, max_velocityY_2 double, " +
                "min_velocityY double, velocityY_std_dev double);");

        int count = 0;
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
            if (actions[count] == "Down"){
                while (actions[count] != "Up"){
                    //Selecting isolated gestures or 'swipes'
                    pressureSample[i] = pressures[count];
                    fingerSizeSample[i] = fingerSizes[count];
                    coordinateXSample[i] = coordX[count];
                    coordinateYSample[i] = coordY[count];
                    velocityXSample[i] = velocityXs[count];
                    velocityYSample[i] = velocityYs[count];
                    count++;
                    i++;
                }
                testCount++;

                ContentValues values = new ContentValues();
                values.put("max_pressure_1", Array_Max_1(pressureSample));
                values.put("max_pressure_2", Array_Max_2(pressureSample));
                values.put("min_pressure", Array_Min(pressureSample));
                values.put("duration", DURATIONS_STORAGE[testCount]);
                values.put("pressure_std_dev", StandardDeviation(pressureSample));
                values.put("max_finger_size_1", Array_Max_1(fingerSizeSample));
                values.put("max_finger_size_2", Array_Max_2(fingerSizeSample));
                values.put("min_finger_size", Array_Min(fingerSizeSample));
                values.put("finger_size_std_dev", StandardDeviation(fingerSizeSample));
                values.put("max_velocityX_1", Array_Max_1(velocityXSample));
                values.put("max_velocityX_2", Array_Max_2(velocityXSample));
                values.put("min_velocityX", Array_Min(velocityXSample));
                values.put("velocityX_std_dev", StandardDeviation(velocityXSample));
                values.put("max_velocityY_1", Array_Max_1(velocityYSample));
                values.put("max_velocityY_2", Array_Max_2(velocityYSample));
                values.put("min_velocityY", Array_Min(velocityYSample));
                values.put("velocityY_std_dev", StandardDeviation(velocityYSample));

                long id = myDatabase.insert("gestures", null, values);
            }
            count++;
            if (actions[count] == null){
                break;
            }
        }
    }


//    public void ExportTestData(View view){
//        //Call Methods
//        Log.e("model generation", "process started");
//        StringBuilder data = new StringBuilder();
////        data.append("P_MAX_1(pressure), P_MAX_2(pressure), P_MIN(pressure), DURATION(ms)," +
////                " STD_DEV(pressure), P_MAX_1(fingerSize), P_MAX_2(fingerSize), P_MIN(fingerSize), " +
////                "STD_DEV(fingerSize), P_MAX_1(coordinateXSample), P_MAX_2(coordinateXSample), P_MIN(coordinateXSample)," +
////                "STD_DEV(coordinateXSample), P_MAX_1(coordinateYSample), P_MAX_2(coordinateYSample), P_MIN(coordinateYSample), " +
////                "STD_DEV(coordinateYSample), P_MAX_1(velocityXSample), P_MAX_2(velocityXSample), P_MIN(velocityXSample), " +
////                "STD_DEV(velocityXSample), P_MAX_1(velocityYSample), P_MAX_2(velocityYSample), P_MIN(velocityYSample), " +
////                "STD_DEV(velocityYSample), Person\n");
////        data.append("P_MAX_1(pressure), Person\n");
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
//                //Adding information to .csv document
////                data.append(Array_Max_1(pressureSample) + "," + Array_Max_2(pressureSample) + "," +
////                        Array_Min(pressureSample) + "," + DURATIONS_STORAGE[testCount] + "," +
////                        StandardDeviation(pressureSample) + "," + Array_Max_1(fingerSizeSample) + "," +
////                        Array_Max_2(fingerSizeSample) + "," + Array_Min(fingerSizeSample) + "," +
////                        StandardDeviation(fingerSizeSample) + "," + Array_Max_1(coordinateXSample) + "," +
////                                Array_Max_2(coordinateXSample) + "," + Array_Min(coordinateXSample) + "," +
////                                StandardDeviation(coordinateXSample) + "," + Array_Max_1(coordinateYSample) + "," +
////                                Array_Max_2(coordinateYSample) + "," + Array_Min(coordinateYSample) + "," +
////                                StandardDeviation(coordinateYSample) + "," + Array_Max_1(velocityXSample) + "," +
////                                Array_Max_2(velocityXSample) + "," + Array_Min(velocityXSample) + "," +
////                                StandardDeviation(velocityXSample)+ "," + Array_Max_1(velocityYSample) + "," +
////                                Array_Max_2(velocityYSample) + "," + Array_Min(velocityYSample) + "," +
////                                StandardDeviation(velocityYSample) + ", Pankaj\n");
//
////                data.append("0.8,Pankaj\n");
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
//            FileOutputStream out = openFileOutput("testData1.arff", Context.MODE_PRIVATE);
//            out.write(data.toString().getBytes());
//            out.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        ConverterUtils.DataSource source = null;
//        FileInputStream fis = null;
//
////        CSVLoader loader = new CSVLoader();
////        try {
////            loader.setSource(new File("testData.csv"));
////            Instances data2 = loader.getDataSet();
////            // save ARFF
////            ArffSaver saver = new ArffSaver();
////            saver.setInstances(data2);
////            saver.setFile(new File("testData.arff"));
////            saver.setDestination(new File("testData.arff"));
////            saver.writeBatch();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
//
//
//        try {
////            source = new ConverterUtils.DataSource("testDataa.csv");
////            Instances train = source.getDataSet();
////            if (train.classIndex() == -1) {
////                train.setClassIndex(train.numAttributes() - 1);
////            }
//
//
////            CSVLoader loader = new CSVLoader();
////            loader.setSource(new File("testData.arff"));
////            Instances data = loader.getDataSet();
//
//
//            fis = openFileInput("testData1.arff");
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader breader = new BufferedReader(isr);
//
//            Instances train = new Instances(breader);
//            train.setClassIndex(train.numAttributes()-1);
//            breader.close();
//
//            NaiveBayes nB = new NaiveBayes();
//            nB.buildClassifier(train);
//            Evaluation eval = new Evaluation(train);
//            eval.crossValidateModel(nB, train, 4, new Random(1));
//
//            System.out.println(eval.toSummaryString("\nResults\n========\n", true));
//            System.out.println(eval.fMeasure(1) + " " + eval.precision(1) + " " + eval.recall(1));
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
//
///*
//
////        ConverterUtils.DataSource source = null;
////        try {
////            source = new ConverterUtils.DataSource("testData.csv");
////            Instances dataset = source.getDataSet();
////            if (dataset.classIndex() == -1) {
////                dataset.setClassIndex(dataset.numAttributes() - 1);
////            }
////
////            // filter
////            Remove rm = new Remove();
////            rm.setAttributeIndices("1");  // remove 1st attribute
////            // classifier
////            J48 j48 = new J48();
////            j48.setUnpruned(true);        // using an unpruned J48
////            // meta-classifier
////            FilteredClassifier fc = new FilteredClassifier();
////            fc.setFilter(rm);
////            fc.setClassifier(j48);
////            // train and make predictions
////            fc.buildClassifier(dataset);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//*/
//
//        FileInputStream fis2 = null;
//
//        try {
//            fis2 = openFileInput("testData1.arff");
//            InputStreamReader isr = new InputStreamReader(fis2);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//
//            String text;
//            Log.e("reading", "from file");
//
//            while((text = br.readLine()) != null) {
//                sb.append(text).append("\n");
//            }
//
//            EditText mEditText = findViewById(R.id.editTextTextPersonName);
//            mEditText.setText(sb.toString());
//        }catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        try{
////            //Saving data to file
////            FileOutputStream out = openFileOutput("testData.csv", Context.MODE_PRIVATE);
////            out.write(data.toString().getBytes());
////            out.close();
////
////            //Exporting
////            File fileLocation = new File(getFilesDir(), "testData.csv");
////            Uri path = FileProvider.getUriForFile(context, "com.example.REU_Application.fileProvider", fileLocation);
////            Intent fileIntent = new Intent(Intent.ACTION_SEND);
////            fileIntent.setType("text/csv");
////            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "testData");
////            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
////            startActivity(fileIntent);
////        }
////        catch(Exception e){
////            e.printStackTrace();
////        }
//    }

//    public void format_data()
//    {
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
//                double maxPressure1 = Array_Max_1(pressureSample);
//                double maxPressure2 = Array_Max_2(pressureSample);
//                double pressureStdDev =
//                //Adding information to .csv document
//                data.append(Array_Max_1(pressureSample) + "," + Array_Max_2(pressureSample) + "," +
//                        Array_Min(pressureSample) + "," + DURATIONS_STORAGE[testCount] + "," +
//                        StandardDeviation(pressureSample) + "," + Array_Max_1(fingerSizeSample) + "," +
//                        Array_Max_2(fingerSizeSample) + "," + Array_Min(fingerSizeSample) + "," +
//                        StandardDeviation(fingerSizeSample) + "," + Array_Max_1(coordinateXSample) + "," +
//                        Array_Max_2(coordinateXSample) + "," + Array_Min(coordinateXSample) + "," +
//                        StandardDeviation(coordinateXSample) + "," + Array_Max_1(coordinateYSample) + "," +
//                        Array_Max_2(coordinateYSample) + "," + Array_Min(coordinateYSample) + "," +
//                        StandardDeviation(coordinateYSample) + "," + Array_Max_1(velocityXSample) + "," +
//                        Array_Max_2(velocityXSample) + "," + Array_Min(velocityXSample) + "," +
//                        StandardDeviation(velocityXSample)+ "," + Array_Max_1(velocityYSample) + "," +
//                        Array_Max_2(velocityYSample) + "," + Array_Min(velocityYSample) + "," +
//                        StandardDeviation(velocityYSample) + "\n");
//                i = 0;
//            }
//            count++;
//            if (actions[count] == null){
//                break;
//            }
//        }
//
//        try{
//            //Saving data to file
//            FileOutputStream out = openFileOutput("testData.csv", Context.MODE_PRIVATE);
//            out.write(data.toString().getBytes());
//            out.close();
//
//            //Exporting
//            Context context = getApplicationContext();
//            File fileLocation = new File(getFilesDir(), "testData.csv");
//            Uri path = FileProvider.getUriForFile(context, "com.example.REU_Application.fileProvider", fileLocation);
//            Intent fileIntent = new Intent(Intent.ACTION_SEND);
//            fileIntent.setType("text/csv");
//            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "testData");
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//            startActivity(fileIntent);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }


//    public void format_data()
//    {
//        List<ArrayList<Double>> formatted_pressures=new ArrayList<ArrayList<Double>>();
//        ArrayList<Double> pressure_list = new ArrayList<Double>();
//
//        List<ArrayList<Double>> formatted_finger_sizes = new ArrayList<ArrayList<Double>>();
//        ArrayList<Double> finger_sizes_list = new ArrayList<Double>();
//
//        ArrayList<Long> duration_times_list = new ArrayList<Long>();
//
//        List<ArrayList<Double>> formatted_velocityX = new ArrayList<ArrayList<Double>>();
//        ArrayList<Double> velocityX_list = new ArrayList<Double>();
//
//        List<ArrayList<Double>> formatted_velocityY = new ArrayList<ArrayList<Double>>();
//        ArrayList<Double> velocityY_list = new ArrayList<Double>();
//
//        int count = 0;
//        for (int i = 0; i< pressures.length; i++) {
//            Log.e("touch index", Integer.toString(touchIndices[i]));
//            if(touchIndices[i] != count) {
//                count++;
//                formatted_pressures.add(pressure_list);
//                formatted_finger_sizes.add(finger_sizes_list);
//                formatted_velocityX.add(velocityX_list);
//                formatted_velocityY.add(velocityY_list);
//
//                pressure_list = new ArrayList();
//            } else {
//                pressure_list.add(pressures[i]);
//                finger_sizes_list.add(fingerSizes[i]);
//                velocityX_list.add(velocityXs[i]);
//                velocityY_list.add(velocityYs[i]);
//            }
//            if (actions[i] == null){
//                break;
//            }
//        }
//
//        System.out.println("pressures list: "+ formatted_pressures);
//        System.out.println("finger_sizes_list list: "+ formatted_finger_sizes);
//        System.out.println("velocityX_list list: "+ formatted_velocityX);
//        System.out.println("velocityY_list list: "+ formatted_velocityY);
//        System.out.println("size: " + formatted_pressures.get(1));
//        storeData(formatted_pressures, formatted_finger_sizes, formatted_velocityX, formatted_velocityY);
//
//
////        Log.e("touch index", Integer.toString(touchIndices.length));
////        Log.e("actions", Integer.toString(actions.length));
////        Log.e("pressures index", Integer.toString(pressures.length));
////        Log.e("durationTimes index", Integer.toString(durationTimes.length));
////        Log.e("fingerSizes index", Integer.toString(fingerSizes.length));
////        Log.e("velocityXs index", Integer.toString(velocityXs.length));
//        return ;
//    }

//    public void storeData(List formatted_pressures, List formatted_finger_sizes, List formatted_velocityX, List formatted_velocityY) {
//        System.out.println("size: " + formatted_pressures.size());
//        for(int i=0; i<formatted_pressures.size(); i++) {
//            ArrayList pressures = (ArrayList) formatted_pressures.get(i);
//            double maxPressure1 = (double)Collections.max(pressures.subList(0, pressures.size()/2));
//            double maxPressure2 = (double)Collections.max(pressures.subList(pressures.size()/2, pressures.size()));
//
//            ArrayList fingerSizes = (ArrayList) formatted_finger_sizes.get(i);
//            double maxFingerSize1 = (double)Collections.max(fingerSizes.subList(0, fingerSizes.size()/2));
//            double maxFingerSize2 = (double)Collections.max(fingerSizes.subList(fingerSizes.size()/2, fingerSizes.size()));
//
//            ArrayList velocityXs = (ArrayList) formatted_velocityX.get(i);
//            double maxVelocityX1 = (double)Collections.max(velocityXs.subList(0, velocityXs.size()/2));
//            double maxVelocityX2 = (double)Collections.max(velocityXs.subList(velocityXs.size()/2, velocityXs.size()));
//
//            ArrayList velocityYs = (ArrayList) formatted_velocityY.get(i);
//            double maxVelocityY1 = (double)Collections.max(velocityYs.subList(0, velocityYs.size()/2));
//            double maxVelocityY2 = (double)Collections.max(velocityYs.subList(velocityYs.size()/2, velocityYs.size()));
//            System.out.println("max pressure1 = "+maxPressure1+ "max pressure2 = "+maxPressure2+"max maxFingerSize1 = "+maxFingerSize1+);
//        }
//
//    }



}