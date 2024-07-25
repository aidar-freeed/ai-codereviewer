package com.adins.mss.base.about.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.networkmonitor.NetworkMonitorDataUsage;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.androidquery.AQuery;
import com.github.jjobes.slidedatetimepicker.SlidingTabLayout;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import org.acra.ACRA;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.pax.ippi.impl.NeptuneUser.getApplicationContext;


/**
 * @author gigin.ginanjar
 */
public class DeviceInfoFragment extends Fragment {
    public static boolean backEnabled = true;
    private static ArrayList<Library> library;
    private static int flag;
    public Libs libs;
    AQuery query;
    int battery;
    long appSize;
    String txtMobileData;
    NetworkMonitorDataUsage monitorDU = new NetworkMonitorDataUsage();
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    /**
     * @param changeLog
     * @param flag
     */
    public static void setChangeLog(ArrayList<Library> changeLog, int flag) {
        library = new ArrayList<Library>();
        library = changeLog;
        DeviceInfoFragment.flag = flag;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
        }
        return (availableBlocks * blockSize) / 1048576;
    }

    public int getBatteryLevel() {
        //int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        int battery = (level * 100) / scale;
//		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);\
        return battery;
    }

    public long getAppSize() {
        try {
            appSize = new File(getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), 0).publicSourceDir).length();
        } catch (NameNotFoundException e) {
            ACRA.getErrorReporter().putCustomData("errorNameNotFound", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorNameNotFound", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set getNameFile"));
            e.printStackTrace();
        }
        return appSize / 1048576;
    }

    public String getMobileDataUsage() {
        String mobileData = Long.toString(monitorDU.getDataThisDay(getContext()) / 1024);
        int size = 0;
        for (int i = 0; i < mobileData.length(); i++) {
            size += 1;
        }
        if (size <= 3)
            txtMobileData = mobileData + " KB";
        else if (size > 3 && size <= 6)
            txtMobileData = mobileData.substring(0, mobileData.length() - 3) + " MB";
        else
            txtMobileData = mobileData.substring(0, mobileData.length() - 3) + " GB";
        return txtMobileData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.new_device_info_tab, container, false);
        TextView txtOsVer = (TextView) view.findViewById(R.id.txtOsVer);
        TextView txtAppVer = (TextView) view.findViewById((R.id.txtAppVer));
        TextView txtBattery = (TextView) view.findViewById((R.id.txtBattery));
//			TextView txtDataUsage = (TextView) view.findViewById((R.id.txtDataUsage));
        TextView txtMobileDataUsage = (TextView) view.findViewById((R.id.txtMobileDataUsage));
        TextView txtMemoryAvailable = (TextView) view.findViewById((R.id.txtMemoryAvailable));
        TextView txtAndroidID = (TextView) view.findViewById((R.id.txtAndroidId));

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        txtAppVer.setText(getString(R.string.appVer) + " : " + Global.APP_VERSION);
        txtOsVer.setText(getString(R.string.androidVersion) + " : " + Build.VERSION.RELEASE);
        txtBattery.setText(getString(R.string.batteryPercen) + " : " + getBatteryLevel() + " %");
        // olivia : UPDATE - Application Storage dihilangkan
//			txtDataUsage.setText(getString(R.string.appStorage) + " : " + Tool.separateThousand((double) getAppSize())+ " MB");
        txtMobileDataUsage.setText(getString(R.string.dataUsage) + " : " + getMobileDataUsage());
        txtMemoryAvailable.setText(getString(R.string.deviceStorage) + " : " + Tool.separateThousand(getAvailableInternalMemorySize()) + " MB");

        final String android_id;
        if(Build.VERSION.SDK_INT > 28){
            android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            txtAndroidID.setVisibility(View.VISIBLE);
        }else {
            android_id = "";
            txtAndroidID.setVisibility(View.GONE);
        }
        txtAndroidID.setText(getString(R.string.androidID)+" : "+android_id);
        txtAndroidID.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Clip", android_id);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),getString(R.string.copied),Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }
}

