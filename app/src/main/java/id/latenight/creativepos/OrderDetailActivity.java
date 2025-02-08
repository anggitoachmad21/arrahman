package id.latenight.creativepos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.latenight.creativepos.adapter.PaymentMethod;
import id.latenight.creativepos.adapter.PaymentMethodAdapter;
import id.latenight.creativepos.util.DatabaseHandler;
import id.latenight.creativepos.util.MyApplication;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;
import static id.latenight.creativepos.util.MyApplication.RC_ENABLE_BLUETOOTH;

public class OrderDetailActivity extends AppCompatActivity implements PaymentMethodAdapter.ImageAdapterListener, View.OnClickListener {

    private List<PaymentMethod> paymentMethodList;
    private ArrayList<String> logisticList,washerList,employeeList;
    private PaymentMethodAdapter paymentMethodAdapter;
    private TableLayout tableLayout;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private String order_id, orderType;
    private String order_type = "1";
    private NumberFormat formatRupiah;
    private TextView totalOrder, type, customer, customer_name, table, waiter, billNo, subtotal, subtotalAfterDiscount, discount, totalDiscount, serviceCharge, totalPayable, ppn, notes, ppnPercent, valueLogistic, shipping, valueWasher1, valueWasher2, valueWasher3;
    private int params_sale_id, params_subtotal, params_subtotal_after_discount, params_given_amount, params_transfer_amount, params_payment_method_type, params_change_amount, params_total_payable, params_vvip;
    private EditText transferAmount, givenAmount, changeAmount;
    private SessionManager sessionManager;
    private boolean is_piutang = false;

    private ProgressDialog loadingDialog;
    private RelativeLayout lytAlert;
    private TextView txtAlert;
    private Animation slideUp,slideDown;
    private LinearLayout lytPayment, lyt_notes, lyt_transfer, lyt_employee, lytWorker, lytvvip;
    private Spinner spinnerLogistic, spinnerWasher1, spinnerWasher2, spinnerWasher3, spinnerEmployee;

    private Button washingProcess, washingFinish, salonProcess, jokProcess, finishOrder;
    private DatabaseHandler db;

    private String member_id = "", balance = "", active_date = "", expired_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();

        sessionManager = new SessionManager(this);
        //Log.e("Printer", printerAddress);

        logisticList = new ArrayList<>();
        washerList = new ArrayList<>();
        employeeList = new ArrayList<>();

        formatRupiah = NumberFormat.getInstance();

        tableLayout = findViewById(R.id.tableLayout);
        lytPayment = findViewById(R.id.lyt_payment);
        lytvvip = findViewById(R.id.lyt_vvip);
        lytWorker = findViewById(R.id.lyt_worker);
        RelativeLayout lytPrinterNotification = findViewById(R.id.lyt_printer_notification);
        TextView notifPrinter = findViewById(R.id.notif_printer);
        Button btnPairPrinter = findViewById(R.id.btn_pair_printer);
        washingProcess = findViewById(R.id.washing_process);
        washingFinish = findViewById(R.id.washing_finish);
        salonProcess = findViewById(R.id.salon_process);
        jokProcess = findViewById(R.id.jok_process);
        spinnerLogistic = findViewById(R.id.logistic);
        spinnerWasher1 = findViewById(R.id.washer1);
        spinnerWasher2 = findViewById(R.id.washer2);
        spinnerWasher3 = findViewById(R.id.washer3);
        spinnerEmployee = findViewById(R.id.spinner_employee);
        finishOrder = findViewById(R.id.finish_order);

        washingProcess.setOnClickListener(this);
        washingFinish.setOnClickListener(this);
        salonProcess.setOnClickListener(this);
        jokProcess.setOnClickListener(this);
        finishOrder.setOnClickListener(this);

        RecyclerView recyclerPaymentMethod = findViewById(R.id.recycler_payment_method);
        recyclerPaymentMethod.setLayoutManager(new GridLayoutManager(this, 3));
        paymentMethodList = new ArrayList<>();
        paymentMethodAdapter = new PaymentMethodAdapter(paymentMethodList, getApplicationContext(), this);
        recyclerPaymentMethod.setAdapter(paymentMethodAdapter);

        valueLogistic = findViewById(R.id.value_logistic);
        valueWasher1 = findViewById(R.id.value_washer1);
        valueWasher2 = findViewById(R.id.value_washer2);
        valueWasher3 = findViewById(R.id.value_washer3);
        shipping = findViewById(R.id.shipping);
        billNo = findViewById(R.id.bill_no);
        type = findViewById(R.id.type);
        customer = findViewById(R.id.customer);
        customer_name = findViewById(R.id.customer_name);
        table = findViewById(R.id.table);
        waiter = findViewById(R.id.waiter);
        subtotal = findViewById(R.id.subtotal);
        subtotalAfterDiscount = findViewById(R.id.subtotal_after_discount);
        ppn = findViewById(R.id.ppn);
        ppnPercent = findViewById(R.id.ppn_percent);
        discount = findViewById(R.id.discount);
        totalDiscount = findViewById(R.id.total_discount);
        serviceCharge = findViewById(R.id.service_charge);
        totalPayable = findViewById(R.id.total_payable);
        totalOrder = findViewById(R.id.total_order);
        transferAmount = findViewById(R.id.transfer_amount);
        givenAmount = findViewById(R.id.given_amount);
        changeAmount = findViewById(R.id.change_amount);
        lytAlert = findViewById(R.id.lyt_alert);
        txtAlert = findViewById(R.id.txt_alert);
        lyt_notes = findViewById(R.id.lyt_notes);
        lyt_transfer = findViewById(R.id.lyt_transfer);
        lyt_employee = findViewById(R.id.lyt_employee);
        notes = findViewById(R.id.notes);
        ImageButton btnCloseAlert = findViewById(R.id.btn_close_alert);
        btnCloseAlert.setOnClickListener(this);
        // slide-up animation
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        Button print_bill = findViewById(R.id.print_bill);

        print_bill.setOnClickListener(this);

        db = new DatabaseHandler(this);

        if (extras != null) {
            order_id = extras.getString("order_id");
            //Log.e("DATA:", db.getSales(Integer.valueOf(order_id)).getData());
            getOderCartList(order_id);
        }
        getPaymentMethods();

        int isConnected = MyApplication.getApplication().isConnected();
        if(!sessionManager.getPrinter().isEmpty()) {
            if(isConnected != 3) {
                notifPrinter.setText(getResources().getString(R.string.unable_connect_with_printer));
                btnPairPrinter.setVisibility(View.VISIBLE);
            } else {
                lytPrinterNotification.setVisibility(View.GONE);
            }
        }
    }

    public void scanPrinter(View view) {
        startActivity(new Intent(this, AdminActivity.class));
    }

    @SuppressLint("SetTextI18n")
    private void getOderCartList(String order_id) {
        Log.e("URL_", URI.API_DETAIL_ORDER+order_id);
        stringRequest = new StringRequest(URI.API_DETAIL_ORDER+order_id, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                //Log.e("RESPONSE", jsonObject.toString());

                order_type = jsonObject.getString("order_type");

                switch (order_type) {
                    case "1":
                        orderType = getApplicationContext().getResources().getString(R.string.dine_in);
                        break;
                    case "2":
                        orderType = getApplicationContext().getResources().getString(R.string.take_away);
                        break;
                    case "3":
                        orderType = getApplicationContext().getResources().getString(R.string.delivery);
                        break;
                    default:
                        spinnerLogistic.setSelection(logisticList.indexOf("Diambil"));
                        orderType = getApplicationContext().getResources().getString(R.string.taken);
                        break;
                }

                String order_status = jsonObject.getString("order_status");
                String payment_status = jsonObject.getString("payment_status");
                String washer_name_1 = jsonObject.getString("washer_name_1");
                String washer_name_2 = jsonObject.getString("washer_name_2");
                String washer_name_3 = jsonObject.getString("washer_name_3");
                boolean is_salon = jsonObject.getBoolean("is_salon");

                TextView title_worker1 = findViewById(R.id.title_worker1);
                TextView title_worker2 = findViewById(R.id.title_worker2);
                TextView title_worker3 = findViewById(R.id.title_worker3);
                TextView value_worker1 = findViewById(R.id.value_worker1);
                TextView value_worker2 = findViewById(R.id.value_worker2);
                TextView value_worker3 = findViewById(R.id.value_worker3);
                String queue_no = jsonObject.getString("queue_no");
                waiter.setText(queue_no);

                if(payment_status.equals("1")) {
                    lytPayment.setVisibility(View.GONE);
                }
                int log_status = jsonObject.getInt("status");

                params_vvip = jsonObject.getInt("vvip_order");
                if(params_vvip == 1){
                    if(log_status == 5) {
                        lytvvip.setVisibility(View.VISIBLE);
                    }
                    lytvvip.setVisibility(View.VISIBLE);
                }

                if(order_status.equals("5")) {
                    getWashers("washer");
                    lytPayment.setVisibility(View.GONE);
                    title_worker3.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                }
                if(order_status.equals("6")) {
                    valueWasher1.setVisibility(View.VISIBLE);
                    valueWasher2.setVisibility(View.VISIBLE);
                    valueWasher1.setText(washer_name_1);
                    valueWasher2.setText(washer_name_2);
                    spinnerWasher1.setVisibility(View.GONE);
                    spinnerWasher2.setVisibility(View.GONE);
                    washingProcess.setVisibility(View.GONE);
                    title_worker3.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                    valueWasher3.setVisibility(View.GONE);
                    washingFinish.setVisibility(View.VISIBLE);
                    lytPayment.setVisibility(View.GONE);
                }
                if(order_status.equals("7")) {
                    getWashers("worker");
                    title_worker1.setText(getResources().getString(R.string.select_worker1));
                    title_worker2.setText(getResources().getString(R.string.select_worker2));
                    title_worker3.setText(getResources().getString(R.string.select_worker3));
                    salonProcess.setVisibility(View.VISIBLE);
                    lytPayment.setVisibility(View.GONE);
                    washingProcess.setVisibility(View.GONE);
                    waiter.setText(jsonObject.getString("queue_no_salon"));
                }
                if(order_status.equals("8")) {
                    valueWasher1.setVisibility(View.VISIBLE);
                    valueWasher2.setVisibility(View.VISIBLE);
                    valueWasher1.setText(washer_name_1);
                    valueWasher2.setText(washer_name_2);
                    spinnerWasher1.setVisibility(View.GONE);
                    spinnerWasher2.setVisibility(View.GONE);
                    washingProcess.setVisibility(View.GONE);
                    title_worker3.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                    valueWasher3.setVisibility(View.GONE);
                    washingFinish.setVisibility(View.VISIBLE);
                    lytPayment.setVisibility(View.GONE);
                }
                if(order_status.equals("9")) {
                    getWashers("worker");
                    valueWasher1.setVisibility(View.VISIBLE);
                    valueWasher2.setVisibility(View.VISIBLE);
                    valueWasher1.setText(washer_name_1);
                    valueWasher2.setText(washer_name_2);
                    spinnerWasher1.setVisibility(View.GONE);
                    spinnerWasher2.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                    title_worker3.setVisibility(View.GONE);
                    washingProcess.setVisibility(View.GONE);
                    valueWasher3.setVisibility(View.GONE);
                    jokProcess.setVisibility(View.VISIBLE);
                    lytPayment.setVisibility(View.GONE);
                    waiter.setText(jsonObject.getString("queue_no_leather"));
                }
                if(order_status.equals("3") || order_status.equals("4")) {
                    lytPayment.setVisibility(View.GONE);
                    spinnerLogistic.setVisibility(View.GONE);
                    valueLogistic.setVisibility(View.VISIBLE);
                    valueWasher1.setVisibility(View.VISIBLE);
                    valueWasher2.setVisibility(View.VISIBLE);
                    valueWasher3.setVisibility(View.VISIBLE);
                    valueWasher1.setText(washer_name_1);
                    valueWasher2.setText(washer_name_2);
                    valueWasher3.setText(washer_name_3);
                    washingProcess.setVisibility(View.GONE);
                    spinnerWasher1.setVisibility(View.GONE);
                    spinnerWasher2.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                    if(is_salon) {
                        lytWorker.setVisibility(View.VISIBLE);
                        value_worker1.setText(jsonObject.getString("worker_name_1"));
                        value_worker2.setText(jsonObject.getString("worker_name_2"));
                        value_worker3.setText(jsonObject.getString("worker_name_3"));
                    }
                }
                if (order_status.equals("1")) {
                    valueWasher1.setVisibility(View.VISIBLE);
                    valueWasher2.setVisibility(View.VISIBLE);
                    valueWasher1.setText(washer_name_1);
                    valueWasher2.setText(washer_name_2);
                    spinnerWasher1.setVisibility(View.GONE);
                    spinnerWasher2.setVisibility(View.GONE);
                    washingProcess.setVisibility(View.GONE);
                    title_worker3.setVisibility(View.GONE);
                    spinnerWasher3.setVisibility(View.GONE);
                    valueWasher3.setVisibility(View.GONE);
                    if(is_salon) {
                        lytWorker.setVisibility(View.VISIBLE);
                        value_worker1.setText(jsonObject.getString("worker_name_1"));
                        value_worker2.setText(jsonObject.getString("worker_name_2"));
                        value_worker3.setText(jsonObject.getString("worker_name_3"));
                    }
                }
                if(queue_no.equals("0")) {
                    LinearLayout lytWorkerWasher = findViewById(R.id.lyt_worker_washer);
                    Button printQueue = findViewById(R.id.print_queue);
                    lytWorkerWasher.setVisibility(View.GONE);
                    printQueue.setVisibility(View.GONE);
                }

                if(jsonObject.getString("cust_notes").equals("Pembayaran Member")){
                    LinearLayout lytWorkerWasher = findViewById(R.id.lyt_worker_washer);
                    Button printQueue = findViewById(R.id.print_queue);
                    lytWorkerWasher.setVisibility(View.GONE);
                    printQueue.setVisibility(View.GONE);
                }

                params_sale_id = jsonObject.getInt("sale_no");
                params_subtotal = jsonObject.getInt("sub_total");
                int discount_value = Integer.parseInt(jsonObject.getString("sub_total_discount_value").replace("%", ""));
                int subtotal_without_discount_shipping = params_subtotal;
                int subtotal_with_discount_shipping = subtotal_without_discount_shipping + jsonObject.getInt("logistic") - discount_value;

                params_subtotal_after_discount = subtotal_with_discount_shipping;

                billNo.setText(getResources().getString(R.string.bill_no) + " " + jsonObject.getString("sale_no"));
                type.setText(orderType);
                customer.setText(jsonObject.getString("customer_name"));
                if(!jsonObject.getString("cust_notes").isEmpty()) {
                    customer_name.setText(jsonObject.getString("cust_notes"));
                } else {
                    customer_name.setVisibility(View.GONE);
                }
                table.setText(jsonObject.getString("sale_no"));
                customer.setText(jsonObject.getString("customer_name"));
                totalOrder.setText(jsonObject.getString("total_items") + " Items");
                valueLogistic.setText(jsonObject.getString("logistic_name"));
                //Log.e("ongkir", String.valueOf(jsonObject.getInt("logistic")));
                shipping.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(jsonObject.getInt("logistic")).replace(',', '.'));
                subtotal.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(subtotal_without_discount_shipping).replace(',', '.'));
                subtotalAfterDiscount.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(subtotal_with_discount_shipping).replace(',', '.'));
                //subtotal.setText(getResources().getString(R.string.currency) +" "+ formatRupiah.format(jsonObject.getInt("sub_total")).replace(',', '.'));
                //subtotalAfterDiscount.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(jsonObject.getInt("sub_total_with_discount")).replace(',', '.'));
                discount.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(discount_value).replace(',', '.'));
                totalDiscount.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(jsonObject.getInt("logistic")).replace(',', '.'));
                //totalDiscount.setText(getResources().getString(R.string.currency) +" "+ formatRupiah.format(jsonObject.getInt("total_discount_amount")).replace(',', '.'));
                serviceCharge.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(jsonObject.getInt("delivery_charge")).replace(',', '.'));
                totalPayable.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(jsonObject.getInt("total_payable")).replace(',', '.'));

                givenAmount.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().isEmpty()) {
                            int value_change_amount;
                            try {
                                value_change_amount = Integer.parseInt(s.toString()) - jsonObject.getInt("total_payable");
                                params_change_amount = value_change_amount;
                                changeAmount.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(value_change_amount).replace(',', '.'));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                String valver = "";
                String duration = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    @SuppressLint("InflateParams") View tableRow = LayoutInflater.from(getApplicationContext()).inflate(R.layout.table_order, null, false);
                    TextView history_display_no = tableRow.findViewById(R.id.menu_name);
                    TextView history_display_date = tableRow.findViewById(R.id.menu_price);
                    TextView history_display_orderid = tableRow.findViewById(R.id.menu_qty);
                    TextView history_display_quantity = tableRow.findViewById(R.id.menu_total);

                    String menu_name = jo.getString("menu_name");
                    int menu_price = jo.getInt("menu_unit_price");
                    int menu_qty = jo.getInt("qty");
                    int menu_total = menu_price * menu_qty;

                    valver =  jo.getString("menu_category");
                    duration = jo.getString("package_duration");

                    String title_new = menu_name.toLowerCase();
                    String capitalize = capitalizeText(title_new);
                    history_display_no.setText(capitalize);
                    history_display_date.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(menu_price).replace(',', '.'));
                    history_display_orderid.setText(String.valueOf(menu_qty));
                    history_display_quantity.setText(getResources().getString(R.string.currency) + " " + formatRupiah.format(menu_total).replace(',', '.'));
                    tableLayout.addView(tableRow);
                }
                if(jsonObject.getString("cust_notes").equals("Pembayaran Member")) {
                    LinearLayout lyt_member = findViewById(R.id.lyt_member);
                    lyt_member.setVisibility(View.VISIBLE);
                    getDateMember(jsonObject.getString("sale_date"), duration);
                    jsonObject.getString("customer_name");
                    getMemberByCustomer(jsonObject.getString("customer_name"));
                    Button update_order = findViewById(R.id.update_order);
                    update_order.setVisibility(View.GONE);
                }

                if (order_status.equals("1")) {
                    getPPN();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        , error -> {
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getDateMember(String sale_date, String duration)
    {
        TextView start_sale_date = findViewById(R.id.start_date_member);
        start_sale_date.setText("Date Loading...");
        TextView end_sale_date = findViewById(R.id.end_date_member);
        end_sale_date.setText("Date Loading...");
        StringRequest stringRequest = new StringRequest(URI.GENERATE_DATE + "?sale_date="+sale_date+"&duration="+duration, response -> {
            Log.e("RESPONSE DATE", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                start_sale_date.setText(jsonObject.getString("active_date"));
                end_sale_date.setText(jsonObject.getString("expired_date"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getMemberByCustomer(String name)
    {
        StringRequest stringRequest = new StringRequest(URI.MEMBER_BY_CUSTOMER_NAME + "?customer_name="+name, response -> {
            Log.e("RESPONSE DATE", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                member_id = jsonObject.getString("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    private void getLogistics() {
//        //Log.e("URL_", URI.API_LOGISTIC+"/"+sessionManager.getId());
//        JsonArrayRequest request = new JsonArrayRequest(URI.API_LOGISTIC+"/"+sessionManager.getId(), response -> {
//            JSONObject jsonObject;
//            //Log.e("Response", response.toString());
//            for (int i = 0; i < response.length(); i++) {
//                try {
//                    jsonObject = response.getJSONObject(i);
//                    logisticList.add(jsonObject.getString("company_name"));
//                    String company_name = jsonObject.getString("company_name");
//
//                    if (order_type.equals("4")) {
//                        if (!company_name.toLowerCase().contains("diambil")) {
//                            logisticList.remove(company_name);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            spinnerLogistic.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.logistic_item, logisticList));
//            //Log.e("Payment ", String.valueOf(paymentMethodList.get(0).getName()));
//        }, error -> {
//        });
//        requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(request);
//    }

    private void getWashers(String type) {
        Log.e("URL_", URI.API_WASHERS+sessionManager.getId()+"/"+type);
        JsonArrayRequest request = new JsonArrayRequest(URI.API_WASHERS+sessionManager.getId()+"/"+type, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    washerList.add(jsonObject.getString("full_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            spinnerWasher1.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.logistic_item, washerList));
            spinnerWasher2.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.logistic_item, washerList));
            spinnerWasher3.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.logistic_item, washerList));
            //Log.e("Payment ", String.valueOf(paymentMethodList.get(0).getName()));
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getEmployees() {
        Log.e("URL_", URI.API_EMPLOYEES);
        JsonArrayRequest request = new JsonArrayRequest(URI.API_EMPLOYEES, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    employeeList.add(jsonObject.getString("full_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            spinnerEmployee.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.logistic_item, employeeList));
            //Log.e("Payment ", String.valueOf(paymentMethodList.get(0).getName()));
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getPaymentMethods() {
        List <id.latenight.creativepos.adapter.sampler.PaymentMethod> paymentMethodList1 = db.getAllPayment();
        for (int i = 0; i < paymentMethodList1.size(); i++) {
            PaymentMethod listData = new PaymentMethod(paymentMethodList1.get(i).getPayment_method_id(), paymentMethodList1.get(i).getName(), paymentMethodList1.get(i).getDescription());
            paymentMethodList.add(listData);
        }
        paymentMethodAdapter.notifyDataSetChanged();
        if(!paymentMethodList.get(0).getName().equals("TUNAI")) {
            lyt_notes.setVisibility(View.VISIBLE);
            lyt_transfer.setVisibility(View.VISIBLE);
            givenAmount.setText(String.valueOf(params_total_payable));
            changeAmount.setText("Rp. 0");
        } else {
            lyt_transfer.setVisibility(View.GONE);
            lyt_notes.setVisibility(View.VISIBLE);
        }
        params_payment_method_type = paymentMethodList.get(0).getId();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onImageSelected(PaymentMethod item) {
        if(!item.getName().equals("TUNAI") && !item.getName().equals("PIUTANG")) {
            lyt_notes.setVisibility(View.VISIBLE);
            lyt_transfer.setVisibility(View.VISIBLE);
            givenAmount.setText(String.valueOf(params_total_payable));
            changeAmount.setText("Rp. 0");
            lyt_employee.setVisibility(View.GONE);
            is_piutang = false;
        } else if(item.getName().equals("PIUTANG")) {
            givenAmount.setText(String.valueOf(params_total_payable));
            changeAmount.setText("Rp. 0");
            lyt_notes.setVisibility(View.GONE);
            lyt_transfer.setVisibility(View.GONE);
            lyt_employee.setVisibility(View.VISIBLE);
            getEmployees();
            is_piutang = true;
        } else {
            givenAmount.setText("");
            changeAmount.setText("");
            lyt_transfer.setVisibility(View.GONE);
            lyt_notes.setVisibility(View.VISIBLE);
            lyt_employee.setVisibility(View.GONE);
            is_piutang = false;
        }
        params_payment_method_type = item.getId();
    }

    @SuppressLint("SetTextI18n")
    public void duaRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 2000;
        givenAmount.setText(String.valueOf(uang));
    }
    @SuppressLint("SetTextI18n")
    public void limaRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 5000;
        givenAmount.setText(String.valueOf(uang));
    }
    @SuppressLint("SetTextI18n")
    public void sepuluhRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 10000;
        givenAmount.setText(String.valueOf(uang));
    }
    @SuppressLint("SetTextI18n")
    public void duapuluhRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 20000;
        givenAmount.setText(String.valueOf(uang));
    }
    @SuppressLint("SetTextI18n")
    public void limapuluhRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 50000;
        givenAmount.setText(String.valueOf(uang));
    }
    @SuppressLint("SetTextI18n")
    public void seratusRibu(View view) {
        if(givenAmount.getText().toString().isEmpty()){
            givenAmount.setText("0");
        }
        int uang = Integer.parseInt(givenAmount.getText().toString());
        uang = uang + 100000;
        givenAmount.setText(String.valueOf(uang));
    }

    public void printText(String header_invoice, String invoice) {
        if(sessionManager.getEnablePrinter().equals("on")) {
            MyApplication.getApplication().sendMessage(header_invoice, invoice);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
        }
    }

    public void updateOrder(View view) {
        //Log.e("test", params_sale_id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("update_order", true);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
        finish();
    }

    public void splitBill(View view) {
        //Log.e("test", params_sale_id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("split_bill", true);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
        finish();
    }

    private void finishOrder() {
        int value_given_amount = params_given_amount + params_transfer_amount;
        if(params_change_amount >= 0) {
            if(value_given_amount >= params_total_payable) {
                showLoading();
                StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_FINISH_ORDER,
                        response -> {
                            Log.e("RESPONSE ", response);
                            try {
                                hideLoading();
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if(success) {
                                    //Log.e("Enable", sessionManager.getEnablePrinter());
                                    if(sessionManager.getEnablePrinter().equals("on")) {
                                        printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                                    }
                                    if(!member_id.isEmpty())
                                    {
                                        updateMember(jsonObject.getString("sales_information"));
                                        return;
                                    }
                                    db.updateNote(Integer.parseInt(order_id), jsonObject.getString("sales_information"));
                                    //db.deleteSales(Integer.parseInt(order_id));
                                    Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    showError(jsonObject.getString("message"));
                                }

                            } catch (JSONException e) {
                                showError("Terjadi kesalahan server");
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            hideLoading();

                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.data != null) {
                                String jsonError = new String(networkResponse.data);
                                // Print Error!
                                Log.e("Error", jsonError);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<>();
                        // the POST parameters:
                        // String logistic_value = spinnerLogistic.getSelectedItem().toString();
                        // params.put("logistic", logistic_value);
                        if(is_piutang) {
                            String employee = spinnerEmployee.getSelectedItem().toString();
                            params.put("employee", employee);
                        }
                        params.put("user_id", sessionManager.getId());
                        params.put("sale_id", String.valueOf(params_sale_id));
                        params.put("close_order", "true");
                        params.put("paid_amount", String.valueOf(params_total_payable));
                        params.put("due_amount", "0");
                        params.put("paid_cash", String.valueOf(params_given_amount));
                        params.put("paid_non_cash", String.valueOf(transferAmount.getText()));
                        params.put("given_amount", String.valueOf(params_given_amount));
                        params.put("change_amount", String.valueOf(params_change_amount));
                        params.put("payment_method_type", String.valueOf(params_payment_method_type));
                        params.put("notes", String.valueOf(notes.getText()));
                        params.put("vvip_order", String.valueOf(params_vvip));
                        return params;
                    }
                };
                postRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getApplicationContext()).add(postRequest);
            } else {
                showError(getResources().getString(R.string.payment_lacking));
            }
        } else {
            showError(getResources().getString(R.string.payment_lacking));
        }
    }

    private void finishOrderVVIP() {
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_FINISH_ORDER,
                response -> {
                    Log.e("RESPONSE ", response);
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            //Log.e("Enable", sessionManager.getEnablePrinter());
                            if(sessionManager.getEnablePrinter().equals("on")) {
                                printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                            }
                            db.updateNote(Integer.parseInt(order_id), jsonObject.getString("sales_information"));
                            //db.deleteSales(Integer.parseInt(order_id));
                            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showError(jsonObject.getString("message"));
                        }

                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideLoading();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("Error", jsonError);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getId());
                params.put("sale_id", String.valueOf(params_sale_id));
                params.put("paid_amount", String.valueOf(params_total_payable));
                params.put("payment_method_type", "7");
                params.put("vvip_order", "1");
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void invoiceDirect(View view) {
        //Log.e("sale_id", String.valueOf(params_sale_id));
        //Log.e("close_order", "true");
        //Log.e("paid_amount", String.valueOf(params_total_payable));
        //Log.e("due_amount", "0");
        //Log.e("given_amount", String.valueOf(params_given_amount));
        //Log.e("change_amount", String.valueOf(params_change_amount));
        //Log.e("payment_method_type", String.valueOf(params_payment_method_type));

        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_INVOICE,
                response -> {
                    //Log.e("RESPONSE ", response);
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            if(sessionManager.getEnablePrinter().equals("on")) {
                                printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                            }
                        } else {
                            showError(jsonObject.getString("message"));
                        }

                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideLoading();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        //Print Error!
                        Log.e("Error", jsonError);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("user_id", sessionManager.getId());
                params.put("sale_id", String.valueOf(params_sale_id));
                params.put("close_order", "true");
                params.put("paid_amount", String.valueOf(params_total_payable));
                params.put("due_amount", "0");
                params.put("paid_cash", String.valueOf(params_given_amount));
                params.put("paid_non_cash", String.valueOf(transferAmount.getText()));
                params.put("given_amount", String.valueOf(params_given_amount));
                params.put("change_amount", String.valueOf(params_change_amount));
                params.put("payment_method_type", String.valueOf(params_payment_method_type));
                params.put("notes", String.valueOf(notes.getText()));
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void printQueue(View view) {
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_QUEUE,
                response -> {
                    //Log.e("RESPONSE ", response);
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            if(sessionManager.getEnablePrinter().equals("on")) {
                                printText(jsonObject.getString("header_invoice"), jsonObject.getString("invoice"));
                            }
                        } else {
                            showError(jsonObject.getString("message"));
                        }

                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideLoading();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        //Print Error!
                        Log.e("Error", jsonError);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("user_id", sessionManager.getId());
                params.put("sale_id", String.valueOf(params_sale_id));
                params.put("close_order", "true");
                params.put("paid_amount", String.valueOf(params_total_payable));
                params.put("due_amount", "0");
                params.put("paid_cash", String.valueOf(params_given_amount));
                params.put("paid_non_cash", String.valueOf(transferAmount.getText()));
                params.put("given_amount", String.valueOf(params_given_amount));
                params.put("change_amount", String.valueOf(params_change_amount));
                params.put("payment_method_type", String.valueOf(params_payment_method_type));
                params.put("notes", String.valueOf(notes.getText()));
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void washingSalonProcess(int type_service) {
        String API_WASHING_SALON = URI.API_WASHING_PROCESS;
        if(type_service == 2) {
            API_WASHING_SALON = URI.API_SALON_PROCESS;
        }if(type_service == 3) {
            API_WASHING_SALON = URI.API_JOK_PROCESS;
        }
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, API_WASHING_SALON,
                response -> {
                    Log.e("RESPONSE ", response);
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            showSuccess(jsonObject.getString("message"));
                            finish();
                            startActivity(getIntent());
                        } else {
                            showError(jsonObject.getString("message"));
                        }

                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideLoading();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        //Print Error!
                        Log.e("Error", jsonError);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                String washer1_value = spinnerWasher1.getSelectedItem().toString();
                String washer2_value = spinnerWasher2.getSelectedItem().toString();
                String washer3_value = spinnerWasher3.getSelectedItem().toString();

                params.put("user_id", sessionManager.getId());
                params.put("sale_id", String.valueOf(params_sale_id));
                params.put("washer_id_1", washer1_value);
                params.put("washer_id_2", washer2_value);
                params.put("washer_id_3", washer3_value);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void washingFinish() {
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_WASHING_FINISH,
                response -> {
                    Log.e("RESPONSE ", response);
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            showSuccess(jsonObject.getString("message"));
                            finish();
                            startActivity(getIntent());
                        } else {
                            showError(jsonObject.getString("message"));
                        }

                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideLoading();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        //Print Error!
                        Log.e("Error", jsonError);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("user_id", sessionManager.getId());
                params.put("sale_id", String.valueOf(params_sale_id));
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    @SuppressLint("SetTextI18n")
    private void getPPN() {
        //Log.e("URL_", URI.API_PPN);
        stringRequest = new StringRequest(URI.API_PPN, response -> {

            float value_ppn = ((params_subtotal_after_discount * Float.parseFloat(response)) / 100);
            //Log.e("PPN", String.valueOf(value_ppn));
            int payable_after_ppn = (int) (params_subtotal_after_discount + value_ppn);


            String s_payable_after_ppn = formatRupiah.format(payable_after_ppn).replace(',', '.');
            String s_value_ppn = formatRupiah.format(value_ppn).replace(',', '.');

            givenAmount.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {}
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int value_change_amount;
                    if(!s.toString().isEmpty()) {
                        params_given_amount = Integer.parseInt(s.toString());
                        if(transferAmount.getText().toString().equals("0") || transferAmount.getText().toString().isEmpty()) {
                            value_change_amount = Integer.parseInt(s.toString()) - payable_after_ppn;
                        } else {
                            value_change_amount = Integer.parseInt(s.toString()) + Integer.parseInt(transferAmount.getText().toString()) - payable_after_ppn;
                        }
                    } else {
                        params_given_amount = 0;
                        if(transferAmount.getText().toString().trim().length() > 0) {
                            value_change_amount = Integer.parseInt(transferAmount.getText().toString()) - payable_after_ppn;
                        } else {
                            value_change_amount = 0;
                        }
                    }
                    changeAmount.setText(getResources().getString(R.string.currency) +" "+ formatRupiah.format(value_change_amount).replace(',', '.'));
                    params_change_amount = value_change_amount;
                }
            });

            transferAmount.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {}
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int value_change_amount;
                    if(!s.toString().isEmpty()) {
                        params_transfer_amount = Integer.parseInt(s.toString());
                        if(givenAmount.getText().toString().equals("0") || givenAmount.getText().toString().isEmpty()) {
                            value_change_amount = Integer.parseInt(s.toString()) - payable_after_ppn;
                        } else {
                            value_change_amount = Integer.parseInt(s.toString()) + Integer.parseInt(givenAmount.getText().toString()) - payable_after_ppn;
                        }
                    } else {
                        params_transfer_amount = 0;
                        if(givenAmount.getText().toString().trim().length() > 0) {
                            value_change_amount = Integer.parseInt(givenAmount.getText().toString()) - payable_after_ppn;
                        } else {
                            value_change_amount = 0;
                        }
                    }
                    changeAmount.setText(getResources().getString(R.string.currency) +" "+ formatRupiah.format(value_change_amount).replace(',', '.'));
                    params_change_amount = value_change_amount;
                }
            });

            params_total_payable = payable_after_ppn;
            ppn.setText(getResources().getString(R.string.currency) +" "+ s_value_ppn);
            ppnPercent.setText((int) value_ppn +"%");
            totalPayable.setText(getResources().getString(R.string.currency) +" "+ s_payable_after_ppn);
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
        //hideLoading();
    }

    private void showSuccess(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        lytAlert.setBackgroundResource(R.drawable.alert_success);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.colorAccent));
        new Handler().postDelayed(() -> {
            lytAlert.setVisibility(View.GONE);
            lytAlert.startAnimation(slideDown);
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.print_bill) {
            String checkGivenAmount = givenAmount.getText().toString();
            if(!checkGivenAmount.isEmpty()) {
                //Log.e("params_change_amount", String.valueOf(params_change_amount));
                if (Integer.parseInt(checkGivenAmount) >= 0 && params_change_amount >= 0) {

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.finish_order)
                            .setMessage("Close this order?")
                            .setIcon(R.drawable.ic_notif)
                            .setPositiveButton(R.string.finish_order, (dialog, whichButton) -> finishOrder())
                            .setNegativeButton(R.string.cancel, null).show();
                }
            }
        }
        if (v.getId() == R.id.btn_close_alert) {
            lytAlert.setVisibility(View.GONE);
        }
        if (v.getId() == R.id.washing_process) {
            washingSalonProcess(1);
        }
        if (v.getId() == R.id.washing_finish) {
            washingFinish();
        }
        if (v.getId() == R.id.salon_process) {
            washingSalonProcess(2);
        }
        if (v.getId() == R.id.jok_process) {
            washingSalonProcess(3);
        }
        if (v.getId() == R.id.finish_order) {
            finishOrderVVIP();
        }
    }

    private void updateMember(String sales_information)
    {
        Log.e("RESPONSE MEMBER ID", member_id + " : " + order_id);
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.UPDATE_MEMBER,
                response -> {
                    hideLoading();
                    db.updateNote(Integer.parseInt(order_id), sales_information);
                    //db.deleteSales(Integer.parseInt(order_id));
                    Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    hideLoading();
                    error.printStackTrace();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("Error", jsonError);
                        Toast.makeText(this, "Gagal Simpan", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:

                params.put("member_id", member_id);
                params.put("sale_no", order_id);
                params.put("active_date", active_date);
                params.put("expired_date", expired_date);
                params.put("status", "1");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}