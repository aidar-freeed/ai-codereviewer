package com.adins.mss.base.syncfile;


import android.util.Log;

import com.adins.mss.base.crashlytics.FireCrash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by shaladin on 9/2/17.
 */

/**
 * class untuk compress dan extract file zip
 */
public class ArchiveManager {

    private String ext = ".zip";

    /**
     * Method for archiving file
     *
     * @param _files      ArrayList source file to be archived
     * @param archiveName Destination File without extension (path + filename)
     */
    public void archive(ArrayList<String> _files, String archiveName) {
        try (FileOutputStream dest = new FileOutputStream(new File(archiveName + ext));
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

            byte[] data = new byte[8192];

            for (int i = 0; i < _files.size(); i++) {
                try (FileInputStream fin = new FileInputStream(_files.get(i));
                     BufferedInputStream origin = new BufferedInputStream(fin, 8192)) {
                    Log.i("ArchiveManager","Adding: " + _files.get(i));
                    ZipEntry zen = new ZipEntry(new File(_files.get(i)).getName());
                    out.putNextEntry(zen);

                    int count;
                    while ((count = origin.read(data, 0, 8192)) != -1) {
                        out.write(data, 0, count);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Method for extracting archive
     *
     * @param archiveFile    source archive file to be extracted
     * @param targetLocation target location extracted file
     */
    public void extract(String archiveFile, String targetLocation) {

        File sourceArchiveFile = new File(archiveFile);
        try (ZipFile zipFile = new ZipFile(sourceArchiveFile, ZipFile.OPEN_READ)){
            int BUFFER = 2048;
            List<String> zipFiles = new ArrayList<>();
            File destinationPath = new File(targetLocation);

            Enumeration zipFileEntries = zipFile.entries();
            String sourceCanonicalName = sourceArchiveFile.getCanonicalPath();
            if (!sourceCanonicalName.startsWith(archiveFile)){
                SecurityException securityException= new SecurityException("Kesalahan Security Terkait Tranversal ZIP");
                throw securityException;
            }else {
                while (zipFileEntries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                    String currentEntry = entry.getName();

                    File destFile = new File(destinationPath, currentEntry);
                    if (currentEntry.endsWith(".zip")) {
                        zipFiles.add(destFile.getAbsolutePath());
                    }

                    File destinationParent = destFile.getParentFile();
                    destinationParent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(destFile);
                         BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                        if (!entry.isDirectory()) {
                            BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                            int currentByte;
                            byte[] data = new byte[BUFFER];

                            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                                dest.write(data, 0, currentByte);
                            }

                            dest.flush();
                            is.close();
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
