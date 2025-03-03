package id.latenight.creativepos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.latenight.creativepos.util.DatabaseHandler;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class DownloadActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private Button btnDownloadMenu, btnDownloadCategories, btnDownloadSubCategories, btnDownloadLabel;
    private Button btnDowloadTables, btnDownloadPayment, btnDownloadWasher, btnDownloadEmployee, btnDownloadPPn, btnDownloadOrder;

    private Button btnSharingCustomer, btnSharingSale, btnImportCustomer, btnImportSale, btnImportCustomerInfo, btnCleanDataSales;
    private boolean doubleBackToExitPressedOnce = false;
    private  String perfectType = "";

    private TextView running_download, max_running_download;
    private LinearLayout backgroundDownload;

    private int socketTimeout = 30000;
    private int maxRetries = 1;

    private DatabaseHandler db;
    private ScrollView menuAdmin;
    private ProgressDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        sessionManager = new SessionManager(this);
        db = new DatabaseHandler(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Button downloadData = findViewById(R.id.download_data);
        downloadData.setOnClickListener(v -> {
            getCountCustomer();
        });

        btnDownloadMenu = findViewById(R.id.download_menu);
        btnDownloadCategories = findViewById(R.id.download_categories);
        btnDownloadSubCategories = findViewById(R.id.download_sub_categories);
        btnDownloadLabel = findViewById(R.id.download_label);
        btnDowloadTables = findViewById(R.id.download_tables);
        btnDownloadPayment = findViewById(R.id.download_payment);
        btnDownloadWasher = findViewById(R.id.download_washer);
        btnDownloadEmployee = findViewById(R.id.download_employee);
        btnDownloadPPn = findViewById(R.id.download_ppn);
        btnDownloadOrder = findViewById(R.id.download_order);

        running_download = findViewById(R.id.running_download);
        max_running_download = findViewById(R.id.max_running_download);
        backgroundDownload = findViewById(R.id.backgroundDownload);
        menuAdmin = findViewById(R.id.menu_admin);

        btnDownloadCategories.setOnClickListener(view ->{
            downloadCategories();
        });

        btnDownloadSubCategories.setOnClickListener(view ->{
            downloadSubCategories();
        });

        btnDownloadLabel.setOnClickListener(view -> {
            downloadLabels();
        });

        btnDowloadTables.setOnClickListener(view -> {
            downloadTables();
        });

        btnDownloadPayment.setOnClickListener(view -> {
            downloadPayement();
        });

        btnDownloadMenu.setOnClickListener(view -> {
            getCountMenus();
        });

        btnDownloadWasher.setOnClickListener(view -> {
            downloadWasher();
        });

        btnDownloadEmployee.setOnClickListener(view -> {
            downloadEmployee();
        });

        btnDownloadPPn.setOnClickListener(view -> {
            downloadPPN();
        });

        btnDownloadOrder.setOnClickListener(view -> {
            getOrder();
        });

        btnSharingCustomer = findViewById(R.id.sharing_customer);
        btnSharingSale = findViewById(R.id.sharing_sale);
        btnSharingCustomer.setOnClickListener(view -> {
            exportData("customer");
        });
        btnSharingSale.setOnClickListener(view -> {
            exportData("orders");
        });

        btnImportCustomer = findViewById(R.id.import_customer);
        btnImportCustomer.setOnClickListener(view -> {
            importData("customers");
        });
        btnImportSale = findViewById(R.id.import_sale);
        btnImportSale.setOnClickListener(view -> {
            importData("orders");
        });

        btnImportCustomerInfo = findViewById(R.id.import_customer_info);
        btnImportCustomerInfo.setOnClickListener(view -> {
            importData("customer_info");
        });

        btnCleanDataSales = findViewById(R.id.clean_data_sales);
        btnCleanDataSales.setOnClickListener(view ->{
            cleanDataSalesToday();
        });
    }


    private void downloadCategories(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_CATEGORIES, response -> {
            try {
                truncateCategories();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++){
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addCategories(jsonObject.getString("name"), jsonObject.getInt("id"));
                    running_download.setText(String.valueOf(i + 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadSubCategories(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_SUBCATEGORIES, response -> {
            try {
                truncateSubCategories();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i=0; i < res.length(); i++){
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addSubCategories(jsonObject.getString("name"), jsonObject.getInt("categories_id"),
                            jsonObject.getInt("id"));
                    running_download.setText(String.valueOf(i + 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Erorr download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadLabels(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_LABELS, response -> {
            try {
                truncateLabels();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++) {
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addLabels(jsonObject.getString("name"), jsonObject.getInt("sub_categories_id"),
                            jsonObject.getInt("id"));
                    running_download.setText(String.valueOf(i + 1));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadMenus(int page){
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_MENUS+"?page="+page, response -> {
            try {
                JSONArray res = new JSONArray(response);
                if (res.length() > 0) {
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject jsonObject = res.getJSONObject(i);
                        db.addMenus(jsonObject.getString("name"), jsonObject.getString("photo"),
                                jsonObject.getInt("sale_price"), jsonObject.getInt("online_price"),
                                jsonObject.getInt("outlet_price"), jsonObject.getInt("online_price"),
                                jsonObject.getInt("reseller_price"), 99,
                                jsonObject.getInt("id"), jsonObject.getInt("categories_id"),
                                jsonObject.getInt("sub_categories_id"), jsonObject.getInt("label_id"));
                    }
                    int summary = page + 1;
                    running_download.setText(String.valueOf(summary));
                    Log.e("COUNT", String.valueOf(summary));
                    downloadMenus(page + 1);
                }else{
                    visibleLoading();
                    Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
                    backgroundDownload.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                visibleLoading();
                Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
                backgroundDownload.setVisibility(View.GONE);
            }
        }, error -> {
            visibleLoading();
            Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
            backgroundDownload.setVisibility(View.GONE);
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void getCountMenus(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.CHECK_COUNT_MENUS, response -> {
            try {
                truncateMenus();
                JSONObject jsonObject = new JSONObject(response);
                max_running_download.setText(String.valueOf(jsonObject.getInt("count")));
                running_download.setText(String.valueOf(1));
                downloadMenus(1);
            } catch (JSONException e) {
                visibleLoading();
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void getCountCustomer(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.CHECK_COUNT_CUSTOMER, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                max_running_download.setText(String.valueOf(jsonObject.getInt("count")));
                int check = continueDownload();
                int pages = 1;
                if(check > 0){
                    pages = check;
                }else{
                    truncateCustomer();
                }
                running_download.setText(String.valueOf(1));
                downloadCustomer(pages);
            } catch (JSONException e) {
                visibleLoading();
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadCustomer(int page){
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_CUSTOMER+"?page="+page, response -> {
            try {
                JSONArray res = new JSONArray(response);
                if (res.length() > 0) {
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject jsonObject = res.getJSONObject(i);
                        JSONObject customerInfo = jsonObject.getJSONObject("customer_info");
                        db.addCustomers(jsonObject.getString("name"), jsonObject.getInt("id"),
                                jsonObject.getInt("is_member"));
                        db.addCustomerInfo(jsonObject.getInt("id"), jsonObject.getString("name"),
                                customerInfo.toString());
                    }
                    running_download.setText(String.valueOf(page + 1));
                    downloadCustomer(page + 1);
                } else {
                    visibleLoading();
                    sessionManager.setLastDownload("");
                    Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
                    backgroundDownload.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
                backgroundDownload.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }, error -> {
            visibleLoading();
            saveLastDownload(String.valueOf(page));
            Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
            backgroundDownload.setVisibility(View.GONE);
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadTables(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_TABLES, response -> {
            try {
                truncateTables();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++) {
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addTables(jsonObject.getString("name"), jsonObject.getInt("id"));
                    running_download.setText(String.valueOf(i + 1));
                }
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Success Download", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            visibleLoading();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadPayement(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_PAYMENT_METHOD, response -> {
            try {
                truncatePaymentMethod();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++) {
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addPaymentMethod(jsonObject.getString("name"), jsonObject.getInt("id"), jsonObject.getString("description"));
                    running_download.setText(String.valueOf(i + 1));
                }
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Success Download", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            visibleLoading();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    public void downloadWasher(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_WASHER, response -> {
            try {
                truncateWasher();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++) {
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addWasher(jsonObject.getString("full_name"), jsonObject.getInt("id"), jsonObject.getString("type"));
                    running_download.setText(String.valueOf(i + 1));
                }
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Success Download", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            visibleLoading();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    public void downloadEmployee(){

        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_EMPLYOEE, response -> {
            try {
                truncateEmployee();
                JSONArray res = new JSONArray(response);
                max_running_download.setText(String.valueOf(res.length()));
                for (int i = 0; i < res.length(); i++) {
                    JSONObject jsonObject = res.getJSONObject(i);
                    db.addEmployee(jsonObject.getString("full_name"), jsonObject.getInt("id"), jsonObject.toString());
                    running_download.setText(String.valueOf(i + 1));
                }
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Success Download", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            visibleLoading();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void getOrder(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.CHECK_COUNT_ORDER, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                max_running_download.setText(String.valueOf(jsonObject.getInt("count")));
                int check = continueDownload();
                int pages = 1;
                if(check > 0){
                    pages = check;
                }else{
                    truncateOrder();
                }
                running_download.setText(String.valueOf(1));
                downloadOrder(pages);
            } catch (JSONException e) {
                visibleLoading();
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadOrder(int page){
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_ORDER+"?page="+page, response -> {
            try {
                JSONArray res = new JSONArray(response);
                if (res.length() > 0) {
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject jsonObjects = res.getJSONObject(i);
                        JSONObject jsonObject = jsonObjects.getJSONObject("sale_object");
                        db.addOrders(jsonObject.getString("sale_no"),jsonObject.getInt("id"), jsonObject.getInt("order_status"), jsonObject.toString(), jsonObject.getString("sale_date"));
                    }
                    running_download.setText(String.valueOf(page + 1));
                    downloadOrder(page + 1);
                } else {
                    visibleLoading();
                    sessionManager.setLastDownload("");
                    Toast.makeText(this, "Success download", Toast.LENGTH_SHORT).show();
                    backgroundDownload.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
                backgroundDownload.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }, error -> {
            visibleLoading();
            saveLastDownload(String.valueOf(page));
            Toast.makeText(this, "Error download", Toast.LENGTH_SHORT).show();
            backgroundDownload.setVisibility(View.GONE);
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    private void downloadPPN(){
        disabledLoading();
        backgroundDownload.setVisibility(View.VISIBLE);
        max_running_download.setText("0");
        running_download.setText("0");
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_PPN, response -> {
            try {
                truncatePaymentMethod();
                JSONObject res = new JSONObject(response);
                max_running_download.setText(String.valueOf(res.length()));
                db.addPPN(res.getInt("percentage"));
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Success Download", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                backgroundDownload.setVisibility(View.GONE);
                Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            visibleLoading();
        }, error -> {
            visibleLoading();
            backgroundDownload.setVisibility(View.GONE);
            Toast.makeText(this, "Error Download", Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        maxRetries,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                Uri selectedFileUri = data.getData();
                showLoading();
                if (perfectType.equals("customers")) {
                    db.truncateCustomers();
                    db.importDatabase(this, "customers", selectedFileUri, loadingDialog);
                } else if (perfectType.equals("customer_info")) {
                    db.truncateCustomerInfo();
                    db.importDatabase(this, "customer_info", selectedFileUri, loadingDialog);
                } else {
                    db.truncateOrders();
                    db.importDatabase(this, "orders", selectedFileUri, loadingDialog);
                }
                break;
        }
    }

    public void exportData(String type) {
        showLoading();
        if (type.equals("customer")) {
            db.exportToDownload(this, "customers", loadingDialog);
            db.exportToDownload(this, "customer_info", loadingDialog);
        } else {
            db.exportToDownload(this, type, loadingDialog);
        }
    }

    public void importData(String type){
        perfectType = type;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    private void disabledLoading(){
        menuAdmin.setVisibility(View.GONE);
    }

    private void visibleLoading(){
        menuAdmin.setVisibility(View.VISIBLE);
    }

    private void truncateCategories(){
        db.truncateCategories();
    }
    private void truncateSubCategories(){
        db.truncateSubCategories();
    }
    private void truncateLabels(){
        db.truncateLabels();
    }
    private void truncatePaymentMethod(){
        db.truncatePaymentMethod();
    }
    private void truncateTables(){
        db.truncateTables();
    }
    private void truncateCustomer(){
        db.truncateCustomers();
    }
    private void truncateMenus(){
        db.truncateMenus();
    }

    private void truncateEmployee(){
        db.truncateEmployee();
    }
    private void truncateWasher(){
        db.truncateWasher();
    }
    private void truncateOrder(){
        db.truncateOrders();
    }

    private void resetDownload(){
        sessionManager.setLastDownload("0");
    }

    private void saveLastDownload(String page){
        sessionManager.setLastDownload(page);
    }

    private int continueDownload(){
        String lasted = sessionManager.getLastDownload();
        int values = 0;
        if(!lasted.isEmpty()){
            values =  Integer.parseInt(lasted);
        }
        return values;
    }

    public void cleanDataSalesToday(){
        db.deleteSalesToday();
        Toast.makeText(this, "Berhasil Bersihkan Data", Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        loadingDialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        loadingDialog.show();
    }

    public void hideLoading() {
        loadingDialog.dismiss();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
