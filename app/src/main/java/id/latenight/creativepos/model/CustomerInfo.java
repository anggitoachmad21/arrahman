package id.latenight.creativepos.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import id.latenight.creativepos.adapter.sampler.Menus;
import id.latenight.creativepos.util.DatabaseHandler;

public class CustomerInfo {
    private DatabaseHandler db;
    public int getCustomerInfoPrice(int id, String customer_name, Context context) {
        db = new DatabaseHandler(context);
        Integer[] wash_categories = {1, 2, 4, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        Integer[] car_categories = {1, 2, 4, 9};
        List<Menus> menusList = db.getAllMenuById(id);
        int price = menusList.get(0).getSale_price();
        List<id.latenight.creativepos.adapter.sampler.CustomerInfo> customerInfos = db.getCustomerInfo(customer_name);
        try {
            if (isInArray(wash_categories, menusList.get(0).getSub_categories_id())) {
                if (isInArray(car_categories, menusList.get(0).getSub_categories_id())) {
                    JSONObject customerInfoJson = new JSONObject(customerInfos.get(0).getCustomer_info());
                    if (customerInfoJson.getInt("counter") == 5) {
                        price = 0;
                    }
                }
            }
        }
        catch (Exception e){
        }
        return price;
    }

    public boolean isInArray(Integer[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return true;
            }
        }
        return false;
    }
}
