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
        allRoomInfo.addAll(parseCSV("floor_4_rooms"));

        for (int i=0; i < allRoomInfo.size(); i++) {
            rooms.add(allRoomInfo.get(i)[0]);
        }

        return rooms;
    }

    public List<String[]> parseCSV (String fileName)
    {
        List<String[]> nodeList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open("nodes/" + fileName + ".csv");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                nodeList.add(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeList;
    }

    /*
        TODO
        this makes the app freeze maybe try using another thread or something
     */
    public List<double[]> generatePath (double startPosX, double startPosY, double endPosX, double endPosY)
    {
        List<String[]> nodeInfo = new ArrayList<>();
        nodeInfo.addAll(parseCSV("floor_4_paths"));
        List<double[]> paths4 = new ArrayList<>();
        double[] nodes = new double[3];
        for (String[] curVal : nodeInfo) {
            nodes[0] = Double.parseDouble(curVal[1]);
            nodes[1] = Double.parseDouble(curVal[2]);
            paths4.add(nodes);
        }

        List<double[]> genPath = new ArrayList<>();
        Boolean path = Boolean.FALSE;
        double activeNode[] = {startPosX, startPosY};

        while (path != Boolean.TRUE)
        {
            double curNode[] = {startPosX, startPosY};
            double shortDist = 100.0;

            for (double[] curVal : paths4) {
                double dist = (Math.sqrt(Math.pow(activeNode[0] - curVal[0], 2) + Math.pow(activeNode[1] - curVal[1], 2)));
                if (dist != 0 && dist < shortDist){
                    shortDist = dist;
                    curNode[0] = curVal[0];
                    curNode[1] = curVal[1];
                }
            }
            activeNode[0] = curNode[0];
            activeNode[1] = curNode[1];
            genPath.add(activeNode);

            if (activeNode[0] == endPosX && activeNode[1] == endPosY){
                path = Boolean.TRUE;
            }
        }

        return genPath;

    }
}
