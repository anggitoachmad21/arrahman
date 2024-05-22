package id.latenight.creativepos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.latenight.creativepos.adapter.Member;
import id.latenight.creativepos.adapter.MemberAdapter;
import id.latenight.creativepos.util.SearchableSpinner;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class AddMemberActivity extends AppCompatActivity {
    private List<Member> list_customer;
    private MemberAdapter customerAdapter;
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private SearchableSpinner spinnerCustomer;
    private String param_customer_id = "";
    private EditText name_customer, nomor_wa;

    private ArrayList<String> customerList;

    private ArrayList<String> typeList;

    private Spinner type_member, type_member_list;
    private Button btn_simpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        sessionManager = new SessionManager(this);
        customerList = new ArrayList<>();
        typeList = new ArrayList<>();
        name_customer = findViewById(R.id.name);
        nomor_wa = findViewById(R.id.no_wa);
        type_member = findViewById(R.id.type_member);
        spinnerCustomer = findViewById(R.id.customer);
        spinnerCustomer.setTitle(getResources().getString(R.string.select_customer));
        spinnerCustomer.setNegativeButton("Tambah Plat Nomor", (dialog, which) -> {
            dialog.dismiss();
        });
        spinnerCustomer.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, customerList));

        btn_simpan = findViewById(R.id.btn_simpan);
        btn_simpan.setOnClickListener(view -> {
            simpanMember();
        });

        getTypeMember();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomer();
    }

    private void getTypeMember()
    {
        StringRequest stringRequest = new StringRequest(URI.TYPE_MEMBER, response -> {
            Log.e("RESPONSE", response.toString());
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    typeList.add(jsonObject.getString("name"));
                }
                type_member.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, typeList));

                type_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        //Log.e("Spinner", String.valueOf(list_customer.get(position).getCounter()));

                        getTypeMemberByName(type_member.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getTypeMemberByName(String name)
    {
        StringRequest stringRequest = new StringRequest(URI.TYPE_MEMBER_BY_NAME + "?name="+name, response -> {
            Log.e("RESPONSE", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                TextView price_member = findViewById(R.id.price_member);
                price_member.setText("Rp. " + jsonObject.getString("price"));
                TextView ket_member = findViewById(R.id.ket_member);
                ket_member.setText(jsonObject.getString("description"));
                TextView name_paket = findViewById(R.id.name_paket);
                name_paket.setText("Paket Member " + jsonObject.getString("name"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getCustomer()
    {
        customerList = new ArrayList<>(Arrays.asList(sessionManager.getCustomers().split(",")));
        spinnerCustomer.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, customerList));
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                param_customer_id = spinnerCustomer.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void simpanMember()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.SIMPAN_MEMBER,
                response -> {
                    Toast.makeText(this, "Berhasil Simpan", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MemberListActivity.class);
                    startActivity(intent);
                },
                error -> {
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

                params.put("customer_id", param_customer_id);
                params.put("customer_name", name_customer.getText().toString());
                params.put("nomor_wa", nomor_wa.getText().toString());
                params.put("type_member", type_member.getSelectedItem().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MemberListActivity.class);
        startActivity(intent);
    }
}