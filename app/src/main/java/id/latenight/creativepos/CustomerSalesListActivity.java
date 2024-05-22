package id.latenight.creativepos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.latenight.creativepos.adapter.Customer;
import id.latenight.creativepos.adapter.CustomerAdapter;
import id.latenight.creativepos.adapter.CustomerSales;
import id.latenight.creativepos.adapter.CustomerSalesAdapter;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class CustomerSalesListActivity extends AppCompatActivity implements CustomerSalesAdapter.ImageAdapterListener {
    private List<CustomerSales> list_customer;
    private CustomerSalesAdapter customerAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private EditText customerName, startDate, endDate;
    private String param_outlet_id="", param_keyword="", param_customer_name, param_start_date="", param_end_date = "";
    private int param_page=1, customer_id;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sales_list);

        sessionManager = new SessionManager(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            customer_id = extras.getInt("customer_id");
            param_customer_name = extras.getString("customer_name");
        }

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select date");
        MaterialDatePicker<Pair<Long, Long>> pickerRange = materialDateBuilder.build();

        progressBar = findViewById(R.id.progressBar);

        customerName = findViewById(R.id.customer_name);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        name = findViewById(R.id.name);
        name.setText(param_customer_name);

        startDate.setOnClickListener(v -> pickerRange.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        endDate.setOnClickListener(v -> pickerRange.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        pickerRange.addOnPositiveButtonClickListener(selection -> {
            Pair selectedDates = pickerRange.getSelection();
            final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
            Date start_date = rangeDate.first;
            Date end_date = rangeDate.second;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
            startDate.setText(simpleFormat.format(start_date));
            endDate.setText(simpleFormat.format(end_date));

            param_start_date = (simpleFormat.format(start_date));
            param_end_date = (simpleFormat.format(end_date));
        });

        RecyclerView recyclerCustomer = findViewById(R.id.recycler_customer);
        recyclerCustomer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list_customer = new ArrayList<>();
        customerAdapter = new CustomerSalesAdapter(list_customer, getApplicationContext(), this);
        recyclerCustomer.setAdapter(customerAdapter);
        recyclerCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (! recyclerView.canScrollVertically(1)){ //1 for down
                param_page = param_page + 1;
                getCustomersList();
            }
            }
        });

        SwipeRefreshLayout swLayout = findViewById(R.id.swlayout);
        swLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swLayout.setOnRefreshListener(() -> {
            list_customer.clear();
            param_page = 1;
            param_keyword = "";
            param_start_date = "";
            param_end_date = "";
            customerName.setText("");
            startDate.setText("");
            endDate.setText("");
            getCustomersList();
            swLayout.setRefreshing(false);
        });

        Button filter = findViewById(R.id.filter);
        Button resetFilter = findViewById(R.id.reset_filter);
        resetFilter.setOnClickListener(v -> {
            list_customer.clear();
            param_page = 1;
            param_keyword = "";
            param_start_date = "";
            param_end_date = "";
            customerName.setText("");
            startDate.setText("");
            endDate.setText("");
            getCustomersList();
        });
        filter.setOnClickListener(v -> {
            list_customer.clear();
            param_page = 1;
            param_keyword = customerName.getText().toString();
            getCustomersList();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCustomersList() {
        progressBar.setVisibility(View.VISIBLE);
        String param_date = "&start_date="+param_start_date+"&end_date="+param_end_date;
        Log.e("URL_",URI.API_CUSTOMER_SALES_HISTORIES+customer_id+"?page="+param_page+"&keyword="+param_keyword+param_date);
        request = new JsonArrayRequest(URI.API_CUSTOMER_SALES_HISTORIES+customer_id+"?page="+param_page+"&keyword="+param_keyword+param_date, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    CustomerSales listData = new CustomerSales(jsonObject.getInt("id"), jsonObject.getString("sale_no"), jsonObject.getString("sale_date"), jsonObject.getInt("total_payable"), jsonObject.getString("washer1"), jsonObject.getString("washer2"));
                    list_customer.add(listData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            customerAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }, error -> {
            progressBar.setVisibility(View.GONE);
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @Override
    public void onResume(){
        super.onResume();
        list_customer.clear();
        getCustomersList();
    }

    @Override
    public void onItemSelected(CustomerSales item) {

    }
}