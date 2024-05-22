package id.latenight.creativepos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.latenight.creativepos.util.MyApplication;
import id.latenight.creativepos.util.SessionManager;
import id.latenight.creativepos.util.URI;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager session;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog loadingDialog;
    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(this);

        if(session.getEnablePrinter().equals("")) {
            session.setEnablePrinter("on");
        }

        if(session.getEnablePrinter().equals("on")) {
            session.setEnablePrinter("on");
        }

        if(!session.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        RadioButton cashier = findViewById(R.id.cashier);
        RadioButton orderHistory = findViewById(R.id.order_history);
        RadioButton customerHistory = findViewById(R.id.customer_history);
        RadioButton admin = findViewById(R.id.admin);
        RadioButton member = findViewById(R.id.member);

        cashier.setOnClickListener(this);
        orderHistory.setOnClickListener(this);
        admin.setOnClickListener(this);
        customerHistory.setOnClickListener(this);
        member.setOnClickListener(this);

        //session.setOpenRegistration("0");
        if(session.getOpenRegistration().isEmpty() || session.getOpenRegistration().equals("0")) {
            checkOpenRegistration(session.getId());
            Log.e("cek balance", session.getOpenRegistration());
        } else {
            if(!session.getOpenRegistration().equals("1")) {
                Intent intent = new Intent(this, OpenRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }

        int isConnected = MyApplication.getApplication().isConnected();
        if(!session.getPrinter().isEmpty()) {
            if(isConnected != 3) {
                MyApplication.getApplication().setupBluetoothConnection(session.getPrinter());
                Log.e("Check Connection", String.valueOf(isConnected));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        registrationFCM();
    }

    private void registrationFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);

                    StringRequest postRequest = new StringRequest(Request.Method.POST, URI.SAVE_TOKEN_FCM,
                            response -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                } catch (JSONException e) {
                                }
                            },
                            error -> {
                                error.printStackTrace();

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

                            params.put("user_id", session.getId());
                            params.put("token", token);
                            return params;
                        }
                    };
                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(getApplicationContext()).add(postRequest);

                });

        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("admin")
                .addOnCompleteListener(task -> {
                    String msg = getString(R.string.msg_subscribed);
                    if (!task.isSuccessful()) {
                        msg = getString(R.string.msg_subscribe_failed);
                    }
                    Log.d(TAG, msg);
                });
        // [END subscribe_topics]
    }

    public void showLoading() {
        loadingDialog = ProgressDialog.show(this, "",
                "Mohon tunggu...", true);
        loadingDialog.show();
    }
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    private void checkOpenRegistration(String id) {
        showLoading();
        StringRequest stringRequest = new StringRequest(URI.CHECK_OPEN_REGISTER+id, response -> {
            if(response.equals("0")) {
                Intent intent = new Intent(this, OpenRegisterActivity.class);
                startActivity(intent);
                finish();
            }
            session.setOpenRegistration(response);
            hideLoading();
        }, error -> {
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.cashier) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.order_history) {
            intent = new Intent(this, OrderHistoryActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.customer_history) {
            intent = new Intent(this, CustomerListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.member) {
            intent = new Intent(this, MemberListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.admin) {
            intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
    }

    public void logout(View view) {
        session.setLogin(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.double_click_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}
