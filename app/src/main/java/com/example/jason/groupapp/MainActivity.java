package com.example.jason.groupapp;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.qozix.tileview.TileView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "fab button pressed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TileView tileView = (TileView)findViewById(R.id.view);
        tileView.setSize(2000, 1466);  // the original size of the untiled image
        tileView.addDetailLevel(1f, "tile-%d-%d.png");
        //setContentView(tileView);
        tileView.defineBounds(0, 0, 1, 1);

        ImageView imageView = new ImageView( this );
        imageView.setImageResource(R.drawable.ic_menu_manage);
        tileView.addMarker(imageView, 0.25, 0.75, null, null);

        ImageView imageView2 = new ImageView( this );
        imageView2.setImageResource(R.drawable.ic_menu_send);
        tileView.addMarker(imageView2, 0.35, 0.76, null, null);

        ArrayList<double[]> points = new ArrayList<>();

        {
            points.add( new double[] {0.25, 0.75} );
            points.add( new double[] {0.35, 0.76} );
        }
        Paint paint = tileView.getDefaultPathPaint();
        tileView.drawPath( points.subList(0, 1), paint );

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            Toast.makeText(MainActivity.this, "nav item 1 selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item2) {
            Toast.makeText(MainActivity.this, "nav item 2 selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item3) {
            Toast.makeText(MainActivity.this, "nav item 3 selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item4) {
            Toast.makeText(MainActivity.this, "nav item 4 selected", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
