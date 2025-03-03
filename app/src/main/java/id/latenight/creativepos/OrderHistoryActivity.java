package id.latenight.creativepos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.latenight.creativepos.adapter.Order;
import id.latenight.creativepos.adapter.OrderAdapter;
import id.latenight.creativepos.model.CustomerInfo;
import id.latenight.creativepos.util.DatabaseHandler;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OrderAdapterListener {

    private List<Order> runningOrderList, orderHistoryList;
    private OrderAdapter runningOrderAdapter, orderHistoryAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ProgressBar progressBar,progressBar2;
    private NumberFormat formatRupiah;
    private TextView totalSalesToday,totalStockToday;
    private DatabaseHandler db;
    private String currentData;
    private SessionManager sessionManager;
    private int param_page=1;
    private Integer[] car_salon, leater_seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        db = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);

        formatRupiah = NumberFormat.getInstance();

        car_salon = new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        leater_seat = new Integer[]{42,43,44,45,46,47,48,49,50,51};

        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        totalSalesToday = findViewById(R.id.total_sales_today);
        totalStockToday = findViewById(R.id.total_stock_today);

        RecyclerView recyclerRunningOrder = findViewById(R.id.recycler_running_order);
        recyclerRunningOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerRunningOrder.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        runningOrderList = new ArrayList<>();
        runningOrderAdapter = new OrderAdapter(runningOrderList, getApplicationContext(), this);
        recyclerRunningOrder.setAdapter(runningOrderAdapter);
        // ----------------
        RecyclerView recyclerOrderHistory = findViewById(R.id.recycler_order_history);
        recyclerOrderHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerOrderHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        orderHistoryList = new ArrayList<>();
        orderHistoryAdapter = new OrderAdapter(orderHistoryList, getApplicationContext(), this);
        recyclerOrderHistory.setAdapter(orderHistoryAdapter);
        recyclerOrderHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){ //1 for down
                    param_page = param_page + 1;
                    getOrderHistory();
                }
            }
        });

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
                orderHistoryAdapter.getFilter().filter(query);
                return false;
            }
        });

        //db.truncate();
        Date d = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        currentData = formatter.format(d);

        getRunningOrder();
        getOrderHistory();

        SwipeRefreshLayout sw_running_order_refresh = findViewById(R.id.sw_running_order_refresh);
        sw_running_order_refresh.setOnRefreshListener(() -> {
            getRunningOrder();
            sw_running_order_refresh.setRefreshing(false);
        });

        SwipeRefreshLayout sw_order_history_refresh = findViewById(R.id.sw_order_history_refresh);
        sw_order_history_refresh.setOnRefreshListener(() -> {
            orderHistoryList.clear();
            param_page = 1;
            getOrderHistory();
            sw_order_history_refresh.setRefreshing(false);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onOrderSelected(Order item) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_id", item.getSaleNo());
        startActivity(intent);
    }

    private void getRunningOrder() {
        runningOrderList.clear();
        runningOrderAdapter.notifyDataSetChanged();
        progressBar2.setVisibility(View.VISIBLE);
        List<id.latenight.creativepos.adapter.sampler.Order> orderLIst = db.getOrderRunning();
        for (int i = 0; i < orderLIst.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(orderLIst.get(i).getOrder_details());
                JSONArray items = new JSONArray(jsonObject.getString("items"));
                String menu_category = "";
                String category = "";
                String status = "";
                for (int j = 0; j < items.length(); j++) {
                    JSONObject js = items.getJSONObject(j);
                    if (js.getInt("category_id") == 1) {
                        category = "Mobil";
                    } else if (js.getInt("category_id") == 2) {
                        category = "Motor";
                    } else if (js.getInt("category_id") == 4) {
                        category = "Elf";
                    } else if (js.getInt("category_id") == 9) {
                        category = "Pick Up";
                    } else if (js.getInt("category_id") == 10) {
                        category = "Cuci Mesin";
                    } else if (new CustomerInfo().isInArray(car_salon, js.getInt("category_id"))) {
                        category = "Salon Mobil";
                    } else if (new CustomerInfo().isInArray(leater_seat, js.getInt("category_id"))) {
                        category = "Jok Mobil";
                    } else {
                        category = "Warung";
                    }

                    if (js.getString("menu_category").equals("15")) {
                        menu_category = "15";
                    }
                }
                if(jsonObject.getInt("order_status") == 9){
                    status = "Proses Leather Seat";
                }else if(jsonObject.getInt("order_status") == 8){
                    status = "Proses Cuci";
                }else if(jsonObject.getInt("order_status") == 7){
                    status = "Proses Salon";
                }else if(jsonObject.getInt("order_status") == 6){
                    status = "Proses Cuci";
                }else if(jsonObject.getInt("order_status") == 5){
                    status = "Masih Mengantri";
                }else if(jsonObject.getInt("order_status") == 4){
                    status = "Batal";
                }else if(jsonObject.getInt("order_status") == 3){
                    status = "Selesai";
                }else if(jsonObject.getInt("order_status") == 2){
                    status = "Sudah Bayar";
                }else if(jsonObject.getInt("order_status") == 1){
                    status = "Dalam Proses";
                }else{
                    status = "Dalam Proses";
                }
                Order listData = new Order(
                        jsonObject.getString("id"),
                        jsonObject.getString("sale_no"),
                        jsonObject.getString("total_payable"),
                        jsonObject.getString("sale_date"),
                        jsonObject.getString("order_time"),
                        jsonObject.getString("order_type"),
                        jsonObject.getString("customer_name"),
                        status,
                        jsonObject.getString("sub_total"),
                        jsonObject.getString("sub_total_discount_value"),
                        jsonObject.getString("total_discount_amount"),
                        jsonObject.getString("cust_notes"),
                        jsonObject.getString("queue_no"),
                        category,
                        jsonObject.getInt("status"),
                        menu_category);
                runningOrderList.add(listData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        runningOrderAdapter.notifyDataSetChanged();
        progressBar2.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void getOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        List<id.latenight.creativepos.adapter.sampler.Order> orderList = db.getOrderHistory(param_page);
        Log.e("RESULT", orderList.toString());
        for (int i = 0; i < orderList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(orderList.get(i).getOrder_details());
                JSONArray items = new JSONArray(jsonObject.getString("items"));
                String menu_category = "";
                String category = "", status = "";
                for (int j = 0; j < items.length(); j++) {
                    JSONObject js = items.getJSONObject(j);
                    if (js.getInt("category_id") == 1) {
                        category = "Mobil";
                    } else if (js.getInt("category_id") == 2) {
                        category = "Motor";
                    } else if (js.getInt("category_id") == 4) {
                        category = "Elf";
                    } else if (js.getInt("category_id") == 9) {
                        category = "Pick Up";
                    } else if (js.getInt("category_id") == 10) {
                        category = "Cuci Mesin";
                    } else if (new CustomerInfo().isInArray(car_salon, js.getInt("category_id"))) {
                        category = "Salon Mobil";
                    } else if (new CustomerInfo().isInArray(leater_seat, js.getInt("category_id"))) {
                        category = "Jok Mobil";
                    } else {
                        category = "Warung";
                    }

                    if (js.getString("menu_category").equals("15")) {
                        menu_category = "15";
                    }
                }
                if (jsonObject.getInt("order_status") == 5) {
                    status = "Masih Mengantri";
                } else if (jsonObject.getInt("order_status") == 4) {
                    status = "Batal";
                } else if (jsonObject.getInt("order_status") == 3) {
                    status = "Selesai";
                } else if (jsonObject.getInt("order_status") == 2) {
                    status = "Sudah Bayar";
                } else if (jsonObject.getInt("order_status") == 1) {
                    status = "Dalam Proses";
                }
                Order listData = new Order(jsonObject.getString("id"), jsonObject.getString("sale_no"), jsonObject.getString("total_payable"), jsonObject.getString("sale_date"), jsonObject.getString("order_time"), jsonObject.getString("order_type"), jsonObject.getString("customer_name"), status, jsonObject.getString("sub_total"), jsonObject.getString("sub_total_discount_value"), jsonObject.getString("total_discount_amount"), jsonObject.getString("cust_notes"), jsonObject.getString("queue_no"), category, jsonObject.getInt("order_status"), menu_category);
                orderHistoryList.add(listData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        orderHistoryAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        getTotalSalesToday();
    }

    @SuppressLint("SetTextI18n")
    private void getStockOut() {
        StringRequest stringRequest = new StringRequest(URI.API_STOCK_OUT + sessionManager.getId(), response -> {

            float value_stock = (Float.parseFloat(response));
            totalStockToday.setText(formatRupiah.format(value_stock).replace(',', '.'));
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
        private void getTotalSalesToday() {
        List<id.latenight.creativepos.adapter.sampler.Order> orderList = db.getOrderHistory(0);
        int totalPayable = 0;
        for (int i=0; i<orderList.size(); i++){
            try {
                JSONObject jsonObject = new JSONObject(orderList.get(i).getOrder_details());
                totalPayable += jsonObject.getInt("total_payable");
            }catch (Exception e){

            }
        }
        totalSalesToday.setText(formatRupiah.format(totalPayable).replace(',', '.'));
    }

    public void holdOrderList(View view) {
        Intent intent = new Intent(this, HoldOrderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("TAG");
        }
    }
}
