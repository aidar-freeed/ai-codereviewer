package com.adins.mss.foundation.security.storepreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.security.SAKFormatter;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;


/**
 * Thanks to emmby at http://stackoverflow.com/questions/785973/what-is-the-most-appropriate-way-to-store-user-settings-in-android-application/6393502#6393502
 * <p>
 * Documentation: http://right-handed-monkey.blogspot.com/2014/04/obscured-shared-preferences-for-android.html
 * This class has the following additions over the original:
 * additional logic for handling the case for when the preferences were not originally encrypted, but now are.
 * The secret key is no longer hard coded, but defined at runtime based on the individual device.
 * The benefit is that if one device is compromised, it now only affects that device.
 * <p>
 * Simply replace your own SharedPreferences object in this one, and any data you read/write will be automatically encrypted and decrypted.
 * <p>
 * Updated usage:
 * ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(this, MY_APP_NAME, Context.MODE_PRIVATE);
 * //to get data
 * prefs.getString("foo", null);
 * //to store data
 * prefs.edit().putString("foo","bar").commit();
 */
public class ObscuredSharedPreferences implements SharedPreferences {
    protected static final String UTF8 = "UTF-8";
    //Set to true if a decryption error was detected
    //in the case of float, int, and long we can tell if there was a parse error
    //this does not detect an error in strings or boolean - that requires more sophisticated checks
    public static boolean decryptionErrorFlag = false;
    //this key is defined at runtime based on ANDROID_ID which is supposed to last the life of the device
    private static char[] SEKRIT = null;
    private static byte[] SALT = null;
    private static char[] backup_secret = null;
    private static byte[] backup_salt = null;
    protected SharedPreferences delegate;
    protected Context context;

    /**
     * Constructor
     *
     * @param context
     * @param delegate - SharedPreferences object from the system
     */
    public ObscuredSharedPreferences(Context context, SharedPreferences delegate) {
        this.delegate = delegate;
        this.context = context;
        //updated thanks to help from bkhall on github
        ObscuredSharedPreferences.setNewKey(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        ObscuredSharedPreferences.setNewSalt(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    /**
     * Only used to change to a new key during runtime.
     * If you don't want to use the default per-device key for example
     *
     * @param key
     */
    public static void setNewKey(String key) {
        SEKRIT = key.toCharArray();
    }

    /**
     * Only used to change to a new salt during runtime.
     * If you don't want to use the default per-device code for example
     *
     * @param salt - this must be a string in UT
     */
    public static void setNewSalt(String salt) {
        try {
            SALT = salt.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accessor to grab the preferences object
     * To improve performance for multiple accesses, please store the returned object in a variable for reuse
     *
     * @param c           - the context used to access the preferences.
     * @param domainName  - domain the shared preferences should be stored under
     * @param contextMode - Typically Context.MODE_PRIVATE
     * @return
     */
    public synchronized static ObscuredSharedPreferences getPrefs(Context c, String domainName, int contextMode) {
        //make sure to use application context since preferences live outside an Activity
        //use for objects that have global scope like: prefs or starting services
        ObscuredSharedPreferences prefs = new ObscuredSharedPreferences(
                c.getApplicationContext(), c.getApplicationContext().getSharedPreferences(domainName, contextMode));
        return prefs;
    }

    /**
     * Push key allows you to hold the current key being used into a holding location so that it can be retrieved later
     * The use case is for when you need to load a new key, but still want to restore the old one.
     */
    public static void pushKey() {
        backup_secret = SEKRIT;
    }

    /**
     * This takes the key previously saved by pushKey() and activates it as the current decryption key
     */
    public static void popKey() {
        SEKRIT = backup_secret;
    }

    /**
     * pushSalt() allows you to hold the current salt being used into a holding location so that it can be retrieved later
     * The use case is for when you need to load a new salt, but still want to restore the old one.
     */
    public static void pushSalt() {
        backup_salt = SALT;
    }

    /**
     * This takes the value previously saved by pushSalt() and activates it as the current salt
     */
    public static void popSalt() {
        SALT = backup_salt;
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException(); // left as an exercise to the reader
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        //if these weren't encrypted, then it won't be a string
        String v;
        try {
            v = delegate.getString(key, null);
        } catch (ClassCastException e) {
            return delegate.getBoolean(key, defValue);
        }
        //Boolean string values should be 'true' or 'false'
        //Boolean.parseBoolean does not throw a format exception, so check manually
        String parsed = decrypt(v);
        if (!checkBooleanString(parsed)) {
            //could not decrypt the Boolean.  Maybe the wrong key was used.
            decryptionErrorFlag = true;
            Log.w(this.getClass().getName(), "Warning, could not decrypt the value.  Possible incorrect key used.");
        }
        return v != null ? Boolean.parseBoolean(parsed) : defValue;
    }

    /**
     * This function checks if a valid string is received on a request for a Boolean object
     *
     * @param str
     * @return
     */
    private boolean checkBooleanString(String str) {
        return ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str));
    }

    @Override
    public float getFloat(String key, float defValue) {
        String v;
        try {
            v = delegate.getString(key, null);
        } catch (ClassCastException e) {
            return delegate.getFloat(key, defValue);
        }
        try {
            return Float.parseFloat(decrypt(v));
        } catch (NumberFormatException e) {
            //could not decrypt the number.  Maybe we are using the wrong key?
            decryptionErrorFlag = true;
            Log.e(this.getClass().getName(), "Warning, could not decrypt the value.  Possible incorrect key.  " + e.getMessage());
        }
        return defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        String v;
        try {
            v = delegate.getString(key, null);
        } catch (ClassCastException e) {
            return delegate.getInt(key, defValue);
        }
        try {
            return Integer.parseInt(decrypt(v));
        } catch (NumberFormatException e) {
            //could not decrypt the number.  Maybe we are using the wrong key?
            decryptionErrorFlag = true;
            Log.e(this.getClass().getName(), "Warning, could not decrypt the value.  Possible incorrect key.  " + e.getMessage());
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        String v;
        try {
            v = delegate.getString(key, null);
        } catch (ClassCastException e) {
            return delegate.getLong(key, defValue);
        }
        try {
            return Long.parseLong(decrypt(v));
        } catch (NumberFormatException e) {
            //could not decrypt the number.  Maybe we are using the wrong key?
            decryptionErrorFlag = true;
            Log.e(this.getClass().getName(), "Warning, could not decrypt the value.  Possible incorrect key.  " + e.getMessage());
        }
        return defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @Override
    public boolean contains(String s) {
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        throw new RuntimeException("This class does not work with String Sets.");
    }

    protected String encrypt(String value) {

        try {
            byte[] cryptedBytes = SAKFormatter.cipherData(value);
            String result = new String(cryptedBytes);
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value) {
        try {
            String result = SAKFormatter.decipherData(value);
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            Log.e(this.getClass().getName(), "Warning, could not decrypt the value.  It may be stored in plaintext.  " + e.getMessage());
            return value;
        }
    }

    public class Editor implements SharedPreferences.Editor {
        protected SharedPreferences.Editor delegate;

        public Editor() {
            this.delegate = ObscuredSharedPreferences.this.delegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            delegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            delegate.putString(key, encrypt(value));
            return this;
        }

        @Override
        public void apply() {
            //to maintain compatibility with android level 7
            delegate.commit();
        }

        @Override
        public Editor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

        @Override
        public Editor remove(String s) {
            delegate.remove(s);
            return this;
        }

        @Override
        public android.content.SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            throw new RuntimeException("This class does not work with String Sets.");
        }
    }
}
