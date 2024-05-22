package id.latenight.creativepos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.adapter.Customer;
import id.latenight.creativepos.adapter.CustomerAdapter;
import id.latenight.creativepos.util.SearchableSpinner;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class CustomerListActivity extends AppCompatActivity implements CustomerAdapter.ImageAdapterListener {
    private List<Customer> list_customer;
    private CustomerAdapter customerAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private EditText customerName;
    private String param_outlet_id="", param_keyword="";
    private int param_page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        sessionManager = new SessionManager(this);

        progressBar = findViewById(R.id.progressBar);

        customerName = findViewById(R.id.customer_name);

        RecyclerView recyclerCustomer = findViewById(R.id.recycler_customer);
        recyclerCustomer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list_customer = new ArrayList<>();
        customerAdapter = new CustomerAdapter(list_customer, getApplicationContext(), this);
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
            getCustomersList();
            swLayout.setRefreshing(false);
        });

        Button filter = findViewById(R.id.filter);
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
        Log.e("URL_",URI.API_CUSTOMER_LIST_HISTORIES+sessionManager.getId()+"?page="+param_page+"&keyword="+param_keyword);
        request = new JsonArrayRequest(URI.API_CUSTOMER_LIST_HISTORIES+sessionManager.getId()+"?page="+param_page+"&keyword="+param_keyword, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    Customer listData = new Customer(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("car_type"), jsonObject.getInt("counter"), jsonObject.getInt("total_redeem"), jsonObject.getInt("total_visit"), jsonObject.getString("last_visit"));
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
    public void onItemSelected(Customer item) {
        Intent intent = new Intent(this, CustomerSalesListActivity.class);
        intent.putExtra("customer_id", item.getId());
        intent.putExtra("customer_name", item.getName());
        startActivity(intent);
    }
}