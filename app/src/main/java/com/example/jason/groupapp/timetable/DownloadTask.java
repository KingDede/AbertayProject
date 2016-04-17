package com.example.jason.groupapp.timetable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Cécile Journeaux on 11/04/2016.
 * Project created for Abertay University.
 * Contact details: cecile.journeaux@gmail.com
 */
public class DownloadTask extends AsyncTask<CharSequence, Integer, File> {

    private File fileDir;
    private File calendarFile;
    private final String filename = "calendar.ics";

    private Context context;
    private ProgressBar progressBar;

    public DownloadTask( Context context, File fileDir, ProgressBar progressBar ) {
        this.context = context;
        this.fileDir = fileDir;
        this.progressBar = progressBar;
    }

    /* ==========================================
     *      Task's lifecycle
     * ==========================================
     */
    @Override
    protected void onPreExecute() {
        Log.w( "Webcal", "Start download");
        calendarFile = new File( fileDir, filename );
        Log.d( "Webcal", "File location : " + calendarFile.getAbsolutePath());
        if ( calendarFile.exists() ) {
            calendarFile.delete();
        }
        try {
            calendarFile.createNewFile();
        } catch (IOException e) {
            Log.e("Webcal", e.getMessage());
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected File doInBackground(CharSequence... params) {

        int count;
        try {

            Log.w("Webcal", "Downloading");

            URL url = new URL(params[0].toString());
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            connection.connect();

            // getting file length
            int lenghtOfFile = connection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream to write file
            OutputStream output = new FileOutputStream(calendarFile);
            byte dataBuffer[] = new byte[1024];

            int total = 0;
            while ( (count = input.read(dataBuffer)) != -1 ) {
                // publishing progress
                total += count;
                Integer[] progressData = new Integer[ 2 ];
                progressData[0] = lenghtOfFile;
                progressData[1] = total;
                publishProgress( progressData );
                // writing data to file
                output.write(dataBuffer, 0, count);
            }
            Log.w("Webcal", "Task completed");
            return calendarFile;
        } catch (MalformedURLException e) {
            Log.e("Webcal", e.getMessage());
        } catch (IOException e) {
            Log.e("Webcal", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(File result) {
        Log.w("Webcal", "Downloaded");
        new RegexTask(context, progressBar).execute(calendarFile);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.w("Webcal", "Running..."+ values[1]);
        progressBar.setMax(values[0] * 2);
        progressBar.setProgress(values[1]);
    }

}
