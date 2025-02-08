package id.latenight.creativepos.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "digCashyrinLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_FULLNAME = "full_name";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_ROLE = "role";
    private static final String KEY_OUTLET = "outlet";
    private static final String KEY_OUTLET_GROUP = "outlet_group";
    private static final String KEY_PRINTER_READY = "printer";
    private static final String KEY_ENABLE_PRINTER = "on";
    private static final String KEY_OPEN_REGISTRATION = "0";
    private static final String KEY_CUSTOMERS = "customers";
    private static final String LAST_DOWNLOAD = "last_download_page";

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setId(String id) {

        editor.putString(KEY_ID, id);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setFullName(String employeeId) {

        editor.putString(KEY_FULLNAME, employeeId);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setDesignation(String designation) {

        editor.putString(KEY_DESIGNATION, designation);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setRole(String role) {

        editor.putString(KEY_ROLE, role);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setOutlet(String outlet) {

        editor.putString(KEY_OUTLET, outlet);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setOutletGroup(String outlet_group) {

        editor.putString(KEY_OUTLET_GROUP, outlet_group);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setPrinter(String printer) {

        editor.putString(KEY_PRINTER_READY, printer);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setEnablePrinter(String enablePrinter) {
        editor.putString(KEY_ENABLE_PRINTER, enablePrinter);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setOpenRegistration(String openRegistration) {
        editor.putString(KEY_OPEN_REGISTRATION, openRegistration);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setCustomers(String customers) {
        editor.putString(KEY_CUSTOMERS, customers);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setLastDownload(String page) {
        editor.putString(LAST_DOWNLOAD, page);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getId(){
        return pref.getString(KEY_ID, "");
    }

    public String getFullname(){
        return pref.getString(KEY_FULLNAME, "");
    }

    public String getDesignation(){
        return pref.getString(KEY_DESIGNATION, "");
    }

    public String getRole(){
        return pref.getString(KEY_ROLE, "");
    }

    public String getOutlet(){
        return pref.getString(KEY_OUTLET, "");
    }

    public String getOutletGroup(){
        return pref.getString(KEY_OUTLET_GROUP, "");
    }

    public String getPrinter(){
        return pref.getString(KEY_PRINTER_READY, "");
    }

    public String getEnablePrinter(){
        return pref.getString(KEY_ENABLE_PRINTER, "");
    }

    public String getOpenRegistration(){
        return pref.getString(KEY_OPEN_REGISTRATION, "");
    }

    public String getCustomers(){
        return pref.getString(KEY_CUSTOMERS, "");
    }

    public String getLastDownload(){
        return pref.getString(LAST_DOWNLOAD, "");
    }
}
