package com.adins.mss.foundation.image;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

public class Utils {
    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_IN_APP_CAMERA = 2;
    public static int picWidth = 480;
    public static int picHeight = 640;
    public static int picQuality = 75;
    public static int picHQWidth = 540;
    public static int picHQHeight = 960;
    public static int picHQQuality = 85;

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            FireCrash.log(ex);
        }
    }

    public static Bitmap getImageBitmap(String url) {
        Bitmap myImg = BitmapFactory.decodeFile(url);
        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);
        return rotated;
    }

    public static Bitmap byteToBitmap(byte[] byte_image) {
        Bitmap bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
        if (bm == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //ignored
            } else {
                options.inPurgeable = true;
            }
            options.inPreferredConfig = Config.RGB_565;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length, options);

            // Calculate inSampleSize
            options.inSampleSize = 4;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length, options);
        }
        return bm;
    }

    public static Bitmap pathToBitmap(File imgFile) {
        Bitmap bitmap = null;
        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //ignored
            } else {
                options.inDither = true;
            }
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            int[] res = getSmallResolution(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, res[0], res[1], false);
        }
        return bitmap;
    }

    public static Bitmap pathToBitmapWithRotation(File imgFile) {
        Bitmap bitmap = null;
        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            int[] res = getSmallResolution(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, res[0], res[1], false);

            int rotation = Utils.getExifRotation(imgFile);
            bitmap = ImageManipulation.rotateImage(bitmap, rotation);
        }
        return bitmap;
    }

    public static Bitmap absPathToBitmap(String absolutePath) {
        Bitmap bitmap = null;
        File imgFile = new File(absolutePath);
        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(absolutePath);
            int[] res = getSmallResolution(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, res[0], res[1], false);
        }
        return bitmap;
    }

    public static byte[] base64ToByte(String base64String) {
        byte[] imageByte = Base64.decode(base64String);
        return imageByte;
    }

    public static String byteToBase64(byte[] byte_image) {
        String base64String = new String(Base64.encode(byte_image));
        return base64String;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        byte[] bitmapArray = null;
        if(bitmap == null)
            return bitmapArray;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

        bitmapArray = stream.toByteArray();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapArray;
    }

    public static byte[] bitmapToByte(Bitmap bitmap, int quality) {
        byte[] bitmapArray = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        bitmapArray = stream.toByteArray();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapArray;
    }

    public static byte[] pathBitmapToByte(File imgFile) {
        Bitmap bitmap = pathToBitmap(imgFile);
        return bitmapToByte(bitmap);
    }

    public static byte[] pathBitmapToByteWithRotation(File imgFile) {
        Bitmap bitmap = pathToBitmap(imgFile);
        int rotation = Utils.getExifRotation(imgFile);
        bitmap = ImageManipulation.rotateImage(bitmap, rotation);
        return bitmapToByte(bitmap);
    }

    public static int getExifRotation(File imageFile) {
        if (imageFile == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            // We only recognize a subset of orientation tag values
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            Logger.e("Error getting Exif data", e.getMessage());
            return 0;
        }
    }

    public static void saveBitmapProfiletoPath(Context context, Bitmap bitmap) {
        OutputStream outputStream = null;
        File filename = new File(context.getFilesDir(), "imgProfile");
        Uri saveUri = Uri.fromFile(filename);
        if (filename.exists()) {
            boolean deleteResult = filename.delete();
            if(!deleteResult){
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            outputStream = context.getContentResolver().openOutputStream(saveUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            }
        } catch (IOException e) {
            Logger.e("Cannot open file: " + saveUri, e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveBitmapHeadertoPath(Context context, Bitmap bitmap) {
        OutputStream outputStream = null;
        File filename = new File(context.getFilesDir(), "imgHeader");
        Uri saveUri = Uri.fromFile(filename);
        if (filename.exists()) {
            boolean deleteResult = filename.delete();
            if(!deleteResult){
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            outputStream = context.getContentResolver().openOutputStream(saveUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            }
        } catch (IOException e) {
            Logger.e("Cannot open file: " + saveUri, e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteLatestPicture(Context context) {

        File f = new File(context.getExternalFilesDir(null) + "/DCIM/Camera");

        File[] files = f.listFiles();

        Arrays.sort(files, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        boolean deleteResult = files[0].delete();
        if(!deleteResult){
            Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteLatestPictureLGE(Context context) {

        File f = new File(context.getExternalFilesDir(null)+ "/DCIM/100LGDSC");

        File[] files = f.listFiles();

        Arrays.sort(files, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        boolean deleteResult = files[0].delete();
        if(!deleteResult){
            Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight) {
        int sourceWidth = bm.getWidth();
        int sourceHeight = bm.getHeight();

        float nPercentH = ((float) newHeight / (float) sourceHeight);
        int destWidth = Math.max(Math.round(sourceWidth * nPercentH), 1);
        int destHeight = newHeight;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(destWidth, destHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, destWidth, destHeight, matrix, false);
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        return resizedBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(File imgFile,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //ignored
        } else {
            options.inDither = false;
        }
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
    }

    public static int neededRotation(File ff) {
        try {
            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            }
            return 0;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ExifData getDataOnExif(File ff) {
        ExifData exifData = new ExifData();
        try {
            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            if (exif != null) {
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    exifData.setOrientation(270);
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    exifData.setOrientation(180);
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    exifData.setOrientation(90);
                } else {
                    exifData.setOrientation(0);
                }
                int sourceWidth = exif.getAttributeInt(
                        ExifInterface.TAG_IMAGE_WIDTH, picWidth);
                int sourceHeight = exif.getAttributeInt(
                        ExifInterface.TAG_IMAGE_LENGTH, picHeight);
                exifData.setWidth(sourceWidth);
                exifData.setHeight(sourceHeight);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifData;
    }

    public static int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        int dimension = 0;
        //If the bitmap is wider than it is tall
        //use the height as the square crop dimension
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            dimension = bitmap.getHeight();
        }
        //If the bitmap is taller than it is wide
        //use the width as the square crop dimension
        else {
            dimension = bitmap.getWidth();
        }
        return dimension;
    }

    public static void setCameraParameter(Context context) {
        String param = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_IMG_QUALITY).getGs_value();
        if (param != null && param.length() > 0) {
            String[] values = Tool.split(param, "&");
            for (String value : values) {
                String[] newParam = Tool.split(value, "=");
                String nCode = newParam[0];
                String nValue = newParam[1];
                if (nCode.equals("width"))
                    picWidth = Integer.valueOf(nValue);
                else if (nCode.equals("height"))
                    picHeight = Integer.valueOf(nValue);
                else if (nCode.equals("jpegquality"))
                    picQuality = Integer.valueOf(nValue);
            }
        }

        String paramHQ = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_IMG_HIGH_QUALITY).getGs_value();
        if (paramHQ != null && paramHQ.length() > 0) {
            String[] values = Tool.split(paramHQ, "&");
            for (String value : values) {
                String[] newParam = Tool.split(value, "=");
                String nCode = newParam[0];
                String nValue = newParam[1];
                if (nCode.equals("width"))
                    picHQWidth = Integer.valueOf(nValue);
                else if (nCode.equals("height"))
                    picHQHeight = Integer.valueOf(nValue);
                else if (nCode.equals("jpegquality"))
                    picHQQuality = Integer.valueOf(nValue);
            }
        }
    }

    public static Bitmap resizeImageByPath(Bitmap bm) {
        int[] res = getSmallResolution(bm.getWidth(), bm.getHeight());
        Bitmap bmp = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
        OutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, picQuality, stream);
        return bmp;
    }

    public static int[] getSmallResolution(int oriW, int oriH) {
        int[] resolution = new int[2];

        double widthRatio = (double) oriW / picWidth;
        double heightRatio = (double) oriH / picHeight;
        double ratio = Math.max(widthRatio, heightRatio);
        if (ratio == 0d) {
            ratio = 1d;
        }
        resolution[0] = (int) Math.floor(oriW / ratio);
        resolution[1] = (int) Math.floor(oriH / ratio);

        return resolution;
    }

    public static byte[] resizeBitmapWithWatermark(Bitmap bm, int rotate, int actualWidth, int actualHeight, int jpegQuality, Activity activity) {
        Log.i("Utils","image quality : " + jpegQuality);
        Bitmap bmp = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

        //rotate image if potraid
        if (rotate != 0) {
            Matrix mat = new Matrix();
            mat.preRotate(rotate);// /in degree

            // Bitmap
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
        }
        //untuk nambah watermark
        Bitmap bmpFinal = ImageManipulation.waterMark(bmp, activity.getString(R.string.watermark), Color.WHITE, 80, 32, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmpFinal.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);

        try {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            if (bmpFinal != null && !bmpFinal.isRecycled()) {
                bmpFinal.recycle();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return stream.toByteArray();
    }

    public static byte[] resizeBitmapFileWithWatermark(File imgFile, int rotate, int actualWidth, int actualHeight, int jpegQuality, Activity activity, boolean isHQ) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (isHQ)
            options.inPreferredConfig = Config.ARGB_8888;
        else
            options.inPreferredConfig = Config.RGB_565;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //ignored
        } else {
            options.inDither = true;
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

        Matrix mat = new Matrix();
        mat.preRotate(rotate);// in degree
        // Bitmap
        Bitmap bmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                bm.getHeight(), mat, true);

        //untuk nambah watermark
        Bitmap bmpFinal = ImageManipulation.waterMark(bmp, activity.getString(R.string.watermark), Color.WHITE, 80, 32, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmpFinal.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);

        try {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            if (bmpFinal != null && !bmpFinal.isRecycled()) {
                bmpFinal.recycle();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return stream.toByteArray();
    }

    public Bitmap transform(Bitmap source, int mSize, boolean isHeightScale) {
        float scale;
        int newSize;
        Bitmap scaleBitmap;
        if (isHeightScale) {
            scale = (float) mSize / source.getHeight();
            newSize = Math.round(source.getWidth() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
        } else {
            scale = (float) mSize / source.getWidth();
            newSize = Math.round(source.getHeight() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
        }
        if (scaleBitmap != source && (source != null && !source.isRecycled())) {
            source.recycle();
        }

        return scaleBitmap;
    }

    public Bitmap transformFromFile(File imgFile, int mSize, boolean isHeightScale) {

        Bitmap scaleBitmap;
        Bitmap source;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //ignored
        } else {
            options.inDither = false;
        }
        source = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        float scale;
        int newSize;
        if (isHeightScale) {
            scale = (float) mSize / source.getHeight();
            newSize = Math.round(source.getWidth() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
        } else {
            scale = (float) mSize / source.getWidth();
            newSize = Math.round(source.getHeight() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
        }

        if (scaleBitmap != source) {
            if (source != null && !source.isRecycled()) {
                source.recycle();
            }
        }

        return scaleBitmap;
    }
}