package com.example.kevin.ecparking;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Kevin Jiang on 10/15/17.
 */

public class StreetsSize {

    private Scanner scanner;
    private int size;
    private InputStream inputStream;

    public void openFile(AssetManager assetManager) {
//        File file = new File("/asset/streetDB.txt");
        try {
//            scanner = new Scanner(file);
            inputStream = assetManager.open("streetDB.txt");
        }
        catch(Exception e) {
            Log.d("DEBUG","Unable to open file for size");
        }

    }

    public void setSize() {
        int count = 0;

        while(scanner.hasNext()) {
            count++;
            scanner.next();
        }
        size = count / 5;
    }

    public void closeFile() {
        try {
            inputStream.close();

        }
        catch(Exception e){
            Log.d("DEBUG","Unable to close file for size");
        }
    }

    public int getSize() { return size; }
}
