package id.latenight.creativepos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        db = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);

        formatRupiah = NumberFormat.getInstance();

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
        Log.e("URL_", URI.API_NEW_ORDER+'/'+sessionManager.getId());
        runningOrderList.clear();
        runningOrderAdapter.notifyDataSetChanged();
        progressBar2.setVisibility(View.VISIBLE);
        request = new JsonArrayRequest(URI.API_NEW_ORDER+'/'+sessionManager.getId(), response -> {
            JSONObject jsonObject;
            db.truncate();
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    Order listData = new Order(
                            jsonObject.getString("id"),
                            jsonObject.getString("sale_no"),
                            jsonObject.getString("total_payable"),
                            jsonObject.getString("sale_date"),
                            jsonObject.getString("order_time"),
                            jsonObject.getString("order_type"),
                            jsonObject.getString("customer_name"),
                            jsonObject.getString("order_status"),
                            jsonObject.getString("sub_total"),
                            jsonObject.getString("sub_total_discount_value"),
                            jsonObject.getString("total_discount_amount"),
                            jsonObject.getString("cust_notes"),
                            jsonObject.getString("queue_no"),
                            jsonObject.getString("category"),
                            jsonObject.getInt("status"));
                    runningOrderList.add(listData);
                    db.addSales(jsonObject.getString("sale_no"), String.valueOf(jsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            runningOrderAdapter.notifyDataSetChanged();
            progressBar2.setVisibility(View.GONE);
            getStockOut();
        }, error -> progressBar2.setVisibility(View.GONE));
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void getOrderHistory() {
        Log.e("URL_", URI.API_TEN_SALES+'/'+sessionManager.getId()+"?page="+param_page);
        progressBar.setVisibility(View.VISIBLE);
        request = new JsonArrayRequest(URI.API_TEN_SALES+'/'+sessionManager.getId()+"?page="+param_page, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    Order listData = new Order(jsonObject.getString("id"), jsonObject.getString("sale_no"), jsonObject.getString("total_payable"), jsonObject.getString("sale_date"), jsonObject.getString("order_time"), jsonObject.getString("order_type"), jsonObject.getString("customer_name"), jsonObject.getString("order_status"), jsonObject.getString("sub_total"), jsonObject.getString("sub_total_discount_value"), jsonObject.getString("total_discount_amount"), jsonObject.getString("cust_notes"), jsonObject.getString("queue_no"), jsonObject.getString("category"), jsonObject.getInt("status"));
                    orderHistoryList.add(listData);
                    db.addSales(jsonObject.getString("sale_no"), String.valueOf(jsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            orderHistoryAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            getTotalSalesToday();
//            int totalPrice = 0;
//            for (int i = 0; i<orderHistoryList.size(); i++)
//            {
//                totalPrice += Float.parseFloat(orderHistoryList.get(i).getTotalPayable());
//                Log.e("total", String.valueOf(orderHistoryList.get(i).getTotalPayable()));
//            }
//            totalSalesToday.setText(getResources().getString(R.string.currency)+" "+formatRupiah.format(totalPrice).replace(',', '.'));
        }, error -> progressBar.setVisibility(View.GONE));
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void getStockOut() {
        Log.e("URL_", URI.API_STOCK_OUT+sessionManager.getId());
        StringRequest stringRequest = new StringRequest(URI.API_STOCK_OUT + sessionManager.getId(), response -> {

            float value_stock = (Float.parseFloat(response));
            Log.e("PPN", String.valueOf(value_stock));
            totalStockToday.setText(formatRupiah.format(value_stock).replace(',', '.'));
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void getTotalSalesToday() {
        Log.e("URL_", URI.API_TOTAL_SALES_TODAY+sessionManager.getId());
        StringRequest stringRequest = new StringRequest(URI.API_TOTAL_SALES_TODAY + sessionManager.getId(), response -> {

            float value = (Float.parseFloat(response));
            Log.e("total sales today", String.valueOf(value));
            totalSalesToday.setText(formatRupiah.format(value).replace(',', '.'));
        }, error -> {
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
