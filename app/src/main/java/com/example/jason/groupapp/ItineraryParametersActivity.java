package com.example.jason.groupapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItineraryParametersActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView tvLocation, tvDestination;
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
        // ----------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* ==========================================
         *      Attributes set-up
         * ==========================================
         */
        // ---------- TextViews
        tvLocation = (AutoCompleteTextView)findViewById(R.id.input_location);
        tvDestination = (AutoCompleteTextView)findViewById(R.id.input_destination);
        // ---------- Buttons
        btnCancel = (Button)findViewById(R.id.button_cancel);
        btnOk = (Button)findViewById(R.id.button_ok);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        /* ==========================================
         *      Nodes set-up
         * ==========================================
         */
        NodePath nPath = new NodePath(this.getApplicationContext());
        List<String> rooms = nPath.getAllRooms();

        /* ==========================================
         *      Auto complete panel set-up
         * ==========================================
         */
        // ---------- Initialisation
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_singlechoice, rooms);
        tvLocation.setThreshold(1);
        tvDestination.setThreshold(1);
        tvLocation.setAdapter(adapter);
        tvDestination.setAdapter(adapter);
        // ---------- Reaction ( with text editor listener )

        // ---------- End of auto complete panel set-up
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
