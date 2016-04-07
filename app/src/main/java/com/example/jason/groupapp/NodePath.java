package com.example.jason.groupapp;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NodePath {
    protected Context context;

    public NodePath (Context context)
    {
        this.context = context.getApplicationContext();
    }

    public List<String> getAllRooms ()
    {
        List<String> rooms = new ArrayList<>();
        List<String[]> allRoomInfo = new ArrayList<>();
        allRoomInfo.addAll(parseRooms4());

        for (int i=0; i < allRoomInfo.size(); i++) {
            rooms.add(allRoomInfo.get(i)[0]);
        }

        return rooms;
    }

    public List<String[]> parseRooms4 ()
    {
        List<String[]> floor4Rooms = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open("nodes/floor_4_rooms.csv");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                floor4Rooms.add(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return floor4Rooms;
    }
}
