package id.latenight.creativepos.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.latenight.creativepos.adapter.sampler.Categories;
import id.latenight.creativepos.adapter.sampler.CustomerInfo;
import id.latenight.creativepos.adapter.sampler.CustomerValues;
import id.latenight.creativepos.adapter.sampler.Employee;
import id.latenight.creativepos.adapter.sampler.Labels;
import id.latenight.creativepos.adapter.sampler.Menus;
import id.latenight.creativepos.adapter.sampler.Order;
import id.latenight.creativepos.adapter.sampler.PPN;
import id.latenight.creativepos.adapter.sampler.PaymentMethod;
import id.latenight.creativepos.adapter.sampler.SubCategories;
import id.latenight.creativepos.adapter.sampler.Tables;
import id.latenight.creativepos.adapter.sampler.Washer;

public class DatabaseHandler extends SQLiteOpenHelper {

    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "Carwash-v5";

    // table name
    private static final String TABLE_TALL = "sales";

    // TABLE CATEGORIES
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_SUB_CATEGORIES = "sub_categories";
    private static final String TABLE_LABELS = "labels";
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String TABLE_MENUS= "menus";
    private static final String TABLE_TABLES= "tables";
    private static final String TABLE_PAYMENT_METHOD= "payment_method";
    private static final String TABLE_ORDERS= "orders";
    private static final String TABLE_CUSTOMER_INFO= "customer_info";
    private static final String TABLE_WASHER= "washer";
    private static final String TABLE_EMPLOYEE= "employee";
    private static final String TABLE_PPN= "ppn";

    // FORM CATEGORIES
    private static final String KEY_NAME = "name";
    private static final String CATEGORIES_ID = "categories_id";
    private static final String KEY_SUB_CATEGORIES_ID = "sub_categories_id";
    private static final String LABEL_ID = "label_id";
    private static final String KEY_ID = "id";
    private static final String KEY_DATA = "data";

    // FORM CUSTOMERS
    private static final String KEY_USER_ID = "user_id";
    private static final String IS_MEMBER = "is_member";

    // FORM MENUS
    private static final String KEY_MENU_ID = "menu_id";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_SALE_PRICE = "sale_price";
    private static final String KEY_RESELLER_PRICE = "reseller_price";
    private static final String KEY_OUTLET_PRICE = "outlet_price";
    private static final String KEY_PARTNER_PRICE = "partner_price";
    private static final String KEY_ONLINE_PRICE = "online_price";
    private static final String KEY_INGREDIENT_STOCK = "ingredient_stock";

    private static final String KEY_TABLES_ID = "table_id";
    private static final String KEY_PAYMENT_METHOD = "payment_method_id";
    private static final String KEY_DESCRIPTION = "description";

    private static final String KEY_LIVE = "del";

    // orders
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_ORDER_NO = "order_no";
    private static final String KEY_ORDER_DETAILS = "order_details";
    private static final String KEY_STATUS = "order_status";

    private static final String KEY_ORDER_DATE = "order_date";
    private static final String KEY_CUSTOMER_DETAILS = "customer_details";

    // employee
    private static final String KEY_EMPLOYEE_DETAIL = "employee_detail";
    private static final String KEY_TYPE = "type";
    private static final String KEY_EMPLOYEE_ID = "employee_id";
    private static final String KEY_WASHER_ID = "washer_id";
    private static final String KEY_PERCENTAGE = "percentage";

    private static final Date d = Calendar.getInstance().getTime();
    @SuppressLint("SimpleDateFormat")
    private static  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATA + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR ,"
                + CATEGORIES_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_CATEGORIES);

        String CREATE_TABLE_SUB_CATEGORIES = "CREATE TABLE " + TABLE_SUB_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR ,"
                + KEY_SUB_CATEGORIES_ID + " INTEGER ,"
                + CATEGORIES_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_SUB_CATEGORIES);

        String CREATE_TABLE_LABELS = "CREATE TABLE " + TABLE_LABELS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_SUB_CATEGORIES_ID + " INTEGER, "
                + LABEL_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_LABELS);

        String CREATE_TABLE_CUSTOMERS = "CREATE TABLE " + TABLE_CUSTOMERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + IS_MEMBER + " INTEGER, "
                + KEY_LIVE + " VARCHAR, "
                + KEY_USER_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_CUSTOMERS);

        String CREATE_TABLE_MENUS = "CREATE TABLE " + TABLE_MENUS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_PHOTO + " VARCHAR, "
                + KEY_LIVE + " VARCHAR, "
                + KEY_RESELLER_PRICE + " INTEGER, "
                + KEY_OUTLET_PRICE + " INTEGER ,"
                + KEY_SALE_PRICE + " INTEGER, "
                + KEY_PARTNER_PRICE + " INTEGER, "
                + KEY_INGREDIENT_STOCK + " INTEGER, "
                + KEY_MENU_ID + " INTEGER, "
                + KEY_SUB_CATEGORIES_ID + " INTEGER ,"
                + CATEGORIES_ID + " INTEGER, "
                + LABEL_ID + " INTEGER, "
                + KEY_ONLINE_PRICE + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_MENUS);

        String CREATE_TABLE_TABLES = "CREATE TABLE " + TABLE_TABLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_TABLES_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_TABLES);

        String CREATE_TABLE_PAYMENT_METHOD = "CREATE TABLE " + TABLE_PAYMENT_METHOD + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_DESCRIPTION+ " VARCHAR, "
                + KEY_PAYMENT_METHOD + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_PAYMENT_METHOD);

        String CREATE_TABLE_ORDER = "CREATE TABLE " + TABLE_ORDERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ORDER_NO + " VARCHAR, "
                + KEY_ORDER_ID + " INTEGER, "
                + KEY_STATUS+ " INTEGER, "
                + KEY_ORDER_DATE+ " DATETIME, "
                + KEY_ORDER_DETAILS + " LONGTEXT " + ")";
        db.execSQL(CREATE_TABLE_ORDER);

        String CREATE_TABLE_CUSTOMER_INFO = "CREATE TABLE " + TABLE_CUSTOMER_INFO + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_USER_ID + " INTEGER, "
                + KEY_CUSTOMER_DETAILS + " LONGTEXT " + ")";
        db.execSQL(CREATE_TABLE_CUSTOMER_INFO);

        String CREATE_TABLE_WASHER = "CREATE TABLE " + TABLE_WASHER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_TYPE + " VARCHAR, "
                + KEY_WASHER_ID + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_WASHER);

        String CREATE_TABLE_EMPLOYEE = "CREATE TABLE " + TABLE_EMPLOYEE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " VARCHAR, "
                + KEY_EMPLOYEE_ID + " INTEGER, "
                + KEY_EMPLOYEE_DETAIL + " LONGTEXT " + ")";
        db.execSQL(CREATE_TABLE_EMPLOYEE);

        String CREATE_TABLE_PPN = "CREATE TABLE " + TABLE_PPN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PERCENTAGE + " INTEGER "+ ")";
        db.execSQL(CREATE_TABLE_PPN);
    }

    // on Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUB_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TABLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_METHOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WASHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PPN);
        onCreate(db);
    }

    public void addSales(String id, String sales){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_DATA, sales);

        db.insert(TABLE_TALL, null, values);
        db.close();
    }

    public void updateNote(int id, String sales) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_DATA, sales);

        // updating row
        db.update(TABLE_TALL, values, KEY_ID + " = ? ", new String[] { String.valueOf(id) });
        db.close();
    }

    public void deleteSales(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TALL, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public Sales getSales(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TALL, new String[] { KEY_ID,
                        KEY_DATA }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Sales sales = new Sales(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return contact
        return sales;
    }
    // get All Record
    public ArrayList<String> getAllSales() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TALL;
        Cursor res =  db.rawQuery( selectQuery, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_DATA)));
            res.moveToNext();
        }
        return array_list;
    }

    public int getSalesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TALL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // CATEGORIES
    public void addCategories(String name, int categories_id){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(CATEGORIES_ID, categories_id);

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public Categories getCategories(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String checkQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CATEGORIES_ID + "= '"+id+ "'";
        Cursor cursor = db.rawQuery(checkQuery,null);

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();

            Categories cart = new Categories(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    (cursor.getString(cursor.getColumnIndex(KEY_NAME))),
                    (cursor.getInt(cursor.getColumnIndex(CATEGORIES_ID)))
            );
            // return contact
            cursor.close();
            return cart;
        } else {
            cursor.close();
            return null;
        }
    }

    public List<Categories> getCategoriesName(String name) {
        List<Categories> carts = new ArrayList<Categories>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE " + KEY_NAME + " LIKE '%" + name + "%' LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Categories cart = new Categories(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID)))
                );

                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Categories> getAllCategories() {
        List<Categories> carts = new ArrayList<Categories>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Categories cart = new Categories(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID)))
                );

                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // Sub categories
    public void addSubCategories(String name, int categories_id, int sub_categories_id){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(CATEGORIES_ID, categories_id);
        values.put(KEY_SUB_CATEGORIES_ID, sub_categories_id);

        db.insert(TABLE_SUB_CATEGORIES, null, values);
        db.close();
    }

    public List<SubCategories> getAllSubCategories(int categories) {
        List<SubCategories> carts = new ArrayList<SubCategories>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUB_CATEGORIES + " WHERE " + CATEGORIES_ID + " = "+ categories;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                SubCategories cart = new SubCategories(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID)))
                );

                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<SubCategories> getSubCategories(String name) {
        List<SubCategories> carts = new ArrayList<SubCategories>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUB_CATEGORIES + " WHERE " + KEY_NAME + " LIKE '%" + name + "%' LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SubCategories cart = new SubCategories(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID)))
                );

                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // LABELS
    public void addLabels(String name, int label_id, int sub_categories_id){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_SUB_CATEGORIES_ID, sub_categories_id);
        values.put(LABEL_ID, label_id);

        db.insert(TABLE_LABELS, null, values);
        db.close();
    }

    public List<Labels> getAllLabels(int subcategories_id) {
        List<Labels> carts = new ArrayList<Labels>();
        String selectQuery = "SELECT  * FROM " + TABLE_LABELS + " WHERE "+ KEY_SUB_CATEGORIES_ID + " = " + subcategories_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Labels cart = new Labels(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(LABEL_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Labels> getLabelName(String name) {
        List<Labels> carts = new ArrayList<Labels>();
        String selectQuery = "SELECT  * FROM " + TABLE_LABELS + " WHERE " + KEY_NAME + " = '"+ name + "' LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Labels cart = new Labels(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(LABEL_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public Labels getLabels(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String checkQuery = "SELECT * FROM " + TABLE_LABELS + " WHERE " + LABEL_ID + "= '"+id+ "'";
        Cursor cursor = db.rawQuery(checkQuery,null);

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();

            Labels cart = new Labels(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    (cursor.getString(cursor.getColumnIndex(KEY_NAME))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                    (cursor.getInt(cursor.getColumnIndex(LABEL_ID)))
            );
            // return contact
            cursor.close();
            return cart;
        } else {
            cursor.close();
            return null;
        }
    }

    // CUSTOMERS
    public void addCustomers(String name, int user_id, int is_member){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(IS_MEMBER, is_member);
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_LIVE, "Live");

        db.insert(TABLE_CUSTOMERS, null, values);
        db.close();
    }

    public List<CustomerValues> getAllCustomer(int page, String keyword) {
        List<CustomerValues> carts = new ArrayList<CustomerValues>();
        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMERS;
        String values = "";
        int size = 50;
        if(!keyword.isEmpty()){
            values = " WHERE "+ KEY_NAME + " LIKE '%"+ keyword + "%'";
        }
        String orderBy = " GROUP BY " + KEY_NAME + " ORDER BY " + KEY_ID + " DESC ";
        String limit = " LIMIT " + size;

        String query = selectQuery+values+orderBy+limit;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                CustomerValues cart = new CustomerValues(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(IS_MEMBER))),
                        (c.getInt(c.getColumnIndex(KEY_USER_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public CustomerValues getCustomer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String checkQuery = "SELECT * FROM " + TABLE_CUSTOMERS+ " WHERE " + KEY_USER_ID + "= '"+id+ "'";
        Cursor cursor = db.rawQuery(checkQuery,null);

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();

            CustomerValues cart = new CustomerValues(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    (cursor.getString(cursor.getColumnIndex(KEY_NAME))),
                    (cursor.getInt(cursor.getColumnIndex(IS_MEMBER))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)))
            );
            // return contact
            cursor.close();
            return cart;
        } else {
            cursor.close();
            return null;
        }
    }

    public void addMenus(String name, String photo, int sale_price,
                         int outlet_price, int online_price, int reseller_price, int partner_price,
                         int ingredient_stock, int menu_id, int categories_id, int sub_categories,
                         int label_id){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHOTO, photo);
        values.put(KEY_ONLINE_PRICE, online_price);
        values.put(KEY_OUTLET_PRICE, outlet_price);
        values.put(KEY_SALE_PRICE, sale_price);
        values.put(KEY_RESELLER_PRICE, reseller_price);
        values.put(KEY_PARTNER_PRICE, partner_price);
        values.put(KEY_INGREDIENT_STOCK, ingredient_stock);
        values.put(KEY_MENU_ID, menu_id);
        values.put(CATEGORIES_ID, categories_id);
        values.put(KEY_SUB_CATEGORIES_ID, sub_categories);
        values.put(LABEL_ID, label_id);
        values.put(KEY_LIVE, "Live");

        db.insert(TABLE_MENUS, null, values);
        db.close();
    }

    public List<Menus> getAllMenus(String keyword, int page, int categories_id,
                                   int sub_categories_id, int label_id) {
        List<Menus> carts = new ArrayList<Menus>();
        String selectQuery = "SELECT * FROM " + TABLE_MENUS + " WHERE " + KEY_LIVE + " = " + "'Live'";
        String keywords = "";
        String categoriess = "";
        String subcategories = "";
        String labels = "";
        int size = 12;
        int offset = 0;
        if(!keyword.isEmpty()){
            keywords = " AND "+ KEY_NAME +" LIKE '%"+ keyword+ "%'";
        }
        if(categories_id != 0){
            categoriess = " AND "+ CATEGORIES_ID + " = " + categories_id;
        }
        if(sub_categories_id != 0){
            subcategories = " AND "+ KEY_SUB_CATEGORIES_ID + " = " + sub_categories_id;
        }
        if(label_id != 0){
            labels = " AND "+ LABEL_ID + " = " + label_id;
        }
        if(page!=0){
            offset = (page * size) - size;
        }
        String limit = "";
        if(keyword.isEmpty()){
            limit = " LIMIT " + size + " OFFSET " + offset;
        }

        String query = selectQuery+keywords+categoriess+subcategories+labels+limit;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Menus cart = new Menus(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getString(c.getColumnIndex(KEY_PHOTO))),
                        (c.getInt(c.getColumnIndex(KEY_SALE_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_ONLINE_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_OUTLET_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_RESELLER_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_PARTNER_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_INGREDIENT_STOCK))),
                        (c.getInt(c.getColumnIndex(KEY_MENU_ID))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(LABEL_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Menus> getAllMenuById(int id) {
        List<Menus> carts = new ArrayList<Menus>();
        String selectQuery = "SELECT * FROM " + TABLE_MENUS + " WHERE " + KEY_MENU_ID + " = " + id;

        String query = selectQuery;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Menus cart = new Menus(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getString(c.getColumnIndex(KEY_PHOTO))),
                        (c.getInt(c.getColumnIndex(KEY_SALE_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_ONLINE_PRICE))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_PARTNER_PRICE))),
                        (c.getInt(c.getColumnIndex(KEY_INGREDIENT_STOCK))),
                        (c.getInt(c.getColumnIndex(KEY_MENU_ID))),
                        (c.getInt(c.getColumnIndex(CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                        (c.getInt(c.getColumnIndex(LABEL_ID)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public Menus getMenu(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String checkQuery = "SELECT * FROM " + TABLE_MENUS+ " WHERE " + KEY_MENU_ID + "= '"+id+ "'";
        Cursor cursor = db.rawQuery(checkQuery,null);

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();

            Menus cart = new Menus(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    (cursor.getString(cursor.getColumnIndex(KEY_NAME))),
                    (cursor.getString(cursor.getColumnIndex(KEY_PHOTO))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_SALE_PRICE))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_ONLINE_PRICE))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_OUTLET_PRICE))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_RESELLER_PRICE))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_PARTNER_PRICE))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_INGREDIENT_STOCK))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_MENU_ID))),
                    (cursor.getInt(cursor.getColumnIndex(CATEGORIES_ID))),
                    (cursor.getInt(cursor.getColumnIndex(KEY_SUB_CATEGORIES_ID))),
                    (cursor.getInt(cursor.getColumnIndex(LABEL_ID)))
            );
            // return contact
            cursor.close();
            return cart;
        } else {
            cursor.close();
            return null;
        }
    }


    // TABLES
    public void addTables(String name, int table_id){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TABLES_ID, table_id);

        db.insert(TABLE_TABLES, null, values);
        db.close();
    }

    public List<Tables> getAllTables() {
        List<Tables> carts = new ArrayList<Tables>();
        String selectQuery = "SELECT  * FROM " + TABLE_TABLES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tables cart = new Tables(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_TABLES_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }
        return carts;
    }

    public List<Tables> getAllTablesByName(String name) {
        List<Tables> carts = new ArrayList<Tables>();
        String selectQuery = "SELECT  * FROM " + TABLE_TABLES + " WHERE " + KEY_NAME + " = " + name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tables cart = new Tables(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_TABLES_ID)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }
        return carts;
    }

    // PAYEMENT METHOD
    public void addPaymentMethod(String name, int payment_id, String description){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_PAYMENT_METHOD, payment_id);

        db.insert(TABLE_PAYMENT_METHOD, null, values);
        db.close();
    }

    public List<PaymentMethod> getAllPayment() {
        List<PaymentMethod> carts = new ArrayList<PaymentMethod>();
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENT_METHOD;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                PaymentMethod cart = new PaymentMethod(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_PAYMENT_METHOD))),
                        (c.getString(c.getColumnIndex(KEY_DESCRIPTION)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // customer info
    public void addCustomerInfo(int user_id, String name ,String customer_info){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_CUSTOMER_DETAILS, customer_info);

        db.insert(TABLE_CUSTOMER_INFO, null, values);
        db.close();
    }

    public void updateCustomerInfo(int user_id, String customer_info){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOMER_DETAILS, customer_info);

        db.update(TABLE_CUSTOMER_INFO, values, KEY_USER_ID + " = ? ", new String[] { String.valueOf(user_id) });
        db.close();
    }

    public List<CustomerInfo> getCustomerInfo(String name) {
        List<CustomerInfo> carts = new ArrayList<CustomerInfo>();
        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER_INFO + " WHERE " + KEY_NAME + " = '" + name + "' GROUP BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                CustomerInfo cart = new CustomerInfo(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getString(c.getColumnIndex(KEY_CUSTOMER_DETAILS)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<CustomerInfo> getAllCustomerInfo() {
        List<CustomerInfo> carts = new ArrayList<CustomerInfo>();
        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER_INFO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                CustomerInfo cart = new CustomerInfo(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getString(c.getColumnIndex(KEY_CUSTOMER_DETAILS)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<CustomerInfo> getAllCustomerInfoLast() {
        List<CustomerInfo> carts = new ArrayList<CustomerInfo>();
        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER_INFO + " ORDER BY " + KEY_ID + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                CustomerInfo cart = new CustomerInfo(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getString(c.getColumnIndex(KEY_CUSTOMER_DETAILS)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // orders
    public void addOrders(String order_no, int order_id, int status, String order_details, String order_date){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORDER_NO, order_no);
        values.put(KEY_ORDER_ID, order_id);
        values.put(KEY_STATUS, status);
        values.put(KEY_ORDER_DETAILS, order_details);
        values.put(KEY_ORDER_DATE, order_date);

        db.insert(TABLE_ORDERS, null, values);
        db.close();
    }

    public void udpdateOrders(int order_id, int status, String order_details){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);
        values.put(KEY_ORDER_DETAILS, order_details);

        db.update(TABLE_ORDERS, values, KEY_ORDER_ID + " = ? ", new String[] { String.valueOf(order_id) });
        db.close();
    }

    public List<Order> getOrderBySale(int order_id) {
        List<Order> carts = new ArrayList<Order>();
        String selectQuery = "SELECT  * FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_ID + " = " + order_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Order> getOrderByALL() {
        List<Order> carts = new ArrayList<Order>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " GROUP BY " + KEY_ORDER_NO + " ORDER BY " + KEY_ORDER_NO + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Order> getOrderByAntrian() {
        String currentData = formatter.format(d);
        List<Order> carts = new ArrayList<Order>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_DATE + " = '" + currentData + "' ORDER BY " + KEY_ID + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Order> getOrderByIdAsc() {
        String currentData = formatter.format(d);
        List<Order> carts = new ArrayList<Order>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_DATE + " = '" + currentData + "' AND " + KEY_STATUS + " IN (3,4)" + " ORDER BY " + KEY_ORDER_NO + " ASC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Order> getOrderHistory(int page){
        String currentData = formatter.format(d);
        List<Order> carts = new ArrayList<Order>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + KEY_STATUS + " IN (2,3) "
                + " AND " + KEY_ORDER_DATE + " = '" + currentData + "' ORDER BY " + KEY_ID + " DESC ";
        int size = 20;
        int offset = 0;
        String limit = "";
        if(page!=0){
            offset = (page * size) - size;
            limit = " LIMIT " + size + " OFFSET " + offset;
        }

        String query = selectQuery+limit;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Order> getOrderRunning(){
        List<Order> carts = new ArrayList<Order>();
        String currentData = formatter.format(d);
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + KEY_STATUS + " NOT IN (3,4) "
                + " AND " + KEY_ORDER_DATE + " = '" + currentData + "' ORDER BY " + KEY_ID + " DESC ";
        String query = selectQuery;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Order cart = new Order(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_ORDER_NO))),
                        (c.getInt(c.getColumnIndex(KEY_ORDER_ID))),
                        (c.getInt(c.getColumnIndex(KEY_STATUS))),
                        (c.getString(c.getColumnIndex(KEY_ORDER_DETAILS)))
                );
                // adding to product list
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }


    // add washer
    public void addWasher(String name, int washer_id, String type){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_WASHER_ID, washer_id);
        values.put(KEY_TYPE, type);

        db.insert(TABLE_WASHER, null, values);
        db.close();
    }

    public List<Washer> getAllWasher(String type) {
        List<Washer> carts = new ArrayList<Washer>();
        String selectQuery = "SELECT  * FROM " + TABLE_WASHER + " WHERE " + KEY_TYPE + " = '" + type + "' ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Washer cart = new Washer(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_WASHER_ID))),
                        (c.getString(c.getColumnIndex(KEY_TYPE)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // add employee
    public void addEmployee(String name, int employee_id, String employee_detail){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMPLOYEE_ID, employee_id);
        values.put(KEY_EMPLOYEE_DETAIL, employee_detail);

        db.insert(TABLE_EMPLOYEE, null, values);
        db.close();
    }

    public List<Employee> getAllEmployee() {
        List<Employee> carts = new ArrayList<Employee>();
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Employee cart = new Employee(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_EMPLOYEE_ID))),
                        (c.getString(c.getColumnIndex(KEY_EMPLOYEE_DETAIL)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Employee> getAllEmployeeByName(String name) {
        List<Employee> carts = new ArrayList<Employee>();
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE + " WHERE " + KEY_NAME + " LIKE '%" + name + "%' LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Employee cart = new Employee(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_EMPLOYEE_ID))),
                        (c.getString(c.getColumnIndex(KEY_EMPLOYEE_DETAIL)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public List<Employee> getAllEmployeeById(String id) {
        List<Employee> carts = new ArrayList<Employee>();
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE + " WHERE " + KEY_EMPLOYEE_ID + " LIKE '%" + id + "%' LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Employee cart = new Employee(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getString(c.getColumnIndex(KEY_NAME))),
                        (c.getInt(c.getColumnIndex(KEY_EMPLOYEE_ID))),
                        (c.getString(c.getColumnIndex(KEY_EMPLOYEE_DETAIL)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    // ppn
    public void addPPN(int percentage){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERCENTAGE, percentage);

        db.insert(TABLE_PPN, null, values);
        db.close();
    }

    public List<PPN> getPPN() {
        List<PPN> carts = new ArrayList<PPN>();
        String selectQuery = "SELECT  * FROM " + TABLE_PPN + " LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                PPN cart = new PPN(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        (c.getInt(c.getColumnIndex(KEY_PERCENTAGE)))
                );
                carts.add(cart);
            } while (c.moveToNext());
        }

        return carts;
    }

    public void truncate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TALL);
        db.close();
    }

    public void truncateCustomers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CUSTOMERS);
        db.close();
    }

    public void truncateCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CATEGORIES);
        db.close();
    }

    public void truncateSubCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SUB_CATEGORIES);
        db.close();
    }

    public void truncateLabels() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LABELS);
        db.close();
    }

    public void truncateTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TABLES);
        db.close();
    }
    public void truncatePaymentMethod() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PAYMENT_METHOD);
        db.close();
    }

    public void truncateMenus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MENUS);
        db.close();
    }

    public void truncateOrders() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ORDERS);
        db.close();
    }

    public void truncateWasher() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_WASHER);
        db.close();
    }
    public void truncateEmployee() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EMPLOYEE);
        db.close();
    }

    public void truncatePPN() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PPN);
        db.close();
    }

    public void truncateCustomerInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CUSTOMER_INFO);
        db.close();
    }

    public void deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_ID + " = " + id);
        db.close();
    }

    public void deleteCustomerInfo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CUSTOMER_INFO + " WHERE " + KEY_USER_ID + " = " + id);
        db.close();
    }

    public void deleteSalesToday() {
        String currentData = formatter.format(d);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_DATE + " = '" + currentData + "'");
        db.close();
    }

    public void exportToDownload(Context context, String tableName, ProgressDialog loadingDialog){
        loadingDialog.show();
        SQLiteDatabase db = getWritableDatabase();
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsFolder.exists()) {
            Toast.makeText(context, "Gagal Export", Toast.LENGTH_SHORT).show();
            return;
        }
        File csvFile = new File(downloadsFolder, tableName+".csv");

        Cursor cursor = db.rawQuery("SELECT * FROM '" + tableName +"'", null);

        int columnCount = cursor.getColumnCount();

        FileWriter writer = null;
        try {
            writer = new FileWriter(csvFile);

            for (int i = 0; i < columnCount; i++) {
                writer.append(cursor.getColumnName(i));
                if (i < columnCount - 1) writer.append(",");
            }
            writer.append("\n");

            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    writer.append(cursor.getString(i));
                    if (i < columnCount - 1) writer.append(",");
                }
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            loadingDialog.dismiss();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) cursor.close();
            Toast.makeText(context, "Berhasil Export", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    public void importDatabase(Context context, String type, Uri fileUri, ProgressDialog loading){
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            if (line != null) {
                String[] columns = line.split(",");

                SQLiteDatabase db = this.getWritableDatabase();
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < columns.length; i++) {
                        if(type.equals("orders")) {
                            if (i >= 5) {
                                String valOf = "";
                                for (int j = 5; j < data.length; j++) {
                                    String coma = "";
                                    if (j < data.length) {
                                        coma = ",";
                                    }
                                    valOf += data[j] + coma;
                                }
                                values.put(KEY_ORDER_DETAILS, valOf);
                            } else {
                                values.put(columns[i], data[i]);
                            }
                        }else if(type.equals("customer_info")){
                            if (i >= 3) {
                                String valOf = "";
                                for (int j = 3; j < data.length; j++) {
                                    String coma = "";
                                    if (j < data.length) {
                                        coma = ",";
                                    }
                                    valOf += data[j] + coma;
                                }
                                values.put(KEY_CUSTOMER_DETAILS, valOf);
                            } else {
                                values.put(columns[i], data[i]);
                            }
                        }else {
                            values.put(columns[i], data[i]);
                        }
                    }
                    db.insert(type, null, values);
                }

                reader.close();
                inputStream.close();
                loading.dismiss();
            }
        } catch (IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            loading.show();
            e.printStackTrace();
        }
    }
}
