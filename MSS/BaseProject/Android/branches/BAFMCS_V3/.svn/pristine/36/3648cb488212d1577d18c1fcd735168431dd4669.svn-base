package com.adins.mss.foundation.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author glen.iglesias
 *         ConfigFileReader is used to read *.properties file in asset and return Properties object
 *         <p>NOTE: it only has one static method. Consider refactoring to combine with other useful method
 */
public class ConfigFileReader {

    public ConfigFileReader() {

    }

    /**
     * @param context Context of the requested properties file. Use caller's context is the default method.
     * @param name    Name of the file, without the .properties file type. Example, use "application" if the file name is "application.properties"
     * @return Properties object if file successfully read and retrieved, or <b>null</b> if failed
     */
    public static Properties propertiesFromFile(Context context, String name) {

        Resources resources = context.getResources();
        AssetManager assetManager = resources.getAssets();

        String fileFullname = name + ".properties";

        try(InputStream inputStream = assetManager.open(fileFullname)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
            e.printStackTrace();
        }

        return null;
    }

}
