package id.latenight.creativepos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import id.latenight.creativepos.R;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<Cart> item;
    private final Context ct;
    AdapterListener listener;
    private Cart listData;
    int product_quantity = 1;
    String angka;
    private final NumberFormat formatRupiah;

    public CartAdapter(List<Cart> item, Context ct, AdapterListener listener) {
        this.item = item;
        this.ct = ct;
        this.listener = listener;
        this.formatRupiah = NumberFormat.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        listData = item.get(position);
        Log.e("ORI PRICE: ", String.valueOf(listData.getOriPrice()));

        String title_new = listData.getName().toLowerCase();
        String capitalize = capitalizeText(title_new);
        holder.name.setText(capitalize);
        String total_price = formatRupiah.format(listData.getOriPrice()*listData.getQty()).replace(',', '.');
        holder.price.setText(ct.getResources().getString(R.string.currency) +" "+ total_price);

        if (holder.qty.getTag() instanceof TextWatcher) {
            holder.qty.removeTextChangedListener((TextWatcher) holder.qty.getTag());
        }
        holder.qty.setText(String.valueOf(listData.getQty()));
        TextWatcher watcher = new TextWatcher() {
            int product_quantity = 1;
            String angka;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    angka = "1";
                } else {
                    angka = String.valueOf(s);
                }
                product_quantity = Integer.parseInt(angka);
                item.get(position).setQty(product_quantity);
                int new_price = item.get(position).getOriPrice() * product_quantity;
                Log.e("TOTAL ITEM: ", item.get(position).getName() +" "+ item.get(position).getQty());
                listData.setPrice(new_price);
                listener.onUpdatePrice(grandTotal(new_price));
                String rupiah = formatRupiah.format(new_price).replace(',', '.');
                holder.price.setText(ct.getResources().getString(R.string.currency) + " " + rupiah);
            }
        };
        holder.qty.addTextChangedListener(watcher);
        holder.qty.setTag(watcher);

        holder.btnDelete.setOnClickListener(view -> {
            int product_quantity = 1;
            try {
                product_quantity = Integer.parseInt(holder.qty.getText().toString());
            } catch(Exception e) {
                product_quantity = 1;
            }
            item.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position,item.size());
            listener.onRemovePrice(item);
            holder.qty.clearFocus();
        });
    }

    private int grandTotal(int price){
        return price;
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView price;
        private final EditText qty;
        private final ImageButton btnPlus;
        private final ImageButton btnMinus;
        private final ImageButton btnDelete;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface AdapterListener {
        void onUpdatePrice(int grandTotal);

        void onRemovePrice(List<Cart> item);
    }
}
