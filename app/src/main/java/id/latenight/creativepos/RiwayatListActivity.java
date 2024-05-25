package id.latenight.creativepos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import id.latenight.creativepos.adapter.Member;
import id.latenight.creativepos.adapter.MemberAdapter;
import id.latenight.creativepos.adapter.Riwayat;
import id.latenight.creativepos.adapter.RiwayatAdapter;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class RiwayatListActivity extends AppCompatActivity implements RiwayatAdapter.ImageAdapterListener {
    private List<Riwayat> list_customer;
    private RiwayatAdapter customerAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private EditText customerName;
    private String param_outlet_id="", param_keyword="";
    private int param_page=1;
    private TextView title_riwayat;

    private Integer member_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_list);

        sessionManager = new SessionManager(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            member_id = extras.getInt("member_id");
        }

        progressBar = findViewById(R.id.progressBar);
//
        customerName = findViewById(R.id.customer_name);
        title_riwayat = findViewById(R.id.title_riwayat);
        title_riwayat.setText("List Riwayat - ");
//
        RecyclerView recyclerCustomer = findViewById(R.id.recycler_customer);
        recyclerCustomer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list_customer = new ArrayList<>();
        customerAdapter = new RiwayatAdapter(list_customer, getApplicationContext(), this);
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

        Button btnAdd = findViewById(R.id.btn_add_member);
        btnAdd.setOnClickListener(view->{
            Intent intent = new Intent(this, AddMemberActivity.class);
            startActivity(intent);
        });
//
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
        Log.e("URL_",URI.API_RIWAYAT_LIST+"/"+member_id+"/"+sessionManager.getId()+"?page="+param_page+"&keyword="+param_keyword);
        request = new JsonArrayRequest(URI.API_RIWAYAT_LIST+"/"+member_id+"/"+sessionManager.getId()+"?page="+param_page+"&keyword="+param_keyword, response -> {
            JSONObject jsonObject;
            //Log.e("Response", response.toString());
            for (int i = 0; i < response.length(); i++){
                try {
                    jsonObject = response.getJSONObject(i);
                    Riwayat listData = new Riwayat(jsonObject.getInt("id"), jsonObject.getString("sale_no"), jsonObject.getString("sale_date"), jsonObject.getString("pembelian"), jsonObject.getString("type_member"), jsonObject.getString("cust_notes"));
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

    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemSelected(Riwayat item) {
    }

    @Override
    public void onSelected(Riwayat item, String options) {

    }
}