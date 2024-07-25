package com.adins.mss.coll.commons;

import android.content.Context;

import com.adins.mss.base.crashlytics.FireCrash;

import java.io.*;

/**
 * Created by Aditya Purwa on 3/5/2015.
 */
public class PersistentCounter {
    public static int getAndIncrement(Context context, String name) {
        File dir = context.getDir("persistentcounter", Context.MODE_PRIVATE);
        File file = new File(dir, name);

        int base = 0;
        if (file.exists()) {
            try( BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                base = Integer.parseInt(line);
            } catch (Exception ex) {
                return 0;
            }
        }


        try (BufferedWriter  writer = new BufferedWriter(new FileWriter(file))){

            writer.write(String.valueOf(base + 1));
            writer.flush();
        } catch (Exception e) {
            FireCrash.log(e);
            return 0;
        }

        return base;
    }

    public static int get(Context context, String name) {
        File dir = context.getDir("persistentcounter", Context.MODE_PRIVATE);
        File file = new File(dir, name);

        int base = 0;
        if (file.exists()) {

            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line = reader.readLine();
                base = Integer.parseInt(line);
            } catch (Exception ex) {
                return 0;
            }
        }
        return base;
    }
}
