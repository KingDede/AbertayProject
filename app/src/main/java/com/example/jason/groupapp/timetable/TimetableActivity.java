package com.example.jason.groupapp.timetable;

import android.os.Bundle;
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

public class TimetableActivity extends AppCompatActivity {

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
        final TextView textURL = (TextView)findViewById(R.id.input_link);

        /* ==========================================
         *      Initialisation of basic controls
         * ==========================================
         */
        // ---------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ---------- FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence URL = textURL.getText();
                if (URL.length() > 0) {
                    if (!URL.toString().startsWith("webcal://")) {
                        File fileDir = getApplicationContext().getCacheDir();
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        try {
                            File calendar = new DownloadTask(fileDir, progressBar).execute("http://"+URL).get();
                            new RegexTask( getApplicationContext(), progressBar ).execute(calendar);
                            finish();
                        } catch (InterruptedException e) {
                            Log.e("Tasks", e.getMessage());
                        } catch (ExecutionException e) {
                            Log.e("Tasks", e.getMessage());
                        }
                    } else {
                        Snackbar.make(view, "Please remove the \"webcal://\" at the start of your link!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });
        // ---------- Back action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
