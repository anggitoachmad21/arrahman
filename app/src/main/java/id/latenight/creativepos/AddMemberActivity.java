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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.latenight.creativepos.adapter.Member;
import id.latenight.creativepos.adapter.MemberAdapter;
import id.latenight.creativepos.util.DatabaseHandler;
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

    private String food_name = "";
    private Spinner type_member, type_member_list;

    private EditText alamat;
    private Button btn_simpan;
    private DatabaseHandler db;
    private Integer member_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            member_id = extras.getInt("member_id");
            getDetailsMember();
        }

        sessionManager = new SessionManager(this);
        db = new DatabaseHandler(this);
        customerList = new ArrayList<>();
        typeList = new ArrayList<>();
        name_customer = findViewById(R.id.name);
        nomor_wa = findViewById(R.id.no_wa);
        type_member = findViewById(R.id.type_member);
        alamat = findViewById(R.id.alamat);
        spinnerCustomer = findViewById(R.id.customer);
        spinnerCustomer.setTitle(getResources().getString(R.string.select_customer));
        spinnerCustomer.setNegativeButton("Tambah Plat Nomor", (dialog, which) -> {
            dialog.dismiss();
        });
        spinnerCustomer.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, customerList));

        btn_simpan = findViewById(R.id.btn_simpan);
        Button btn_kembali = findViewById(R.id.btn_kembali);
        btn_simpan.setOnClickListener(view -> {
            simpanMember();
        });

        if(member_id != 0)
        {
//            btn_kembali.setVisibility(View.GONE);
            btn_simpan.setVisibility(View.GONE);
        }

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
                food_name = jsonObject.getString("id");
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
        if (member_id == 0) {
            spinnerCustomer.setFocusable(false);
        }
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                param_customer_id = spinnerCustomer.getSelectedItem().toString();
                getTypeCarByName(spinnerCustomer.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void getDetailsMember()
    {
        Log.e("URL", URI.DETAIL_MEMBER + "/"+member_id);
        StringRequest stringRequest = new StringRequest(URI.DETAIL_MEMBER + "/"+member_id, response -> {
            Log.e("RESPONSE", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                spinnerCustomer.setSelection(customerList.indexOf(jsonObject.getString("customer_name")));
                type_member.setSelection(typeList.indexOf(jsonObject.getString("member_name")));
                getTypeCarByName(jsonObject.getString("customer_name"));
                getTypeMemberByName(jsonObject.getString("member_name"));
                name_customer.setText(jsonObject.getString("name"));
                alamat.setText(jsonObject.getString("alamat"));
                nomor_wa.setText(jsonObject.getString("nomor_wa"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getTypeCarByName(String name)
    {
        StringRequest stringRequest = new StringRequest(URI.TYPE_CAR_BY_NAME + "?name="+name, response -> {
            Log.e("RESPONSE", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                EditText car = findViewById(R.id.car);
                car.setText(jsonObject.getString("car_type"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void simpanMember()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, URI.SIMPAN_MEMBER,
                response -> {
            String member_id = "1";
                    payOrder(member_id);
//                    Log.e("RESPONSE", response.toString());
//                    Toast.makeText(this, "Berhasil Simpan", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(this, MemberListActivity   .class);
//                    startActivity(intent);
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
                params.put("alamat", alamat.getText().toString());
                params.put("nomor_wa", nomor_wa.getText().toString());
                params.put("type_member", type_member.getSelectedItem().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    @SuppressLint("SetTextI18n")
    private void payOrder(String member_id) {
        String URL_POST;
        URL_POST = URI.API_PLACE_ORDER;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_POST,
                response -> {
                    Log.e("RESPONSE ", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success) {
                            db.addSales(jsonObject.getString("sale_no"), jsonObject.getString("sales_information"));
                            showSuccess(jsonObject.getString("message"));
                            Intent intent = new Intent(this, OrderDetailActivity.class);
                            intent.putExtra("order_id", jsonObject.getString("sale_no"));
                            startActivity(intent);
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
                // the POST parameters:

                String customer_id = param_customer_id;
                String waiter_id = sessionManager.getId();
                String orders_table;
                if(sessionManager.getOutlet().equals("3")) {
                    orders_table = "VIP 1";
                } else {
                    orders_table = "1";
                }
                String value_shipping = "";
                String value_notes = "";

                JSONArray array=new JSONArray();
                JSONObject obj=new JSONObject();
                try {
                    obj.put("id",food_name);
                    obj.put("qty", 1);
                    array.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TextView price_member = findViewById(R.id.price_member);
                String price = price_member.getText().toString();
                params.put("user_id", sessionManager.getId());
                params.put("customer_id", customer_id);
                params.put("waiter_id", waiter_id);
                params.put("payment_type_member", "1");
                params.put("order_type", "1");
                params.put("sub_total", price.replace("Rp. ", ""));
                params.put("orders_table", orders_table);
                params.put("discount", "0");
                params.put("logistic", value_shipping);
                params.put("notes", value_notes);
                params.put("total_items_in_cart", "1");
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

    private void showSuccess(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    private void showError(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MemberListActivity.class);
        startActivity(intent);
    }
}