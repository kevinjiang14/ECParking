package com.example.kevin.ecparking;

import android.util.Log;

import java.io.File;
import java.util.Scanner;

/**
 * Created by Kevin Jiang on 10/15/17.
 */

public class Street {
    public Scanner file;
    public String inLat;
    public String endLat;
    public String inLon;
    public String endLon;
    public String ava;
    public String element;


    public void openFile() {
        try {
            file = new Scanner(new File("streetDB.txt"));
        }
        catch(Exception e) {
            Log.d("DEBUG","Unable to open file");
        }

    }
    private void setElement() {
        element = file.next();
    }
    public String getElement() {
        setElement();
        return element;
    }

    public void closeFile() {
        file.close();
    }
}
