package id.latenight.creativepos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import id.latenight.creativepos.adapter.Order;
import id.latenight.creativepos.adapter.DeleteOrderAdapter;
import id.latenight.creativepos.util.DatabaseHandler;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

import static id.latenight.creativepos.util.URI.API_DELETE_SALES;

public class DeleteSalesActivity extends AppCompatActivity implements DeleteOrderAdapter.DeleteOrderAdapterListener {

    private List<Order> orderHistoryList;
    private DeleteOrderAdapter orderHistoryAdapter;
    private ProgressBar progressBar;
    private RelativeLayout lytAlert;
    private TextView txtAlert;
    private Animation slideUp;
    private SessionManager sessionManager;
    private EditText date;
    private Spinner spinnerStatusOrder;
    private String[] statusOrders = {"Unpaid","Paid"};
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sales);
        sessionManager = new SessionManager(this);

        spinnerStatusOrder = findViewById(R.id.spinner_status);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statusOrders);
        spinnerStatusOrder.setAdapter(adapter);
        spinnerStatusOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                getOrderHistory();
            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });

        progressBar = findViewById(R.id.progressBar);

        RecyclerView recyclerOrderHistory = findViewById(R.id.recycler_order_history);
        recyclerOrderHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerOrderHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        orderHistoryList = new ArrayList<>();
        orderHistoryAdapter = new DeleteOrderAdapter(orderHistoryList, getApplicationContext(), this);
        recyclerOrderHistory.setAdapter(orderHistoryAdapter);

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

        lytAlert = findViewById(R.id.lyt_alert);
        txtAlert = findViewById(R.id.txt_alert);
        db = new DatabaseHandler(this);
        // slide-up animation
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        date = findViewById(R.id.date);

        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(sdf.format(myCalendar.getTime()));
            getOrderHistory();
        };
        date.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(DeleteSalesActivity.this, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),  myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onOrderSelected(Order item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_sales)
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setIcon(R.drawable.ic_notif)
                .setPositiveButton(R.string.delete_sales, (dialog, whichButton) -> deleteSales(item.getSaleNo(), orderHistoryList.indexOf(item)))
                .setNegativeButton(R.string.cancel, null).show();
    }

    @SuppressLint("SetTextI18n")
    private void getOrderHistory() {
        Log.e("URL_", URI.API_ALL_ORDER+'/'+sessionManager.getId()+ "?date=" +date.getText().toString()+ "&status=" +spinnerStatusOrder.getSelectedItem().toString());
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest request = new JsonArrayRequest(URI.API_ALL_ORDER+'/'+sessionManager.getId()+ "?date=" +date.getText().toString()+ "&status=" +spinnerStatusOrder.getSelectedItem().toString(), response -> {
            JSONObject jsonObject;
            Log.e("Response", response.toString());
            orderHistoryList.clear();
            for (int i = 0; i < response.length(); i++) {
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
                            jsonObject.getInt("status"),
                            jsonObject.getString("payment_type_member"));
                    orderHistoryList.add(listData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            orderHistoryAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }, error -> progressBar.setVisibility(View.GONE));
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void deleteSales(String sale_id, int position) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, API_DELETE_SALES,
                response -> {
                    Log.e("RESPONSE ", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            showSuccess(jsonObject.getString("message"));
                            orderHistoryList.remove(position);
                            orderHistoryAdapter.notifyItemRemoved(position);
                            db.deleteOrder(Integer.valueOf(sale_id));
                        } else {
                            showError(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        showError("Terjadi kesalahan server");
                    }
                },
                error -> {
                    error.printStackTrace();
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

                params.put("sale_id", sale_id);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public void showError(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.error));
        progressBar.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        lytAlert.setVisibility(View.VISIBLE);
        lytAlert.startAnimation(slideUp);
        lytAlert.setBackgroundResource(R.drawable.alert_success);
        txtAlert.setText(message);
        txtAlert.setTextColor(getResources().getColor(R.color.colorAccent));
        progressBar.setVisibility(View.GONE);
    }

    public void hideLoading(View view) {
        lytAlert.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();
    }
}
