package com.example.jason.groupapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ItineraryParametersActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvLocation, tvDestination;
    private Button btnCancel, btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* ==========================================
         *      Creation
         * ==========================================
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_parameters);

        /* ==========================================
         *      Initialisation of basic controls
         * ==========================================
         */
        // ---------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* ==========================================
         *      Attributes set-up
         * ==========================================
         */
        // ---------- TextViews
        tvLocation = (TextView)findViewById(R.id.input_location);
        tvDestination = (TextView)findViewById(R.id.input_destination);
        // ---------- Buttons
        btnCancel = (Button)findViewById(R.id.button_cancel);
        btnOk = (Button)findViewById(R.id.button_ok);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        Intent intent = new Intent();
        switch (buttonId) {
            case R.id.button_cancel:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.button_ok:
                String location = tvLocation.getText().toString();
                String destination = tvDestination.getText().toString();
                if ( location.isEmpty() || destination.isEmpty() ) {
                    Snackbar.make(v, "Please fill both fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Bundle parameters = new Bundle();
                    parameters.putString("loc", location );
                    parameters.putString("dest", destination );
                    intent.putExtras(parameters);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
