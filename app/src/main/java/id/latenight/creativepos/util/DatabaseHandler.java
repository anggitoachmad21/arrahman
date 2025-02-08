package id.latenight.creativepos.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.adapter.sampler.Categories;
import id.latenight.creativepos.adapter.sampler.CustomerValues;
import id.latenight.creativepos.adapter.sampler.Labels;
import id.latenight.creativepos.adapter.sampler.Menus;
import id.latenight.creativepos.adapter.sampler.PaymentMethod;
import id.latenight.creativepos.adapter.sampler.SubCategories;
import id.latenight.creativepos.adapter.sampler.Tables;

public class DatabaseHandler extends SQLiteOpenHelper {

    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "Carwash-v2";

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
        String limit = " LIMIT " + size;

        String query = selectQuery+values+limit;
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
            offset = (page * size) - page;
        }
        String limit = " LIMIT " + size + " OFFSET " + offset;

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
                        (c.getInt(c.getColumnIndex(KEY_MENU_ID)))
                );
                // adding to product list
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
                    (cursor.getInt(cursor.getColumnIndex(KEY_MENU_ID)))
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
}
