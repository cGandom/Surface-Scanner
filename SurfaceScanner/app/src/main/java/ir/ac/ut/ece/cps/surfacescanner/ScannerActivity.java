package ir.ac.ut.ece.cps.surfacescanner;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.ac.ut.ece.cps.surfacescanner.models.SurfaceScanner;

public class ScannerActivity extends AppCompatActivity {

    private boolean isScanning = false;
    private SurfaceScanner surfaceScanner;
    float chartYaxisRange = 30f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        this.getSupportActionBar().hide();

        surfaceScanner = new SurfaceScanner(this);

        setupChart();

        PlayPauseView playPauseView = findViewById(R.id.triggerScanBtn);
        playPauseView.fadeIn();
        playPauseView.setOnClickListener(v -> {
            vibrate();
            if (!isScanning) {
                playPauseView.setState(PlayPauseView.STATE_PLAY);
                startScanning();
            }
            else {
                playPauseView.setState(PlayPauseView.STATE_PAUSE);
                stopScanning();
            }
            isScanning = !isScanning;
        });
    }

    private void setupChart() {
        LineChart chart = findViewById(R.id.resultGraph);
        chart.getDescription().setEnabled(false);

        chart.setNoDataText(getString(R.string.chart_empty));
        chart.setGridBackgroundColor(Color.WHITE);
        chart.setNoDataTextColor(Color.WHITE);
        chart.setBorderColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setGridColor(Color.WHITE);
        chart.getXAxis().setAxisLineColor(Color.WHITE);

        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setGridColor(Color.WHITE);
        chart.getAxisLeft().setAxisLineColor(Color.WHITE);
        chart.getAxisLeft().setZeroLineColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getAxisRight().setGridColor(Color.WHITE);
        chart.getAxisRight().setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setZeroLineColor(Color.WHITE);

        chart.getLegend().setTextColor(Color.WHITE);

        chart.setDrawingCacheBackgroundColor(Color.WHITE);

        chart.getAxisLeft().setAxisMinimum(-chartYaxisRange);
        chart.getAxisLeft().setAxisMaximum(chartYaxisRange);
        chart.getAxisRight().setAxisMinimum(-chartYaxisRange);
        chart.getAxisRight().setAxisMaximum(chartYaxisRange);
    }

    private void startScanning() {
        Toast.makeText(getBaseContext(), R.string.scan_started, Toast.LENGTH_SHORT).show();
        surfaceScanner.startScan();
    }

    private void stopScanning() {
        Toast.makeText(getBaseContext(), R.string.scan_stopped, Toast.LENGTH_SHORT).show();
        ArrayList<Pair<Double, Double>> positions = surfaceScanner.stopScan();
        displayGraph(positions);
    }

    private void displayGraph(ArrayList<Pair<Double, Double>> positions) {
        LineChart chart = findViewById(R.id.resultGraph);
        float extendedChartYaxisRange = chartYaxisRange;

        List<Entry> entries = new ArrayList<Entry>();
        for (Pair<Double, Double> position : positions) {
            entries.add(new Entry(position.first.floatValue(), position.second.floatValue()));

            if (Math.abs(position.second) >= extendedChartYaxisRange)
                extendedChartYaxisRange = (float) (1.2 * Math.abs(position.second));
        }
        chart.getAxisLeft().setAxisMinimum(-extendedChartYaxisRange);
        chart.getAxisLeft().setAxisMaximum(extendedChartYaxisRange);
        chart.getAxisRight().setAxisMinimum(-extendedChartYaxisRange);
        chart.getAxisRight().setAxisMaximum(extendedChartYaxisRange);

        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataSet = new LineDataSet(entries, "Unevenness");
        dataSet.setColor(Color.CYAN);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(3.0f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.animateX(500);
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(50);
        }
    }


}