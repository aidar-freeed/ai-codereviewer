package com.adins.mss.base.syncfile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * class untuk mengambil SHA1 signature dari sebuah file
 */
public class FileSigner {

    /**
     * method untuk mendapatkan SHA1 string signature
     *
     * @param file File yang ingin diambil signature SHA1 nya
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getSha1String(final File file) throws NoSuchAlgorithmException, IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            final byte[] buffer = new byte[1024];
            for (int read = 0; (read = is.read(buffer)) != -1; ) {
                messageDigest.update(buffer, 0, read);
            }
        }

        // Convert the byte to hex format
        try (Formatter formatter = new Formatter()) {
            for (final byte b : messageDigest.digest()) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

    /**
     * method untuk export string SHA1 menjadi bentuk file.SHA1
     *
     * @param file
     */
    public static void createSha1File(final File file) {
        String parentDir;
        if (file.getAbsoluteFile().getParent().isEmpty()) {
            parentDir = "";
        } else parentDir = file.getAbsoluteFile().getParent();

        try (PrintWriter out = new PrintWriter(parentDir + "\\" + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".sha1")) {
            out.println(getSha1String(file));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            System.out.println("Failed to create SHA1 Signature");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Failed to create SHA1 Signature");
            e.printStackTrace();
        }
    }

}
