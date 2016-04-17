package com.example.jason.groupapp.timetable;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jason.groupapp.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class TimetableActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textURL;
    private FloatingActionButton fab;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* ==========================================
         *      Creation
         * ==========================================
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        /* ==========================================
         *      Attributes set-up
         * ==========================================
         */
        // ---------- Initialisation
        textURL = (TextView)findViewById(R.id.input_link);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // ---------- Visibility
        fab.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        /* ==========================================
         *      Initialisation of basic controls
         * ==========================================
         */
        // ---------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ---------- FloatingActionButton
        fab.setOnClickListener(this);
        // ---------- Back action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {

        if ( v.getId() == R.id.fab ) {
            CharSequence URL = textURL.getText();
            if (URL.length() > 0) {
                if (!URL.toString().startsWith("webcal://")) {
                    File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//getApplicationContext().getExternalFilesDir( null );//getCacheDir();
                    Log.e("Calendar", fileDir.toString());
                    try {
                        fab.setVisibility(View.GONE);
                        new DownloadTask(v.getContext(), fileDir, progressBar).execute("http://" + URL).get();
                    } catch (InterruptedException e) {
                        Log.e("Tasks", e.getMessage());
                    } catch (ExecutionException e) {
                        Log.e("Tasks", e.getMessage());
                    }
                } else {
                    Snackbar.make(v, "Please remove the \"webcal://\" at the start of your link!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    }
}
