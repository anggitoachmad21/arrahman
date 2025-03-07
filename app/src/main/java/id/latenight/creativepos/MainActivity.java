package id.latenight.creativepos;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;
import static id.latenight.creativepos.util.MyApplication.RC_ENABLE_BLUETOOTH;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.latenight.creativepos.adapter.Cart;
import id.latenight.creativepos.adapter.CartAdapter;
import id.latenight.creativepos.adapter.Customer;
import id.latenight.creativepos.adapter.Product;
import id.latenight.creativepos.adapter.ProductAdapter;
import id.latenight.creativepos.adapter.sampler.Categories;
import id.latenight.creativepos.adapter.sampler.CustomerInfo;
import id.latenight.creativepos.adapter.sampler.CustomerValues;
import id.latenight.creativepos.adapter.sampler.Labels;
import id.latenight.creativepos.adapter.sampler.Menus;
import id.latenight.creativepos.adapter.sampler.Order;
import id.latenight.creativepos.adapter.sampler.SubCategories;
import id.latenight.creativepos.adapter.sampler.Tables;
import id.latenight.creativepos.util.DatabaseHandler;
import id.latenight.creativepos.util.MyApplication;
import id.latenight.creativepos.util.SearchableSpinner;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class MainActivity extends AppCompatActivity implements ProductAdapter.ImageAdapterListener, CartAdapter.AdapterListener, View.OnClickListener {

    private List<Product> list_product;
    private List<Cart> list_cart;
    private List<Customer> list_customer;
    private ArrayList<String> customerList, tableList;
    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private RadioButton radioDinein, radioTakeaway, radioDelivery, radioTaken;
    private Button btnAddDiscount, btnUseDiscount, btnRemoveDiscount, btnUseDiscountSalon, btnRemoveDiscountSalon;
    private ImageButton btnAddCustomer;
    private Button btnUseShipping;
    private Button btnRemoveShipping;
    private ImageView btnBackTab;
    private ProgressBar progressBar;
    private Spinner spinnerTable;
    private SearchableSpinner spinnerCustomer;
    private ArrayAdapter<String> spinnerCustomerAdapter ;
    private String orderType = "1";
    private TextView totalCart;
    private RelativeLayout lytAlert;
    private TextView txtAlert;
    private Animation slideUp,slideDown;
    private ProgressDialog loadingDialog;
    private String order_id;
    private Boolean update_order = false;
    private Boolean split_bill = false;
    private Boolean use_discount = false;
    private Boolean use_discount_salon = false;
    private Boolean use_discount_jok = false;
    private NumberFormat formatRupiah;
    private EditText shipping, discount, discountSalon, notes;
    private SessionManager sessionManager;
    private AlertDialog dialog;
    private DatabaseHandler db;
    private String currentTotalPrice;
    private RecyclerView recyclerMenu;
    private TabLayout tabCategory;
    private String param_customer_id = "0";
    private String param_category = "0";
    private String param_subcategory = "0";
    private String param_label = "0";
    private String param_keyword = "";
    private String param_keyword_customer = "";
    private EditText txt_name, txt_car_type, txt_handphone, txt_address;
    Button btnUseDiscountDialog, btnUseDiscountSalonDialog, btnUseDiscountJokDialog, btnRemoveDiscountDialog, btnRemoveDiscountSalonDialog, btnRemoveDiscountJokDialog;
    EditText discountAmount, discountSalonAmount, discountJokAmount;
    private int param_discount_amount=0, param_discount_salon_amount=0, param_discount_jok_amount=0;
    private AutoCompleteTextView selectCustomer;
    List<String> suggestions = new ArrayList<>();
    private int param_page=1;

    private int param_customer = 1;

    private EditText customerNew;

    private int socketTimeout = 30000;
    private int maxRetries = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        db = new DatabaseHandler(this);

        if(db.getAllCustomer(0, "").size() == 0){
            Toast.makeText(this, "Harap Download Data Customer Terlebih Dahulu", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
            finish();
            return;
        }

        if(db.getAllCustomerInfo().size() == 0){
            Toast.makeText(this, "Harap Download Data Customer Info Terlebih Dahulu Atau Download ulang customer", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
            finish();
            return;
        }

        if(db.getAllMenus("", 0, 0, 0, 0).size() == 0){
            Toast.makeText(this, "Harap Download Data Menu Terlebih Dahulu", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
            finish();
            return;
        }

        if(db.getAllCategories().size() == 0){
            Toast.makeText(this, "Harap Download Data Categories Terlebih Dahulu", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
            finish();
            return;
        }

        sessionManager = new SessionManager(this);

        formatRupiah = NumberFormat.getInstance();

        TextView username = findViewById(R.id.username);
        username.setText(sessionManager.getFullname());
        progressBar = findViewById(R.id.progressBar);
        totalCart = findViewById(R.id.total_cart);
        Button btnOrder = findViewById(R.id.btn_order);
        discount = findViewById(R.id.discount);
        discountSalon = findViewById(R.id.discount_salon);
        shipping = findViewById(R.id.shipping);
        notes = findViewById(R.id.notes);
        btnUseDiscount = findViewById(R.id.btn_use_discount);
        btnRemoveDiscount = findViewById(R.id.btn_remove_discount);
        btnUseDiscountSalon = findViewById(R.id.btn_use_discount_salon);
        btnRemoveDiscountSalon = findViewById(R.id.btn_remove_discount_salon);
        btnUseShipping = findViewById(R.id.btn_use_shipping);
        btnRemoveShipping = findViewById(R.id.btn_remove_shipping);
        btnAddCustomer = findViewById(R.id.btn_add_customer);
        btnAddDiscount = findViewById(R.id.btn_add_discount);

        customerNew = findViewById(R.id.customerNew);

        btnOrder.setOnClickListener(this);
        btnUseDiscount.setOnClickListener(this);
        btnRemoveDiscount.setOnClickListener(this);
        btnUseDiscountSalon.setOnClickListener(this);
        btnRemoveDiscountSalon.setOnClickListener(this);
        btnUseShipping.setOnClickListener(this);
        btnRemoveShipping.setOnClickListener(this);
        btnAddCustomer.setOnClickListener(this);
        btnAddDiscount.setOnClickListener(this);

        Button btnPay = findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(this);

        recyclerMenu = findViewById(R.id.recycler_menu);
        //recyclerMenu.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerMenu.setLayoutManager(new GridLayoutManager(this, 4));
        list_product = new ArrayList<>();
        customerList = new ArrayList<>();
        tableList = new ArrayList<>();
        list_customer =  new ArrayList<>();
        productAdapter = new ProductAdapter(list_product, getApplicationContext(), this);
        recyclerMenu.setAdapter(productAdapter);
        recyclerMenu.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){ //1 for down
                    param_page = param_page + 1;
                    getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                }
            }
        });

        LinearLayout lytBtnDineIn = findViewById(R.id.lyt_btn_dine_in);
        LinearLayout lytBtnDelivery = findViewById(R.id.lyt_btn_delivery);
        LinearLayout lytBtnTakeAway = findViewById(R.id.lyt_btn_take_away);
        LinearLayout lytBtnTaken = findViewById(R.id.lyt_btn_taken);
        LinearLayout lytTable = findViewById(R.id.lyt_table);

        radioDinein = findViewById(R.id.radioDinein);
        radioTakeaway = findViewById(R.id.radioTakeaway);
        radioDelivery = findViewById(R.id.radioDelivery);
        radioTaken = findViewById(R.id.radioTaken);

        if(sessionManager.getOutlet().equals("3")) {
            lytBtnTaken.setVisibility(View.VISIBLE);
            lytBtnDineIn.setVisibility(View.GONE);
            lytBtnTakeAway.setVisibility(View.GONE);
            lytTable.setVisibility(View.GONE);
            radioTaken.setChecked(true);
            orderType = "4";
        } else if(sessionManager.getOutlet().equals("10")) {
            lytBtnTaken.setVisibility(View.GONE);
            lytBtnDineIn.setVisibility(View.GONE);
            lytBtnTakeAway.setVisibility(View.GONE);
            lytTable.setVisibility(View.GONE);
            radioDelivery.setChecked(true);
            orderType = "3";
        }
        //Log.e("Order type", orderType);

        RecyclerView recyclerCart = findViewById(R.id.recycler_cart);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list_cart = new ArrayList<>();
        cartAdapter = new CartAdapter(list_cart, getApplicationContext(), this);
        recyclerCart.setAdapter(cartAdapter);

        spinnerCustomer = findViewById(R.id.customer);
        spinnerCustomer.setTitle(getResources().getString(R.string.select_customer));
        spinnerCustomer.setNegativeButton(getResources().getString(R.string.add_customer), (dialog, which) -> {
            addCustomerForm();
            dialog.dismiss();
        });
        customerNew.setOnClickListener(view -> {
            customerListNew();
        });
        spinnerCustomer.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, customerList));
        selectCustomer = findViewById(R.id.select_customer);
        selectCustomer.setThreshold(1);
        selectCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                param_keyword_customer = editable.toString();
                //getSearchCustomers();
            }
        });

        spinnerTable = findViewById(R.id.table);

        SearchView searchView = findViewById(R.id.mSearch);
        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //productAdapter.getFilter().filter(query);
                list_product.clear();
                param_page = 1;
                param_keyword = query;
                getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                return false;
            }
        });

        lytAlert = findViewById(R.id.lyt_alert);
        txtAlert = findViewById(R.id.txt_alert);
        ImageButton btnCloseAlert = findViewById(R.id.btn_close_alert);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnCloseAlert.setOnClickListener(this);

        // slide-up animation
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        if (extras != null) {
            update_order = extras.getBoolean("update_order");
            split_bill = extras.getBoolean("split_bill");
            order_id = extras.getString("order_id");

            if(update_order) {
                btnOrder.setText(getResources().getString(R.string.update_order));
                btnPay.setVisibility(View.GONE);
                getOderCartList(order_id);
            }
            if(split_bill) {
                btnOrder.setText(getResources().getString(R.string.split_bill));
                getOderCartList(order_id);
            }
        }
        btnBackTab = findViewById(R.id.btn_back_tab);
        tabCategory = findViewById(R.id.tab_category);
        getCategories();
        if(!update_order && !split_bill) {
            getCustomers("", "");
        }
        getTables();
    }

    private void addDiscountForm() {
        AlertDialog.Builder dialogAddCustomer = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogAddCustomerView = inflater.inflate(R.layout.form_add_discount, null);
        dialogAddCustomer.setView(dialogAddCustomerView);
        dialogAddCustomer.setCancelable(true);
        dialogAddCustomer.setTitle(getResources().getString(R.string.discount));

        btnUseDiscountDialog = dialogAddCustomerView.findViewById(R.id.btn_use_discount);
        btnUseDiscountSalonDialog = dialogAddCustomerView.findViewById(R.id.btn_use_discount_salon);
        btnUseDiscountJokDialog = dialogAddCustomerView.findViewById(R.id.btn_use_discount_jok);
        btnRemoveDiscountDialog = dialogAddCustomerView.findViewById(R.id.btn_remove_discount);
        btnRemoveDiscountSalonDialog = dialogAddCustomerView.findViewById(R.id.btn_remove_discount_salon);
        btnRemoveDiscountJokDialog = dialogAddCustomerView.findViewById(R.id.btn_remove_discount_jok);
        discountAmount = dialogAddCustomerView.findViewById(R.id.discount);
        discountSalonAmount = dialogAddCustomerView.findViewById(R.id.discount_salon);
        discountJokAmount = dialogAddCustomerView.findViewById(R.id.discount_jok);

        btnUseDiscountDialog.setOnClickListener(view -> {
            if(!discountAmount.getText().toString().equals("")) {
                param_discount_amount = Integer.parseInt(discountAmount.getText().toString());
                useDiscount();
            }
        });
        btnRemoveDiscountDialog.setOnClickListener(view -> {
            removeDiscount();
        });
        btnUseDiscountSalonDialog.setOnClickListener(view -> {
            if(!discountSalonAmount.getText().toString().equals("")) {
                param_discount_salon_amount = Integer.parseInt(discountSalonAmount.getText().toString());
                useDiscountSalon();
            }
        });
        btnRemoveDiscountSalonDialog.setOnClickListener(view -> {
            removeDiscountSalon();
        });

        btnUseDiscountJokDialog.setOnClickListener(view -> {
            if(!discountJokAmount.getText().toString().equals("")) {
                param_discount_jok_amount = Integer.parseInt(discountJokAmount.getText().toString());
                useDiscountJok();
            }
        });
        btnRemoveDiscountJokDialog.setOnClickListener(view -> {
            removeDiscountJok();
        });

        if(param_discount_amount != 0) {
            discountAmount.setText(String.valueOf(param_discount_amount));
            useDiscount();
        }
        if(param_discount_salon_amount != 0) {
            discountSalonAmount.setText(String.valueOf(param_discount_salon_amount));
            useDiscountSalon();
        }
        if(param_discount_jok_amount != 0) {
            discountJokAmount.setText(String.valueOf(param_discount_jok_amount));
            useDiscountJok();
        }

        dialogAddCustomer.setNegativeButton(getResources().getString(R.string.cancel), null);

        final AlertDialog mAlertDialog = dialogAddCustomer.create();
        mAlertDialog.show();
    }

    private void kosong(){
        txt_name.setText(null);
        txt_car_type.setText(null);
        txt_handphone.setText(null);
        txt_address.setText(null);
    }
    private void addCustomerForm() {
        AlertDialog.Builder dialogAddCustomer = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogAddCustomerView = inflater.inflate(R.layout.form_add_customer, null);
        dialogAddCustomer.setView(dialogAddCustomerView);
        dialogAddCustomer.setCancelable(true);
        dialogAddCustomer.setTitle(getResources().getString(R.string.add_customer));

        txt_name        = dialogAddCustomerView.findViewById(R.id.name);
        txt_car_type    = dialogAddCustomerView.findViewById(R.id.car_type);
        txt_handphone   = dialogAddCustomerView.findViewById(R.id.handphone);
        txt_address     = dialogAddCustomerView.findViewById(R.id.address);

        kosong();

        dialogAddCustomer.setPositiveButton(getResources().getString(R.string.save), null);
        dialogAddCustomer.setNegativeButton(getResources().getString(R.string.cancel), null);

        final AlertDialog mAlertDialog = dialogAddCustomer.create();
        mAlertDialog.setOnShowListener(dialog -> {
            Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                String name         = txt_name.getText().toString();
                String car_type     = txt_car_type.getText().toString();
                String handphone    = txt_handphone.getText().toString();
                String address      = txt_address.getText().toString();

                if(name.isEmpty()) {
                    txt_name.setHintTextColor(getResources().getColor(R.color.error));
                    txt_name.setBackgroundResource(R.drawable.border_error);
                } else if(car_type.isEmpty()) {
                    txt_car_type.setHintTextColor(getResources().getColor(R.color.error));
                    txt_car_type.setBackgroundResource(R.drawable.border_error);
                } else {
                    addCustomer(name,car_type,handphone,address);
                    dialog.dismiss();
                }
            });
        });
        mAlertDialog.show();
    }

    private void addCustomer(String name, String car_type, String handphone, String address) {
        showLoading();
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.API_ADD_CUSTOMER,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        String msg = jsonObject.getString("msg");
                        if(error) {
                            showError(msg);
                        } else {
                            String msg_name = jsonObject.getString("name");
                            JSONObject customer_info = new JSONObject(jsonObject.getString("data"));
                            db.addCustomers(customer_info.getString("name"), customer_info.getInt("id"), customer_info.getInt("is_member"));
                            getCustomerInfo(customer_info.getInt("id"), msg);
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
                params.put("name", name);
                params.put("car_type", car_type);
                params.put("phone", handphone);
                params.put("address", address);
                params.put("user_id", sessionManager.getId());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void getCustomerInfo(int customer_id, String msg){
        StringRequest stringRequest = new StringRequest(URI.UPDATE_CUSTOMER_INFO+"/"+customer_id, response -> {
            try {
                JSONObject res = new JSONObject(response);
                db.addCustomerInfo(res.getInt("id"), res.getString("name"), res.toString());
                getCustomers(res.getString("name"), "");
                showSuccess(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        30000,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }


    public void getInformation(int id, int status, JSONObject resp){
        StringRequest stringRequest = new StringRequest(URI.DOWNLOAD_ORDER_DETAIL+"/"+id, response -> {
            try {
                Log.e("HIT GET INFORMATION", "Yes");
                JSONObject res = new JSONObject(response);
                db.addOrders(res.getString("sale_no"), res.getInt("sale_no"), res.getInt("order_status"), res.toString(), res.getString("sale_date"));
                if(status == 1){
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra("order_id", res.getString("sale_no"));
                    startActivity(intent);
                    printTextWithHeader(resp.getString("header_invoice"), resp.getString("antrian"));
                }else if(status == 2){
                    Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                }else if(status == 3){
                    printTextWithHeader(resp.getString("header_invoice"), resp.getString("antrian"));
                    Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        30000,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(stringRequest);
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioDinein:
                if (checked) {
                    radioDinein.setChecked(true);
                    radioTakeaway.setChecked(false);
                    radioDelivery.setChecked(false);
                    radioTaken.setChecked(false);
                    orderType = "1";
                }
                break;
            case R.id.radioTakeaway:
                if (checked) {
                    radioDinein.setChecked(false);
                    radioTakeaway.setChecked(true);
                    radioDelivery.setChecked(false);
                    radioTaken.setChecked(false);
                    orderType = "2";
                }
                break;
            case R.id.radioDelivery:
                if (checked) {
                    radioDinein.setChecked(false);
                    radioTakeaway.setChecked(false);
                    radioDelivery.setChecked(true);
                    radioTaken.setChecked(false);
                    orderType = "3";
                }
                break;
            case R.id.radioTaken:
                if (checked) {
                    radioDinein.setChecked(false);
                    radioTakeaway.setChecked(false);
                    radioDelivery.setChecked(false);
                    radioTaken.setChecked(true);
                    orderType = "4";
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void getOderCartList(String order_id) {
        progressBar.setVisibility(View.VISIBLE);
        List<Order> orderList = db.getOrderBySale(Integer.parseInt(order_id));
        if(orderList.size() > 0){
            String response = orderList.get(0).getOrder_details();
            try {
                JSONObject jsonObject = new JSONObject(response);

                Log.e("RESPONSE", response);

                String getShipping = jsonObject.getString("logistic");
                shipping.setText(getShipping);

                if (!jsonObject.getString("cust_notes").equals("null")) {
                    notes.setText(jsonObject.getString("cust_notes"));
                } else {
                    notes.setText("");
                }

                getCustomers(jsonObject.getString("customer_name"), "older");

                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    Cart listData = new Cart(jo.getInt("food_menu_id"), jo.getString("menu_name"), jo.getInt("menu_unit_price"), jo.getInt("menu_unit_price"), jo.getInt("qty"));
                    list_cart.add(listData);
                }
                cartAdapter.notifyDataSetChanged();
                int totalPrice = 0;
                for (int i = 0; i < cartAdapter.getItemCount(); i++) {
                    list_cart.get(i).setPrice(list_cart.get(i).getOriPrice() * list_cart.get(i).getQty());
                    totalPrice += list_cart.get(i).getPrice();
                }
                String rupiah = formatRupiah.format(totalPrice).replace(',', '.');
                totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);

                int getDiscount = jsonObject.getInt("total_discount_amount");
                if (getDiscount != 0) {
                    param_discount_amount = getDiscount;
                }

                int getDiscountSalon = jsonObject.getInt("total_discount_salon_amount");
                if (getDiscountSalon != 0) {
                    param_discount_salon_amount = getDiscountSalon;
                }

                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    public void customerListOnline(AlertDialog mAlertDialog, String param_keywords)
    {
        List <CustomerValues> customerValuesList = db.getAllCustomer(param_page, param_keywords);
        LinearLayout customer = mAlertDialog.findViewById(R.id.listCustomer);
        for (int i=0; i < customerValuesList.size(); i++){
            @SuppressLint("InflateParams") View tableRow = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_customers_new, null, false);
            TextView plat_no = tableRow.findViewById(R.id.plat_no);
            TextView status = tableRow.findViewById(R.id.status);
            String name = "";
            name = customerValuesList.get(i).getName();
            plat_no.setText(customerValuesList.get(i).getName());
            if(customerValuesList.get(i).getIs_member() == 1) {
                status.setText("Member");
            }
            String finalName = name;
            tableRow.setOnClickListener(view -> {
                getCustomers(finalName, "");
                mAlertDialog.dismiss();
            });
            customer.addView(tableRow);
        }
    }

    private void customerListNew()
    {
        AlertDialog.Builder dialogAddCustomer = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogAddCustomerView = inflater.inflate(R.layout.form_list_customer, null);
        dialogAddCustomer.setView(dialogAddCustomerView);
        dialogAddCustomer.setCancelable(true);
        dialogAddCustomer.setTitle("List Pelanggan");

        dialogAddCustomer.setPositiveButton("Tambahkan Customer", null);
        dialogAddCustomer.setNegativeButton(getResources().getString(R.string.cancel), null);

        final AlertDialog mAlertDialog = dialogAddCustomer.create();
        mAlertDialog.setOnShowListener(dialog -> {
            customerListOnline(mAlertDialog, "");
            SwipeRefreshLayout swLayout = mAlertDialog.findViewById(R.id.swlayout);
            swLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            swLayout.setOnRefreshListener(() -> {
                param_customer = param_customer +1;
                customerListOnline(mAlertDialog, "");
                swLayout.setRefreshing(false);
            });
            EditText search = mAlertDialog.findViewById(R.id.search);
            search.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        LinearLayout customer = mAlertDialog.findViewById(R.id.listCustomer);
                        customer.removeAllViews();
                        param_customer = 1;
                        customerListOnline(mAlertDialog, s.toString());
                    }
                    else{
                        param_customer = 1;
                        customerListOnline(mAlertDialog, "");
                    }
                }
            });
            Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                addCustomerForm();
            });
        });
        mAlertDialog.show();
    }

    private void getMenuList(String api_customer_id, String api_category, String api_sub_category, String api_label) {
        List<Categories> categories = db.getCategoriesName(api_category);
        int cat_id = 0;
        for (int i=0; i<categories.size(); i++){
            cat_id = categories.get(i).getCategories_id();
            break;
        }
        List<SubCategories> subcategories = db.getSubCategories(api_sub_category);
        int sub_id = 0;
        for (int i=0; i < subcategories.size(); i++){
            sub_id = subcategories.get(i).getSub_categories_id();
            break;
        }
        List<Labels> labels = db.getLabelName(api_label);
        int label_id = 0;
        for (int i=0; i<labels.size(); i++){
            label_id = labels.get(i).getLabel_id();
            break;
        }
        List <Menus> menusList = db.getAllMenus(param_keyword, param_page, cat_id, sub_id, label_id);
        progressBar.setVisibility(View.VISIBLE);
        for (int i = 0; i < menusList.size(); i++) {
            int salePrice = menusList.get(i).getSale_price();
            Product listData = new Product(menusList.get(i).getMenu_id(), menusList.get(i).getPhoto(), menusList.get(i).getName(), salePrice, menusList.get(i).getReseller_price(), menusList.get(i).getOutlet_price(), menusList.get(i).getPartner_price(), menusList.get(i).getOnline_price(), menusList.get(i).getIngredient_stock(), api_customer_id);
            list_product.add(listData);
        }
        productAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void getCustomers(String val_customer_name, String condition) {
        List <CustomerValues> customerValuesList = db.getAllCustomer(0, "");
        if(val_customer_name.isEmpty()){
            customerNew.setText(customerValuesList.get(0).getName());
            param_customer_id = customerValuesList.get(0).getName();
        }else{
            customerNew.setText(val_customer_name);
            param_customer_id = customerNew.getText().toString();
        }
        list_cart.clear();
        cartAdapter.notifyDataSetChanged();
        onUpdatePrice(0);

        getCustomerInfo(param_customer_id);
        getMenuList(param_customer_id, param_category, param_subcategory, param_label);
    }

    private void getCustomerInfo(String customer_name) {
        LinearLayout lyt_member = findViewById(R.id.lyt_member);
        LinearLayout lyt_no_member = findViewById(R.id.lyt_no_member);
        lyt_member.setVisibility(View.GONE);
        TextView diskon_member_saldo = findViewById(R.id.diskon_member_saldo);
        TextView diskon_member_free = findViewById(R.id.diskon_member_free);
        TextView diskon_member_use = findViewById(R.id.diskon_member_use);
        TextView status_member = findViewById(R.id.status_member);
        TextView freeWashInfo = findViewById(R.id.free_wash_info);
        freeWashInfo.setText("Loading...");
        List<CustomerInfo> customerInfos = db.getCustomerInfo(customer_name);
        if(customerInfos.size() > 0){
            try {
                JSONObject jsonObject = new JSONObject(customerInfos.get(0).getCustomer_info());
                if(jsonObject.getBoolean("check_status") == true) {
                    lyt_no_member.setVisibility(View.GONE);
                    lyt_member.setVisibility(View.VISIBLE);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("member");
                    diskon_member_saldo.setText(jsonObject1.getString("balance") + " X Cuci Lagi");
                    diskon_member_free.setText((5 - jsonObject.getInt("counter")) + " X Cuci Lagi");
                    status_member.setText(jsonObject1.getString("message_active"));
                    diskon_member_use.setText(jsonObject1.getString("message_discount"));
                    return;
                }
                lyt_no_member.setVisibility(View.VISIBLE);
                freeWashInfo.setText((5-jsonObject.getInt("counter")) + "X lagi untuk cuci gratis");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCategories() {
        tabCategory.removeAllTabs();
        btnBackTab.setVisibility(View.GONE);
        param_category = "0";
        param_subcategory = "0";
        param_label = "0";
        tabCategory.addTab(tabCategory.newTab().setText("Semua Produk"));

        List <Categories> categoriesList = db.getAllCategories();
        for (int i = 0; i < categoriesList.size(); i++){
            String title_new = categoriesList.get(i).getName().toLowerCase();
            String capitalize = capitalizeText(title_new);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize, Html.FROM_HTML_MODE_LEGACY)));
            } else {
                tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize)));
            }
        }
        tabCategory.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                list_product.clear();
                param_page = 1;
                param_category = tab.getText().toString();
                if(param_category.equals("Semua Produk")) {
                    getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                } else {
                    getSubCategories(param_category);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                list_product.clear();
                param_page = 1;
                param_category = tab.getText().toString();
                //Log.e("tab", param_category);
                if(param_category.equals("Semua Produk")) {
                    getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                } else {
                    getSubCategories(param_category);
                }
            }
        });
    }

    private void getSubCategories(String category) {
        btnBackTab.setVisibility(View.VISIBLE);
        btnBackTab.setOnClickListener(v -> getCategories());
        tabCategory.removeAllTabs();
        tabCategory.clearOnTabSelectedListeners();
        tabCategory.addTab(tabCategory.newTab().setText("Semua Produk"));

        List<Categories> categories = db.getCategoriesName(category);
        int cat_id = 0;
        for (int i=0; i<categories.size(); i++){
            cat_id = categories.get(i).getCategories_id();
            break;
        }
        Log.e("NAME", category);
        Log.e("RESPONSE ITEM", String.valueOf(cat_id));
        List <SubCategories> subCategoriesList = db.getAllSubCategories(cat_id);
        if(subCategoriesList.size() > 0) {
            for (int i = 0; i < subCategoriesList.size(); i++) {
                String title_new = subCategoriesList.get(i).getName().toLowerCase();
                String capitalize = capitalizeText(title_new);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize, Html.FROM_HTML_MODE_LEGACY)));
                } else {
                    tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize)));
                }
            }
            list_product.clear();
            param_page = 1;
            getMenuList(param_customer_id, param_category, param_subcategory, param_label);
            tabCategory.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    list_product.clear();
                    param_page = 1;
                    param_subcategory = tab.getText().toString();
                    //Log.e("sub_tab", param_subcategory);
                    if(param_subcategory.equals("Semua Produk")) {
                        getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                    } else {
                        getLabels(param_subcategory);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    list_product.clear();
                    param_page = 1;
                    param_subcategory = tab.getText().toString();
                    //Log.e("sub_tab", param_category);
                    if(param_subcategory.equals("Semua Produk")) {
                        getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                    } else {
                        getLabels(param_subcategory);
                    }
                }
            });
        } else {
            list_product.clear();
            param_page = 1;
            getMenuList(param_customer_id, param_category, param_subcategory, param_label);
        }
    }

    private void getLabels(String category) {
        btnBackTab.setVisibility(View.VISIBLE);
        btnBackTab.setOnClickListener(v -> getCategories());
        List<SubCategories> subcategories = db.getSubCategories(category);
        int sub_id = 0;
        for (int i=0; i<subcategories.size(); i++){
            sub_id = subcategories.get(i).getCategories_id();
            break;
        }
        List <Labels> labelsList = db.getAllLabels(sub_id);
        if(labelsList.size() > 0) {
            tabCategory.removeAllTabs();
            tabCategory.clearOnTabSelectedListeners();
            tabCategory.addTab(tabCategory.newTab().setText("Semua Produk"));
            for (int i = 0; i < labelsList.size(); i++) {
                String title_new = labelsList.get(i).getName().toLowerCase();
                String capitalize = capitalizeText(title_new);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize, Html.FROM_HTML_MODE_LEGACY)));
                } else {
                    tabCategory.addTab(tabCategory.newTab().setText(Html.fromHtml(capitalize)));
                }
            }
            list_product.clear();
            param_page = 1;
            getMenuList(param_customer_id, param_category, param_subcategory, param_label);
            tabCategory.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    list_product.clear();
                    param_page = 1;
                    param_label = tab.getText().toString();
                    //Log.e("label", param_label);
                    getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    list_product.clear();
                    param_page = 1;
                    getMenuList(param_customer_id, param_category, param_subcategory, param_label);
                }
            });
        } else {
            list_product.clear();
            param_page = 1;
            getMenuList(param_customer_id, param_category, param_subcategory, param_label);
        }
    }

    private void getTables() {
        List <Tables> tablesList = db.getAllTables();
        for (int i=0; i<tablesList.size(); i++) {
            tableList.add(tablesList.get(i).getName());
        }
    }

    @Override
    public void onImageSelected(Product item) {
        showLoading();
        int info = new id.latenight.creativepos.model.CustomerInfo().getCustomerInfoPrice(item.getId(), param_customer_id, this);
        if(item.getIngredient_stock() == 0){
            showError(getResources().getString(R.string.out_of_stock));
        }else{
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            recyclerMenu.requestFocus();
            addItem(item, info);
        }
        hideLoading();
    }

    @SuppressLint("SetTextI18n")
    private void addItem(Product item, int newPrice) {
        if(use_discount) {
            removeDiscount();
        }
        if(use_discount_salon) {
            removeDiscountSalon();
        }
        if(use_discount_jok) {
            removeDiscountJok();
        }
        int  valuePrice = newPrice;
        Cart listData = new Cart(item.getId(), item.getTitle(), valuePrice, valuePrice, 1);
        list_cart.add(listData);
        cartAdapter.notifyItemChanged(-1);

        int totalPrice = 0;
        for (int i = 0; i < cartAdapter.getItemCount(); i++) {
            totalPrice += list_cart.get(i).getPrice();
        }

        String rupiah = formatRupiah.format(totalPrice);
        totalCart.setText(getResources().getString(R.string.currency) +" "+ rupiah.replace(',', '.'));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdatePrice(int grandTotal) {
        if(use_discount) {
            removeDiscount();
        }
        if(use_discount_salon) {
            removeDiscountSalon();
        }
        if(use_discount_jok) {
            removeDiscountJok();
        }

        int totalPrice = 0;
        for (int i = 0; i < cartAdapter.getItemCount(); i++) {
            totalPrice += list_cart.get(i).getPrice();
            //Log.e("TOTAL  "+i+": ", String.valueOf(list_cart.get(i).getName()));
        }

        String rupiah = formatRupiah.format(totalPrice);
        totalCart.setText(getResources().getString(R.string.currency) +" "+ rupiah.replace(',', '.'));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRemovePrice(List<Cart> item) {
        if(use_discount) {
            removeDiscount();
        }
        if(use_discount_salon) {
            removeDiscountSalon();
        }
        if(use_discount_jok) {
            removeDiscountJok();
        }

        int totalPrice = 0;
        for (int i = 0; i < item.size(); i++) {
            totalPrice += list_cart.get(i).getPrice();
        }

        String rupiah = formatRupiah.format(totalPrice);
        totalCart.setText(getResources().getString(R.string.currency) +" "+ rupiah.replace(',', '.'));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_order) {
            if(list_cart.size() > 0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                if(update_order) {
                    alertDialog.setPositiveButton(R.string.update_order, (dialog, whichButton) -> placeOrder(false));
                    alertDialog.setTitle(R.string.update_order);
                    alertDialog.setMessage(getResources().getString(R.string.sure_to_update_order));
                } else if(split_bill) {
                    alertDialog.setPositiveButton(R.string.split_bill, (dialog, whichButton) -> placeOrder(false));
                    alertDialog.setTitle(R.string.split_bill);
                    alertDialog.setMessage(getResources().getString(R.string.sure_to_update_order));
                } else {
                    alertDialog.setPositiveButton(R.string.save, (dialog, whichButton) -> placeOrder(false));
                    alertDialog.setNeutralButton(R.string.print, (dialog, whichButton) -> placeOrder(false));
                    alertDialog.setTitle(R.string.print_queue);
                    alertDialog.setMessage(getResources().getString(R.string.process_order_to_kitchet));
                }

                alertDialog.setIcon(R.drawable.ic_notif);
                alertDialog.setNegativeButton(R.string.cancel, null);
                AlertDialog orderConfirm = alertDialog.show();
                orderConfirm.show();
                Button placeorder = orderConfirm.getButton(DialogInterface.BUTTON_POSITIVE);
                placeorder.setTextColor(getResources().getColor(R.color.black));
                placeorder.setAllCaps(false);
                Button holdorder = orderConfirm.getButton(DialogInterface.BUTTON_NEUTRAL);
                holdorder.setTextColor(getResources().getColor(R.color.button_filled_blue_gradient));
                holdorder.setAllCaps(false);
                Button cancel = orderConfirm.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancel.setTextColor(getResources().getColor(R.color.error));
                cancel.setAllCaps(false);
            }
        }
        if(v.getId() == R.id.btn_close_alert){
            lytAlert.setVisibility(View.GONE);
        }
        if(v.getId() == R.id.btn_back){
            super.onBackPressed();
        }
        if(v.getId() == R.id.btn_pay){
            payOrder();
        }
        if(v.getId() == R.id.btn_use_discount){
            useDiscount();
        }
        if(v.getId() == R.id.btn_remove_discount){
            removeDiscount();
        }
        if(v.getId() == R.id.btn_use_discount_salon){
            useDiscountSalon();
        }
        if(v.getId() == R.id.btn_remove_discount_salon){
            removeDiscountSalon();
        }
        if(v.getId() == R.id.btn_use_shipping){
            useShipping();
        }
        if(v.getId() == R.id.btn_remove_shipping){
            removeShipping();
        }
        if(v.getId() == R.id.btn_add_customer){
            addCustomerForm();
        }
        if(v.getId() == R.id.btn_add_discount){
            addDiscountForm();
        }
    }

    @SuppressLint("SetTextI18n")
    private void useDiscount() {
        showLoading();
        if(param_discount_amount != 0) {
            currentTotalPrice = totalCart.getText().toString();
            currentTotalPrice = currentTotalPrice.replace("Rp. ", "").replace(".", "");

            if (Integer.parseInt(currentTotalPrice) < param_discount_amount) {
                showError(getResources().getString(R.string.discount_failed));
            } else {
                int afterDiscount = Integer.parseInt(currentTotalPrice) - param_discount_amount;
                String rupiah = formatRupiah.format(afterDiscount).replace(',', '.');
                totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
                btnUseDiscount.setVisibility(View.GONE);
                btnRemoveDiscount.setVisibility(View.VISIBLE);
                btnUseDiscountDialog.setVisibility(View.GONE);
                btnRemoveDiscountDialog.setVisibility(View.VISIBLE);
                use_discount = true;
                showSuccess(getResources().getString(R.string.discount_added));
            }
        } else {
            showError(getResources().getString(R.string.discount_error));
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeDiscount() {
        String rupiah = formatRupiah.format(Integer.valueOf(currentTotalPrice)).replace(',', '.');
        totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
        btnRemoveDiscount.setVisibility(View.GONE);
        btnUseDiscount.setVisibility(View.VISIBLE);
        btnRemoveDiscountDialog.setVisibility(View.GONE);
        btnUseDiscountDialog.setVisibility(View.VISIBLE);
        use_discount = false;
        showSuccess(getResources().getString(R.string.discount_deleted));
    }

    @SuppressLint("SetTextI18n")
    private void useDiscountSalon() {
        showLoading();
        if(param_discount_salon_amount != 0) {
            currentTotalPrice = totalCart.getText().toString();
            currentTotalPrice = currentTotalPrice.replace("Rp. ", "").replace(".", "");

            if (Integer.parseInt(currentTotalPrice) < param_discount_salon_amount) {
                showError(getResources().getString(R.string.discount_failed));
            } else {
                int afterDiscount = Integer.parseInt(currentTotalPrice) - param_discount_salon_amount;
                String rupiah = formatRupiah.format(afterDiscount).replace(',', '.');
                totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
                btnUseDiscountSalon.setVisibility(View.GONE);
                btnRemoveDiscountSalon.setVisibility(View.VISIBLE);
                btnUseDiscountSalonDialog.setVisibility(View.GONE);
                btnRemoveDiscountSalonDialog.setVisibility(View.VISIBLE);
                use_discount_salon = true;
                showSuccess(getResources().getString(R.string.discount_added));
            }
        } else {
            showError(getResources().getString(R.string.discount_error));
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeDiscountSalon() {
        String rupiah = formatRupiah.format(Integer.valueOf(currentTotalPrice)).replace(',', '.');
        totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
        btnRemoveDiscountSalon.setVisibility(View.GONE);
        btnUseDiscountSalon.setVisibility(View.VISIBLE);
        btnRemoveDiscountSalonDialog.setVisibility(View.GONE);
        btnUseDiscountSalonDialog.setVisibility(View.VISIBLE);
        use_discount_salon = false;
        showSuccess(getResources().getString(R.string.discount_deleted));
    }

    @SuppressLint("SetTextI18n")
    private void useDiscountJok() {
        showLoading();
        if(param_discount_jok_amount != 0) {
            currentTotalPrice = totalCart.getText().toString();
            currentTotalPrice = currentTotalPrice.replace("Rp. ", "").replace(".", "");

            if (Integer.parseInt(currentTotalPrice) < param_discount_jok_amount) {
                showError(getResources().getString(R.string.discount_failed));
            } else {
                int afterDiscount = Integer.parseInt(currentTotalPrice) - param_discount_jok_amount;
                String rupiah = formatRupiah.format(afterDiscount).replace(',', '.');
                totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
                btnUseDiscountJokDialog.setVisibility(View.GONE);
                btnRemoveDiscountJokDialog.setVisibility(View.VISIBLE);
                use_discount_jok = true;
                showSuccess(getResources().getString(R.string.discount_added));
            }
        } else {
            showError(getResources().getString(R.string.discount_error));
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeDiscountJok() {
        String rupiah = formatRupiah.format(Integer.valueOf(currentTotalPrice)).replace(',', '.');
        totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
        btnRemoveDiscountJokDialog.setVisibility(View.GONE);
        btnUseDiscountJokDialog.setVisibility(View.VISIBLE);
        use_discount_jok = false;
        showSuccess(getResources().getString(R.string.discount_deleted));
    }

    @SuppressLint("SetTextI18n")
    private void useShipping() {
        showLoading();
        String shipping_value = shipping.getText().toString();
        shipping_value = shipping_value.replace("Rp. ", "").replace(".","");

        currentTotalPrice = totalCart.getText().toString();
        currentTotalPrice = currentTotalPrice.replace("Rp. ", "").replace(".","");

        int afterShipping = Integer.parseInt(currentTotalPrice) + Integer.parseInt(shipping_value);
        String rupiah = formatRupiah.format(afterShipping).replace(',', '.');
        totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
        btnUseShipping.setVisibility(View.GONE);
        btnRemoveShipping.setVisibility(View.VISIBLE);
        showSuccess(getResources().getString(R.string.shipping_price_added));
    }

    @SuppressLint("SetTextI18n")
    private void removeShipping() {
        String rupiah = formatRupiah.format(Integer.parseInt(currentTotalPrice)).replace(',', '.');
        totalCart.setText(getResources().getString(R.string.currency) + " " + rupiah);
        btnRemoveShipping.setVisibility(View.GONE);
        btnUseShipping.setVisibility(View.VISIBLE);
        showError(getResources().getString(R.string.shipping_price_deleted));
    }

    @SuppressLint("InflateParams")
    public void PrintKOT(String invoice_kitchen, String invoice_bar, String invoice_table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_print_kot, null);
        builder.setView(dialogView);
        //dialog.setCancelable(false);
        builder.setIcon(R.drawable.ic_notif);
        builder.setTitle(getResources().getString(R.string.print_order_list));

        Button kitchen = dialogView.findViewById(R.id.kitchen);
        Button bar = dialogView.findViewById(R.id.bar);
        Button table = dialogView.findViewById(R.id.table);
        Button close = dialogView.findViewById(R.id.close);

        kitchen.setOnClickListener(v -> buttonPrintKot(invoice_kitchen));
        bar.setOnClickListener(v -> buttonPrintKot(invoice_bar));
        table.setOnClickListener(v -> buttonPrintKot(invoice_table));

        dialog = builder.show();


        close.setOnClickListener(v -> dialog.dismiss());
    }

    private void buttonPrintKot(String invoice) {
        //Log.e("STRUK", invoice);
        if(sessionManager.getEnablePrinter().equals("on")) {
            printText(invoice);
        }
    }

    public void printText(String invoice) {
        if(sessionManager.getEnablePrinter().equals("on")) {
            String header = "";
            MyApplication.getApplication().sendMessage(header, invoice);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
        }
    }

    public void printTextWithHeader(String header_invoice, String invoice) {
        if(sessionManager.getEnablePrinter().equals("on")) {
            MyApplication.getApplication().sendMessage(header_invoice, invoice);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
        }
    }

    @SuppressLint("SetTextI18n")
    private void placeOrder(boolean is_hold) {
        showLoading();
        String URL_POST;
        if(is_hold) {
            URL_POST = URI.API_HOLD_ORDER;
        } else {
            URL_POST = URI.API_PLACE_ORDER;
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_POST,
                response -> {
                    Log.e("HIT GET PLACE ORDER", "Yes");
                    try {
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            if(update_order) {
                                db.deleteOrder(Integer.parseInt(jsonObject.getString("sale_no")));
                                db.deleteSales(Integer.parseInt(jsonObject.getString("sale_no")));
                            }
                            db.addSales(jsonObject.getString("sale_no"), jsonObject.getString("sales_information"));
                            showSuccess(jsonObject.getString("message"));
                            list_cart.clear();
                            cartAdapter.notifyDataSetChanged();
                            if(update_order){
                                getInformation(jsonObject.getInt("sale_no"), 2, jsonObject);
                            } else if(split_bill){
                                getInformation(jsonObject.getInt("sale_no"), 2, jsonObject);
                            } else {
                                getInformation(jsonObject.getInt("sale_no"), 3, jsonObject);
                            }
                            discount.setText("");
                            notes.setText("");
                            totalCart.setText("Rp. 0");
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
                    showError("Terjadi kesalahan server");

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

                String customer_id = param_customer_id;
                String waiter_id = sessionManager.getId();
                String orders_table;

                if (sessionManager.getOutlet().equals("3")) {
                    orders_table = "VIP 1";
                } else {
                    List<Tables> tablesList = db.getAllTables();
                    for (int i = 0; i < tablesList.size(); i++) {
                        tableList.add(tablesList.get(i).getName());
                    }
                    orders_table = tablesList.get(0).getName();
                }

                String value_shipping = shipping.getText().toString();
                String value_notes = notes.getText().toString();

                JSONArray array=new JSONArray();
                for(int i=0;i<list_cart.size();i++){
                    JSONObject obj=new JSONObject();
                    try {
                        obj.put("id",list_cart.get(i).getId());
                        obj.put("qty",list_cart.get(i).getQty());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
                }

                if(update_order) {
                    params.put("sale_id", order_id);
                } else if(split_bill) {
                    params.put("new_sale_id", order_id);
                    params.put("split_bill", split_bill.toString());
                }
                params.put("user_id", sessionManager.getId());
                params.put("customer_id", customer_id);
                params.put("waiter_id", waiter_id);
                params.put("order_type", orderType);
                params.put("sub_total", totalCart.getText().toString());
                params.put("orders_table", orders_table);
                if(use_discount) {
                    params.put("discount", String.valueOf(param_discount_amount));
                }
                if(use_discount_salon) {
                    params.put("discount_salon", String.valueOf(param_discount_salon_amount));
                }
                if(use_discount_jok) {
                    params.put("discount_jok", String.valueOf(param_discount_jok_amount));
                }
                params.put("logistic", value_shipping);
                params.put("notes", value_notes);
                params.put("total_items_in_cart", String.valueOf(list_cart.size()));
                params.put("items", array.toString());
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
    private void payOrder() {
        showLoading();
        String URL_POST;
        URL_POST = URI.API_PLACE_ORDER;
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_POST,
                response -> {
                    try {
                        Log.e("HIT PAY ORDER", "Yes");
                        hideLoading();
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            //Log.e("RESPONSE ", jsonObject.getString("sale_no") + " | "+ jsonObject.getString("sales_information"));
                            db.addSales(jsonObject.getString("sale_no"), jsonObject.getString("sales_information"));
                            showSuccess(jsonObject.getString("message"));
                            list_cart.clear();
                            cartAdapter.notifyDataSetChanged();
                            discount.setText("");
                            notes.setText("");
                            totalCart.setText("Rp. 0");
                            getInformation(jsonObject.getInt("sale_no"), 1, jsonObject);
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
                    showError("Terjadi kesalahan server");

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

                String customer_id = param_customer_id;
                String waiter_id = sessionManager.getId();
                String orders_table;

                if (sessionManager.getOutlet().equals("3")) {
                    orders_table = "VIP 1";
                } else {
                    List<Tables> tablesList = db.getAllTables();
                    for (int i = 0; i < tablesList.size(); i++) {
                        tableList.add(tablesList.get(i).getName());
                    }
                    orders_table = tablesList.get(0).getName();
                }
                String value_shipping = shipping.getText().toString();
                String value_notes = notes.getText().toString();

                JSONArray array=new JSONArray();
                for(int i=0;i<list_cart.size();i++){
                    JSONObject obj=new JSONObject();
                    try {
                        obj.put("id",list_cart.get(i).getId());
                        obj.put("qty",list_cart.get(i).getQty());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
                }

                if(update_order) {
                    params.put("sale_id", order_id);
                } else if(split_bill) {
                    params.put("new_sale_id", order_id);
                    params.put("split_bill", split_bill.toString());
                }
                params.put("user_id", sessionManager.getId());
                params.put("customer_id", customer_id);
                params.put("waiter_id", waiter_id);
                params.put("order_type", orderType);
                params.put("sub_total", totalCart.getText().toString());
                params.put("orders_table", orders_table);
                if(use_discount) {
                    params.put("discount", String.valueOf(param_discount_amount));
                }
                if(use_discount_salon) {
                    params.put("discount_salon", String.valueOf(param_discount_salon_amount));
                }
                if(use_discount_jok) {
                    params.put("discount_jok", String.valueOf(param_discount_jok_amount));
                }
                params.put("logistic", value_shipping);
                params.put("notes", value_notes);
                params.put("total_items_in_cart", String.valueOf(list_cart.size()));
                params.put("items", array.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void showLoading() {
        loadingDialog = ProgressDialog.show(this, "",
                "Mohon tunggu...", true);
        loadingDialog.show();
    }
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    public void showError(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        lytAlert.setBackgroundResource(R.drawable.alert_error);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.error));
        hideLoading();
        new Handler().postDelayed(() -> {
            lytAlert.setVisibility(View.GONE);
            lytAlert.startAnimation(slideDown);
        }, 3000);
    }

    private void showSuccess(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        lytAlert.setBackgroundResource(R.drawable.alert_success);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.colorAccent));
        hideLoading();
        new Handler().postDelayed(() -> {
            lytAlert.setVisibility(View.GONE);
            lytAlert.startAnimation(slideDown);
        }, 3000);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
