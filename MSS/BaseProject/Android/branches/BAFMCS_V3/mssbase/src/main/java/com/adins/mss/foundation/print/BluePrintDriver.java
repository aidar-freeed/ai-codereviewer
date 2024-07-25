package com.adins.mss.foundation.print;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BluePrintDriver {
    public static final int Code128_B = 732;
    private static final int DEFAULT_CMD_BUFFER_LEN = 1048576;
    public static String ErrorMessage = "No_Error_Message";
    public static boolean TextPosWinStyle = false;
    private static OutputStream myOutStream = null;
    private static InputStream myInStream = null;
    private static BluetoothSocket mySocket = null;
    private static BluetoothAdapter myBluetoothAdapter;
    private static BluetoothDevice myDevice;
    private static int mIndex = 0;
    private static byte[] mCmdBuffer = new byte[1048576];

    public BluePrintDriver() {
    }

    public static boolean open(BluetoothAdapter myBluetoothAdapter, BluetoothDevice btDevice) {
        return SPPOpen(myBluetoothAdapter, btDevice);
    }

    public static void close() {
        SPPClose();
    }

    public static boolean OpenPrinter(String BDAddr) {
        if (BDAddr == "") {
            ErrorMessage = "There is no available printer";
            return false;
        } else {
            myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (myBluetoothAdapter == null) {
                ErrorMessage = "Bluetooth system error";
                return false;
            } else {
                myDevice = myBluetoothAdapter.getRemoteDevice(BDAddr);
                if (myDevice == null) {
                    ErrorMessage = "Read Bluetooth device error";
                    return false;
                } else {
                    return SPPOpen(myBluetoothAdapter, myDevice);
                }
            }
        }
    }

    private static boolean SPPOpen(BluetoothAdapter BluetoothAdapter, BluetoothDevice btDevice) {
        boolean error = false;
        myBluetoothAdapter = BluetoothAdapter;
        myDevice = btDevice;
        if (!myBluetoothAdapter.isEnabled()) {
            ErrorMessage = "Bluetooth adapter is off";
            return false;
        } else {
            try {
                Method e3 = myDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE);
                mySocket = (BluetoothSocket) e3.invoke(myDevice, Integer.valueOf(1));
            } catch (SecurityException var7) {
                mySocket = null;
                ErrorMessage = "Bluetooth port error";
                return false;
            } catch (NoSuchMethodException var8) {
                mySocket = null;
                ErrorMessage = "Bluetooth port error";
                return false;
            } catch (IllegalArgumentException var9) {
                mySocket = null;
                ErrorMessage = "Bluetooth port error";
                return false;
            } catch (IllegalAccessException var10) {
                mySocket = null;
                ErrorMessage = "Bluetooth port error";
                return false;
            } catch (InvocationTargetException var11) {
                mySocket = null;
                ErrorMessage = "Bluetooth port error";
                return false;
            }

            try {
                mySocket.connect();
            } catch (IOException var6) {
                ErrorMessage = var6.getLocalizedMessage();
                mySocket = null;
                return false;
            }

            try {
                myOutStream = mySocket.getOutputStream();
            } catch (IOException var5) {
                myOutStream = null;
                error = true;
            }

            try {
                myInStream = mySocket.getInputStream();
            } catch (IOException var4) {
                myInStream = null;
                error = true;
            }

            if (error) {
                SPPClose();
                return false;
            } else {
                return true;
            }
        }
    }

    private static boolean SPPClose() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
        }

        if (myOutStream != null) {
            try {
                myOutStream.flush();
            } catch (IOException var5) {
            }

            try {
                myOutStream.close();
            } catch (IOException var4) {
            }

            myOutStream = null;
        }

        if (myInStream != null) {
            try {
                myInStream.close();
            } catch (IOException var3) {
            }

            myInStream = null;
        }

        if (mySocket != null) {
            try {
                mySocket.close();
            } catch (IOException var2) {
            }

            mySocket = null;
        }

        try {
            Thread.sleep(200L);
        } catch (InterruptedException var1) {
            Thread.currentThread().interrupt();
        }

        return true;
    }

    public static boolean IsNoConnection() {
        return myOutStream == null;
    }

    public static boolean isPrinterConnected() {
        try {
            byte[] e = new byte[]{(byte) 27, (byte) 64};
            if (myOutStream == null) {
                return false;
            }

            myOutStream.write(e);
            Thread.sleep(1000);
            myOutStream.write(e);
        } catch (Exception var1) {
            var1.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean InitPrinter() {
        try {
            byte[] e = new byte[]{(byte) 27, (byte) 64};
            if (myOutStream == null) {
                return false;
            }

            myOutStream.write(e);
        } catch (IOException var1) {
            var1.printStackTrace();
            return false;
        }

        return true;
    }

    public static void ImportData(byte[] data, int dataLen) {
        int DataLength = dataLen;

        for (int i = 0; i < DataLength; ++i) {
            mCmdBuffer[mIndex++] = data[i];
        }

    }

    public static void ImportData(String dataString) {
        byte[] data = null;

        try {
            data = dataString.getBytes("GBK");
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        int DataLength = data != null?data.length:0;

        for (int i = 0; i < DataLength; ++i) {
            mCmdBuffer[mIndex++] = data[i];
        }

    }

    public static void ImportData(String dataString, boolean bGBK) {
        byte[] data = null;
        if (bGBK) {
            try {
                data = dataString.getBytes("GBK");
            } catch (UnsupportedEncodingException var5) {
            }
        } else {
            data = dataString.getBytes();
        }

        int DataLength = data != null?data.length:0;

        for (int i = 0; i < DataLength; ++i) {
            mCmdBuffer[mIndex++] = data[i];
        }

    }

    public static void ImportData(String dataString, byte[] img) {
        byte[] data = null;
        int Totallen = 0;
        byte[] Buff = new byte[2048];
        int len = 0;
        int panjang = 3599;
        Totallen = dataString.getBytes().length + panjang + 1;
//        Totallen = panjang + 1;
        ByteBuffer sendBuffer = ByteBuffer.allocate(Totallen);
        byte[] SendBuffer = sendBuffer.array();
        try {
            data = dataString.getBytes("GBK");
            byte[] SendData = data;

            for (int i = 0; i < SendBuffer.length; i++)
                SendBuffer[i] = 0;
            for (int i = 0; i < len; i++)
                SendBuffer[i] = Buff[i];

            for (int i = 0; i < panjang; i++)
                SendBuffer[(i)] = img[i];
            for (int i = 0; i < SendData.length; i++)
                SendBuffer[(i + panjang)] = SendData[i];
            SendBuffer[(SendBuffer.length - 1)] = 0;

        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        int DataLength = SendBuffer.length;

        for (int i = 0; i < DataLength; ++i) {
            mCmdBuffer[mIndex++] = SendBuffer[i];
        }

    }

    public static void ClearData() {
        mIndex = 0;
    }

    public static void WakeUpPritner() {
        byte[] b = new byte[3];

        try {
            myOutStream.write(b);
            Thread.sleep(100L);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static void Begin() {
        WakeUpPritner();
        InitPrinter();
        ClearData();
    }

    public static boolean excute() {
        if (mIndex > 0) {
            try {
                myOutStream.write(mCmdBuffer, 0, mIndex);
                myOutStream.flush();
                mIndex = 0;
                return true;
            } catch (IOException var1) {
                var1.printStackTrace();
                return false;
            }
        } else {
            //return false;
            return true;
        }
    }

    public static void LF() {
        mCmdBuffer[mIndex++] = 10;
    }

    public static void SetZoom(byte param) {
        mCmdBuffer[mIndex++] = 29;
        mCmdBuffer[mIndex++] = 33;
        mCmdBuffer[mIndex++] = param;
    }

    public static void SetCharacterFont(byte param) {
        mCmdBuffer[mIndex++] = 27;
        mCmdBuffer[mIndex++] = 77;
        mCmdBuffer[mIndex++] = param;
    }

    public static void SetUnderline(byte param) {
        mCmdBuffer[mIndex++] = 27;
        mCmdBuffer[mIndex++] = 45;
        mCmdBuffer[mIndex++] = param;
    }

    public static void AddBold(byte param) {
        mCmdBuffer[mIndex++] = 27;
        mCmdBuffer[mIndex++] = 69;
        mCmdBuffer[mIndex++] = param;
    }

    public static void AddInverse(byte param) {
        mCmdBuffer[mIndex++] = 29;
        mCmdBuffer[mIndex++] = 66;
        mCmdBuffer[mIndex++] = param;
    }

    public static void SetLineSpace(byte param) {
        mCmdBuffer[mIndex++] = 27;
        mCmdBuffer[mIndex++] = 51;
        mCmdBuffer[mIndex++] = 3;
    }

    public static void AddAlignMode(byte param) {
        mCmdBuffer[mIndex++] = 27;
        mCmdBuffer[mIndex++] = 97;
        mCmdBuffer[mIndex++] = param;
    }

    public static void AddCodePrint(int CodeType, String data) {
        switch (CodeType) {
            case 732:
                Code128_B(data);
            default:
        }
    }

    public static void Code128_B(String data) {
        byte m = 73;
        int num = data.length();
        int transNum = 0;
        mCmdBuffer[mIndex++] = 29;
        mCmdBuffer[mIndex++] = 107;
        mCmdBuffer[mIndex++] = m;
        int Code128C = mIndex++;
        mCmdBuffer[mIndex++] = 123;
        mCmdBuffer[mIndex++] = 66;

        int checkcodeID;
        for (checkcodeID = 0; checkcodeID < num; ++checkcodeID) {
            if (data.charAt(checkcodeID) > 127 || data.charAt(checkcodeID) < 32) {
                return;
            }
        }

        if (num <= 30) {
            for (checkcodeID = 0; checkcodeID < num; ++checkcodeID) {
                mCmdBuffer[mIndex++] = (byte) data.charAt(checkcodeID);
                if (data.charAt(checkcodeID) == 123) {
                    mCmdBuffer[mIndex++] = (byte) data.charAt(checkcodeID);
                    ++transNum;
                }
            }

            checkcodeID = 104;
            int n = 1;

            for (int i = 0; i < num; ++i) {
                checkcodeID += n++ * (data.charAt(i) - 32);
            }

            checkcodeID %= 103;
            if (checkcodeID >= 0 && checkcodeID <= 95) {
                mCmdBuffer[mIndex++] = (byte) (checkcodeID + 32);
                mCmdBuffer[Code128C] = (byte) (num + 3 + transNum);
            } else if (checkcodeID == 96) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 51;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 97) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 50;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 98) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 83;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 99) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 67;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 100) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 52;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 101) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 65;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            } else if (checkcodeID == 102) {
                mCmdBuffer[mIndex++] = 123;
                mCmdBuffer[mIndex++] = 49;
                mCmdBuffer[Code128C] = (byte) (num + 4 + transNum);
            }

        }
    }

    public static boolean SPPWrite(byte[] Data, int DataLen) {
        try {
            myOutStream.write(Data, 0, DataLen);
            return true;
        } catch (IOException var3) {
            ErrorMessage = "Failed to send Bluetooth data";
            return false;
        }
    }

    public static boolean SPPWrite(byte[] buffer) {
        try {
            myOutStream.write(buffer);
            return true;
        } catch (IOException var2) {
            ErrorMessage = "Failed to send Bluetooth data";
            return false;
        }
    }

    public static boolean SPPReadTimeout(byte[] Data, int DataLen, int Timeout) {
        for (int i = 0; i < Timeout / 50; ++i) {
            try {
                if (myInStream.available() >= DataLen) {
                    try {
                        myInStream.read(Data, 0, DataLen);
                        return true;
                    } catch (IOException var5) {
                        ErrorMessage = "Failed to read Bluetooth data";
                        return false;
                    }
                }
            } catch (IOException var7) {
                ErrorMessage = "Failed to read Bluetooth data";
                return false;
            }

            try {
                Thread.sleep(50L);
            } catch (InterruptedException var6) {
                Thread.currentThread().interrupt();
                ErrorMessage = "Failed to read Bluetooth data";
                return false;
            }
        }

        ErrorMessage = "The Bluetooth read data timeout";
        return false;
    }

    private static void SPPFlush() {
        boolean i = false;
        int DataLen = 0;

        try {
            DataLen = myInStream.available();
        } catch (IOException var4) {
        }

        for (int var5 = 0; var5 < DataLen; ++var5) {
            try {
                myInStream.read();
            } catch (IOException var3) {
            }
        }

    }

    public static boolean zp_open(BluetoothAdapter myBluetoothAdapter, BluetoothDevice btDevice) {
        return SPPOpen(myBluetoothAdapter, btDevice);
    }

    public static void printString(String str) {
        try {
            SPPWrite(str.getBytes("GBK"));
            SPPWrite(new byte[]{(byte) 10});
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
        }

    }

    public static void printParameterSet(byte[] buf) {
        SPPWrite(buf);
    }

    public static void printByteData(byte[] buf) {
        SPPWrite(buf);
        SPPWrite(new byte[]{(byte) 10});
    }

    public static void zeroBytes(byte[] zerobuff, int len) {
        byte[] zeroByteArray = new byte[len];
        Arrays.fill(zerobuff, (byte) 0);
    }

    private static boolean printByteImage(byte[] buff, int nToWrite) {
        int quotient = nToWrite / 10000;
        int remainder = nToWrite % 10000;

        try {
            if (nToWrite > 10000) {
                for (int i = 0; i < quotient; ++i) {
                    myOutStream.write(buff, i * 10000, 10000);
                }

                myOutStream.write(buff, 10000 * quotient, remainder);
                myOutStream.flush();
            } else {
                myOutStream.write(buff, 0, nToWrite);
                myOutStream.flush();
            }
            return true;
        } catch (IOException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public static void printImage() {
        printParameterSet(new byte[]{(byte) 27, (byte) 64});
        printParameterSet(new byte[]{(byte) 27, (byte) 33, (byte) 0});
        byte[] bufTemp2 = new byte[]{(byte) 27, (byte) 64, (byte) 27, (byte) 74, (byte) 24, (byte) 29, (byte) 118, (byte) 48, (byte) 0, (byte) 16, (byte) 0, (byte) -128, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -9, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -13, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 12, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 14, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 15, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -13, (byte) -1, (byte) -16, (byte) 15, (byte) -128, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -31, (byte) -1, (byte) -16, (byte) 15, (byte) -64, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) -1, (byte) -16, (byte) 15, (byte) -32, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 127, (byte) -16, (byte) 15, (byte) -16, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 63, (byte) -16, (byte) 15, (byte) -8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 31, (byte) -16, (byte) 15, (byte) -8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 15, (byte) -16, (byte) 15, (byte) -16, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 7, (byte) -16, (byte) 15, (byte) -32, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -32, (byte) 3, (byte) -16, (byte) 15, (byte) -64, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 1, (byte) -16, (byte) 15, (byte) -128, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -8, (byte) 0, (byte) -16, (byte) 15, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -4, (byte) 0, (byte) 112, (byte) 14, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -2, (byte) 0, (byte) 48, (byte) 12, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 16, (byte) 8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 0, (byte) 0, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 0, (byte) 0, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -32, (byte) 0, (byte) 0, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 0, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -8, (byte) 0, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -4, (byte) 0, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -2, (byte) 0, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -2, (byte) 0, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -4, (byte) 0, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -8, (byte) 0, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 0, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -32, (byte) 0, (byte) 0, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 0, (byte) 0, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 0, (byte) 0, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 16, (byte) 8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -2, (byte) 0, (byte) 48, (byte) 12, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -4, (byte) 0, (byte) 112, (byte) 14, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -8, (byte) 0, (byte) -16, (byte) 15, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 1, (byte) -16, (byte) 15, (byte) -128, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -32, (byte) 3, (byte) -16, (byte) 15, (byte) -64, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 7, (byte) -16, (byte) 15, (byte) -32, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 15, (byte) -16, (byte) 15, (byte) -16, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 31, (byte) -16, (byte) 15, (byte) -8, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 63, (byte) -16, (byte) 15, (byte) -4, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -128, (byte) 127, (byte) -16, (byte) 15, (byte) -8, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) -1, (byte) -16, (byte) 15, (byte) -16, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -31, (byte) -1, (byte) -16, (byte) 15, (byte) -32, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -13, (byte) -1, (byte) -16, (byte) 15, (byte) -64, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 15, (byte) -128, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 15, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 14, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 12, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 8, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 3, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 7, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 31, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 63, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) 127, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -16, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -15, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -13, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -9, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 10};
        printByteData(bufTemp2);
        printString("");
        printParameterSet(new byte[]{(byte) 27, (byte) 64});
        printParameterSet(new byte[]{(byte) 27, (byte) 97, (byte) 0});
    }
}

