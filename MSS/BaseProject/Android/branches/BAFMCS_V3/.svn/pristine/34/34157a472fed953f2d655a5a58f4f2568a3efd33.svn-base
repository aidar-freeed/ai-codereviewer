package com.adins.mss.base.commons

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.Keep
import android.util.Base64
import com.adins.mss.base.PrintActivity

import com.adins.mss.base.crashlytics.FireCrash
import com.adins.mss.base.mainmenu.MainMenuActivity
import com.adins.mss.base.util.UserSession
import com.adins.mss.constant.Global
import com.adins.mss.foundation.db.DaoOpenHelper
import com.adins.mss.foundation.formatter.Tool
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel
import java.security.MessageDigest

import de.greenrobot.dao.database.Database
import zj.com.cn.bluetooth.sdk.Main_Activity1
import java.util.*

/**
 * Helper class to support development
 * @Author Kusnendi.Muhamad
 * 31/01/2018
 */
class SecondHelper {

    fun salt(length: Int): String {
        val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < length) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }

    fun getNameFromUrl(url: String): String {
        return url.replaceFirst(".*/([^/?]+).*".toRegex(), "$1")
    }

    @Throws(Exception::class)
    fun signature(file: File, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        val `is` = FileInputStream(file)

        var n = 0
        val buffer = ByteArray(BUFFER)
        while (n != -1) {
            n = `is`.read(buffer)
            if (n > 0) {
                digest.update(buffer, 0, n)
            }
        }

        `is`.close()
        val result = digest.digest()
        val hexString = bytesToHex(result)
        return hexString.toUpperCase()
    }

    fun fromBase64(encoded: String): ByteArray {
        //        return Base64.getDecoder().decode(encoded);
        return Base64.decode(encoded, Base64.DEFAULT)
    }

//    @Throws(IOException::class)
//    fun toBase64(file: File): String {
//        var textB64: String? = null
//        val bytes = loadFile(file)
//        //        byte[] encoded  = Base64.getEncoder().encode(bytes);
//        val encoded = Base64.encode(bytes, Base64.DEFAULT)
//
//        textB64 = String(encoded)
//        return textB64
//    }

    fun toBase64(bytes: ByteArray): String {
        //        byte[] encoded = Base64.getEncoder().encode(bytes);
        val encoded = Base64.encode(bytes, Base64.DEFAULT)
        return String(encoded)
    }

    fun isFileExist(path: String): Boolean {
        val file = File(path)
        return file.exists()
    }

    fun createFile(bytes: ByteArray, pathFile: String): File? {
        try {
            val file = File(pathFile)
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()

            return file
        } catch (io: IOException) {
            io.printStackTrace()
        }

        return null
    }

//    @Throws(IOException::class)
//    fun loadFile(file: File): ByteArray {
//        val `is` = FileInputStream(file)
//
//        val length = file.length()
//        if (length > Integer.MAX_VALUE) {
//            //
//        }
//
//        val bytes = ByteArray(length.toInt())
//        var offset = 0
//        var read = 0
//
//        while (offset < bytes.size && (read = `is`.read(bytes, offset, bytes.size - offset)) >= 0) {
//            offset += read
//        }
//
//        if (offset < bytes.size) {
//            throw IOException("Could not completely read file " + file.name)
//        }
//
//        `is`.close()
//        return bytes
//    }

    @Throws(IOException::class)
    fun copy(sourceFile: File, destFile: File): File {

        if (!destFile.parentFile.exists())
            destFile.parentFile.mkdirs()

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination!!.transferFrom(source, 0, source!!.size())
        } finally {
            source?.close()
            destination?.close()
        }

        return destFile
    }

    fun bytesToHex(bytes: ByteArray): String {
        val buffer = StringBuffer()
        for (i in bytes.indices) {
            if (bytes[i].toInt() and 0xff < 0x10)
                buffer.append("0")
            buffer.append(java.lang.Long.toString((bytes[i].toInt() and 0xff).toLong(), 16))
        }

        return buffer.toString()
    }


    fun isServiceAvailable(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (manager != null) {
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name.equals(service.service.className, ignoreCase = true)) {
                    return true
                }
            }
        }

        return false
    }

    @Keep companion object {
        private val BUFFER = 2048
        var CHIPER_SHA1 = "SHA-1"
        var CHIPER_MD5 = "MD5"

        val instance: SecondHelper
            get() = SecondHelper()

        fun clearSession(context: Context) {
            try {
                UserSession.clear()
                val sharedPref = ObscuredSharedPreferences.getPrefs(context,
                        "GlobalData", Context.MODE_PRIVATE)

                val sharedPrefEditor = sharedPref.edit()
                sharedPrefEditor.remove("HAS_LOGGED")
                sharedPrefEditor.commit()
            } catch (e: Exception) {
                FireCrash.log(e)
                // TODO: handle exception
            }

        }

        fun broadcastRouteEvent(context: Context, action: String) {
            val intent = Intent(MainMenuActivity.ACTION_ROUTE_EVENT)
            intent.putExtra(Global.EXTRA_ACTION_MENU, action)
            context.sendBroadcast(intent)
        }

        fun updateDatabase(context: Context, path: String) {
            val db = DaoOpenHelper.getDb(context)

            try {
                db.execSQL("ATTACH '$path' AS EXTDB")
                db.execSQL("REPLACE INTO MS_LOOKUP SELECT * FROM EXTDB.MS_LOOKUP")
                db.execSQL("REPLACE INTO MS_MIGRATION SELECT * FROM EXTDB.MS_MIGRATION")
                db.execSQL("REPLACE INTO MS_BLACKLIST SELECT * FROM EXTDB.MS_BLACKLIST")
                db.execSQL("REPLACE INTO MS_PO SELECT * FROM EXTDB.MS_PO")
                db.execSQL("REPLACE INTO MS_RULES SELECT * FROM EXTDB.MS_RULES")
                db.execSQL("REPLACE INTO MS_PO_ASSET SELECT * FROM EXTDB.MS_PO_ASSET")
                db.execSQL("REPLACE INTO MS_PO_DEALER SELECT * FROM EXTDB.MS_PO_DEALER")
                db.execSQL("REPLACE INTO MS_TMP_RULE SELECT * FROM EXTDB.MS_TMP_RULE")
                db.execSQL("REPLACE INTO MS_SYNC SELECT * FROM EXTDB.MS_SYNC")
                db.execSQL("DETACH 'EXTDB'")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun resetSession(activity: Activity, tClass: Class<*>) {
            SecondHelper.clearSession(activity)
            val login = Intent(activity, tClass)
            login.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            login.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(login)
            activity.finish()
        }

        fun isAlwaysFinishActivities(context: Context): Boolean {
            var alwaysFinishActivitiesInt = 0
            if (Build.VERSION.SDK_INT >= 17) {
                alwaysFinishActivitiesInt = Settings.System.getInt(context.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0)
            } else {
                alwaysFinishActivitiesInt = Settings.System.getInt(context.contentResolver, Settings.System.ALWAYS_FINISH_ACTIVITIES, 0)
            }

            return alwaysFinishActivitiesInt == 1

//            return if (alwaysFinishActivitiesInt == 1) {
//                true
//            } else {
//                false
//            }
        }

        fun alwaysFinishActivityState(context: Context, state: Boolean) {
            val mode = if (state) 1 else 0
            if (Build.VERSION.SDK_INT >= 17) {
                Settings.System.putInt(context.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, mode)
            } else {
                Settings.System.putInt(context.contentResolver, Settings.System.ALWAYS_FINISH_ACTIVITIES, mode)
            }
        }

        //Method for read developer options state
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun isDevMode(context: Context): Boolean {
            var mode = 0

            if (Build.VERSION.SDK_INT >= 17) {
                mode = Settings.Secure.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)
            } else {
                mode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0)
            }

            return mode == 1
        }

        fun isUUID(text : String) : Boolean {
            val pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"
            return text.matches(Regex(pattern));
        }

        fun doPrint(context: Context, taskId: String?, source: String?) {
            val printPrefs  = context.getSharedPreferences("printPrefs", Context.MODE_PRIVATE)
//            val printManger = if (printPrefs.getInt("printerType", 1) == 1) Main_Activity1::class.java else PrintActivity::class.java
            val printIntent = Intent(context, Main_Activity1::class.java)
            printIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            printIntent.putExtra("taskId", taskId)
            printIntent.putExtra("source", source)

            if (printPrefs.contains("printerAddress"))
                printIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, printPrefs.getString("printerAddress", ""))

            //Start print activity
            context.startActivity(printIntent)
        }

        fun doPrint(context: Context, params: PrintParams) {
            val printPrefs = context.getSharedPreferences("printPrefs", Context.MODE_PRIVATE)
            val printIntent = Intent(context, Main_Activity1::class.java)
            printIntent.putExtra("taskId", params.taskId)
            if (params.source != null) printIntent.putExtra("source", params.source)
            if (params.isPrintDeposit != null) printIntent.putExtra("isPrintDeposit", params.isPrintDeposit!!)
            if (params.isDummy != null) printIntent.putExtra("isDummy", params.isDummy!!)

            if (printPrefs.contains("printerAddress"))
                printIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, printPrefs.getString("printerAddress", ""))

            //Start print activity
            context.startActivity(printIntent)
        }

        fun isCharging(context: Context) : Boolean {
            val intent= context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val plugged  = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

        // Method for receive battery change level
        fun registerBatteryChangeReceiver(context: Context, broadcastReceiver: BroadcastReceiver) {
            context.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }

        // Method for stop receiver battery change
        fun unregisterBatteryChangeReceiver(context: Context, broadcastReceiver: BroadcastReceiver) {
            context.unregisterReceiver(broadcastReceiver)
        }

        // Method for receive power connection
        fun registerPowerConnectionReceiver(context: Context, broadcastReceiver: BroadcastReceiver) {
            val powerStateFilter = IntentFilter()
            powerStateFilter.addAction(Intent.ACTION_POWER_CONNECTED)
            powerStateFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            context.registerReceiver(broadcastReceiver, powerStateFilter)
        }

        fun unregisterPowerConnection(context: Context, broadcastReceiver: BroadcastReceiver) {
            context.unregisterReceiver(broadcastReceiver)
        }

        fun recordCashOnHand(context: Context, amount: String) {
            val preference = context.getSharedPreferences("cohPrefs", Context.MODE_PRIVATE)
            preference.edit().putString("cashOnHand", amount)
                    .putLong("timestamp", Tool.truncTime(Date()).time)
                    .apply()
        }
    }
    @Keep
    open class PrintParams(var taskId: String?, var source: String?, var isPrintDeposit: Boolean?, var isDummy: Boolean?)
}