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
        allRoomInfo.addAll(parseCSV("floor_1_rooms"));
        allRoomInfo.addAll(parseCSV("floor_5_rooms"));

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
        for (String[] curVal : nodeInfo) {
            double[] nodes = new double[3];
            nodes[0] = Double.parseDouble(curVal[1]);
            nodes[1] = Double.parseDouble(curVal[2]);
            paths4.add(nodes);
        }

        double finalNode[] = {0, 0};
        double shortDistEnd = Double.MAX_VALUE;
        for (double[] curVal : paths4) {
            double dist = (Math.sqrt(Math.pow(endPosX - curVal[0], 2) + Math.pow(endPosY - curVal[1], 2)));
            if (dist != 0 && dist < shortDistEnd){
                shortDistEnd = dist;
                finalNode[0] = curVal[0];
                finalNode[1] = curVal[1];
            }
            //Log.e("curval values", "Curval X: " + curVal[0] + " Y: " + curVal[1]);
            //Log.e("within for loop", "Active node X: "+ finalNode[0]+" Y: " + finalNode[1] + " Curval Node X: " + curVal[0] + " Y: " + curVal[1]+ " Distance: " +dist+ " ShortDist: " +shortDistEnd);
        }

        //Log.e("final node", "X: " + finalNode[0] + " Y: " + finalNode[1]);


        List<double[]> genPath = new ArrayList<>();
        Boolean path = Boolean.FALSE;
        double activeNode[] = {startPosX, startPosY};

        while (path != Boolean.TRUE)
        {
            Log.e("While loop starts", "------------");
            double curNode[] = {startPosX, startPosY};
            double shortDist = Double.MAX_VALUE;

            for (double[] curVal : paths4) {
                //Log.e("curval values", "Curval X: " + curVal[0] + " Y: " + curVal[1]);
                double dist = (Math.sqrt(Math.pow(activeNode[0] - curVal[0], 2) + Math.pow(activeNode[1] - curVal[1], 2)));
                if (dist != 0 && dist < shortDist && curVal[0] != activeNode[0] && curVal[1] != activeNode[1]){
                    shortDist = dist;
                    curNode[0] = curVal[0];
                    curNode[1] = curVal[1];
                }
                Log.e("within for loop",
                        "Active node X: "+ activeNode[0]+" Y: " + activeNode[1] +
                        " Curval Node X: " + curVal[0] + " Y: " + curVal[1]+
                        " Curnode Node X: " + curNode[0] + " Y: " + curNode[1]+
                        " Distance: " +dist+
                                " ShortDist: " +shortDist);
            }
            activeNode[0] = curNode[0];
            activeNode[1] = curNode[1];
            genPath.add(activeNode);

            if (activeNode[0] == finalNode[0] && activeNode[1] == finalNode[1]){
                Log.e("exit while loop", "Active node X: "+ activeNode[0]+" Y: " + activeNode[1] + "Target Node X: " + finalNode[0] + " Y: " + finalNode[1]);
                path = Boolean.TRUE;
            }
        }

        return genPath;

    }
}
