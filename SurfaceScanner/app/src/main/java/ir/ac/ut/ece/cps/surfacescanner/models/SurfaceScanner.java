package ir.ac.ut.ece.cps.surfacescanner.models;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Pair;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

import ir.ac.ut.ece.cps.surfacescanner.ScannerActivity;

public class SurfaceScanner {

    private final Integer sampleRateMs = 20;

    private final Vector3 gyrLastValues = new Vector3(0, 0, 0);
    private final Vector3 accLastValues = new Vector3(0, 0, 0);
    public ArrayList<Pair<Double, Double>> positions = new ArrayList<>();
    public Double velocity;
    public Double angle;
    public TimerTask timerTask;

    public SurfaceScanner(ScannerActivity activity) {

        SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gyrLastValues.update(event.values[0], event.values[1], event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);


        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener accelerometerSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                accLastValues.update(event.values[0], event.values[1], event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(accelerometerSensorListener,
                accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startScan() {
        this.angle = 0.0;
        this.velocity = 0.0;
        this.accLastValues.clear();
        this.gyrLastValues.clear();
        this.positions.clear();
        this.positions.add(new Pair<>(0.0, 0.0));

        Timer timer = new Timer();

        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                calculatePosition();
            }
        };
        timer.schedule(this.timerTask, 0, this.sampleRateMs);
    }

    public ArrayList<Pair<Double, Double>> stopScan() {
        this.timerTask.cancel();
        return this.positions;
    }

    public void calculatePosition() {
        double sampleRateS = (double) this.sampleRateMs / 1000;

        double theta = (-this.gyrLastValues.getY() * sampleRateS) + this.angle;
        double newVelocity = this.accLastValues.getX() * sampleRateS + velocity;

        Pair<Double, Double> lastPosition = this.positions.get(this.positions.size() - 1);
        Pair<Double, Double> newPositions = new Pair<>(
            lastPosition.first + newVelocity * Math.cos(theta),
            lastPosition.second + newVelocity * Math.sin(theta)
        );
        this.positions.add(newPositions);
        this.velocity = newVelocity;
        this.angle = theta;
    }
}
