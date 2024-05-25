package id.latenight.creativepos.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberAutoUpdate {
    private  String finish;
    public String updated(Context commons)
    {
        finish = "finish";
        StringRequest stringRequest = new StringRequest(URI.CHECK_AND_UPDATE, response -> {
//            try {
//                JSONObject jsonObject = new JSONObject(response);
                Log.e("RESPONSE CHECK OTOMATIS", response);
                finish = "finish";
//            } catch (JSONException e) {
//                finish = "cannot";
//                throw new RuntimeException(e);
//            }
        }, error -> {
            finish = "cannot";
        });
        RequestQueue requestQueue = Volley.newRequestQueue(commons);
        requestQueue.add(stringRequest);
        return finish;
    }
}
