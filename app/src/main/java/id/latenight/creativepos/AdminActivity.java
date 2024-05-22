package id.latenight.creativepos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.latenight.creativepos.adapter.Customer;
import id.latenight.creativepos.util.DeviceActivity;
import id.latenight.creativepos.util.MyApplication;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

import static id.latenight.creativepos.util.MyApplication.RC_ENABLE_BLUETOOTH;
import static id.latenight.creativepos.util.MyApplication.RC_CONNECT_DEVICE;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ProgressDialog loadingDialog;
    private SessionManager sessionManager;
    private TextView pairDevice;

    private ProgressBar progressBar;
    private RelativeLayout lytAlert;
    private TextView txtAlert;
    private Button scanPrinter, btnReportStock;
    private ArrayList<String> customerList;

    private Animation slideUp, slideDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);

        String printerAddress = sessionManager.getPrinter();
        Log.e("Printer", printerAddress);
        Log.e("Enabler", sessionManager.getEnablePrinter());

        Button deleteSales = findViewById(R.id.delete_sales);
        pairDevice = findViewById(R.id.pair_device);

        scanPrinter = findViewById(R.id.scan_printer);
        scanPrinter.setOnClickListener(this);

        customerList = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        lytAlert = findViewById(R.id.lyt_alert);
        txtAlert = findViewById(R.id.txt_alert);

        // slide-up animation
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        int isConnected = MyApplication.getApplication().isConnected();
        if(!sessionManager.getPrinter().isEmpty()) {
            if(isConnected != 3) {
                MyApplication.getApplication().setupBluetoothConnection(sessionManager.getPrinter());
                Log.e("Check Connection", String.valueOf(isConnected));
            } else {
                pairDevice.setText(getResources().getString(R.string.connected_with_printer));
            }
        }

        if(mBluetoothAdapter == null) {
            pairDevice.setText(getResources().getString(R.string.turn_on_bluetooth));
            sessionManager.setEnablePrinter("off");
        }

        Button closeCashier = findViewById(R.id.close_cashier);
        closeCashier.setOnClickListener(v -> closeCashier());

        Button testPrint = findViewById(R.id.test_print);
        testPrint.setOnClickListener(v -> {
            String body = "Printer Ready";
            MyApplication.getApplication().testPrint(body);
        });

        Button downloadData = findViewById(R.id.download_data);
        downloadData.setOnClickListener(v -> {
            downloadDataCustomers();
        });

        Switch enablePrint = findViewById(R.id.enable_print);

        if (sessionManager.getEnablePrinter().equals("on")) {
            enablePrint.setChecked(true);
        }

        enablePrint.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
                sessionManager.setEnablePrinter("on");
                Log.e("Enable", "ENABLE");
            }
            else {
                Log.e("Enable", "DISABLE");
                sessionManager.setEnablePrinter("off");
            }
        });

        //Log.e("Role", sessionManager.getRole());
        //if(!sessionManager.getRole().equals("Admin")) {
            //deleteSales.setVisibility(View.GONE);
        //}

        btnReportStock = findViewById(R.id.print_report_stock);
        Log.e("Outlet ID ", sessionManager.getOutlet());

        Button printReportTepung = findViewById(R.id.print_report_tepung);
        Button printReportKebuli = findViewById(R.id.print_report_kebuli);
        //if(sessionManager.getOutletGroup().equals("3")) {
            printReportTepung.setVisibility(View.GONE);
            printReportKebuli.setVisibility(View.GONE);
        //}
    }

    private void downloadDataCustomers() {
        showLoading();
        customerList.clear();
        JsonArrayRequest request = new JsonArrayRequest(URI.API_CUSTOMER+sessionManager.getId(), response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    customerList.add(jsonObject.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String array_customer = Arrays.toString(customerList.toArray());
            sessionManager.setCustomers(array_customer.replace("]","").replace("[","").replace(" ",""));
            Log.e("customers", sessionManager.getCustomers());
            hideLoading();
        }, error -> {
            hideLoading();
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void getDailyReportTunai(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_REPORT+sessionManager.getId()+"/tunai", response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportDebit(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_REPORT+sessionManager.getId()+"/debit", response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportStock(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_STOCK_REPORT+sessionManager.getOutlet(), response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportProduct(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_PRODUCT_REPORT+sessionManager.getOutlet(), response -> {
            Log.e("URL", URI.API_DAILY_PRODUCT_REPORT+sessionManager.getOutlet());
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportTepung(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_SALES_REPORT+sessionManager.getId()+"/1", response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportKebuli(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_SALES_REPORT+sessionManager.getId()+"/2", response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getDailyReportReceipable(View view) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.API_DAILY_RECEIPABLE_REPORT+sessionManager.getId(), response -> {
            Log.e("URL", response);
            if(sessionManager.getEnablePrinter().equals("on")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deleteSales(View view) {
        Intent intent = new Intent(this, DeleteSalesActivity.class);
        startActivity(intent);
    }

    public void closeCashier() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.BALANCE_CLOSE_REGISTER,
                response -> {
                    Log.e("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if(success.equals("true")) {
                            sessionManager.setOpenRegistration("0");
                            Intent intent = new Intent(getApplicationContext(), OpenRegisterActivity.class);
                            intent.putExtra("closed", "true");
                            startActivity(intent);
                            finish();
                        } else {
                            showError(getResources().getString(R.string.error_message));
                        }

                    } catch (JSONException e) {
                        showError(getResources().getString(R.string.error_message));
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.startAnimation(slideDown);
                    }
                },
                error -> {
                    error.printStackTrace();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.startAnimation(slideDown);

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("Error", jsonError);
                        showError("Anda tidak dapat menutup laci kasir");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:

                params.put("user_id", sessionManager.getId());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i("TAG", "onActivityResult: bluetooth aktif");
                    pairDevice.setText(getResources().getString(R.string.disconnected_with_printer));
                } else {
                    Log.i("TAG", "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                }
                break;
            case RC_CONNECT_DEVICE:
                Log.e("resultCode", String.valueOf(resultCode));
                Log.e("RESULT_OK", String.valueOf(RESULT_OK));
                if (resultCode == RESULT_OK) {
                    int isConnected = MyApplication.getApplication().isConnected();
                    if(isConnected != 3) {
                        Log.e("Check Connection", String.valueOf(isConnected));
                        String address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                        MyApplication.getApplication().setupBluetoothConnection(address);
                        pairDevice.setText(getResources().getString(R.string.connected_with_printer));
                    }
                }
                break;
        }
    }

    public void printText(String header_invoice, String invoice) {
        if(sessionManager.getEnablePrinter().equals("on")) {
            String header = header_invoice;
            String body = invoice;
            MyApplication.getApplication().dailyReport(header, body);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
        }
    }

    public void scanPrinter() {
        if(sessionManager.getEnablePrinter().equals("on")) {
            startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
        }
    }

    public void showLoading() {
        loadingDialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        loadingDialog.show();
    }
    public void hideLoading() {
        loadingDialog.dismiss();
    }
    
    public void showError(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.error));
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.startAnimation(slideDown);
    }

    public void hideAlert(View view) {
        lytAlert.setVisibility(View.INVISIBLE);
        lytAlert.startAnimation(slideDown);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_printer) {
            scanPrinter();
        }
    }
}
