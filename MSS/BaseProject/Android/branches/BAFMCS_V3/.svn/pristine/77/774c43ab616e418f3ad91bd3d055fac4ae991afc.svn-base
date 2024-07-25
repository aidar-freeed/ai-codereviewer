package com.adins.mss.base.log;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.bixolon.printer.print.BitmapManager;
import com.bixolon.printer.service.ServiceManager;
import com.bixolon.printer.utility.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class BxlService {
    public static final int BXL_SUCCESS = 0;
    public static final int BXL_ERR_INITIALIZE = 101;
    public static final int BXL_ERR_OVERFLOW = 102;
    public static final int BXL_ERR_NOTSUPPORT = 103;
    public static final int BXL_ERR_INVALID_MODE = 104;
    public static final int BXL_ERR_BUFFER_FULL = 107;
    public static final int BXL_ERR_ARGUMENT = 201;
    public static final int BXL_ERR_INVALIDDATA = 202;
    public static final int BXL_ERR_BARCODEDATA = 301;
    public static final int BXL_ERR_OPEN = 401;
    public static final int BXL_ERR_WRITE = 402;
    public static final int BXL_ERR_READ = 403;
    public static final int BXL_ERR_IMAGEOPEN = 501;
    public static final int BXL_STS_NORMAL = 600;
    public static final int BXL_STS_COVEROPEN = 601;
    public static final int BXL_STS_EMPTYPAPER = 602;
    public static final int BXL_STS_POWEROVER = 603;
    public static final int BXL_STS_MSRMODE = 604;
    public static final int BXL_STS_ICMODE = 605;
    public static final int BXL_STS_ERROR = 606;
    public static final int BXL_PWR_HIGH = 700;
    public static final int BXL_PWR_MIDDLE = 701;
    public static final int BXL_PWR_LOW = 702;
    public static final int BXL_PWR_SMALL = 703;
    public static final int BXL_PWR_NOT = 704;
    public static final int BXL_INIT_BUFFER_MODE = 0;
    public static final int BXL_INIT_BLUETOOTH_MODE = 1;
    public static final int BXL_ALIGNMENT_LEFT = 0;
    public static final int BXL_ALIGNMENT_CENTER = 1;
    public static final int BXL_ALIGNMENT_RIGHT = 2;
    public static final int BXL_FT_DEFAULT = 0;
    public static final int BXL_FT_FONTB = 1;
    public static final int BXL_FT_BOLD = 2;
    public static final int BXL_FT_UNDERLINE = 4;
    public static final int BXL_FT_REVERSE = 8;
    public static final int BXL_TS_0WIDTH = 0;
    public static final int BXL_TS_1WIDTH = 16;
    public static final int BXL_TS_2WIDTH = 32;
    public static final int BXL_TS_3WIDTH = 48;
    public static final int BXL_TS_4WIDTH = 64;
    public static final int BXL_TS_5WIDTH = 80;
    public static final int BXL_TS_6WIDTH = 96;
    public static final int BXL_TS_7WIDTH = 112;
    public static final int BXL_TS_0HEIGHT = 0;
    public static final int BXL_TS_1HEIGHT = 1;
    public static final int BXL_TS_2HEIGHT = 2;
    public static final int BXL_TS_3HEIGHT = 3;
    public static final int BXL_TS_4HEIGHT = 4;
    public static final int BXL_TS_5HEIGHT = 5;
    public static final int BXL_TS_6HEIGHT = 6;
    public static final int BXL_TS_7HEIGHT = 7;
    public static final int BXL_WIDTH_FULL = -1;
    public static final int BXL_WIDTH_NONE = -2;
    public static final int BXL_BCS_UPCA = 101;
    public static final int BXL_BCS_UPCE = 102;
    public static final int BXL_BCS_EAN13 = 103;
    public static final int BXL_BCS_JAN13 = 104;
    public static final int BXL_BCS_EAN8 = 105;
    public static final int BXL_BCS_JAN8 = 106;
    public static final int BXL_BCS_Code39 = 107;
    public static final int BXL_BCS_ITF = 108;
    public static final int BXL_BCS_Codabar = 109;
    public static final int BXL_BCS_Code93 = 110;
    public static final int BXL_BCS_Code128 = 111;
    public static final int BXL_BCS_PDF417 = 200;
    public static final int BXL_BCS_QRCODE_MODEL2 = 202;
    public static final int BXL_BCS_QRCODE_MODEL1 = 203;
    public static final int BXL_BCS_DATAMATRIX = 204;
    public static final int BXL_BCS_MAXICODE_MODE2 = 205;
    public static final int BXL_BCS_MAXICODE_MODE3 = 206;
    public static final int BXL_BCS_MAXICODE_MODE4 = 207;
    public static final int BXL_BC_TEXT_NONE = 0;
    public static final int BXL_BC_TEXT_ABOVE = 1;
    public static final int BXL_BC_TEXT_BELOW = 2;
    public static final int BXL_MSR_TRACK123 = 0;
    public static final int BXL_MSR_TRACK1 = 1;
    public static final int BXL_MSR_TRACK2 = 2;
    public static final int BXL_MSR_TRACK3 = 3;
    public static final int BXL_MSR_TRACK12 = 4;
    public static final int BXL_MSR_TRACK23 = 5;
    public static final int BXL_CS_PC437 = 0;
    public static final int BXL_CS_Katakana = 1;
    public static final int BXL_CS_PC850 = 2;
    public static final int BXL_CS_PC860 = 3;
    public static final int BXL_CS_PC863 = 4;
    public static final int BXL_CS_PC865 = 5;
    public static final int BXL_CS_WPC1252 = 16;
    public static final int BXL_CS_PC866 = 17;
    public static final int BXL_CS_PC852 = 18;
    public static final int BXL_CS_PC858 = 19;
    public static final int BXL_CS_PC862 = 21;
    public static final int BXL_CS_PC864 = 22;
    public static final int BXL_CS_THAI42 = 23;
    public static final int BXL_CS_WPC1253 = 24;
    public static final int BXL_CS_WPC1254 = 25;
    public static final int BXL_CS_WPC1257 = 26;
    public static final int BXL_CS_FARSI = 27;
    public static final int BXL_CS_WPC1251 = 28;
    public static final int BXL_CS_PC737 = 29;
    public static final int BXL_CS_PC775 = 30;
    public static final int BXL_CS_THAI14 = 31;
    public static final int BXL_CS_WPC1255 = 33;
    public static final int BXL_CS_THAI11 = 34;
    public static final int BXL_CS_THAI18 = 35;
    public static final int BXL_CS_PC855 = 36;
    public static final int BXL_CS_PC857 = 37;
    public static final int BXL_CS_PC928 = 38;
    public static final int BXL_CS_THAI16 = 39;
    public static final int BXL_CS_WPC1256 = 40;
    public static final int BXL_CS_WPC1258 = 41;
    public static final int BXL_CS_USER = 255;
    public static final int BXL_ICS_USA = 0;
    public static final int BXL_ICS_FRANCE = 1;
    public static final int BXL_ICS_GERMANY = 2;
    public static final int BXL_ICS_UK = 3;
    public static final int BXL_ICS_DENMARK1 = 4;
    public static final int BXL_ICS_SWEDEN = 5;
    public static final int BXL_ICS_ITALY = 6;
    public static final int BXL_ICS_SPAIN1 = 7;
    public static final int BXL_ICS_NORWAY = 9;
    public static final int BXL_ICS_DENMARK2 = 10;
    public static final int BXL_ICS_SPAIN2 = 11;
    public static final int BXL_ICS_LATINAMERICA = 12;
    public static final int BXL_ICS_KOREA = 13;
    public static final int MSR_ERR_CONNECT = 0;
    public static final int MSR_TRACK123_COMMAND = 1;
    public static final int MSR_TRACK1_AUTO = 2;
    public static final int MSR_TRACK2_AUTO = 3;
    public static final int MSR_TRACK3_AUTO = 4;
    public static final int MSR_TRACK12_AUTO = 5;
    public static final int MSR_TRACK23_AUTO = 6;
    public static final int MSR_TRACK123_AUTO = 7;
    public static final int MSR_NOTUSED = 8;
    private static final int Com_Initial = 0;
    private static final int Com_BlueTooth = 1;
    private static final int Com_WiFi_TCP = 2;
    private static final int Samsung_GalaxyA = 0;
    private static final int Samsung_GalaxyS = 1;
    private static final int HTC_Desire = 2;
    private static final String TAG = "BXLSERVICE";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int Data_Stor = 1;
    private static final int Data_Load = 0;
    private static final byte[] czESC02 = {16, 4, 2};
    private static final byte[] czESC04 = {16, 4, 4};
    private static final byte[] GetCmdPower = {29, 73, 98};
    private static final byte[] MSR_TRACK123_READERMODE_COMMAND = {27, 77, 66};
    private static final byte[] MSR_CANCEL_READERMODE_COMMAND = {27, 77, 99};
    private static final byte[] IC_PowerON = {31, 83, 18};
    private static final byte[] IC_PowerOFF = {31, 83, 17};
    private static final byte[] IC_StatusGet = {31, 83, 23};
    private static final byte[] MSR_SETTING_VALUE_COMMAND = {27, 77, 73};
    private static final byte[] ESC_CR = {13};
    private static final byte[] ESC_LF = {10};
    private static final byte[] ESC_FF = {12};
    private static final byte[] msr_track_tail = {3, 13, 10};
    private static final byte[] IC_ApduHeader = {31, 83, 21};
    private static final byte[] ESC_AlignNormal = {27, 97};
    private static final byte[] ESC_AlignCenter = {27, 97, 1};
    private static final byte[] ESC_AlignRight = {27, 97, 2};
    private static final byte[] GS_B_Enabled = {29, 66, 1};
    private static final byte[] GS_B_Disabled = {29, 66, 0};
    public static int iMsrReadMode = 1;
    private static byte[] GS_FontSize = {29, 33, 0};
    private static byte[] ESC_FontChar = {27, 33, 0};
    BixolonPrintManager bixolonPrintManager;
    Context context;
    private BluetoothAdapter mAdapter = null;
    private BluetoothDevice mDevice = null;
    private BluetoothSocket mSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private ConnectThread mConnectThread = null;
    private ConnectedThread mConnectedThread = null;
    private Socket wSocket = null;
    private int MsgType;
    private boolean recievedState = true;
    private boolean connectState = false;
    private byte[] recvBuffer = new byte[3074];
    private byte[] recvBuffer2 = new byte[1024];
    private int recvBytes;
    private byte[] PackagectsendBuffer = new byte[4096];
    private byte[] PackagectsendBuffer1 = new byte[256];
    private String reAddr = new String();
    private int rePort = 0;
    private int m_lState = 600;
    private int m_cCodePage = 0;
    private int m_cInterCharSet = 0;
    private boolean logenable = false;
    private int Com_Mode = 0;
    private int DeviceMoldel = 1;
    private int StatusCheck = 0;
    private byte[] MsrTrack1 = new byte[200];
    private byte[] MsrTrack2 = new byte[200];
    private byte[] MsrTrack3 = new byte[200];
    private int MsrTrack1_Status = 0;
    private int MsrTrack2_Status = 0;
    private int MsrTrack3_Status = 0;
    private byte[] iImage1 = null;
    private byte[] iImage2 = null;
    private int iImage1Size = 0;
    private int iImage2Size = 0;
    private ByteBuffer iImage1_ByteBuffer = null;
    private ByteBuffer iImage2_ByteBuffer = null;
    private byte[] globaliOutBuffer_iOutBuffer = null;
    private int globaliOutBuffer_MaxLength = 0;
    private int globaliOutBuffer_Size = 0;
    private ByteBuffer globaliOutBuffer_ByteBuffer = null;
    private int nWaitTimeforDisconnect = 3000;
    private boolean bTerminate = false;
    private String mstrEncoding;
    private ServiceManager mServiceManager;
    private Random rand;
    public BxlService(BixolonPrintManager bixolonPrintManager) {

        this.bixolonPrintManager = bixolonPrintManager;
        this.context = bixolonPrintManager.context;
        Log.i("BXLSERVICE", "ON Contructor_BxlService");
        String DeviceModeName = "HTC Desire";
        if (DeviceModeName.equals(Build.MODEL)) {
            this.DeviceMoldel = 2;
            Log.i("BXLSERVICE", Build.MODEL + " " + DeviceModeName.equals(Build.MODEL));
        } else {
            this.DeviceMoldel = 1;
            Log.i("BXLSERVICE", Build.MODEL + " " + DeviceModeName.equals(Build.MODEL));
        }

        this.mstrEncoding = getEncoding();
    }

    private static void mywait(int waitTime) {
        Runtime runtimein = Runtime.getRuntime();
        synchronized (runtimein) {
            try {
                runtimein.wait(waitTime);
            } catch (InterruptedException localInterruptedException) {
            }
        }
    }

    // bong Oct 29th, 2014 - need to get bluetooth device in BixolonPrinterManager
    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public synchronized int write(byte[] out) {
        Log.i("BXLSERVICE", "write out.length " + out.length);
        int returnvalue = 0;

        if (this.DeviceMoldel == 2) {
            int TheNumberOfPackage = out.length / this.PackagectsendBuffer1.length;
            int TheRemainderOfPackage = out.length % this.PackagectsendBuffer1.length;
            if (out.length <= this.PackagectsendBuffer1.length) {
                Log.i("BXLSERVICE", "write(outSize<=PackagectsendBuffer1Size)");
                returnvalue = this.mConnectedThread.write(out);

                SystemClock.sleep(50L);
                return returnvalue;
            }
            Log.i("BXLSERVICE", "write(outSize>PackagectsendBuffer1Size)");
            for (int counter0 = 0; counter0 < TheNumberOfPackage; counter0++) {
                for (int counter1 = 0; counter1 < this.PackagectsendBuffer1.length; counter1++)
                    this.PackagectsendBuffer1[counter1] = out[(this.PackagectsendBuffer1.length * counter0 + counter1)];
                Log.i("BXLSERVICE", "write(outSize<=PackagectsendBuffer1Size) :" + counter0 + " " + TheNumberOfPackage);
                returnvalue = this.mConnectedThread.write(this.PackagectsendBuffer1);
                if (returnvalue != 0) return returnvalue;
                SystemClock.sleep(60L);
            }
            if (TheRemainderOfPackage != 0) {
                byte[] temp = new byte[TheRemainderOfPackage];
                Log.i("BXLSERVICE", "write(outSize<=PackagectsendBuffer1Size) :" + TheRemainderOfPackage);

                for (int counter1 = 0; counter1 < TheRemainderOfPackage; counter1++) {
                    temp[counter1] = out[(this.PackagectsendBuffer1.length * TheNumberOfPackage + counter1)];
                }
                Log.i("BXLSERVICE", "remind : " + TheRemainderOfPackage + " write(RemainderOfPackage1):" + temp.length);
                returnvalue = this.mConnectedThread.write(temp);
                SystemClock.sleep(60L);
            }
        } else {
            int TheNumberOfPackage = out.length / this.PackagectsendBuffer.length;
            int TheRemainderOfPackage = out.length % this.PackagectsendBuffer.length;
            if (out.length <= this.PackagectsendBuffer.length) {
                Log.i("BXLSERVICE", "write(outSize<=PackagectsendBufferSize):" + out.length);
                returnvalue = this.mConnectedThread.write(out);
                SystemClock.sleep(20L);
                return returnvalue;
            }

            for (int counter0 = 0; counter0 < TheNumberOfPackage; counter0++) {
                for (int counter1 = 0; counter1 < this.PackagectsendBuffer.length; counter1++) {
                    this.PackagectsendBuffer[counter1] = out[(this.PackagectsendBuffer.length * counter0 + counter1)];
                }
                returnvalue = this.mConnectedThread.write(this.PackagectsendBuffer);
                Log.i("BXLSERVICE", "write(outSize>PackagectsendBufferSize):" + this.PackagectsendBuffer.length);
                if (returnvalue != 0) return returnvalue;
                SystemClock.sleep(20L);
            }

            if (TheRemainderOfPackage != 0) {
                byte[] temp = new byte[TheRemainderOfPackage];

                for (int counter1 = 0; counter1 < TheRemainderOfPackage; counter1++) {
                    temp[counter1] = out[(this.PackagectsendBuffer.length * TheNumberOfPackage + counter1)];
                }
                returnvalue = this.mConnectedThread.write(temp);
                Log.i("BXLSERVICE", "remind : " + TheRemainderOfPackage + " write(RemainderOfPackage):" + temp.length);
                SystemClock.sleep(20L);
            }
        }

        if (this.logenable)
            Log.i("BXLSERVICE", "write out.length  " + out.length + "ended..................");
        return returnvalue;
    }

    public synchronized int Connect() {
        Log.i("BXLSERVICE", "Bluetooth connect");

        if (this.Com_Mode == 2) {
            Logger.e("BXLSERVICE", "fail : Opened Connection of the WiFi_TCP ");
            return 401;
        }

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        this.mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.mAdapter == null) {
            Logger.e("BXLSERVICE", "Device does not support Bluetooth");
            this.Com_Mode = 0;
            return 401;
        }

        if (!this.mAdapter.isEnabled()) {
            Logger.e("BXLSERVICE", "Bluetooth is disable...trun on bluetooth.");
            this.Com_Mode = 0;
            return 401;
        }

        Set<BluetoothDevice> pairedDevices = this.mAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //bixolon
                if ((device.getAddress().startsWith("00:06")) || (device.getAddress().startsWith("00:19"))) {
                    //sato
                    //if ((device.getAddress().startsWith("00:01"))) {
                    //zebra
                    //if ((device.getAddress().startsWith("AC:3F"))) {
                    Logger.e("BXLSERVICE", "Bluetooth ConnectThread new ConnectThread(device)...");
                    this.mConnectThread = new ConnectThread(device);
                    this.mConnectThread.setDaemon(true);
                    this.mConnectThread.start();

                    for (int i = 1; i < 500; i++) {
                        SystemClock.sleep(10L);
                        if ((!this.mConnectThread.isAlive()) && (this.mSocket != null)) {
                            Logger.e("BXLSERVICE", "Bluetooth connect success");

                            this.recvBytes = 0;
                            this.bTerminate = false;
                            this.mConnectedThread = new ConnectedThread(this.mSocket);
                            this.mConnectedThread.setDaemon(true);
                            this.mConnectedThread.start();
                            this.connectState = true;
                            this.Com_Mode = 1;
                            return 0;
                        }
                        if ((!this.mConnectThread.isAlive()) && (this.mSocket == null)) {
                            Logger.e("BXLSERVICE", "Bluetooth connect exception");
                            return 401;
                        }
                        if (this.logenable)
                            Logger.e("BXLSERVICE", "Bluetooth connect waiting...." + i * 10 + "msec");
                    }
                    Logger.e("BXLSERVICE", "Bluetooth connect timeout");
                    if (this.mConnectThread != null) {
                        this.mConnectThread.cancel();
                        this.mConnectThread = null;
                    }
                    return 401;
                }
            }
        }

        Logger.e("BXLSERVICE", "Bluetooth does not exist");
        return 401;
    }

    public synchronized int Connect(String BtAddr) {
        int[] returnValue = new int[1];
        returnValue[0] = 0;

        if (this.Com_Mode == 2) {
            Logger.e("BXLSERVICE", "fail : Opened Connection of the WiFi_TCP ");
            return 401;
        }

        returnValue[0] = _Connect(BtAddr);

        this.MsgType = 1;
        this.recvBytes = 0;
        returnValue[0] = write(MSR_SETTING_VALUE_COMMAND);
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "GetStatus_write_ERROR");
            return returnValue[0];
        }

        for (int i = 0; i <= 10; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes == 4) {
                    if ((this.recvBuffer[0] == 55) && (this.recvBuffer[1] == -128) && (this.recvBuffer[3] == 0)) {
                        if (this.recvBuffer[2] == 65) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 66) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 67) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 68) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 69) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 70) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        if (this.recvBuffer[2] == 71) {
                            this.recvBytes = 0;
                            this.Com_Mode = 1;

                            return 0;
                        }
                        this.recvBytes = 0;
                        this.Com_Mode = 1;

                        return 0;
                    }

                    this.recvBytes = 0;
                    this.Com_Mode = 1;
                    return 403;
                }

                this.recvBytes = 0;
                this.Com_Mode = 1;
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "Unable to read; close the socket and get out");
        return 403;
    }

    public synchronized int _Connect(String BtAddr) {
        Log.i("BXLSERVICE", "Bluetooth connect");

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mAdapter == null) {
            Logger.e("BXLSERVICE", "Device does not support Bluetooth");
            return 401;
        }

        this.mDevice = this.mAdapter.getRemoteDevice(BtAddr);
        if (this.mDevice == null) {
            Logger.e("BXLSERVICE", "Device does not support Bluetooth");
            return 401;
        }

        this.mConnectThread = new ConnectThread(this.mDevice);
        this.mConnectThread.setDaemon(true);
        this.mConnectThread.start();

        for (int i = 0; i < 50; i++) {
            SystemClock.sleep(100L);
            if ((!this.mConnectThread.isAlive()) && (this.mSocket != null)) {
                Logger.e("BXLSERVICE", "Bluetooth connect success");
                this.recvBytes = 0;
                this.bTerminate = false;
                this.mConnectedThread = new ConnectedThread(this.mSocket);
                this.mConnectedThread.setDaemon(true);
                this.mConnectedThread.start();
                this.connectState = true;
                this.Com_Mode = 1;
                return 0;
            }
            if ((!this.mConnectThread.isAlive()) && (this.mSocket == null)) {
                Logger.e("BXLSERVICE", "Bluetooth connect exception");
                return 401;
            }
        }

        Logger.e("BXLSERVICE", "Bluetooth connect timeout");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        return 401;
    }

    public synchronized int Connect(String Addr, int Port) {
        Log.i("BXLSERVICE", "Wifi connect");

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.Com_Mode == 1) {
            Logger.e("BXLSERVICE", "fail : Opened Connection of the BlueTooth ");
            return 401;
        }

        this.mConnectThread = new ConnectThread(Addr, Port);
        this.mConnectThread.setDaemon(true);
        if (this.wSocket == null) {
            Logger.e("BXLSERVICE", "WiFi Connetion Error_Connect(String BtAddr)");
            return 401;
        }

        Logger.e("BXLSERVICE", "WiFi connect success");
        this.recvBytes = 0;
        this.bTerminate = false;
        this.mConnectedThread = new ConnectedThread(this.wSocket);
        this.mConnectedThread.setDaemon(true);
        SystemClock.sleep(100L);

        byte[] sendApdu = {16, 4, 2};
        try {
            this.mOutStream.write(sendApdu, 0, sendApdu.length);
            this.mInStream.read(this.recvBuffer2, 0, 1024);
            Logger.e("BXLSERVICE", "WiFi_readTest " + this.recvBuffer2[0]);
        } catch (IOException ex) {
            Logger.e("BXLSERVICE", "Fail to close Socket");
            ex.printStackTrace();
            return 401;
        }

        this.bTerminate = false;
        this.mConnectedThread.start();
        this.mConnectedThread.setDaemon(true);
        this.connectState = true;
        this.Com_Mode = 2;

        this.reAddr = Addr;
        this.rePort = Port;

        return 0;
    }

    public synchronized int ReConnect() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "ReConnect");

        if (this.Com_Mode == 1)
            returnValue[0] = Connect();
        else if (this.Com_Mode == 2) {
            returnValue[0] = Connect(this.reAddr, this.rePort);
        }

        return returnValue[0];
    }

    public synchronized int Disconnect() {
        return Disconnect(this.nWaitTimeforDisconnect);
    }

    public int getnWaitTimeforDisconnect() {
        return this.nWaitTimeforDisconnect;
    }

    public void setnWaitTimeforDisconnect(int nWaitTimeforDisconnect) {
        this.nWaitTimeforDisconnect = nWaitTimeforDisconnect;
    }

    public int Disconnect(int waitTimeforDisconnect) {
        Log.i("BXLSERVICE", "Bluetooth disconnect");

        this.bTerminate = true;

        Log.i("BXLSERVICE", "Bluetooth disconnect : sleep= " + waitTimeforDisconnect);

        mywait(100);
        SystemClock.sleep(waitTimeforDisconnect);
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            mywait(100);

            this.mConnectedThread.cancel();

            this.mConnectedThread = null;
        }

        if (this.mConnectThread != null) {
            this.mConnectThread.interrupt();
            mywait(100);
            this.mConnectThread.cancel();

            this.mConnectThread = null;
        }

        mywait(100);
        this.Com_Mode = 0;
        return 0;
    }

    public String getEncoding() {
        Locale locale = Locale.getDefault();

        String lang = locale.getLanguage();
        String country = locale.getCountry();

        Log.i("BXLSERVICE", "Current language :" + lang + ", country :" + country);

        if (lang.equals("ko")) {
            return "EUC_KR";
        }
        if (lang.equals("ja")) {
            return "SJIS";
        }
        if (lang.equals("zh")) {
            if (country.equals("CN")) {
                return "EUC_CN";
            }

            return "BIG5";
        }

        return null;
    }

    public int PrintText(String Data, int Alignment, int Attribute, int TextSize) {
        return PrintText(Data, Alignment, Attribute, TextSize, this.mstrEncoding);
    }

    public int PrintTextSato(String Data) {
        return PrintTextSato(Data, this.mstrEncoding);
    }

    public int PrintTextZebra(String Data) {
        return PrintTextZebra(Data, this.mstrEncoding);
    }

    public int PrintLogoSato() {
        int[] returnValue = new int[1];
        //image adira dalam byte
        //byte[] a = new byte[]{2, 27, 65, 27, 73, 71, 49, 27, 80, 83, 27, 27, 71, 72, 48, 48, 53, 55, 48, 48, 54, 53, 48, 51, 50, 48, 48, 49, 48, 49, 48, 49, 48, 50, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -47, 0, 0, 1, 5, 84, 1, 1, 1, 0, 0, 0, 0, 1, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -8, 0, -1, -1, -1, -1, -8, 3, -1, -8, 127, -1, -1, -1, -8, 0, 3, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -8, 0, -1, -1, -1, -1, -4, 3, -1, -8, 127, -1, -1, -1, -4, 0, 7, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -1, -8, 0, -1, -1, -1, -1, -2, 3, -1, -8, 127, -1, -1, -1, -4, 0, 15, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -1, -8, 0, 127, -1, -1, -1, -1, 3, -1, -16, 127, -1, -1, -1, -2, 0, 31, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -1, -4, 0, -1, -1, -1, -1, -1, -121, -1, -16, -1, -1, -1, -1, -2, 0, 63, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -1, -4, 0, 127, -2, 0, 127, -1, -121, -1, -16, -1, -2, 0, -1, -2, 0, 127, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, -1, -4, 0, -1, -2, 0, 63, -1, -113, -1, -31, -1, -2, 1, -1, -2, 0, -1, -1, -8, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 54, 53, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 54, 56, 0, 0, 0, 0, -1, -1, -4, 0, -1, -4, 0, 31, -1, -113, -1, -31, -1, -4, 3, -1, -4, 1, -1, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -2, 0, -1, -4, 0, 31, -1, -113, -1, -29, -1, -4, 7, -1, -8, 3, -1, -1, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, -2, -1, -2, 1, -1, -4, 0, 31, -1, -97, -1, -61, -1, -8, 15, -1, -16, 7, -3, -1, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -4, -1, -2, 1, -1, -8, 0, 63, -1, -97, -1, -61, -1, -8, 31, -1, -32, 15, -7, -1, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -8, -1, -2, 3, -1, -8, 0, 31, -1, 31, -1, -121, -1, -16, 31, -1, -64, 31, -15, -1, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -8, -1, -2, 3, -1, -8, 0, 63, -1, 63, -1, -121, -1, -8, 63, -1, -128, 63, -32, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -32, 69, 84, 3, -1, -16, 0, 127, -2, 63, -1, 7, -1, -16, 127, -1, 0, 127, -64, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -30, 34, 2, 7, -1, -16, 0, 127, -2, 127, -1, 15, -1, -32, -1, -2, 0, -1, -128, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, -64, 0, 1, 7, -1, -16, 0, -1, -4, 127, -1, 15, -1, -32, -1, -2, 1, -1, 0, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -120, -120, -113, 15, -1, -32, 1, -1, -8, -1, -2, 15, -1, -32, -1, -2, 3, -1, 0, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 127, 15, -1, -32, 3, -1, -16, -1, -2, 31, -1, -64, 127, -1, 7, -2, 0, 127, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, -2, 32, 35, -1, -113, -1, -32, 15, -1, -16, -1, -2, 63, -1, -64, 127, -1, -113, -4, 0, 127, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -4, 0, 31, -1, -97, -1, -64, 31, -1, -63, -1, -4, 63, -1, -128, 63, -1, -97, -8, 0, 127, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -4, -120, 63, -1, -97, -1, -64, -1, -1, -63, -1, -4, 63, -1, -128, 63, -1, -97, -8, 0, 127, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -8, 0, 63, -1, -97, -1, -41, -1, -1, 1, -1, -4, 63, -1, -128, 63, -1, -1, -16, 0, 127, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -14, 0, 63, -1, -65, -1, -1, -1, -2, 3, -1, -8, 127, -1, -128, 63, -1, -1, -32, 0, 63, -1, -128, 0, 0, 0, 0, 0, 0, 0, 0, 127, -32, 0, 31, -1, -1, -1, -1, -1, -16, 3, -1, -8, 127, -1, 0, 31, -1, -1, -64, 0, 63, -1, -128, 0, 0, 0, 0, 0, 0, 0, 0, -1, -32, 0, 31, -1, -1, -1, -1, -1, -128, 3, -1, -8, -1, -1, 0, 15, -1, -1, -128, 0, 63, -1, -128, 0, 0, 0, 0, 0, 0, 0, 1, -1, -64, 0, 31, -1, -1, -1, -1, -16, 0, 7, -1, -16, -1, -2, 0, 15, -1, -1, 0, 0, 63, -1, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 57, 48, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 51, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -16, 0, 28, 0, 7, 28, 0, 1, -64, 0, 12, 48, 0, 7, -64, 0, 31, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -8, 0, 28, 0, 7, -100, 0, 3, -32, 0, 14, 56, 0, 15, -16, 0, 63, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 28, 0, 7, -100, 0, 3, -32, 0, 15, 48, 0, 28, 112, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -128, 0, 28, 0, 15, -36, 0, 3, -32, 0, 15, -72, 0, 28, 0, 0, 58, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -16, 0, 28, 0, 7, -36, 0, 7, 112, 0, 13, -80, 0, 28, 0, 0, 31, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -96, 0, 28, 0, 6, -4, 0, 7, 48, 0, 13, -8, 0, 28, 0, 0, 58, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 28, 0, 6, 124, 0, 7, 112, 0, 12, -16, 0, 28, 48, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 28, 0, 6, 60, 0, 15, -8, 0, 12, -8, 0, 12, 112, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 28, 0, 6, 60, 0, 14, 24, 0, 12, 112, 0, 15, -16, 0, 31, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 28, 0, 6, 28, 0, 14, 24, 0, 12, 56, 0, 7, -32, 0, 63, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 81, 48, 48, 48, 49, 27, 90, 3};
        //image fif dalam byte
        byte[] fif = new byte[]{2, 27, 65, 27, 73, 71, 49, 27, 80, 83, 27, 27, 71, 72, 48, 48, 49, 56, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 51, 54, 0, 0, 0, 21, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 85, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 80, 1, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, -64, 5, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -128, 85, 85, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, -128, -94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 1, 85, 85, 16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 2, -120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 62, 7, 80, 1, 84, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 10, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 29, 64, 0, 80, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 42, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 58, 0, 10, -88, 0, 0, -70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 63, 0, 0, 85, 5, 0, 0, 3, -1, -1, 31, 7, -1, -2, 7, -1, -32, 63, -1, -32, 7, -1, -64, 60, 0, 124, 63, -1, -32, 0, 0, 96, 110, 0, 0, 0, 0, 0, 0, 2, -18, -18, 14, 6, -18, -18, 14, -18, -32, 46, -18, -32, 14, -18, -32, 44, 0, 108, 46, -18, -32, 0, 0, 112, 126, 1, 80, 21, 1, 0, 0, 3, -1, -1, 31, 7, -1, -2, 63, -1, -8, 63, -1, -8, 63, -125, -8, 60, 0, 124, 63, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 52, 49, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 54, 56, 0, 48, -72, 0, 0, 0, 0, 0, 0, 3, -128, 0, 11, 3, -128, 0, 42, 0, -88, 40, 0, -88, 42, 0, -88, 40, 0, 40, 42, 0, -88, 0, 0, 96, -4, 5, 80, 21, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, 124, 0, 124, 60, 0, 120, 124, 0, 124, 60, 0, 124, 62, 0, 124, 0, 0, 96, -8, 2, 32, 32, 0, 0, 0, 2, -64, 0, 14, 6, -128, 0, 104, 0, 0, 44, 0, 104, 104, 0, 108, 44, 0, 108, 46, 0, 108, 0, 0, 97, -8, 21, 64, 21, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, 120, 0, 0, 60, 0, 120, 120, 0, 60, 60, 0, 124, 62, 0, 124, 0, 0, 1, -80, 8, 0, 0, 0, 0, 0, 2, -128, 0, 27, 2, -128, 0, 56, 0, 0, 56, 0, -72, -72, 0, 56, 56, 0, 56, 58, 0, 56, 0, 0, 3, -16, 21, 64, 85, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, -8, 0, 0, 61, 87, -16, -8, 0, 62, 60, 0, 124, 63, 87, -4, 0, 0, 2, -32, 42, 0, -94, 2, 0, 0, 2, -18, -20, 14, 6, -18, -24, -24, 6, -20, 46, -18, -32, -24, 0, 46, 44, 0, 108, 46, -18, -24, 0, 0, 3, -32, 85, 1, 84, 5, 0, 0, 3, -1, -4, 31, 7, -1, -8, -8, 7, -4, 63, -1, 0, -8, 0, 60, 60, 0, 124, 63, -1, -16, 0, 0, 3, -128, -86, 0, -120, 0, 0, 0, 3, -85, -88, 11, 3, -85, -96, 40, 3, -88, 43, -85, -128, -88, 0, 40, 40, 0, 40, 43, -85, -128, 0, 0, 7, -64, 116, 3, 84, 5, 0, 0, 3, -64, 0, 31, 7, -128, 0, 120, 0, 124, 60, 7, -64, 120, 0, 124, 60, 0, 124, 62, 0, 0, 0, 0, 6, -64, -88, 2, -88, 10, 0, 0, 2, -64, 0, 14, 6, -128, 0, 104, 0, 108, 44, 2, -32, 104, 0, 108, 46, 0, 108, 46, 0, 0, 0, 0, 7, -127, -40, 5, 80, 21, 0, 0, 3, -64, 0, 31, 7, -128, 0, 124, 0, 124, 60, 1, -16, 124, 0, 124, 62, 0, 120, 62, 0, 0, 0, 0, 3, -126, -96, 10, -96, 40, 0, 0, 3, -128, 0, 26, 3, -128, 0, 58, 0, -72, 56, 0, -80, 58, 0, -72, 27, 0, -72, 58, 0, 0, 0, 0, 7, -125, -16, 31, 64, 84, 0, 0, 3, -64, 0, 31, 7, -128, 0, 63, -1, -16, 60, 0, -8, 63, -57, -16, 31, -1, -16, 62, 0, 0, 0, 0, 6, 6, -32, 14, -128, -88, 0, 0, 2, -64, 0, 14, 6, -128, 0, 14, -18, -32, 44, 0, -24, 14, -18, -32, 14, -18, -32, 46, 0, 0, 0, 0, 7, 7, -32, 63, -127, -48, 0, 0, 3, -64, 0, 31, 7, -128, 0, 7, -1, -64, 60, 0, 124, 7, -1, -64, 3, -1, -64, 62, 0, 0, 0, 0, 3, 3, -128, 58, 0, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 15, -64, 126, 3, 96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 14, -128, -4, 2, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 15, -128, -4, 15, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 11, -127, -72, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 15, 1, -16, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 14, 2, -32, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 3, -32, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 54, 53, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 54, 56, 0, 0, 11, 3, -128, -80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 7, -64, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 6, -128, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 7, -127, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 15, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 8, 12, 38, 32, 0, -64, 104, -120, -120, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 12, 64, 33, 8, -64, 68, -124, -119, 9, 8, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 56, 57, 48, 48, 54, 53, 48, 51, 50, 48, 48, 49, 48, 49, 48, 49, 48, 50, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -86, 40, 42, 8, 34, -127, -120, 10, 0, 33, 8, -128, -128, -128, 9, 9, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 18, 68, -111, 36, -110, 2, 72, 18, 56, 33, 113, 32, -124, -39, 9, 9, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 36, -126, 0, 72, 2, 2, 32, 32, 32, -124, -128, 8, 8, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 64, 17, 4, -126, 0, 72, 31, 2, 33, 17, -16, 4, -119, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 17, 32, -126, 2, 8, 32, 2, 32, 18, 16, 0, -120, -120, -120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 56, 17, 28, 114, 3, -56, 33, 60, 33, 10, 16, 120, -124, 112, -15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 81, 48, 48, 48, 49, 27, 90, 3};
        //returnValue[0] = write(fif);
        fif = null;
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "PrintText_write_ERROR");
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintText_Unable to write; closed the socket and get out");
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int PrintTextSato(String Data, String encoding) {
        byte[] Buff = new byte[2048];
        int len = 0;
        int Totallen = 0;
        int[] returnValue = new int[1];
        //logo FIF kecil
        //byte[] fif = new byte[]{2, 27, 65, 27, 73, 71, 49, 27, 80, 83, 27, 27, 71, 72, 48, 48, 49, 56, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 51, 54, 0, 0, 0, 21, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 85, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 80, 1, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, -64, 5, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -128, 85, 85, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, -128, -94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 1, 85, 85, 16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 2, -120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 62, 7, 80, 1, 84, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 10, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 29, 64, 0, 80, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 42, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 58, 0, 10, -88, 0, 0, -70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 63, 0, 0, 85, 5, 0, 0, 3, -1, -1, 31, 7, -1, -2, 7, -1, -32, 63, -1, -32, 7, -1, -64, 60, 0, 124, 63, -1, -32, 0, 0, 96, 110, 0, 0, 0, 0, 0, 0, 2, -18, -18, 14, 6, -18, -18, 14, -18, -32, 46, -18, -32, 14, -18, -32, 44, 0, 108, 46, -18, -32, 0, 0, 112, 126, 1, 80, 21, 1, 0, 0, 3, -1, -1, 31, 7, -1, -2, 63, -1, -8, 63, -1, -8, 63, -125, -8, 60, 0, 124, 63, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 52, 49, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 54, 56, 0, 48, -72, 0, 0, 0, 0, 0, 0, 3, -128, 0, 11, 3, -128, 0, 42, 0, -88, 40, 0, -88, 42, 0, -88, 40, 0, 40, 42, 0, -88, 0, 0, 96, -4, 5, 80, 21, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, 124, 0, 124, 60, 0, 120, 124, 0, 124, 60, 0, 124, 62, 0, 124, 0, 0, 96, -8, 2, 32, 32, 0, 0, 0, 2, -64, 0, 14, 6, -128, 0, 104, 0, 0, 44, 0, 104, 104, 0, 108, 44, 0, 108, 46, 0, 108, 0, 0, 97, -8, 21, 64, 21, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, 120, 0, 0, 60, 0, 120, 120, 0, 60, 60, 0, 124, 62, 0, 124, 0, 0, 1, -80, 8, 0, 0, 0, 0, 0, 2, -128, 0, 27, 2, -128, 0, 56, 0, 0, 56, 0, -72, -72, 0, 56, 56, 0, 56, 58, 0, 56, 0, 0, 3, -16, 21, 64, 85, 5, 64, 0, 3, -64, 0, 31, 7, -128, 0, -8, 0, 0, 61, 87, -16, -8, 0, 62, 60, 0, 124, 63, 87, -4, 0, 0, 2, -32, 42, 0, -94, 2, 0, 0, 2, -18, -20, 14, 6, -18, -24, -24, 6, -20, 46, -18, -32, -24, 0, 46, 44, 0, 108, 46, -18, -24, 0, 0, 3, -32, 85, 1, 84, 5, 0, 0, 3, -1, -4, 31, 7, -1, -8, -8, 7, -4, 63, -1, 0, -8, 0, 60, 60, 0, 124, 63, -1, -16, 0, 0, 3, -128, -86, 0, -120, 0, 0, 0, 3, -85, -88, 11, 3, -85, -96, 40, 3, -88, 43, -85, -128, -88, 0, 40, 40, 0, 40, 43, -85, -128, 0, 0, 7, -64, 116, 3, 84, 5, 0, 0, 3, -64, 0, 31, 7, -128, 0, 120, 0, 124, 60, 7, -64, 120, 0, 124, 60, 0, 124, 62, 0, 0, 0, 0, 6, -64, -88, 2, -88, 10, 0, 0, 2, -64, 0, 14, 6, -128, 0, 104, 0, 108, 44, 2, -32, 104, 0, 108, 46, 0, 108, 46, 0, 0, 0, 0, 7, -127, -40, 5, 80, 21, 0, 0, 3, -64, 0, 31, 7, -128, 0, 124, 0, 124, 60, 1, -16, 124, 0, 124, 62, 0, 120, 62, 0, 0, 0, 0, 3, -126, -96, 10, -96, 40, 0, 0, 3, -128, 0, 26, 3, -128, 0, 58, 0, -72, 56, 0, -80, 58, 0, -72, 27, 0, -72, 58, 0, 0, 0, 0, 7, -125, -16, 31, 64, 84, 0, 0, 3, -64, 0, 31, 7, -128, 0, 63, -1, -16, 60, 0, -8, 63, -57, -16, 31, -1, -16, 62, 0, 0, 0, 0, 6, 6, -32, 14, -128, -88, 0, 0, 2, -64, 0, 14, 6, -128, 0, 14, -18, -32, 44, 0, -24, 14, -18, -32, 14, -18, -32, 46, 0, 0, 0, 0, 7, 7, -32, 63, -127, -48, 0, 0, 3, -64, 0, 31, 7, -128, 0, 7, -1, -64, 60, 0, 124, 7, -1, -64, 3, -1, -64, 62, 0, 0, 0, 0, 3, 3, -128, 58, 0, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 15, -64, 126, 3, 96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 14, -128, -4, 2, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 15, -128, -4, 15, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 11, -127, -72, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 15, 1, -16, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 14, 2, -32, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 3, -32, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 54, 53, 48, 48, 54, 53, 48, 51, 50, 48, 48, 51, 48, 49, 48, 49, 48, 55, 54, 56, 0, 0, 11, 3, -128, -80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 7, -64, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 6, -128, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 7, -127, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 15, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 8, 12, 38, 32, 0, -64, 104, -120, -120, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 12, 64, 33, 8, -64, 68, -124, -119, 9, 8, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 56, 57, 48, 48, 54, 53, 48, 51, 50, 48, 48, 49, 48, 49, 48, 49, 48, 50, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -86, 40, 42, 8, 34, -127, -120, 10, 0, 33, 8, -128, -128, -128, 9, 9, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 18, 68, -111, 36, -110, 2, 72, 18, 56, 33, 113, 32, -124, -39, 9, 9, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 36, -126, 0, 72, 2, 2, 32, 32, 32, -124, -128, 8, 8, -96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 64, 17, 4, -126, 0, 72, 31, 2, 33, 17, -16, 4, -119, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 17, 32, -126, 2, 8, 32, 2, 32, 18, 16, 0, -120, -120, -120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 56, 17, 28, 114, 3, -56, 33, 60, 33, 10, 16, 120, -124, 112, -15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        //logo FIF besar
        byte[] fif = new byte[]{2, 27, 65, 27, 73, 71, 49, 27, 80, 83, 27, 27, 71, 72, 48, 48, 48, 57, 48, 48, 51, 51, 48, 52, 48, 48, 48, 50, 48, 49, 48, 49, 48, 54, 52, 48, 0, 0, 0, 15, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -1, -1, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -1, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -1, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 69, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, -1, -128, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -64, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, -128, 0, -6, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 7, -1, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 0, 15, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -4, 17, -1, -1, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 50, 53, 48, 48, 51, 51, 48, 52, 48, 48, 48, 51, 48, 49, 48, 49, 48, 57, 54, 48, 3, -4, 1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -4, 7, -1, -1, -1, -64, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, -16, 7, -1, -1, -1, -64, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -16, 31, -1, -1, -1, -64, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -32, 31, -2, 0, -1, -32, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -64, 31, -4, 0, 127, -16, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -64, 63, -16, 0, 63, -8, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -64, 127, -16, 0, 31, -8, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, -128, -1, -128, 0, 7, -4, 7, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 1, -1, 0, 0, 7, -4, 7, -64, 0, 0, 1, 119, 119, 112, 20, 1, 119, 119, 124, 0, 87, 116, 0, 23, 119, 116, 0, 1, 127, -16, 0, 116, 0, 3, 64, 119, 119, 80, 0, 15, 3, -2, 0, 0, 7, -4, 3, -64, 0, 0, 3, -1, -1, -8, 62, 3, -1, -1, -4, 2, -1, -2, 0, 63, -1, -2, 0, 3, -1, -2, 0, 126, 0, 3, -32, 127, -1, -6, 0, 31, 3, -2, 0, 16, 7, -4, 7, -64, 0, 0, 7, -1, -1, -8, 127, 1, -1, -1, -4, 7, -1, -1, -128, 127, -1, -1, 0, 7, -1, -1, 0, 127, 0, 7, -32, 127, -1, -1, 0, 30, 3, -2, 0, 120, 3, -4, 3, -32, 0, 0, 3, -1, -1, -8, 62, 1, -1, -1, -4, 15, -1, -1, -64, 63, -1, -1, -128, 15, -1, -1, -128, 126, 0, 3, -32, 127, -1, -1, -128, 30, 7, -4, 1, -2, 1, -4, 7, -16, 0, 0, 7, -11, 85, 80, 127, 1, -11, 85, 84, 63, -12, 95, -16, 127, 85, 127, -32, 127, -64, 127, -64, 126, 0, 7, -32, 127, 85, 127, -64, 28, 7, -24, 3, -2, 1, -4, 3, -32, 0, 0, 3, -32, 0, 0, 110, 3, -32, 0, 0, 47, -128, 3, -16, 46, 0, 15, -32, 127, -128, 15, -32, 126, 0, 3, -32, 110, 0, 15, -64, 28, 31, -16, 7, -1, 1, -4, 7, -16, 0, 0, 7, -16, 0, 0, 127, 1, -16, 0, 0, 63, 0, 3, -8, 127, 0, 23, -16, -1, 0, 7, -32, 127, 0, 7, -16, 127, 0, 23, -64, 24, 15, -32, 7, -2, 1, -4, 3, -32, 0, 0, 3, -32, 0, 0, 62, 1, -16, 0, 0, 62, 0, 1, -8, 62, 0, 3, -16, -2, 0, 3, -32, 126, 0, 3, -32, 126, 0, 7, -32, 28, 31, -32, 15, -4, 3, -4, 7, -16, 0, 0, 7, -32, 0, 0, 127, 1, -16, 0, 0, 124, 0, 0, 0, 127, 0, 7, -16, -4, 0, 3, -16, 126, 0, 7, -32, 126, 0, 7, -32, 8, 63, -32, 15, -8, 3, -4, 3, -32, 0, 0, 3, -32, 0, 0, 62, 3, -16, 0, 0, 124, 0, 0, 0, 62, 0, 3, -16, -8, 0, 3, -16, 126, 0, 3, -32, 126, 0, 7, -32, 24, 63, -64, 31, -8, 7, -4, 7, -16, 0, 0, 7, -16, 0, 0, 127, 1, -16, 0, 0, 124, 0, 0, 0, 127, 0, 7, -15, -4, 0, 1, -16, 127, 0, 7, -32, 127, 0, 7, -64, 0, 63, -128, 63, -8, 7, -4, 7, -32, 0, 0, 3, -32, 0, 0, 62, 1, -16, 0, 0, -4, 0, 0, 0, 62, 0, 15, -32, -8, 0, 1, -16, 126, 0, 3, -32, 126, 0, 15, -64, 0, 63, -64, 127, -16, 7, -4, 7, -16, 0, 0, 7, -32, 0, 0, 127, 1, -16, 0, 0, 124, 0, 0, 0, 127, 68, 95, -32, -8, 0, 1, -16, 126, 0, 7, -32, 127, 68, 127, -64, 0, 47, 0, -1, -64, 15, -8, 15, -32, 0, 0, 3, -1, -1, -64, 126, 3, -1, -1, -32, 124, 0, 127, -8, 63, -1, -1, 0, -8, 0, 1, -16, 126, 0, 3, -32, 127, -1, -1, -64, 0, 63, 1, -1, -64, 31, -8, 31, -16, 0, 0, 7, -1, -1, -64, 127, 1, -1, -1, -16, 124, 0, 127, -8, 127, -1, -4, 1, -8, 0, 1, -16, 127, 0, 7, -16, 127, -1, -1, -64, 13, 10, 27, 71, 72, 48, 48, 52, 57, 48, 48, 51, 51, 48, 52, 48, 48, 48, 51, 48, 49, 48, 49, 48, 57, 54, 48, 0, 62, 1, -1, -128, 31, -8, 15, -32, 0, 0, 3, -1, -1, -64, 62, 1, -1, -1, -32, -4, 0, 127, -8, 63, -1, -16, 0, -8, 0, 1, -8, 126, 0, 3, -32, 127, -1, -2, 0, 0, 126, 1, -1, 0, 63, -16, 31, -64, 0, 0, 7, -1, -1, -64, 127, 1, -1, -1, -32, 124, 0, 127, -8, 127, -1, -4, 0, -4, 0, 1, -16, 126, 0, 7, -32, 127, -1, -4, 0, 0, 124, 3, -2, 0, 127, -32, 31, -128, 0, 0, 3, -32, 0, 0, 62, 3, -16, 0, 0, 124, 0, 0, -8, 62, 0, -2, 0, -8, 0, 3, -16, 126, 0, 3, -32, 126, 0, 0, 0, 0, 124, 3, -1, 0, 127, -64, 31, -128, 0, 0, 7, -32, 0, 0, 127, 1, -16, 0, 0, 124, 0, 1, -8, 127, 0, 127, 0, -4, 0, 3, -16, 127, 0, 7, -64, 127, 0, 0, 0, 0, -4, 3, -4, 0, -1, -64, 63, -128, 0, 0, 3, -32, 0, 0, 62, 1, -16, 0, 0, 62, 0, 1, -8, 62, 0, 63, -128, -2, 0, 3, -32, 63, 0, 7, -64, 126, 0, 0, 0, 0, 124, 7, -4, 1, -1, -64, 127, 0, 0, 0, 7, -32, 0, 0, 127, 1, -16, 0, 0, 127, 0, 3, -16, 127, 0, 31, -128, -2, 0, 7, -32, 127, 0, 7, -64, 126, 0, 0, 0, 0, -20, 15, -8, 1, -1, -128, 110, 0, 0, 0, 3, -32, 0, 0, 110, 1, -16, 0, 0, 63, 0, 3, -16, 62, 0, 31, -128, 127, 0, 15, -32, 63, -128, 15, -64, 126, 0, 0, 0, 17, -4, 31, -16, 23, -1, 1, -2, 0, 0, 0, 7, -16, 0, 0, 127, 1, -16, 0, 0, 31, -1, -1, -16, 127, 0, 31, -16, 127, -47, 127, -64, 31, -1, -1, -64, 127, 0, 0, 0, 0, -8, 31, -16, 7, -4, 1, -2, 0, 0, 0, 3, -32, 0, 0, 62, 1, -16, 0, 0, 15, -1, -1, -64, 62, 0, 15, -32, 31, -1, -1, -128, 15, -1, -1, -128, 62, 0, 0, 0, 0, -16, 31, -16, 7, -4, 3, -4, 0, 0, 0, 7, -32, 0, 0, 127, 1, -16, 0, 0, 7, -1, -1, -64, 127, 0, 7, -16, 15, -1, -1, 0, 15, -1, -1, 0, 126, 0, 0, 0, 0, -16, 31, -32, 7, -8, 3, -8, 0, 0, 0, 3, -32, 0, 0, 126, 3, -16, 0, 0, 3, -1, -1, 0, 62, 0, 3, -16, 7, -1, -2, 0, 3, -1, -4, 0, 126, 0, 0, 0, 1, -16, 31, -64, 31, -16, 7, -16, 0, 0, 0, 1, 0, 0, 0, 16, 1, 0, 0, 0, 0, 17, 16, 0, 16, 0, 1, 16, 0, 95, -48, 0, 0, 17, 0, 0, 16, 0, 0, 0, 0, -32, 31, -128, 31, -32, 15, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -16, 63, -64, 63, -64, 31, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, 63, -128, 63, -128, 63, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -16, 63, 0, 127, -112, 63, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 96, 62, 0, -1, -128, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 126, 0, -1, 0, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 126, 0, -2, 0, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 126, 1, -4, 1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 126, 0, -8, 3, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 126, 1, -8, 7, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 110, 3, -24, 7, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 124, 7, -16, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 55, 51, 48, 48, 51, 51, 48, 52, 48, 48, 48, 51, 48, 49, 48, 49, 48, 57, 54, 48, 0, 0, 126, 3, -16, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 124, 7, -32, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 7, -64, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 124, 7, -64, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 15, -128, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 15, -128, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 10, 27, 71, 72, 48, 48, 57, 57, 48, 48, 51, 51, 48, 52, 48, 48, 48, 50, 48, 49, 48, 49, 48, 53, 54, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -128, 0, 0, 0, 24, 2, 7, -57, -13, -16, 48, 3, -31, -8, 124, 16, 71, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 16, 2, 8, 1, 2, 8, 48, 2, 33, 12, 98, 48, 2, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 4, 4, 65, -64, 64, 0, 4, 80, 5, 8, 1, 6, 0, 88, 4, 17, 4, 65, 16, 70, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -68, 62, 46, -16, -8, 120, -24, 14, 56, 9, 8, 1, 2, 8, 72, 4, 1, 4, 1, 32, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 114, 50, 17, 17, -103, -35, -48, 19, 16, 29, -123, 17, 7, 88, 92, 4, 17, 92, 65, 16, 70, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 66, 0, 33, 0, -120, -120, -128, 33, 16, 8, -125, -63, 3, -32, -116, 12, -15, -8, 1, -80, 3, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 66, 126, 33, 1, -127, -4, -64, 33, 16, 29, -64, 33, 6, 48, -44, 4, 17, 16, 65, 16, 70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 66, 0, 33, 0, -128, -128, -128, 33, 16, 63, -64, 33, 2, 16, -4, 4, 1, 8, 1, 48, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67, 0, 33, 1, -119, -127, -64, 49, 16, 49, 80, 97, 3, 25, -124, 4, 17, 12, 67, 16, 70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 66, 34, 33, 0, -120, -120, -128, 18, 16, 32, 8, -31, 2, 9, 2, 2, 33, 8, 34, 8, -126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 66, 30, 33, 1, -16, 112, -64, 28, 16, 96, 71, -63, 6, 9, 2, 1, -63, 4, 28, 7, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Totallen = Data.getBytes().length + fif.length + 1;
        ByteBuffer sendBuffer = ByteBuffer.allocate(Totallen);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = 0;

        for (int i = 0; i < len; i++) SendBuffer[i] = Buff[i];

        byte[] SendData;
        try {
            SendData = Data.getBytes(encoding);
        } catch (NullPointerException nE) {

            nE.printStackTrace();
            SendData = Data.getBytes();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            SendData = Data.getBytes();
        }
        for (int i = 0; i < fif.length; i++) SendBuffer[(i)] = fif[i];
        for (int i = 0; i < SendData.length; i++) SendBuffer[(i + fif.length)] = SendData[i];
        SendBuffer[(SendBuffer.length - 1)] = 0;

        this.recievedState = true;
        this.recvBytes = 0;

        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "PrintText_write_ERROR");
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintText_Unable to write; closed the socket and get out");
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        return returnValue[0];
    }

    public int PrintTextZebra(String Data, String encoding) {
        byte[] Buff = new byte[2048];
        int len = 0;
        int Totallen = 0;
        int[] returnValue = new int[1];
        byte[] fif = new byte[]{};
        Totallen = Data.getBytes().length + fif.length + 1;
        ByteBuffer sendBuffer = ByteBuffer.allocate(Totallen);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = 0;

        for (int i = 0; i < len; i++) SendBuffer[i] = Buff[i];

        byte[] SendData;
        try {
            SendData = Data.getBytes(encoding);
        } catch (NullPointerException nE) {

            nE.printStackTrace();
            SendData = Data.getBytes();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            SendData = Data.getBytes();
        }
        for (int i = 0; i < fif.length; i++) SendBuffer[(i)] = fif[i];
        for (int i = 0; i < SendData.length; i++) SendBuffer[(i + fif.length)] = SendData[i];
        SendBuffer[(SendBuffer.length - 1)] = 0;

        this.recievedState = true;
        this.recvBytes = 0;

        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;
        SystemClock.sleep(500);
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "PrintText_write_ERROR");
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintText_Unable to write; closed the socket and get out");
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        return returnValue[0];
    }

    public int PrintText(String Data, int Alignment, int Attribute, int TextSize, String encoding) {
        byte[] Buff = new byte[200];

        int len = 0;
        int Totallen = 0;
        byte[] pszAlignMode = new byte[3];
        byte[] pszBaseMode = new byte[14];
        byte[] pszTsMode = new byte[3];
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "PrintText");

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "PrintText_Printer status fail : ");
            return this.StatusCheck;
        }

        returnValue[0] = 0;

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        len = MakeSecurityCode(Buff);

        returnValue[0] = prnAlignMode(Alignment, pszAlignMode);
        if (returnValue[0] == 201) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return 201;
        }

        returnValue[0] = prnBaseMode(Attribute, pszBaseMode);
        if (returnValue[0] == 201) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return 201;
        }

        returnValue[0] = prnTextSizeMode(TextSize, pszTsMode);
        if (returnValue[0] == 201) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return 201;
        }

        Totallen = len + pszAlignMode.length + pszBaseMode.length + pszTsMode.length + Data.getBytes().length + 1;
        ByteBuffer sendBuffer = ByteBuffer.allocate(Totallen);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = 0;

        for (int i = 0; i < len; i++) SendBuffer[i] = Buff[i];

        for (int i = 0; i < pszBaseMode.length; i++) SendBuffer[(len + i)] = pszBaseMode[i];

        for (int i = 0; i < pszAlignMode.length; i++)
            SendBuffer[(len + pszBaseMode.length + i)] = pszAlignMode[i];

        for (int i = 0; i < pszTsMode.length; i++)
            SendBuffer[(len + pszBaseMode.length + pszAlignMode.length + i)] = pszTsMode[i];

        byte[] SendData;
        try {
            SendData = Data.getBytes(encoding);
        } catch (NullPointerException nE) {

            nE.printStackTrace();
            SendData = Data.getBytes();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            SendData = Data.getBytes();
        }

        for (int i = 0; i < SendData.length; i++)
            SendBuffer[(len + pszBaseMode.length + pszAlignMode.length + pszTsMode.length + i)] = SendData[i];
        SendBuffer[(SendBuffer.length - 1)] = 0;

        this.recievedState = true;
        this.recvBytes = 0;
        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "PrintText_write_ERROR");
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintText_Unable to write; closed the socket and get out");
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        return returnValue[0];
    }

    public int GetStatus() {
        int[] returnValue = new int[1];
        int check_receiveData = 403;
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "GetStatus");

        if (!this.connectState) {
            if (ReConnect() != 0) {
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                Logger.e("BXLSERVICE", "GetStatus_Bluetooth reconnect fail");
                return 401;
            }
            SystemClock.sleep(50L);
        }

        this.MsgType = 0;
        this.recvBytes = 0;
        if (this.logenable)
            Log.i("BXLSERVICE", "GetStatus_receivedState to false......: current vlaue" + this.recievedState);
        this.recievedState = false;

        Log.i("BXLSERVICE", "GetStatus_write_DLE_02");
        returnValue[0] = write(czESC02);
        if (returnValue[0] == 402) {
            Logger.e("BXLSERVICE", "GetStatus_write_ERROR");
            this.recievedState = true;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        for (int i = 0; i <= 100; i++) {
            if (this.logenable) {
                Log.i("BXLSERVICE", "GetStatus :  recvBuffer[0]:" + this.recvBuffer[0] + "length :" + this.recvBytes + "receivedStatus " + this.recievedState);
            }

            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes == 1) {
                    if ((this.recvBuffer[0] & 0x4) == 4) {
                        if (this.m_lState == 604) {
                            this.recievedState = false;
                            this.MsgType = 2;
                        }
                        return 601;
                    }
                    if ((this.recvBuffer[0] & 0x40) == 64) {
                        if (this.m_lState == 604) {
                            this.recievedState = false;
                            this.MsgType = 2;
                        }
                        return 606;
                    }

                    Logger.e("BXLSERVICE", "GetStatus_paper sensor ok");
                    check_receiveData = 0;
                    break;
                }

                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 403;
            }

            Logger.e("BXLSERVICE", "GetStatus_readBuffer check repeat :" + i);
            SystemClock.sleep(100L);
        }

        if (check_receiveData != 0) {
            Logger.e("BXLSERVICE", "GetStatus_Unable to read; close the socket and get out");
            this.connectState = false;
            this.recievedState = true;
            this.recvBytes = 0;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetStatus_Unable to read; closed the socket and get out");
            return 403;
        }

        check_receiveData = 403;

        if (!this.connectState) {
            if (ReConnect() != 0) {
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                Logger.e("BXLSERVICE", "Bluetooth reconnect fail");
                return 401;
            }
            SystemClock.sleep(50L);
        }

        returnValue[0] = 0;
        this.MsgType = 0;
        this.recvBytes = 0;
        this.recievedState = false;
        Log.i("BXLSERVICE", "GetStatus_write_DLE_04");
        returnValue[0] = write(czESC04);
        if (returnValue[0] == 402) {
            Log.i("BXLSERVICE", "GetStatus_write_ERROR");
            this.recievedState = true;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes == 1) {
                    if ((this.recvBuffer[0] & 0x60) == 96) {
                        if (this.m_lState == 604) {
                            this.recievedState = false;
                            this.MsgType = 2;
                        }
                        return 602;
                    }

                    check_receiveData = 0;
                    break;
                }

                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 403;
            }

            SystemClock.sleep(100L);
            Logger.e("BXLSERVICE", "GetStatus_readBuffer check repeat :" + i);
        }

        if (check_receiveData != 0) {
            Logger.e("BXLSERVICE", "GetStatus_Unable to read; close the socket and get out");
            this.connectState = false;
            this.recievedState = true;
            this.recvBytes = 0;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetStatus_Unable to read; closed the socket and get out");

            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }

            return 403;
        }

        returnValue[0] = 0;

        if (this.Com_Mode != 2) {
            check_receiveData = 403;
            if (returnValue[0] == 0) {
                returnValue[0] = GetPowerStatus();
                if (returnValue[0] == 704) {
                    if (this.m_lState == 604) {
                        this.recievedState = false;
                        this.MsgType = 2;
                    }
                    return 603;
                }
                if ((returnValue[0] == 700) ||
                        (returnValue[0] == 701) ||
                        (returnValue[0] == 702) ||
                        (returnValue[0] == 703)) {
                    if (this.m_lState == 604) {
                        this.recievedState = false;
                        this.MsgType = 2;
                    }
                    return 0;
                }
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return returnValue[0];
            }
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int GetPowerStatus() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "GetPowerStatus");

        if (this.Com_Mode == 2) {
            Logger.e("BXLSERVICE", "fail : Opened Connection of the WiFi_TCP ");
            return 103;
        }

        if (!this.connectState) {
            if (ReConnect() != 0) {
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                Log.i("BXLSERVICE", "GetPowerStatus_Bluetooth reconnect fail");
                return 401;
            }
            SystemClock.sleep(50L);
        }

        returnValue[0] = 0;

        this.MsgType = 1;
        this.recvBytes = 0;
        returnValue[0] = write(GetCmdPower);

        if (returnValue[0] == 402) {
            Log.i("BXLSERVICE", "GetPowerStatus_write_ERROR");
            this.recievedState = true;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            this.connectState = false;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetPowerStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes == 4) {
                    if ((this.recvBuffer[0] == 55) && (this.recvBuffer[1] == 69) && (this.recvBuffer[3] == 0)) {
                        if ((this.recvBuffer[2] & 0x30) == 48) {
                            this.recvBytes = 0;
                            if (this.m_lState == 604) {
                                this.recievedState = false;
                                this.MsgType = 2;
                            }
                            return 700;
                        }
                        if ((this.recvBuffer[2] & 0x31) == 49) {
                            this.recvBytes = 0;
                            if (this.m_lState == 604) {
                                this.recievedState = false;
                                this.MsgType = 2;
                            }
                            return 701;
                        }
                        if ((this.recvBuffer[2] & 0x32) == 50) {
                            this.recvBytes = 0;
                            if (this.m_lState == 604) {
                                this.recievedState = false;
                                this.MsgType = 2;
                            }
                            return 702;
                        }
                        if ((this.recvBuffer[2] & 0x33) == 51) {
                            this.recvBytes = 0;
                            if (this.m_lState == 604) {
                                this.recievedState = false;
                                this.MsgType = 2;
                            }
                            return 703;
                        }
                        if ((this.recvBuffer[2] & 0x34) == 52) {
                            this.recvBytes = 0;
                            if (this.m_lState == 604) {
                                this.recievedState = false;
                                this.MsgType = 2;
                            }
                            return 704;
                        }

                        this.recvBytes = 0;
                        if (this.m_lState == 604) {
                            this.recievedState = false;
                            this.MsgType = 2;
                        }
                        return 606;
                    }

                    this.recvBytes = 0;
                    if (this.m_lState == 604) {
                        this.recievedState = false;
                        this.MsgType = 2;
                    }
                    return 403;
                }

                this.recvBytes = 0;
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "GetPowerStatusUnable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "GetPowerStatusUnable to read; closed the socket and get out");
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return 403;
    }

    public int SetCharacterSet(int Value) {
        Log.i("BXLSERVICE", "SetCharacterSet");
        if (((Value < 0) || (Value > 5)) &&
                ((16 > Value) || (Value > 19)) &&
                ((21 > Value) || (Value > 31)) &&
                ((33 > Value) || (Value > 41)) &&
                (Value != 255)) {
            this.m_cCodePage = 0;
            return 201;
        }
        this.m_cCodePage = Value;
        return 0;
    }

    public int CheckSetCharacterSet() {
        Log.i("BXLSERVICE", "CheckSetCharacterSet");
        return this.m_cCodePage;
    }

    public int SetInterCharacterSet(int Value) {
        Log.i("BXLSERVICE", "SetInterCharacterSet");
        if ((Value < 0) || (Value > 13)) {
            this.m_cInterCharSet = 0;
            return 201;
        }
        this.m_cInterCharSet = Value;
        return 0;
    }

    public int CheckSetInterCharacterSet() {
        Log.i("BXLSERVICE", "CheckSetCharacterSet");
        return this.m_cInterCharSet;
    }

    public int PrintEscText(byte[] Data) {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "PrintEscText");

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "PrintEscText_Printer status fail : ");
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        Logger.e("BXLSERVICE", "PrintEscText_Step0 : ");
        if (this.globaliOutBuffer_ByteBuffer != null) {
            this.globaliOutBuffer_ByteBuffer.clear();
            this.globaliOutBuffer_ByteBuffer = null;
        }
        if (this.globaliOutBuffer_iOutBuffer != null) {
            this.globaliOutBuffer_iOutBuffer = null;
            this.globaliOutBuffer_MaxLength = 0;
            this.globaliOutBuffer_Size = 0;
        }

        this.globaliOutBuffer_ByteBuffer = ByteBuffer.allocate(999);

        this.globaliOutBuffer_iOutBuffer = this.globaliOutBuffer_ByteBuffer.array();
        this.globaliOutBuffer_MaxLength = 999;
        this.globaliOutBuffer_Size = 0;
        for (int i = 0; i < this.globaliOutBuffer_iOutBuffer.length; i++)
            this.globaliOutBuffer_iOutBuffer[i] = 0;
        Logger.e("BXLSERVICE", "PrintEscText_Step1 : ");

        returnValue[0] = ptrPrintEscText(Data, Data.length);
        Logger.e("BXLSERVICE", "PrintEscText_Step2 : ");
        if (returnValue[0] != 0) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return returnValue[0];
        }

        ByteBuffer sendBuffer = ByteBuffer.allocate(this.globaliOutBuffer_Size);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++)
            SendBuffer[i] = this.globaliOutBuffer_iOutBuffer[i];

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;
        returnValue[0] = write(SendBuffer);

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "PrintEscText_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetStatus_Unable to write; closed the socket and get out");
        }
        this.recievedState = true;
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int PrintBarcode(byte[] Data, int DataSize, int Symbology, int Height, int Width, int Alignment, int TextPosition) {
        byte[] Buff = new byte[200];
        int len = 0;
        int Totallen = 0;
        byte[] pParsedData = new byte[1024];
        int[] uiParsedData = new int[1];
        byte[] pData = {27, 97};

        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "PrintBarcode");

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "PrintBarcode_Printer status fail : ");
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        for (int i = 0; i < Buff.length; i++) Buff[i] = 0;
        for (int i = 0; i < pParsedData.length; i++) pParsedData[i] = 0;
        len = MakeSecurityCode(Buff);

        uiParsedData[0] = 0;

        switch (Symbology) {
            case 200:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
                returnValue[0] = bcMake2st(Data, DataSize, Symbology, Height, Width, Alignment, TextPosition, pParsedData, uiParsedData);
                break;
            case 201:
            default:
                returnValue[0] = bcMake1st(Data, DataSize, Symbology, Height, Width, Alignment, TextPosition, pParsedData, uiParsedData);
        }

        if (returnValue[0] != 0) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return returnValue[0];
        }

        switch (Alignment) {
            case 0:
                pData[2] = 0;
                break;
            case 1:
                pData[2] = 1;
                break;
            case 2:
                pData[2] = 2;
                break;
            default:
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 201;
        }

        Totallen = len + uiParsedData[0] + pData.length;
        ByteBuffer sendBuffer = ByteBuffer.allocate(Totallen);
        byte[] SendBuffer = sendBuffer.array();

        for (int i = 0; i < len; i++) SendBuffer[i] = Buff[i];

        for (int i = 0; i < pData.length; i++) SendBuffer[(i + len)] = pData[i];

        for (int i = 0; i < uiParsedData[0]; i++)
            SendBuffer[(i + len + pData.length)] = pParsedData[i];

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;
        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "PrintBarcode_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintBarcode_Unable to write; closed the socket and get out");
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int PrintImageZebra(Bitmap bmp, int Width, int Alignment, int Level) {
        Bitmap orgImage = null;
        Bitmap sclaledImage = null;
        int aWidth = 0;
        int aHeight = 0;
        byte[] pData = {27, 97, 1};
        int[] pdwValue = new int[1];
        int bmp_bytewidth = 0;
        int BufferSize = 0;

        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "PrintImage ");
        Log.i("BXLSERVICE", "PrintImage_ExternalStorageDirectory " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("BXLSERVICE", "PrintImage_ExternalStorageState " + Environment.getExternalStorageState());

        if (((Width != -1) && (Width != -2) && (1 > Width)) ||
                (Alignment < 0) || (Alignment > 2) ||
                (Level < 0) || (Level > 100)) {
            Log.i("BXLSERVICE", "PrintImage_Width(or Alignment or Level)' value Error");
            return 201;
        }

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "Printer status fail : ");
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        if ((orgImage = bmp) == null) {
            Logger.e("BXLSERVICE", "bmp-- does not exist_PrintImage");
            return 201;
        }

        aHeight = orgImage.getHeight();
        aWidth = orgImage.getWidth();

        if (Width == -1)
            Width = 384;
        else if (Width == -2) {
            Width = aWidth;
        }
        if (aWidth == Width) {
            Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -1");
            if (Width <= 384) {
                if (aWidth % 4 == 0) {
                    aWidth = Width;
                } else {
                    Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -2");
                    aWidth -= aWidth % 4;
                    Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -3");
                }
            } else {
                aWidth = 384;
                aHeight = aHeight * aWidth / Width;
            }
        } else if (Width <= 384) {
            aHeight = aHeight * Width / aWidth;
            aWidth = Width;
        } else {
            aHeight = aHeight * 384 / aWidth;
            aWidth = 384;
        }

        Log.w("BXLSERVICE", "BXLSERVICE after  aWidth:" + aWidth + " aHeight:" + aHeight + " -4");
        sclaledImage = Bitmap.createScaledBitmap(orgImage, aWidth, aHeight, true);
        orgImage.recycle();
        orgImage = null;

        ByteBuffer DataRGB = ByteBuffer.allocate(aWidth * aHeight * 2);
        sclaledImage.copyPixelsToBuffer(DataRGB);
        sclaledImage.recycle();
        sclaledImage = null;

        bmp_bytewidth = aWidth / 8;
        if (aWidth % 8 != 0) bmp_bytewidth++;
        BufferSize = 11 + bmp_bytewidth * aHeight;
        ByteBuffer DataRGB_Out = ByteBuffer.allocate(BufferSize);
        byte[] temBuffer = DataRGB_Out.array();
        for (int i = 0; i < temBuffer.length; i++) temBuffer[i] = 0;
        Mono_Gsv(DataRGB.array(), DataRGB_Out.array(), aWidth, aHeight, pdwValue, Level);

        switch (Alignment) {
            case 0:
                pData[2] = 0;
                break;
            case 1:
                pData[2] = 1;
                break;
            case 2:
                pData[2] = 2;
                break;
            default:
                break;
        }

        DataRGB.clear();
        DataRGB = null;

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;

        if (returnValue[0] == 402) {
            this.recievedState = true;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            this.connectState = false;
            DataRGB_Out.clear();
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetPowerStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        int widthOfImageInBytes = (bmp.getWidth() + 7) / 8;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String header = "! 0 200 200 " + bmp.getHeight() + " 1\r\n";
        try {
            baos.write(header.getBytes());
            baos.write("CG ".getBytes());
            baos.write(String.valueOf(widthOfImageInBytes).getBytes());
            baos.write(" ".getBytes());
            baos.write(String.valueOf(bmp.getHeight()).getBytes());
            baos.write(" ".getBytes());
        } catch (Exception e) {
            FireCrash.log(e);
        }

        returnValue[0] = write(baos.toByteArray());

        DataRGB_Out.clear();
        DataRGB_Out = null;
        SystemClock.sleep(500);

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "PrintImage_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintImage_Unable to write; closed the socket and get out");
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public void printBitmap(Bitmap bitmap, int alignment, int width, int level,
                            boolean getResponse) {
        Log.i("BixolonPrinter", "++ printBitmap(" + bitmap + ", " + alignment
                + ", " + width + ", " + level + ") ++");

        if (bitmap == null) {
            return;
        }

        int MAX_WIDTH = 384;
        if (width == -1)
            width = MAX_WIDTH;
        else if ((width == 0) || (width < 0)) {
            width = bitmap.getWidth();
        }
        if (width > MAX_WIDTH) {
            width = MAX_WIDTH;
        }

        byte[] printerData = null;
        try {
            printerData = BitmapManager.bitmap2printerData(bitmap, width,
                    level, 0);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Logger.e("BixolonPrinter", e.toString());
            return;
        }

        if (printerData.length > 196480) {
            return;
        }

        int height = BitmapManager.getBitmapHeight(bitmap, width);

        int capacity = Command.ALIGNMENT_LEFT.length
                + Command.RASTER_BIT_IMAGE_NORMAL.length + 4
                + printerData.length;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        switch (alignment) {
            case 1:
                buffer.put(Command.ALIGNMENT_CENTER);
                break;
            case 2:
                buffer.put(Command.ALIGNMENT_RIGHT);
                break;
            default:
                buffer.put(Command.ALIGNMENT_LEFT);
        }

        buffer.put(Command.RASTER_BIT_IMAGE_NORMAL);
        int widthBytes = BitmapManager.bytesOfWidth(width);
        buffer.put((byte) (widthBytes % 256));
        buffer.put((byte) (widthBytes / 256));
        buffer.put((byte) (height % 256));
        buffer.put((byte) (height / 256));
        buffer.put(printerData);

        if (mServiceManager == null) {
            this.mServiceManager = new ServiceManager(hashCode(), context,
                    bixolonPrintManager.mHandler, null);
        }

        write(buffer.array());
    }

    public int PrintImage(Bitmap bmp, int Width, int Alignment, int Level) {
        Bitmap orgImage = null;
        Bitmap sclaledImage = null;
        int aWidth = 0;
        int aHeight = 0;
        byte[] pData = {27, 97, 1};
        int[] pdwValue = new int[1];
        int bmp_bytewidth = 0;
        int BufferSize = 0;

        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "PrintImage ");
        Log.i("BXLSERVICE", "PrintImage_ExternalStorageDirectory " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("BXLSERVICE", "PrintImage_ExternalStorageState " + Environment.getExternalStorageState());

        if (((Width != -1) && (Width != -2) && (1 > Width)) ||
                (Alignment < 0) || (Alignment > 2) ||
                (Level < 0) || (Level > 100)) {
            Log.i("BXLSERVICE", "PrintImage_Width(or Alignment or Level)' value Error");
            return 201;
        }

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "Printer status fail : ");
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        if ((orgImage = bmp) == null) {
            Logger.e("BXLSERVICE", "bmp-- does not exist_PrintImage");
            return 201;
        }

        aHeight = orgImage.getHeight();
        aWidth = orgImage.getWidth();

        if (Width == -1)
            Width = 384;
        else if (Width == -2) {
            Width = aWidth;
        }
        if (aWidth == Width) {
            Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -1");
            if (Width <= 384) {
                if (aWidth % 4 == 0) {
                    aWidth = Width;
                } else {
                    Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -2");
                    aWidth -= aWidth % 4;
                    Log.w("BXLSERVICE", "BXLSERVICE aWidth:" + aWidth + " -3");
                }
            } else {
                aWidth = 384;
                aHeight = aHeight * aWidth / Width;
            }
        } else if (Width <= 384) {
            aHeight = aHeight * Width / aWidth;
            aWidth = Width;
        } else {
            aHeight = aHeight * 384 / aWidth;
            aWidth = 384;
        }

        Log.w("BXLSERVICE", "BXLSERVICE after  aWidth:" + aWidth + " aHeight:" + aHeight + " -4");
        sclaledImage = Bitmap.createScaledBitmap(orgImage, aWidth, aHeight, true);
        orgImage.recycle();
        orgImage = null;


        ByteBuffer DataRGB = ByteBuffer.allocate(aWidth * aHeight * 2);
        sclaledImage.copyPixelsToBuffer(DataRGB);
        sclaledImage.recycle();
        sclaledImage = null;

        bmp_bytewidth = aWidth / 8;
        if (aWidth % 8 != 0) bmp_bytewidth++;
        BufferSize = 11 + bmp_bytewidth * aHeight;
        ByteBuffer DataRGB_Out = ByteBuffer.allocate(BufferSize);
        byte[] temBuffer = DataRGB_Out.array();
        for (int i = 0; i < temBuffer.length; i++) temBuffer[i] = 0;
        Mono_Gsv(DataRGB.array(), DataRGB_Out.array(), aWidth, aHeight, pdwValue, Level);

        switch (Alignment) {
            case 0:
                pData[2] = 0;
                break;
            case 1:
                pData[2] = 1;
                break;
            case 2:
                pData[2] = 2;
                break;
            default:
                break;
        }

        DataRGB.clear();
        DataRGB = null;

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;
        returnValue[0] = write(pData);

        if (returnValue[0] == 402) {
            this.recievedState = true;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            this.connectState = false;
            DataRGB_Out.clear();
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "GetPowerStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        returnValue[0] = write(DataRGB_Out.array());
        DataRGB_Out.clear();
        DataRGB_Out = null;
        SystemClock.sleep(500);

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "PrintImage_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "PrintImage_Unable to write; closed the socket and get out");
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int SetImage(int Number, String FileName, int Width, int Alignment, int Level) {
        Bitmap orgImage = null;
        Bitmap sclaledImage = null;
        int aWidth = 0;
        int aHeight = 0;

        byte[] pData = {27, 97};
        int[] aSendCount = new int[1];
        int bmp_bytewidth = 0;
        int BufferSize = 0;

        int needBytes = 0;

        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "SetImage " + FileName);
        Log.i("BXLSERVICE", "SetImage_ExternalStorageDirectory " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("BXLSERVICE", "SetImage_ExternalStorageState " + Environment.getExternalStorageState());

        if (((Width != -1) && (Width != -2) && (1 > Width)) ||
                (Alignment < 0) || (Alignment > 2) ||
                (Level < 0) || (Level > 100)) {
            Logger.e("BXLSERVICE", "SetImage_Width(or Alignment or Level)' value Error");
            return 201;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "SetImage_Printer status fail : " + returnValue[0]);
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            return returnValue[0];
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if ((orgImage = BitmapFactory.decodeFile(FileName, opts)) == null) {
            Logger.e("BXLSERVICE", FileName + " does not exist_SetImage");
            return 201;
        }

        aHeight = orgImage.getHeight();
        aWidth = orgImage.getWidth();

        if (Width == -1)
            Width = 384;
        else if (Width == -2) {
            Width = aWidth;
        }
        if (aWidth == Width) {
            if (Width > 384) {
                aWidth = 384;
                aHeight = aHeight * aWidth / Width;
            }
        } else if (Width <= 384) {
            aHeight = aHeight * Width / aWidth;
            aWidth = Width;
        } else {
            aHeight = aHeight * 384 / aWidth;
            aWidth = 384;
        }

        sclaledImage = Bitmap.createScaledBitmap(orgImage, aWidth, aHeight, true);
        orgImage.recycle();
        orgImage = null;

        if (this.logenable) {
            Log.i("BXLSERVICE", "SetImage: " + sclaledImage.getConfig() + " " +
                    sclaledImage.getWidth() + " " + sclaledImage.getHeight());
        }

        ByteBuffer DataRGB = ByteBuffer.allocate(aWidth * aHeight * 10);
        sclaledImage.copyPixelsToBuffer(DataRGB);
        sclaledImage.recycle();
        sclaledImage = null;

        bmp_bytewidth = aWidth / 8;
        if (aWidth % 8 != 0) bmp_bytewidth++;
        BufferSize = 11 + bmp_bytewidth * aHeight;
        ByteBuffer DataRGB_Out = ByteBuffer.allocate(BufferSize);

        Mono_Gsv(DataRGB.array(), DataRGB_Out.array(), aWidth, aHeight, aSendCount, Level);
        byte[] SetBuffer = DataRGB_Out.array();

        switch (Alignment) {
            case 0:
                pData[2] = 0;
                break;
            case 1:
                pData[2] = 1;
                break;
            case 2:
                pData[2] = 2;
                break;
            default:
                break;
        }

        if (Number == 1) {
            if (this.iImage1 != null) {
                this.iImage1_ByteBuffer.clear();
                this.iImage1_ByteBuffer = null;
                this.iImage1 = null;
                this.iImage1Size = 0;
            }

            needBytes = aSendCount[0] + 3 + 999;
            this.iImage1_ByteBuffer = ByteBuffer.allocate(needBytes);
            this.iImage1 = this.iImage1_ByteBuffer.array();
            for (int i = 0; i < this.iImage1.length; i++) this.iImage1[i] = 0;
            for (int i = 0; i < pData.length; i++) this.iImage1[i] = pData[i];
            for (int i = 0; i < SetBuffer.length; i++)
                this.iImage1[(i + pData.length)] = SetBuffer[i];
            this.iImage1Size = (aSendCount[0] + 3);
        } else {
            if (this.iImage2 != null) {
                this.iImage2_ByteBuffer.clear();
                this.iImage2_ByteBuffer = null;
                this.iImage2 = null;
                this.iImage2Size = 0;
            }

            needBytes = aSendCount[0] + 3 + 999;
            this.iImage2_ByteBuffer = ByteBuffer.allocate(needBytes);
            this.iImage2 = this.iImage2_ByteBuffer.array();
            for (int i = 0; i < this.iImage2.length; i++) this.iImage2[i] = 0;
            for (int i = 0; i < pData.length; i++) this.iImage2[i] = pData[i];
            for (int i = 0; i < SetBuffer.length; i++)
                this.iImage2[(i + pData.length)] = SetBuffer[i];
            this.iImage2Size = (aSendCount[0] + 3);
        }

        return returnValue[0];
    }

    public int LineFeed(int Value) {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "LineFeed");

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "LineFeed_Printer status fail : ");
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        ByteBuffer sendBuffer = ByteBuffer.allocate(ESC_LF.length * Value);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = 0;
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = ESC_LF[0];

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;

        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "LineFeed_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "LineFeed_Unable to write; closed the socket and get out");
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int MarkFeed(int Value) {
        int[] returnValue = new int[1];

        returnValue[0] = 0;
        Log.i("BXLSERVICE", "LineFeed");

        if (this.StatusCheck != 0) {
            Logger.e("BXLSERVICE", "MarkFeed_Printer status fail : " + returnValue[0]);
            return this.StatusCheck;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        returnValue[0] = 0;

        ByteBuffer sendBuffer = ByteBuffer.allocate(ESC_FF.length * Value);
        byte[] SendBuffer = sendBuffer.array();
        for (int i = 0; i < SendBuffer.length; i++) SendBuffer[i] = 0;
        for (int i = 0; i < SendBuffer.length; i++) {
            SendBuffer[i] = ESC_FF[0];
        }

        returnValue[0] = 0;
        this.recievedState = true;
        this.recvBytes = 0;
        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "MarkFeed_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "MarkFeed_Unable to write; closed the socket and get out");
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        return returnValue[0];
    }

    public int MsrReady() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "MsrReady");

        if ((iMsrReadMode == 8) || (this.m_lState == 605)) {
            return 103;
        }

        Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 1");

        if ((iMsrReadMode == 1) ||
                (iMsrReadMode == 2) ||
                (iMsrReadMode == 3) ||
                (iMsrReadMode == 4) ||
                (iMsrReadMode == 5) ||
                (iMsrReadMode == 6) ||
                (iMsrReadMode == 7)) {
            Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 2");

            if ((returnValue[0] = GetStatus()) != 0) {
                Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 3");
                if ((returnValue[0] != 604) && (returnValue[0] != 603)) {
                    Logger.e("BXLSERVICE", "MsrReady_Printer status fail : " + returnValue[0]);
                    Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 3");
                    if (this.m_lState == 604) {
                        this.recievedState = false;
                        this.MsgType = 2;
                    }
                    return returnValue[0];
                }
            }
            Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 5");
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }

            returnValue[0] = 0;
            this.m_lState = 604;

            this.MsgType = 2;
            this.recievedState = false;
            this.recvBytes = 0;
            return returnValue[0];
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 6");
        returnValue[0] = 103;

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        Log.w("BXLSERVICE", "BXLSERVICE MsrReady step 7");
        return returnValue[0];
    }

    public int MsrGetData() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "msrGetData");

        if (!this.connectState) {
            if (ReConnect() != 0) {
                Log.i("BXLSERVICE", "MsrGetData_Bluetooth reconnect fail");
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 401;
            }
            SystemClock.sleep(50L);
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        Log.i("BXLSERVICE", "msrGetData");
        if ((iMsrReadMode == 1) && (this.m_lState != 604)) {
            return 103;
        }

        returnValue[0] = write(MSR_TRACK123_READERMODE_COMMAND);
        if (returnValue[0] == 402) {
            this.connectState = false;
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            Logger.e("BXLSERVICE", "MsrReady_write_ERROR");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "MsrReady_Unable to write; closed the socket and get out");
        }

        return returnValue[0];
    }

    public int MsrCancel() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "MsrCancel");

        if ((iMsrReadMode == 8) || (this.m_lState == 605)) {
            return 103;
        }

        if (!this.connectState) {
            if ((ReConnect() != 0) &&
                    (returnValue[0] != 604) && (returnValue[0] != 603)) {
                Logger.e("BXLSERVICE", "MsrCancel_Bluetooth reconnect fail");
                if (this.m_lState == 604) {
                    this.recievedState = false;
                    this.MsgType = 2;
                }
                return 401;
            }

            SystemClock.sleep(50L);
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        if ((iMsrReadMode == 1) ||
                (iMsrReadMode == 2) ||
                (iMsrReadMode == 3) ||
                (iMsrReadMode == 4) ||
                (iMsrReadMode == 5) ||
                (iMsrReadMode == 6) ||
                (iMsrReadMode == 7)) {
            if (this.m_lState != 604) {
                return 0;
            }

            returnValue[0] = 0;
            this.recievedState = true;
            this.recvBytes = 0;
            returnValue[0] = write(MSR_CANCEL_READERMODE_COMMAND);

            if (returnValue[0] == 402) {
                this.connectState = false;
                Logger.e("BXLSERVICE", "MsrReady_write_ERROR");
                if (this.mConnectThread != null) {
                    this.mConnectThread.cancel();
                    this.mConnectThread = null;
                }
                if (this.mConnectedThread != null) {
                    this.mConnectedThread.interrupt();
                    this.bTerminate = true;
                    SystemClock.sleep(100L);
                    this.mConnectedThread.cancel();
                    this.mConnectedThread = null;
                }
                Logger.e("BXLSERVICE", "MsrReady_Unable to write; closed the socket and get out");
            }
            this.m_lState = 600;
            return returnValue[0];
        }
        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }
        returnValue[0] = 103;

        return returnValue[0];
    }

    public byte[] MsrTrack1() {
        Log.i("BXLSERVICE", "Msrtrack1");
        if ((!this.connectState) &&
                (ReConnect() != 0)) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            Logger.e("BXLSERVICE", "MsrTrack1_Bluetooth reconnect fail");
            return null;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        if (this.MsrTrack1_Status == 0) {
            for (int i = 0; i < this.MsrTrack1.length; i++) this.MsrTrack1[i] = 0;
            return null;
        }

        this.MsrTrack1_Status = 0;
        return this.MsrTrack1;
    }

    public byte[] MsrTrack2() {
        Log.i("BXLSERVICE", "Msrtrack2");
        if ((!this.connectState) &&
                (ReConnect() != 0)) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            Logger.e("BXLSERVICE", "MsrTrack2_Bluetooth reconnect fail");
            return null;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        if (this.MsrTrack2_Status == 0) {
            for (int i = 0; i < this.MsrTrack2.length; i++) this.MsrTrack2[i] = 0;
            return null;
        }

        this.MsrTrack2_Status = 0;
        return this.MsrTrack2;
    }

    public byte[] MsrTrack3() {
        Log.i("BXLSERVICE", "Msrtrack3");

        if ((!this.connectState) &&
                (ReConnect() != 0)) {
            if (this.m_lState == 604) {
                this.recievedState = false;
                this.MsgType = 2;
            }
            Logger.e("BXLSERVICE", "MsrTrack3_Bluetooth reconnect fail");
            return null;
        }

        if (this.m_lState == 604) {
            this.recievedState = false;
            this.MsgType = 2;
        }

        if (this.MsrTrack3_Status == 0) {
            for (int i = 0; i < this.MsrTrack3.length; i++) this.MsrTrack3[i] = 0;
            return null;
        }

        this.MsrTrack3_Status = 0;
        return this.MsrTrack3;
    }

    public int IcOn() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "icOn");

        if ((iMsrReadMode != 8) && (this.m_lState == 604)) {
            return 103;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "IcOn_Printer status fail : " + returnValue[0]);
            return returnValue[0];
        }

        returnValue[0] = write(IC_PowerON);
        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "IcOn_write_ERROR");
            this.recievedState = true;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "IcOn_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        returnValue[0] = 0;
        this.MsgType = 1;
        this.recvBytes = 0;

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes >= 4) {
                    if ((this.recvBuffer[0] == 2) && (this.recvBuffer[1] == -128)) {
                        if (this.recvBuffer[3] == 0) {
                            this.m_lState = 605;
                        }
                        this.recvBytes = 0;
                        return this.recvBuffer[3];
                    }

                    this.recvBytes = 0;
                    return 403;
                }

                this.recvBytes = 0;
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "IcOn_Unable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "IcOn_Unable to read; closed the socket and get out");
        return 403;
    }

    public int IcOff() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "icOff");

        if ((iMsrReadMode != 8) && (this.m_lState == 604)) {
            return 103;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "IcOff_Printer status fail : " + returnValue[0]);
            return returnValue[0];
        }

        returnValue[0] = write(IC_PowerOFF);
        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "IcOff_write_ERROR");
            this.recievedState = true;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "IcOff_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        returnValue[0] = 0;
        this.MsgType = 1;
        this.recvBytes = 0;

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes >= 4) {
                    if ((this.recvBuffer[0] == 2) && (this.recvBuffer[1] == -128)) {
                        if (this.recvBuffer[3] == 0) {
                            this.m_lState = 600;
                        }
                        this.recvBytes = 0;
                        return this.recvBuffer[3];
                    }

                    this.recvBytes = 0;
                    return 403;
                }

                this.recvBytes = 0;
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "IcOff_Unable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "IcOff_Unable to read; close the socket and get out");
        return 403;
    }

    public int IcGetStatus() {
        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "IcGetStatus");

        if ((iMsrReadMode != 8) && (this.m_lState == 604)) {
            return 103;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "IcGetStatus_Printer status fail : " + returnValue[0]);
            return returnValue[0];
        }

        returnValue[0] = write(IC_StatusGet);
        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "IcGetStatus_write_ERROR");
            this.recievedState = true;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);

                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "IcGetStatus_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        returnValue[0] = 0;
        this.MsgType = 1;
        this.recvBytes = 0;

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes >= 4) {
                    if ((this.recvBuffer[0] == 2) && (this.recvBuffer[1] == -128)) {
                        this.recvBytes = 0;
                        return this.recvBuffer[3];
                    }

                    this.recvBytes = 0;
                    return 403;
                }

                this.recvBytes = 0;
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "IcGetStatus_Unable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "IcGetStatus_Unable to read; close the socket and get out");
        return 403;
    }

    public int Directio(byte[] pSend, int uiLength, byte[] pReceive, int[] uiReceiveLength) {
        int[] returnValue = new int[1];
        returnValue[0] = 0;
        Log.i("BXLSERVICE", "Directio");

        if ((this.m_lState == 605) || (this.m_lState == 604)) {
            return 103;
        }

        if ((pSend == null) || (uiLength == 0) || (pReceive == null) || (uiReceiveLength[0] == 0)) {
            return 201;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "Directio_Printer status fail : " + returnValue[0]);
            return returnValue[0];
        }

        returnValue[0] = write(pSend);
        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "DirectioSendData_write_ERROR");
            this.recievedState = true;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "DirectioSendData_Unable to write; closed the socket and get out");
            return returnValue[0];
        }

        this.MsgType = 0;
        this.recvBytes = 0;

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes > uiReceiveLength[0]) {
                    this.recvBytes = 0;
                    this.recievedState = true;
                    return 102;
                }
                for (int counter = 0; counter < this.recvBytes; counter++) {
                    pReceive[counter] = this.recvBuffer[counter];
                }
                uiReceiveLength[0] = this.recvBytes;

                return 0;
            }
            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "Directio_Unable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "Directio_Unable to read; closed the socket and get out");
        return 403;
    }

    public int IcApdu(byte[] pApdu, int uiLength, byte[] pReceive, int[] uiReceiveLength) {
        int[] returnValue = new int[1];
        returnValue[0] = 0;

        Log.i("BXLSERVICE", "IcApdu");
        if ((iMsrReadMode != 8) && (this.m_lState == 604)) {
            uiReceiveLength[0] = 0;
            return 103;
        }

        if ((pApdu == null) || (uiLength == 0) || (pReceive == null) || (uiReceiveLength[0] == 0)) {
            uiReceiveLength[0] = 0;
            return 201;
        }

        if (((returnValue[0] = GetStatus()) != 0) &&
                (returnValue[0] != 604) && (returnValue[0] != 605) && (returnValue[0] != 603)) {
            Logger.e("BXLSERVICE", "IcApdu_Printer status fail : " + returnValue[0]);
            uiReceiveLength[0] = 0;
            return returnValue[0];
        }

        ByteBuffer sendBuffer = ByteBuffer.allocate(uiLength + IC_ApduHeader.length + 1);
        for (int i = 0; i < IC_ApduHeader.length; i++) sendBuffer.array()[i] = IC_ApduHeader[i];
        sendBuffer.array()[IC_ApduHeader.length] = ((byte) uiLength);
        for (int i = 0; i < uiLength; i++)
            sendBuffer.array()[(IC_ApduHeader.length + 1 + i)] = pApdu[i];

        this.MsgType = 3;
        returnValue[0] = write(sendBuffer.array());
        sendBuffer.clear();
        sendBuffer = null;

        if (returnValue[0] == 402) {
            this.connectState = false;
            Logger.e("BXLSERVICE", "IcApduSendData_write_ERROR");
            this.recievedState = true;
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.interrupt();
                this.bTerminate = true;
                SystemClock.sleep(100L);
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            Logger.e("BXLSERVICE", "IcApduSendData_Unable to write; closed the socket and get out");
            uiReceiveLength[0] = 0;
            return returnValue[0];
        }

        this.recievedState = false;
        this.recvBytes = 0;

        for (int i = 0; i <= 100; i++) {
            if ((this.recievedState) && (this.recvBytes != 0)) {
                if (this.recvBytes > uiReceiveLength[0]) {
                    this.recvBytes = 0;
                    this.recievedState = true;
                    uiReceiveLength[0] = 0;
                    return 102;
                }
                if ((this.recvBuffer[0] == 2) && (this.recvBuffer[1] == -128)) {
                    for (int counter = 0; counter < this.recvBytes - 5; counter++) {
                        pReceive[counter] = this.recvBuffer[(counter + 4)];
                    }
                    uiReceiveLength[0] = (this.recvBytes - 5);
                    return this.recvBuffer[3];
                }
                uiReceiveLength[0] = 0;
                return 403;
            }

            SystemClock.sleep(100L);
        }

        this.connectState = false;
        this.recievedState = true;
        this.recvBytes = 0;
        Logger.e("BXLSERVICE", "IcApdu_Unable to read; close the socket and get out");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.interrupt();
            this.bTerminate = true;
            SystemClock.sleep(100L);
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        Logger.e("BXLSERVICE", "IcApdu_Unable to read; close the socket and get out");
        return 403;
    }

    private void ptrmsrGetDataReceiveData(byte[] Data, int DataSize) {
        int at_1 = 0;
        int at_2 = 0;
        int at_3 = 0;
        int sz_1 = 0;
        int sz_2 = 0;
        int sz_3 = 0;
        int iTrackBuffer_Size = 0;
        byte[] iTrackBuffer = null;

        for (int i = 0; i < this.MsrTrack1.length; i++) this.MsrTrack1[i] = 0;
        this.MsrTrack1_Status = 0;
        for (int i = 0; i < this.MsrTrack2.length; i++) this.MsrTrack2[i] = 0;
        this.MsrTrack2_Status = 0;
        for (int i = 0; i < this.MsrTrack3.length; i++) this.MsrTrack3[i] = 0;
        this.MsrTrack3_Status = 0;

        iTrackBuffer_Size = DataSize;
        iTrackBuffer = Data;

        if ((iTrackBuffer_Size >= 3) &&
                (iTrackBuffer[0] == 2) && (65 <= iTrackBuffer[1]) && (iTrackBuffer[1] <= 70) && (iTrackBuffer[2] == 49) &&
                (iTrackBuffer[(iTrackBuffer_Size - 3)] == msr_track_tail[0]) &&
                (iTrackBuffer[(iTrackBuffer_Size - 2)] == msr_track_tail[1]) &&
                (iTrackBuffer[(iTrackBuffer_Size - 1)] == msr_track_tail[2])) {
            if (iTrackBuffer[1] <= 65) {
                at_1 = 5;
                sz_1 = iTrackBuffer_Size - 3 - at_1;
                for (int i = 0; i < sz_1; i++) this.MsrTrack1[i] = iTrackBuffer[(at_1 + i)];
                this.MsrTrack1_Status = 1;
            } else if (iTrackBuffer[1] <= 66) {
                at_2 = 5;
                sz_2 = iTrackBuffer_Size - 3 - at_2;
                for (int i = 0; i < sz_2; i++) this.MsrTrack2[i] = iTrackBuffer[(at_2 + i)];
                this.MsrTrack2_Status = 1;
            } else if (iTrackBuffer[1] <= 67) {
                at_1 = 6;
                for (int i = at_1; i < iTrackBuffer_Size; i++) {
                    if (iTrackBuffer[i] == 28) {
                        sz_1 = i - at_1;
                        at_2 = i + 1;
                        sz_2 = iTrackBuffer_Size - 3 - at_2;
                        break;
                    }
                }
                for (int i = 0; i < sz_1; i++) this.MsrTrack1[i] = iTrackBuffer[(at_1 + i)];
                this.MsrTrack1_Status = 1;
                for (int i = 0; i < sz_2; i++) this.MsrTrack2[i] = iTrackBuffer[(at_2 + i)];
                this.MsrTrack2_Status = 1;
            } else if (iTrackBuffer[1] <= 68) {
                at_3 = 5;
                sz_3 = iTrackBuffer_Size - 3 - at_3;
                for (int i = 0; i < sz_3; i++) this.MsrTrack3[i] = iTrackBuffer[(at_3 + i)];
                this.MsrTrack3_Status = 1;
            } else if (iTrackBuffer[1] <= 69) {
                at_2 = 6;
                for (int i = at_2; i < iTrackBuffer_Size; i++) {
                    if (iTrackBuffer[i] == 28) {
                        sz_2 = i - at_2;
                        at_3 = i + 1;
                        sz_3 = iTrackBuffer_Size - 3 - at_3;
                        break;
                    }
                }

                for (int i = 0; i < sz_2; i++) this.MsrTrack2[i] = iTrackBuffer[(at_2 + i)];
                this.MsrTrack2_Status = 1;
                for (int i = 0; i < sz_3; i++) this.MsrTrack3[i] = iTrackBuffer[(at_3 + i)];
                this.MsrTrack3_Status = 1;
            } else if (iTrackBuffer[1] <= 70) {
                at_1 = 6;
                for (int i = at_1; i < iTrackBuffer_Size; i++) {
                    if (iTrackBuffer[i] == 28) {
                        sz_1 = i - at_1;
                        at_2 = i + 1;
                        break;
                    }
                }
                for (int i = at_2; i < iTrackBuffer_Size; i++) {
                    if (iTrackBuffer[i] == 28) {
                        sz_2 = i - at_2;
                        at_3 = i + 1;
                        sz_3 = iTrackBuffer_Size - 3 - at_3;
                        break;
                    }
                }
                for (int i = 0; i < sz_1; i++) this.MsrTrack1[i] = iTrackBuffer[(at_1 + i)];
                this.MsrTrack1_Status = 1;
                for (int i = 0; i < sz_2; i++) this.MsrTrack2[i] = iTrackBuffer[(at_2 + i)];
                this.MsrTrack2_Status = 1;
                for (int i = 0; i < sz_3; i++) this.MsrTrack3[i] = iTrackBuffer[(at_3 + i)];
                this.MsrTrack3_Status = 1;
            }
        }
    }

    private int ptrPrintEscText(byte[] Data, int DataSize) {
        int lResultCode = 0;
        byte[] Buff = new byte[300];
        int len = 0;

        int nData = 0;
        byte[] szBuf = null;

        byte cPrintMode = 0;
        byte cOldPrintMode = 0;
        byte cIntialMode = 0;
        int nNumber = 0;
        byte lowchar = 0;
        byte highchar = 0;
        byte[] strBasePrinterSet = null;
        int strBasePrinterSetSize = 0;
        byte[] ESC_UnderLinethick = {27, 45};

        boolean bChrSizeChange = false;
        byte[] strChrSizeCmd = new byte[3];
        int num = 0;
        byte[] aBuf = new byte[1];
        int cnt = 0;
        int returningValue = 0;

        byte[] iPrinterSettings = new byte[12];
        int iPrinterSettingsSize = 0;

        boolean ProcessCharacter = true;

        byte[] s_Defaults = {
                27, 61, 1,
                27, 97};
        byte TChar = 0;

        byte[] s_PrintMode = {27, 33};

        for (int i = 0; i < Buff.length; i++) Buff[i] = 0;
        len = MakeSecurityCode(Buff);

        inputOutBuffer(Buff, len);
        ByteBuffer TemData_ByteBuffer = ByteBuffer.allocate(Data.length + 1);
        szBuf = TemData_ByteBuffer.array();
        for (int i = 0; i < Data.length; i++) szBuf[i] = Data[i];
        szBuf[Data.length] = 0;

        for (int i = 0; i < s_Defaults.length; i++) iPrinterSettings[i] = s_Defaults[i];
        iPrinterSettingsSize = s_Defaults.length;

        if (this.m_cCodePage >= 0) {
            TChar = 27;
            iPrinterSettings[iPrinterSettingsSize] = TChar;
            iPrinterSettingsSize++;
            TChar = 116;
            iPrinterSettings[iPrinterSettingsSize] = TChar;
            iPrinterSettingsSize++;
            TChar = (byte) (this.m_cCodePage & 0xFF);
            iPrinterSettings[iPrinterSettingsSize] = TChar;
            iPrinterSettingsSize++;
        }

        for (int i = 0; i < s_PrintMode.length; i++)
            iPrinterSettings[(i + iPrinterSettingsSize)] = s_PrintMode[i];
        iPrinterSettingsSize += s_PrintMode.length;

        strBasePrinterSet = iPrinterSettings;
        strBasePrinterSetSize = iPrinterSettingsSize;

        inputOutBuffer(strBasePrinterSet, strBasePrinterSetSize);

        while (szBuf[num] != 0) {
            switch (szBuf[num]) {
                case 13:
                    if (cOldPrintMode != cPrintMode) {
                        cOldPrintMode = cPrintMode;
                        cPrintMode = cOldPrintMode;
                        ESC_FontChar[2] = cPrintMode;

                        inputOutBuffer(ESC_FontChar, ESC_FontChar.length);
                    }

                    if (bChrSizeChange) {
                        bChrSizeChange = false;

                        inputOutBuffer(strChrSizeCmd, strChrSizeCmd.length);
                    }

                    inputOutBuffer(ESC_CR, ESC_CR.length);
                    num++;

                    break;
                case 10:
                    if (cOldPrintMode != cPrintMode) {
                        cOldPrintMode = cPrintMode;
                        ESC_FontChar[2] = cPrintMode;

                        inputOutBuffer(ESC_FontChar, ESC_FontChar.length);
                    }

                    if (bChrSizeChange) {
                        bChrSizeChange = false;

                        inputOutBuffer(strChrSizeCmd, strChrSizeCmd.length);
                    }

                    inputOutBuffer(ESC_LF, ESC_LF.length);
                    num++;

                    break;
                case 27:
                    if (szBuf[(num + 1)] != 124) {
                        ProcessCharacter = false;
                    }

                    if (ProcessCharacter) {
                        nNumber = 0;
                        lowchar = 45;
                        highchar = 45;
                        cnt = 0;

                        if ((szBuf[(num + 2 + cnt)] >= 48) && (szBuf[(num + 2 + cnt)] <= 57)) {
                            nNumber = 0;
                            do {
                                nNumber = nNumber * 10 + (szBuf[(num + 2 + cnt)] - 48);
                                cnt++;
                            }
                            while ((szBuf[(num + 2 + cnt)] >= 48) && (szBuf[(num + 2 + cnt)] <= 57));
                        }

                    }

                    if (ProcessCharacter) {
                        if ((szBuf[(num + 2 + cnt)] >= 97) && (szBuf[(num + 2 + cnt)] <= 122)) {
                            lowchar = szBuf[(num + 2 + cnt)];
                            cnt++;
                            if (((szBuf[(num + 2 + cnt)] >= 65) && (szBuf[(num + 2 + cnt)] <= 90)) || ((szBuf[(num + 2 + cnt)] >= 97) && (szBuf[(num + 2 + cnt)] <= 122))) {
                                highchar = szBuf[(num + 2 + cnt)];
                                cnt++;
                            } else {
                                lResultCode = 202;
                                ProcessCharacter = false;
                            }
                        } else if ((szBuf[(num + 2 + cnt)] >= 65) && (szBuf[(num + 2 + cnt)] <= 90)) {
                            highchar = szBuf[(num + 2 + cnt)];
                            cnt++;
                        } else if (szBuf[(num + 2 + cnt)] != 118) {
                            lResultCode = 202;
                            ProcessCharacter = false;
                        }

                    }

                    if (ProcessCharacter) {
                        num = num + 2 + cnt;

                        switch (highchar) {
                            case 66:
                                switch (nNumber) {
                                    case 1:
                                        if (this.iImage1 != null) {
                                            inputOutBuffer(this.iImage1, this.iImage1Size);
                                        }

                                        break;
                                    case 2:
                                        if (this.iImage2 != null) {
                                            inputOutBuffer(this.iImage2, this.iImage2Size);
                                        }

                                        break;
                                    default:
                                        lResultCode = 202;
                                }

                                break;
                            case 70:
                                if (nNumber < 1)
                                    nNumber = 1;
                                if (nNumber != 0) {
                                    switch (lowchar) {
                                        case 108:
                                            inputOutBuffer_uiCount(ESC_LF, ESC_LF.length, nNumber);

                                            break;
                                        default:
                                            lResultCode = 202;
                                            ProcessCharacter = false;
                                    }

                                }

                                break;
                            case 67:
                                switch (lowchar) {
                                    case 98:
                                        if (nNumber == 0)
                                            cPrintMode = (byte) (cPrintMode | 0x8);
                                        else {
                                            lResultCode = 202;
                                        }

                                        break;
                                    case 117:
                                        if (nNumber == 0) {
                                            cPrintMode = (byte) (cPrintMode | 0x80);
                                        } else if (nNumber == 1) {
                                            ESC_UnderLinethick[2] = 1;

                                            inputOutBuffer(ESC_UnderLinethick, ESC_UnderLinethick.length);
                                        } else if (nNumber == 2) {
                                            ESC_UnderLinethick[2] = 2;

                                            inputOutBuffer(ESC_UnderLinethick, ESC_UnderLinethick.length);
                                        } else {
                                            lResultCode = 202;
                                        }

                                        break;
                                    case 114:
                                        if (nNumber == 0) {
                                            inputOutBuffer(GS_B_Enabled, GS_B_Enabled.length);
                                        } else lResultCode = 202;

                                        break;
                                    case 45:
                                        if ((1 <= nNumber) && (nNumber <= 4)) {
                                            cPrintMode = (byte) (cPrintMode & 0xCF);

                                            if ((nNumber == 2) || (nNumber == 4)) {
                                                cPrintMode = (byte) (cPrintMode | 0x20);
                                            }

                                            if (nNumber >= 3) {
                                                cPrintMode = (byte) (cPrintMode | 0x10);
                                            }
                                        }

                                        break;
                                    case 104:
                                        if ((nNumber < 1) || (nNumber > 8)) {
                                            lResultCode = 202;
                                        } else {
                                            GS_FontSize[2] = ((byte) (GS_FontSize[2] & 0xF));
                                            int tmp1366_1365 = 2;
                                            byte[] tmp1366_1362 = GS_FontSize;
                                            tmp1366_1362[tmp1366_1365] = ((byte) (tmp1366_1362[tmp1366_1365] + (byte) ((nNumber - 1) * 16 & 0xFF)));
                                            bChrSizeChange = true;
                                            for (int i = 0; i < GS_FontSize.length; i++)
                                                strChrSizeCmd[i] = GS_FontSize[i];
                                        }

                                        break;
                                    case 118:
                                        if ((nNumber < 1) || (nNumber > 8)) {
                                            lResultCode = 202;
                                        } else {
                                            GS_FontSize[2] = ((byte) (GS_FontSize[2] & 0xF0));
                                            int tmp1457_1456 = 2;
                                            byte[] tmp1457_1453 = GS_FontSize;
                                            tmp1457_1453[tmp1457_1456] = ((byte) (tmp1457_1453[tmp1457_1456] + (byte) (nNumber - 1 & 0xFF)));
                                            bChrSizeChange = true;
                                            for (int i = 0; i < GS_FontSize.length; i++)
                                                strChrSizeCmd[i] = GS_FontSize[i];
                                        }

                                        break;
                                    default:
                                        lResultCode = 202;
                                }

                                break;
                            case 65:
                                if (nNumber != 0)
                                    lResultCode = 202;
                                switch (lowchar) {
                                    case 99:
                                        inputOutBuffer(ESC_AlignCenter, ESC_AlignCenter.length);
                                        break;
                                    case 114:
                                        inputOutBuffer(ESC_AlignRight, ESC_AlignRight.length);
                                        break;
                                    case 108:
                                        inputOutBuffer(ESC_AlignNormal, ESC_AlignNormal.length);
                                        break;
                                    default:
                                        lResultCode = 202;
                                }

                                break;
                            case 78:
                                inputOutBuffer(strBasePrinterSet, strBasePrinterSetSize);

                                GS_FontSize[2] = 0;
                                inputOutBuffer(GS_FontSize, GS_FontSize.length);

                                cPrintMode = cIntialMode;

                                inputOutBuffer(GS_B_Disabled, GS_B_Disabled.length);

                                break;
                            default:
                                lResultCode = 202;
                        }

                        if (ProcessCharacter) {
                            continue;
                        }
                    }
                    break;
                default:
                    break;
            }

            if (!ProcessCharacter) ProcessCharacter = true;

            if (cOldPrintMode != cPrintMode) {
                cOldPrintMode = cPrintMode;
                ESC_FontChar[2] = cPrintMode;

                inputOutBuffer(ESC_FontChar, ESC_FontChar.length);
            }
            if (bChrSizeChange) {
                bChrSizeChange = false;

                inputOutBuffer(strChrSizeCmd, strChrSizeCmd.length);
            }
            aBuf[0] = szBuf[num];
            num++;

            int i = inputOutBuffer(aBuf, 1);
        }

        inputOutBuffer(strBasePrinterSet, strBasePrinterSetSize);

        int ptrErr = inputOutBuffer(GS_B_Disabled, GS_B_Disabled.length);
        return 0;
    }

    private int RandomFun(int startNum, int endNum) {
        if(rand == null){
            rand = new Random();
        }
        int ranNum = rand.nextInt(32767);
        if (ranNum < 0) ranNum *= -1;
        return ranNum % (endNum - startNum + 1) + startNum;
    }

    private int MakeSecurityCode(byte[] Buff) {
        int kCount = 0;

        int Dbc = 0;
        byte[] SECURITY_COMMAND = {27, 29, 0, 28, 122, 0};

        byte[] PRINTMODE_COMMAND = {27, 33, 16};
        byte[] JUSTIFICATION_COMMAND = {27, 97, 2};
        byte[] CHARACTERSIZE_COMMAND = {29, 33, 16};
        byte[] CUTTING_COMMAND = {29, 86, 66};
        byte[] NVDOWNLOAD_COMMAND = {28, 113, 1};
        byte[] SECURITY_DATA = {66, 73, 88, 79, 76,
                79, 78, 80, 82, 73,
                78, 84, 69, 82, 68,
                82, 73, 86, 69, 82,
                83, 82, 80, 6, 51,
                53, 48, 80, 108, 117};
        int m;
        do {
            m = RandomFun(0, 300);
            m %= 100;
        } while ((m < 81) || (m >= 100));
        int n;
        do {
            n = RandomFun(0, 300);
            n %= 30;
        } while ((n <= 0) || (n >= 30) ||
                (m % n == 0));

        int T = m % n;
        int X = T / 5;
        int Y = T % 5;
        int k = X + Y;

        if (k < 10) {
            k = 20 - k;
        }
        SECURITY_COMMAND[2] = ((byte) (m & 0xFF));
        SECURITY_COMMAND[5] = ((byte) (n & 0xFF));

        for (int i = 0; i < 6; i++) {
            Buff[(Dbc++)] = SECURITY_COMMAND[i];
        }
        for (int i = 0; i < Y + 2; i++) {
            Buff[(Dbc++)] = SECURITY_DATA[(kCount++)];
        }
        switch (Y) {
            case 0:
                for (int i = 0; i < 3; i++)
                    Buff[(Dbc++)] = PRINTMODE_COMMAND[i];
                break;
            case 1:
                for (int i = 0; i < 3; i++)
                    Buff[(Dbc++)] = JUSTIFICATION_COMMAND[i];
                break;
            case 2:
                for (int i = 0; i < 3; i++)
                    Buff[(Dbc++)] = CHARACTERSIZE_COMMAND[i];
                break;
            case 3:
                for (int i = 0; i < 3; i++)
                    Buff[(Dbc++)] = CUTTING_COMMAND[i];
                break;
            case 4:
                for (int i = 0; i < 3; i++)
                    Buff[(Dbc++)] = NVDOWNLOAD_COMMAND[i];
                break;
            default:
                for (int i = 0; i < 3; i++) {
                    Buff[(Dbc++)] = PRINTMODE_COMMAND[i];
                }
        }

        for (int i = kCount + 3; i < k; i++) {
            Buff[(Dbc++)] = SECURITY_DATA[(kCount++)];
        }
        return Dbc;
    }

    private void Mono_Gsv(byte[] DataRGB, byte[] DataRGB_Out, int cxBitmap, int cyBitmap, int[] pdwValue, int Level) {
        byte[] aColor = new byte[3];

        int SendBufferCount = 0;
        byte[] Buffer = null;

        byte color = 0;

        byte[] dbBrightness = {1, 5, 8, 7, 9, 3, 4, 2, 6};

        int count = 0;

        int bmp_bitwidth = cxBitmap;
        int bmp_height = cyBitmap;
        int bmp_bytewidth = bmp_bitwidth / 8;

        Buffer = DataRGB_Out;

        SendBufferCount = 8;

        Level /= 10;
        if (Level > 9)
            Level = 9;
        else if (Level < 0) {
            Level = 0;
        }
        for (int y = 0; y < bmp_height; y++) {
            count = 0;
            for (int x = 0; x < bmp_bitwidth; x++) {
                GetPixel(DataRGB, cxBitmap, cyBitmap, aColor, x, y);
                if (dbBrightness[(x % 3 + y % 3 * 3)] <= Level)
                    color = 0;
                else if ((aColor[0] == 31) && (aColor[1] == 63) && (aColor[2] == 31))
                    color = 0;
                else {
                    color = 1;
                }
                byte shift = (byte) (7 - x % 8);
                int tmp224_222 = SendBufferCount;
                byte[] tmp224_220 = Buffer;
                tmp224_220[tmp224_222] = ((byte) ((tmp224_220[tmp224_222] & 0xff) + (color << shift)));

                if (count == 7) {
                    SendBufferCount++;
                    count = 0;
                } else {
                    count++;
                }
            }
            if (count != 0) SendBufferCount++;

        }

        Buffer[0] = 29;
        Buffer[1] = 118;
        Buffer[2] = 48;
        Buffer[3] = 0;
        Buffer[4] = ((byte) (bmp_bytewidth % 256));
        Buffer[5] = ((byte) (bmp_bytewidth / 256));
        Buffer[6] = ((byte) (bmp_height % 256));
        Buffer[7] = ((byte) (bmp_height / 256));

        pdwValue[0] = SendBufferCount;
    }

    private int prnAlignMode(int Alignment, byte[] pAlignMode) {
        switch (Alignment) {
            case 0:
                for (int i = 0; i < ESC_AlignNormal.length; i++) pAlignMode[i] = ESC_AlignNormal[i];
                break;
            case 1:
                for (int i = 0; i < ESC_AlignCenter.length; i++) pAlignMode[i] = ESC_AlignCenter[i];
                break;
            case 2:
                for (int i = 0; i < ESC_AlignRight.length; i++) pAlignMode[i] = ESC_AlignRight[i];
                break;
            default:
                return 201;
        }

        return 0;
    }

    private int prnBaseMode(int Attribute, byte[] pBaseMode) {
        int nPos = 0;
        byte cMode = 0;
        byte[] ESC_Init = new byte[2];
        byte[] ChrSet = {27, 116, 0};
        byte[] IChrSet = {27, 82, 0};

        if ((Attribute < 0) || (15 < Attribute)) {
            return 201;
        }

        for (int i = 0; i < ESC_Init.length; i++) pBaseMode[i] = ESC_Init[i];
        nPos += ESC_Init.length;

        if (Attribute == 1) {
            cMode = (byte) (cMode | 0x1);
        }
        if (Attribute == 2) {
            cMode = (byte) (cMode | 0x8);
        }
        if (Attribute == 4) {
            cMode = (byte) (cMode | 0x80);
        }
        ESC_FontChar[2] = cMode;
        for (int i = 0; i < ESC_FontChar.length; i++) pBaseMode[(nPos + i)] = ESC_FontChar[i];
        nPos += ESC_FontChar.length;

        if ((Attribute & 0x8) == 8) {
            for (int i = 0; i < GS_B_Enabled.length; i++) pBaseMode[(nPos + i)] = GS_B_Enabled[i];
            nPos += GS_B_Enabled.length;
        } else {
            for (int i = 0; i < GS_B_Disabled.length; i++) pBaseMode[(nPos + i)] = GS_B_Disabled[i];
            nPos += GS_B_Disabled.length;
        }

        ChrSet[2] = ((byte) (this.m_cCodePage & 0xFF));
        IChrSet[2] = ((byte) (this.m_cInterCharSet & 0xFF));

        for (int i = 0; i < ChrSet.length; i++) pBaseMode[(nPos + i)] = ChrSet[i];
        nPos += ChrSet.length;

        for (int i = 0; i < IChrSet.length; i++) pBaseMode[(nPos + i)] = IChrSet[i];
        nPos += IChrSet.length;

        return 0;
    }

    private int prnTextSizeMode(int TextSize, byte[] pTsMode) {
        int nPos = 0;
        byte cMode = 0;

        if (((TextSize < 0) || (TextSize > 7)) &&
                ((16 > TextSize) || (TextSize > 23)) &&
                ((32 > TextSize) || (TextSize > 39)) &&
                ((48 > TextSize) || (TextSize > 55)) &&
                ((64 > TextSize) || (TextSize > 71)) &&
                ((80 > TextSize) || (TextSize > 87)) &&
                ((96 > TextSize) || (TextSize > 103)) && (
                (112 > TextSize) || (TextSize > 119))) {
            return 201;
        }

        cMode = (byte) (TextSize & 0xFF);

        GS_FontSize[2] = cMode;
        for (int i = 0; i < GS_FontSize.length; i++) pTsMode[(nPos + i)] = GS_FontSize[i];
        nPos += GS_FontSize.length;

        return 0;
    }

    private void GetPixel(byte[] DataRGB, int cxBitmap, int cyBitmap, byte[] aColor, int x, int y) {
        int X_axis = 0;
        int Y_axis = 0;

        X_axis = 2 * x;
        Y_axis = 2 * (cxBitmap * y);

        int Tem_DataRGB = ((DataRGB[(X_axis + Y_axis)] & 0xFF) << 8) + (DataRGB[(X_axis + Y_axis + 1)] & 0xFF);

        aColor[0] = ((byte) ((Tem_DataRGB & 0xF800) >> 11));
        aColor[1] = ((byte) ((Tem_DataRGB & 0x7E0) >> 5));
        aColor[2] = ((byte) (Tem_DataRGB & 0x1F));
    }

    private int bcMake1st(byte[] Data, int DataSize, int Symbology, int Height, int Width, int Alignment, int TextPosition, byte[] pParsedData, int[] uiParsedData) {
        byte BarWidth = 0;
        byte BarHeight = 0;
        byte[] RData = new byte[300];
        int nDS = 0;
        int BarDataSize = 0;
        int i = 0;
        byte[] pBData = Data;
        int n1 = 0;

        byte[] pData = new byte[17];

        for (i = 0; i < RData.length; i++) RData[i] = 0;
        BarDataSize = DataSize;

        RData[(nDS++)] = 29;
        RData[(nDS++)] = 107;

        switch (Symbology) {
            case 101:
                RData[(nDS++)] = 65;

                if ((BarDataSize > 12) || (BarDataSize < 11)) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));
                for (i = 0; i < BarDataSize; i++) {
                    if ((pBData[i] < 48) || (pBData[i] > 57)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 102:
                RData[(nDS++)] = 66;

                if ((BarDataSize > 12) || (BarDataSize < 11))
                    return 301;
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((i == 0) && (pBData[i] != 48)) {
                        return 301;
                    }
                    if ((pBData[i] < 48) || (pBData[i] > 57))
                        return 301;
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 105:
            case 106:
                RData[(nDS++)] = 68;

                if ((BarDataSize > 8) || (BarDataSize < 7)) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((pBData[i] < 48) || (pBData[i] > 57)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 103:
            case 104:
                RData[(nDS++)] = 67;

                if ((BarDataSize > 13) || (BarDataSize < 12))
                    return 301;
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((pBData[i] < 48) || (pBData[i] > 57)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 108:
                RData[(nDS++)] = 70;

                if ((BarDataSize > 255) || (BarDataSize % 2 != 0)) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((pBData[i] < 48) || (pBData[i] > 57)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 109:
                n1 = 0;
                RData[(nDS++)] = 71;
                if (BarDataSize > 255) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((i == 0) && ((pBData[i] < 65) || (pBData[i] > 90))) {
                        return 301;
                    }
                    if (((pBData[i] >= 48) && (pBData[i] <= 57)) || (pBData[i] == 65) || (pBData[i] == 66) || (pBData[i] == 67) || (pBData[i] == 68) || (pBData[i] == 36) || (pBData[i] == 45))
                        n1++;
                    else if ((pBData[i] != 43) && (pBData[i] != 46) && (pBData[i] != 47) && (pBData[i] != 58)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 107:
                RData[(nDS++)] = 69;

                if (BarDataSize > 255) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((pBData[i] != 32) && (pBData[i] != 36) && (pBData[i] != 37) && (pBData[i] != 43) && (pBData[i] != 45) && (pBData[i] != 46) && (pBData[i] != 47)) {
                        if (((pBData[i] < 65) || (pBData[i] > 90)) && ((pBData[i] < 48) || (pBData[i] > 57))) {
                            return 301;
                        }
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 110:
                RData[(nDS++)] = 72;
                if (BarDataSize > 255) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((-128 > pBData[i]) && (pBData[i] < 0))
                        return 301;
                    RData[(nDS++)] = pBData[i];
                }

                break;
            case 111:
                RData[(nDS++)] = 73;

                if ((BarDataSize > 255) || (BarDataSize < 2)) {
                    return 301;
                }
                RData[(nDS++)] = ((byte) (BarDataSize & 0xFF));

                for (i = 0; i < BarDataSize; i++) {
                    if ((i == 0) && (pBData[i] != 123)) {
                        RData[(nDS - 1)] = ((byte) (BarDataSize + 2 & 0xFF));
                        RData[(nDS++)] = 123;
                        RData[(nDS++)] = 65;
                    }

                    if ((-128 > pBData[i]) && (pBData[i] < 0)) {
                        return 301;
                    }
                    RData[(nDS++)] = pBData[i];
                }

                break;
            default:
                return 103;
        }

        BarWidth = (byte) (Width & 0xFF);
        BarHeight = (byte) (Height & 0xFF);

        for (i = 0; i < pData.length; i++) pData[i] = 0;

        pData[0] = 0;
        pData[1] = 0;

        pData[2] = 27;
        pData[3] = 97;

        switch (Alignment) {
            case 0:
                pData[4] = 0;
                break;
            case 1:
                pData[4] = 1;
                break;
            case 2:
                pData[4] = 2;
                break;
            default:
                return 201;
        }

        pData[5] = 29;
        pData[6] = 72;
        switch (TextPosition) {
            case 0:
                pData[7] = 0;
                break;
            case 1:
                pData[7] = 1;
                break;
            case 2:
                pData[7] = 2;
                break;
            default:
                return 201;
        }

        pData[8] = 29;
        pData[9] = 102;

        pData[10] = 1;

        if ((Height < 1) || (Height > 255)) {
            return 201;
        }
        pData[11] = 29;
        pData[12] = 104;
        pData[13] = BarHeight;

        if ((Width < 2) || (Width > 6)) {
            return 201;
        }
        pData[14] = 29;
        pData[15] = 119;
        pData[16] = BarWidth;

        for (i = 0; i < pData.length; i++) pParsedData[i] = pData[i];
        for (i = 0; i < nDS; i++) pParsedData[(i + 17)] = RData[i];

        uiParsedData[0] = (17 + nDS);
        return 0;
    }

    private int bcMake2st(byte[] Data, int DataSize, int Symbology, int Height, int Width, int Alignment, int TextPosition, byte[] pParsedData, int[] uiParsedData) {
        int k = 0;
        int i = 0;
        byte[] pdf417Cmd_Set_columnrow = {29, 40, 107, 3, 0, 48, 65, 0, 29, 40, 107, 3, 0, 48, 66};
        byte[] pdf417Cmd_Set_width_mod = {29, 40, 107, 3, 0, 48, 67, 3};
        byte[] pdf417Cmd_Set_high_mod = {29, 40, 107, 3, 0, 48, 68, 3};
        byte[] pdf417Cmd_Set_store = {29, 40, 107, 0, 0, 48, 80, 48};
        byte[] pdf417Cmd_Set_print = {29, 40, 107, 3, 0, 48, 81, 48};
        byte[] QrCodeCmd_Set_model = {29, 40, 107, 4, 0, 49, 65, 50};
        byte[] QrCodeCmd_Set_mod = {29, 40, 107, 3, 0, 49, 67, 3};
        byte[] QrCodeCmd_Set_store = {29, 40, 107, 0, 0, 49, 80, 48};
        byte[] QrCodeCmd_Set_print = {29, 40, 107, 3, 0, 49, 81, 48};
        byte[] DataMatrix_Set_mod = {29, 40, 107, 3, 0, 51, 67, 3};
        byte[] DataMatrix_Set_store = {29, 40, 107, 0, 0, 51, 80, 48};
        byte[] DataMatrix_Set_print = {29, 40, 107, 3, 0, 51, 81, 48};
        byte[] MaxiCodeCmd_SetMode = {29, 40, 107, 3, 0, 50, 65, 50};
        byte[] MaxiCodeCmd_Set_store = {29, 40, 107, 0, 0, 50, 80, 48};
        byte[] MaxiCodeCmd_Set_print = {29, 40, 107, 3, 0, 50, 81, 48};

        switch (Symbology) {
            case 200:
                for (i = 0; i < pdf417Cmd_Set_columnrow.length; i++)
                    pParsedData[i] = pdf417Cmd_Set_columnrow[i];
                uiParsedData[0] = pdf417Cmd_Set_columnrow.length;

                if (Width > 2)
                    Width = 3;
                else {
                    Width = 2;
                }
                pdf417Cmd_Set_width_mod[7] = ((byte) (Width & 0xFF));
                for (i = 0; i < pdf417Cmd_Set_width_mod.length; i++)
                    pParsedData[(i + uiParsedData[0])] = pdf417Cmd_Set_width_mod[i];
                uiParsedData[0] += pdf417Cmd_Set_width_mod.length;

                for (i = 0; i < pdf417Cmd_Set_high_mod.length; i++)
                    pParsedData[(i + uiParsedData[0])] = pdf417Cmd_Set_high_mod[i];
                uiParsedData[0] += pdf417Cmd_Set_high_mod.length;

                k = DataSize + 3;
                pdf417Cmd_Set_store[3] = ((byte) (k % 256 & 0xFF));
                pdf417Cmd_Set_store[4] = ((byte) (k / 256 & 0xFF));

                for (i = 0; i < pdf417Cmd_Set_store.length; i++)
                    pParsedData[(i + uiParsedData[0])] = pdf417Cmd_Set_store[i];
                uiParsedData[0] += pdf417Cmd_Set_store.length;

                for (i = 0; i < DataSize; i++) pParsedData[(i + uiParsedData[0])] = Data[i];
                uiParsedData[0] += DataSize;

                for (i = 0; i < pdf417Cmd_Set_print.length; i++)
                    pParsedData[(i + uiParsedData[0])] = pdf417Cmd_Set_print[i];
                uiParsedData[0] += pdf417Cmd_Set_print.length;

                break;
            case 202:
            case 203:
                if (Symbology == 203)
                    QrCodeCmd_Set_model[7] = 49;
                else {
                    QrCodeCmd_Set_model[7] = 50;
                }
                for (i = 0; i < QrCodeCmd_Set_model.length; i++)
                    pParsedData[i] = QrCodeCmd_Set_model[i];
                uiParsedData[0] = QrCodeCmd_Set_model.length;

                if (Width > 8) Width = 8;
                if (Width < 1) Width = 1;

                QrCodeCmd_Set_mod[7] = ((byte) (Width & 0xFF));
                for (i = 0; i < QrCodeCmd_Set_mod.length; i++)
                    pParsedData[(i + uiParsedData[0])] = QrCodeCmd_Set_mod[i];
                uiParsedData[0] += QrCodeCmd_Set_mod.length;

                k = DataSize + 3;
                QrCodeCmd_Set_store[3] = ((byte) (k % 256 & 0xFF));
                QrCodeCmd_Set_store[4] = ((byte) (k / 256 & 0xFF));

                for (i = 0; i < QrCodeCmd_Set_store.length; i++)
                    pParsedData[(i + uiParsedData[0])] = QrCodeCmd_Set_store[i];
                uiParsedData[0] += QrCodeCmd_Set_store.length;

                for (i = 0; i < DataSize; i++) pParsedData[(i + uiParsedData[0])] = Data[i];
                uiParsedData[0] += DataSize;

                for (i = 0; i < QrCodeCmd_Set_print.length; i++)
                    pParsedData[(i + uiParsedData[0])] = QrCodeCmd_Set_print[i];
                uiParsedData[0] += QrCodeCmd_Set_print.length;

                break;
            case 204:
                if (Width > 6) Width = 6;
                if (Width < 2) Width = 2;

                DataMatrix_Set_mod[7] = ((byte) (Width & 0xFF));
                for (i = 0; i < DataMatrix_Set_mod.length; i++)
                    pParsedData[i] = DataMatrix_Set_mod[i];
                uiParsedData[0] = DataMatrix_Set_mod.length;

                k = DataSize + 3;
                DataMatrix_Set_store[3] = ((byte) (k % 256 & 0xFF));
                DataMatrix_Set_store[4] = ((byte) (k / 256 & 0xFF));

                for (i = 0; i < DataMatrix_Set_store.length; i++)
                    pParsedData[(i + uiParsedData[0])] = DataMatrix_Set_store[i];
                uiParsedData[0] += DataMatrix_Set_store.length;

                for (i = 0; i < DataSize; i++) pParsedData[(i + uiParsedData[0])] = Data[i];
                uiParsedData[0] += DataSize;

                for (i = 0; i < DataMatrix_Set_print.length; i++)
                    pParsedData[(i + uiParsedData[0])] = DataMatrix_Set_print[i];
                uiParsedData[0] += DataMatrix_Set_print.length;

                break;
            case 205:
            case 206:
            case 207:
                if (Symbology == 205)
                    MaxiCodeCmd_SetMode[7] = 50;
                else if (Symbology == 206)
                    MaxiCodeCmd_SetMode[7] = 51;
                else {
                    MaxiCodeCmd_SetMode[7] = 52;
                }
                for (i = 0; i < MaxiCodeCmd_SetMode.length; i++)
                    pParsedData[i] = MaxiCodeCmd_SetMode[i];
                uiParsedData[0] = MaxiCodeCmd_SetMode.length;

                k = DataSize + 3;
                MaxiCodeCmd_Set_store[3] = ((byte) (k % 256 & 0xFF));
                MaxiCodeCmd_Set_store[4] = ((byte) (k / 256 & 0xFF));

                for (i = 0; i < MaxiCodeCmd_Set_store.length; i++)
                    pParsedData[(i + uiParsedData[0])] = MaxiCodeCmd_Set_store[i];
                uiParsedData[0] += MaxiCodeCmd_Set_store.length;

                for (i = 0; i < DataSize; i++) pParsedData[(i + uiParsedData[0])] = Data[i];
                uiParsedData[0] += DataSize;

                for (i = 0; i < MaxiCodeCmd_Set_print.length; i++)
                    pParsedData[(i + uiParsedData[0])] = MaxiCodeCmd_Set_print[i];
                uiParsedData[0] += MaxiCodeCmd_Set_print.length;

                break;
            case 201:
            default:
                return 103;
        }
        return 0;
    }

    private int inputOutBuffer_init(byte[] Buff) {
        return inputOutBuffer(Buff, Buff.length);
    }

    private int inputOutBuffer_uiCount(byte[] Buff, int uiLength, int uiCount) {
        int returnvalue = 0;
        for (int Count = 0; Count < uiCount; Count++) {
            returnvalue = inputOutBuffer(Buff, uiLength);
            if (returnvalue != 0) break;
        }
        return returnvalue;
    }

    private int inputOutBuffer(byte[] Buff, int uiLength) {
        byte[] tem_point_buffer = null;
        ByteBuffer TemglobaliOutBuffer_ByteBuffer = null;
        if (this.globaliOutBuffer_Size + uiLength > this.globaliOutBuffer_MaxLength) {
            TemglobaliOutBuffer_ByteBuffer = ByteBuffer.allocate(this.globaliOutBuffer_Size);
            tem_point_buffer = TemglobaliOutBuffer_ByteBuffer.array();
            for (int i = 0; i < this.globaliOutBuffer_Size; i++)
                tem_point_buffer[i] = this.globaliOutBuffer_iOutBuffer[i];
            int tem_current_size = this.globaliOutBuffer_Size;
            int needBytes = this.globaliOutBuffer_Size + uiLength + 999;

            this.globaliOutBuffer_ByteBuffer.clear();
            this.globaliOutBuffer_ByteBuffer = null;
            this.globaliOutBuffer_ByteBuffer = ByteBuffer.allocate(needBytes);

            this.globaliOutBuffer_iOutBuffer = this.globaliOutBuffer_ByteBuffer.array();
            this.globaliOutBuffer_MaxLength = needBytes;
            this.globaliOutBuffer_Size = 0;
            for (int i = 0; i < tem_current_size; i++)
                this.globaliOutBuffer_iOutBuffer[(this.globaliOutBuffer_Size + i)] = tem_point_buffer[i];
            this.globaliOutBuffer_Size = tem_current_size;
            tem_point_buffer = null;
            TemglobaliOutBuffer_ByteBuffer.clear();
            TemglobaliOutBuffer_ByteBuffer = null;
        }
        for (int i = 0; i < uiLength; i++)
            this.globaliOutBuffer_iOutBuffer[(this.globaliOutBuffer_Size + i)] = Buff[i];
        this.globaliOutBuffer_Size += uiLength;
        return 0;
    }

    public void PrintOutput(String FunctionName, byte[] Data) {
        char[] HexData = new char[2];
        if (this.logenable)
            for (int i = 0; i < Data.length; i++) {
                DexToHex(Data[i], HexData);
                Log.i("BXLSERVICE", FunctionName + "Data[" + i + "]= Dec:" + Data[i] + ", Hex:0x" + HexData[0] + HexData[1]);
            }
    }

    public void DexToHex(byte Data, char[] CharData) {
        byte temData0 = (byte) ((Data & 0xFF) / 16);
        byte temData1 = (byte) ((Data & 0xFF) % 16);

        if (temData0 < 10) {
            CharData[0] = ((char) (48 + temData0));
        } else {
            CharData[0] = ((char) (97 + (temData0 - 10)));
        }

        if (temData1 < 10) {
            CharData[1] = ((char) (48 + temData1));
        } else {
            CharData[1] = ((char) (97 + (temData1 - 10)));
        }
    }

    private void checkGC() {
        System.runFinalization();
    }

    private class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            Logger.e("BXLSERVICE", "Bluetooth ConnectThread ..");
            BxlService.this.mDevice = device;
            try {
                tmp = BxlService.this.mDevice.createRfcommSocketToServiceRecord(BxlService.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                BxlService.this.connectState = false;
            }
            BxlService.this.mSocket = tmp;
        }

        public ConnectThread(String Addr, int Port) {
            BxlService.this.wSocket = null;
            try {
                BxlService.this.wSocket = new Socket(Addr, Port);
            } catch (IOException e) {
                e.printStackTrace();
                BxlService.this.connectState = false;
            }
        }

        @Override
        public void run() {
            if (BxlService.this.mAdapter.isDiscovering()) {
                Log.i("BXLSERVICE", "BXLSERVICE cancelDiscovery");
                BxlService.this.mAdapter.cancelDiscovery();
            }

            try {
                Log.i("BXLSERVICE", "BXLSERVICE connect thread try connect bluetooth socket");
                BxlService.this.mSocket.connect();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                BxlService.this.connectState = false;
                try {
                    if (BxlService.this.mSocket != null) {
                        BxlService.this.mSocket.close();
                        BxlService.this.mSocket = null;
                    }
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }

                Log.i("BXLSERVICE", "BXLSERVICE connection errors");
                return;
            }

            Log.i("BXLSERVICE", "BXLSERVICE connection : mConnectThread connection complete..");
        }

        public void cancel() {
            try {
                Log.i("BXLSERVICE", "ConnectThread close socket");
                BxlService.this.connectState = false;
                if (BxlService.this.Com_Mode == 1) {
                    if (BxlService.this.mSocket != null) {
                        BxlService.this.mSocket.close();
                        BxlService.this.mSocket = null;
                    }
                } else if (BxlService.this.wSocket != null) {
                    BxlService.this.wSocket.close();
                    BxlService.this.wSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        char[] HexValue = new char[2];

        public ConnectedThread(BluetoothSocket socket) {
            Log.i("BXLSERVICE", "ConnectedThread_Start");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                BxlService.this.connectState = false;
            }
            BxlService.this.mInStream = tmpIn;
            BxlService.this.mOutStream = tmpOut;
        }

        public ConnectedThread(Socket socket) {
            Log.i("BXLSERVICE", "ConnectedThread_Start");
            try {
                BxlService.this.mInStream = socket.getInputStream();
                BxlService.this.mOutStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                BxlService.this.connectState = false;
            }
        }

        @Override
        public void run() {
            int totalRecvBytes = 0;
            Log.i("BXLSERVICE", "ConnectedThread Run Starated");
            try {
                while (!BxlService.this.bTerminate) {
                    if (BxlService.this.recievedState) {
                        if (BxlService.this.logenable)
                            Log.i("BXLSERVICE", "Thread(run) : Thread count :" + Thread.activeCount() + "\n");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("BXLSERVICE", "ConnectedThread(StandBYGet_message)");
                        BxlService.this.recvBytes = BxlService.this.mInStream.read(BxlService.this.recvBuffer2, 0, 1024);
                        Log.i("BXLSERVICE", "ConnectedThread(Get_throw away message)");
                        if (BxlService.this.recievedState) {
                            Log.i("BXLSERVICE", "throw away message");
                        }

                        for (int i = 0; i < BxlService.this.recvBytes; i++) {
                            BxlService.this.recvBuffer[(totalRecvBytes + i)] = BxlService.this.recvBuffer2[i];
                        }
                        totalRecvBytes += BxlService.this.recvBytes;

                        if (BxlService.this.logenable) {
                            Log.i("BXLSERVICE", "Recv Ok : " + BxlService.this.recvBytes + " " + totalRecvBytes);
                            for (int i = 0; i < totalRecvBytes; i++) {
                                BxlService.this.DexToHex(BxlService.this.recvBuffer[i], this.HexValue);
                                Log.i("BXLSERVICE", " recvBuffer[" + i + "] = Dec: " + BxlService.this.recvBuffer[i] + " ,Hex :0x" + this.HexValue[0] + this.HexValue[1]);
                            }
                        }

                        if (BxlService.this.MsgType == 0) {
                            if (BxlService.this.logenable) {
                                Log.i("BXLSERVICE", "MsgType == 0");
                            }
                            if (totalRecvBytes >= 1) {
                                Log.i("BXLSERVICE", "asembly message ok");
                                BxlService.this.recvBytes = totalRecvBytes;
                                totalRecvBytes = 0;
                                BxlService.this.recievedState = true;
                            }
                        }

                        if (BxlService.this.MsgType == 1) {
                            if (BxlService.this.logenable) {
                                Log.i("BXLSERVICE", "MsgType == 1");
                            }
                            if (totalRecvBytes >= 4) {
                                Log.i("BXLSERVICE", "asembly message ok");
                                BxlService.this.recvBytes = totalRecvBytes;
                                totalRecvBytes = 0;
                                BxlService.this.recievedState = true;
                            }
                        }

                        if (BxlService.this.MsgType == 2) {
                            if (BxlService.this.logenable) {
                                Log.i("BXLSERVICE", "MsgType == 2");
                            }

                            if ((totalRecvBytes >= 3) &&
                                    (BxlService.this.recvBuffer[(totalRecvBytes - 3)] == 3) &&
                                    (BxlService.this.recvBuffer[(totalRecvBytes - 2)] == 13) &&
                                    (BxlService.this.recvBuffer[(totalRecvBytes - 1)] == 10) &&
                                    (BxlService.this.recvBuffer[0] == 2) &&
                                    (BxlService.this.recvBuffer[2] == 49) && (
                                    (BxlService.this.recvBuffer[1] == 65) ||
                                            (BxlService.this.recvBuffer[1] == 66) ||
                                            (BxlService.this.recvBuffer[1] == 67) ||
                                            (BxlService.this.recvBuffer[1] == 68) ||
                                            (BxlService.this.recvBuffer[1] == 69) ||
                                            (BxlService.this.recvBuffer[1] == 70) ||
                                            (BxlService.this.recvBuffer[1] == 71))) {
                                BxlService.this.recvBytes = totalRecvBytes;
                                totalRecvBytes = 0;

                                BxlService.this.ptrmsrGetDataReceiveData(BxlService.this.recvBuffer, BxlService.this.recvBytes);
                                BxlService.this.recvBytes = 0;
                            }
                        }

                        if (BxlService.this.MsgType == 3) {
                            if (BxlService.this.logenable) {
                                Log.i("BXLSERVICE", "MsgType == 3");
                            }

                            if (totalRecvBytes >= 5) {
                                Log.i("BXLSERVICE", "asembly message ok");
                                BxlService.this.recvBytes = totalRecvBytes;
                                totalRecvBytes = 0;
                                BxlService.this.recievedState = true;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("BXLSERVICE", "ConnectedThread IOException..");
                BxlService.this.connectState = false;
            }
        }

        public int write(byte[] bytes) {
            try {
                BxlService.this.mOutStream.write(bytes);
                BxlService.this.mOutStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                BxlService.this.connectState = false;
                Log.i("BXLSERVICE", "Data Write IOException..");
                return 402;
            }
            return 0;
        }

        public void cancel() {
            try {
                Log.i("BXLSERVICE", "ConnectedThread close socket");
                BxlService.this.connectState = false;
                if (BxlService.this.mInStream != null) {
                    BxlService.this.mInStream.close();
                    BxlService.this.mInStream = null;
                }
                if (BxlService.this.mOutStream != null) {
                    BxlService.this.mOutStream.close();
                    BxlService.this.mInStream = null;
                }
                if (BxlService.this.Com_Mode == 1) {
                    if (BxlService.this.mSocket != null) {
                        BxlService.this.mSocket.close();
                        BxlService.this.mSocket = null;
                    }
                } else if (BxlService.this.wSocket != null) {
                    BxlService.this.wSocket.close();
                    BxlService.this.wSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}