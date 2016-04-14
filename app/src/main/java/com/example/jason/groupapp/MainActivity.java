package com.example.jason.groupapp;

import android.content.Intent;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.groupapp.timetable.TimetableActivity;
import com.qozix.tileview.TileView;
import com.qozix.tileview.paths.CompositePathView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final int ITINERARY_REQUEST = 42;

    private TileView tileView1, tileView2, tileView3, tileView4, tileView5;

    private Button btnLevel1, btnLevel2 , btnLevel3, btnLevel4, btnLevel5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* ==========================================
         *      Creation
         * ==========================================
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ==========================================
         *      Initialisation of basic controls
         * ==========================================
         */
        // ---------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ---------- FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // ---------- Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* ==========================================
         *      Image set-up
         * ==========================================
         */
        // ---------- Creation
        tileView1 = (TileView)findViewById(R.id.view1);
        tileView2 = (TileView)findViewById(R.id.view2);
        tileView3 = (TileView)findViewById(R.id.view3);
        tileView4 = (TileView)findViewById(R.id.view4);
        tileView5 = (TileView)findViewById(R.id.view5);
        // ---------- Settings
        //Set the size of the original image
        tileView1.setSize(2915, 2100);
        tileView2.setSize(2915, 2100);
        tileView3.setSize(2915, 2100);
        tileView4.setSize(2915, 2100);
        tileView5.setSize(2915, 2100);
        //assign the tile sets to appropriate tileview
        tileView1.addDetailLevel(1f, "abertaylevel1/%d-%d.png", 485, 350);
        tileView2.addDetailLevel(1f, "abertaylevel2/%d-%d.png", 485, 350);
        tileView3.addDetailLevel(1f, "abertaylevel3/%d-%d.png", 485, 350);
        tileView4.addDetailLevel(1f, "abertaylevel4/%d-%d.png", 485, 350);
        tileView5.addDetailLevel(1f, "abertaylevel5/%d-%d.png", 485, 350);
        // Set the bounds for each tileview for placing the paths/markers
        tileView1.defineBounds(0, 0, 2915, 2100);
        tileView2.defineBounds(0, 0, 2915, 2100);
        tileView3.defineBounds(0, 0, 2915, 2100);
        tileView4.defineBounds(0, 0, 2915, 2100);
        tileView5.defineBounds(0, 0, 2915, 2100);
        // ---------- End of image set-up


        /* ==========================================
         *      Buttons set-up
         * ==========================================
         */
        // ---------- Creation
        btnLevel1 = (Button) findViewById(R.id.button1);
        btnLevel2 = (Button) findViewById(R.id.button2);
        btnLevel3 = (Button) findViewById(R.id.button3);
        btnLevel4 = (Button) findViewById(R.id.button4);
        btnLevel5 = (Button) findViewById(R.id.button5);
        // ---------- Adding listeners
        btnLevel1.setOnClickListener( this );
        btnLevel2.setOnClickListener( this );
        btnLevel3.setOnClickListener( this );
        btnLevel4.setOnClickListener( this );
        btnLevel5.setOnClickListener( this );
        // ---------- End of buttons set-up

        /* ==========================================
         *      Nodes set-up
         * ==========================================
         */
        NodePath nPath = new NodePath(this.getApplicationContext());
        List<String> rooms = nPath.getAllRooms();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_singlechoice, rooms);

        /* ==========================================
         *      Auto complete panel set-up
         * ==========================================
         */
        // ---------- Creation
        final AutoCompleteTextView textStart = (AutoCompleteTextView) findViewById(R.id.textStart);
        final AutoCompleteTextView textEnd = (AutoCompleteTextView) findViewById(R.id.textEnd);
        // ---------- Initialisation
        textStart.setThreshold(1);
        textEnd.setThreshold(1);
        textStart.setAdapter(adapter);
        textEnd.setAdapter(adapter);
        // ---------- Launch ( with floating action button )
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "fab button pressed", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                textStart.setText("");
                textEnd.setText("");
                textStart.setVisibility(View.VISIBLE);
                textEnd.setVisibility(View.GONE);
            }
        });
        // ---------- Reaction ( with text editor listener )
        textStart.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textStart.setVisibility(View.GONE);
                    textEnd.setVisibility(View.VISIBLE);
                    textEnd.requestFocus();
                    return true;
                }
                return false;
            }
        });
        textEnd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textStart.setVisibility(View.GONE);
                    textEnd.setVisibility(View.GONE);
                    //Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.blue_empty);
                    ImageView imageView2 = new ImageView(getApplicationContext());
                    imageView2.setImageResource(R.drawable.red_empty);

                    List<double[]> points = drawPath(textStart.getText().toString(), textEnd.getText().toString());

                    tileView4.addMarker(imageView, points.get(0)[0], points.get(0)[1], -0.5f, -1.0f);
                    tileView4.addMarker(imageView2, points.get(1)[0], points.get(1)[1], -0.5f, -1.0f);
                    tileView4.drawPath(points, null);
                    return true;
                }
                return false;
            }
        });
        // ---------- End of auto complete panel set-up


        /* ==========================================
         *      Pin markers set-up
         * ==========================================
         */
        ImageView imageView = new ImageView( this );
        imageView.setImageResource(R.drawable.blue_empty);
        tileView1.addMarker(imageView, 620, 1815, -0.5f, -1.0f);

        ImageView imageView2 = new ImageView( this );
        imageView2.setImageResource(R.drawable.red_empty);
        tileView1.addMarker(imageView2, 1195, 1530, -0.5f, -1.0f);

        /* ==========================================
         *      TODO
         *      I'm lost there
         * ==========================================
         */
//        ArrayList<double[]> points = new ArrayList<>();
//
//        {
//            points.add( new double[] {620, 1815} );
//            points.add( new double[] {1195, 1815} );
//            points.add( new double[] {1195, 1530} );
//        }
        Paint paint = tileView1.getDefaultPathPaint();
        // get metrics for programmatic DP
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // dress up the path effects and draw it between some points
        paint.setShadowLayer(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
                0x66000000
        );
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics));
        paint.setPathEffect(
                new CornerPathEffect(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics)
                )
        );
        //tileView1.drawPath(points, null);

//        Path myPath = new Path();
//        myPath.moveTo(0.25f, 0.75f);
//        myPath.lineTo(0.35f, 0.76f);
//        CompositePathView.DrawablePath drawablePath = new CompositePathView.DrawablePath();
//        drawablePath.path = myPath;
//        //drawablePath.paint = paint;
//        tileView.drawPath(drawablePath);

    }

    /*
        TODO
        fix search returning incorrect room when names are similar
        e.g searching using 4501 as destination puts a marker on 4501.4
     */
    public List<double[]> drawPath (String start, String end)
    {
        Toast.makeText(MainActivity.this, "Start = " + start + " End = " + end , Toast.LENGTH_SHORT).show();
        NodePath nPath = new NodePath(this.getApplicationContext());
        String[] startInfo = new String[3];
        String[] endInfo = new String[3];
        List<String[]> floor4Rooms = nPath.parseCSV("floor_4_rooms");

        for (String[] curVal : floor4Rooms) {
            if (curVal[0].contains(start)) {
                startInfo[0] = curVal[0];
                startInfo[1] = curVal[1];
                startInfo[2] = curVal[2];
            }
            else if (curVal[0].contains(end)) {
                endInfo[0] = curVal[0];
                endInfo[1] = curVal[1];
                endInfo[2] = curVal[2];
            }
        }
        Double startPosX = Double.parseDouble(startInfo[1]);
        Double startPosY = Double.parseDouble(startInfo[2]);
        Double endPosX = Double.parseDouble(endInfo[1]);
        Double endPosY = Double.parseDouble(endInfo[2]);


        List<double[]> points = new ArrayList<>();
        {
            points.add( new double[] {startPosX, startPosY} );
            points.add(new double[]{endPosX, endPosY});
        }
        //points.addAll(nPath.generatePath(startPosX, startPosY, endPosX, endPosY));

        return points;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ItineraryParametersActivity.class);
            startActivityForResult(intent, ITINERARY_REQUEST );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch ( id ) {
            case R.id.nav_item_sync:
                // ---------- Start activity for downloading timetable
                Intent intent = new Intent(this, TimetableActivity.class);
                startActivity( intent );
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        int buttonId = v.getId();
        switch (buttonId) {
            case R.id.button1:
                displayFloor(1);
                break;
            case R.id.button2:
                displayFloor(2);
                break;
            case R.id.button3:
                displayFloor(3);
                break;
            case R.id.button4:
                displayFloor(4);
                break;
            case R.id.button5:
                displayFloor(5);
                break;
            default:
                break;

        }

    }

    private void displayFloor( int number ) {
        tileView1.setVisibility(View.GONE);
        tileView2.setVisibility(View.GONE);
        tileView3.setVisibility(View.GONE);
        tileView4.setVisibility(View.GONE);
        tileView5.setVisibility(View.GONE);
        switch (number) {
            case 1:
                tileView1.setVisibility(View.VISIBLE);
                break;
            case 2:
                tileView2.setVisibility(View.VISIBLE);
                break;
            case 3:
                tileView3.setVisibility(View.VISIBLE);
                break;
            case 4:
                tileView4.setVisibility(View.VISIBLE);
                break;
            case 5:
                tileView5.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ITINERARY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String location = res.getString("loc");
                    String destination = res.getString("dest");
                    Log.d( "Result", "location: " + location + " - destination: " + destination );
                }
                break;
        }
    }


}
