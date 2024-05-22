package id.latenight.creativepos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.R;

public class DeleteOrderAdapter extends RecyclerView.Adapter<DeleteOrderAdapter.ViewHolder> implements Filterable {

    List<Order> order, filterList;
    Context ct;
    DeleteOrderAdapterListener listener;
    CustomFilter filter;

    public DeleteOrderAdapter(List<Order> order, Context ct, DeleteOrderAdapterListener listener) {
        this.order = order;
        this.filterList = order;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_order_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Order listData = order.get(position);

        int status = order.get(position).getStatus();
        if(status == 4) {
            holder.lytWrapper.setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.btn_filled_gold));
        }

        NumberFormat formatRupiah = NumberFormat.getInstance();
        double totalPayable = Double.parseDouble(listData.getTotalPayable());
        String rupiah = formatRupiah.format(totalPayable);

        holder.number.setText((position + 1) + ".");
        holder.sale_no.setText(listData.getSaleNo());
        holder.customer.setText(listData.getCustomerName());
        holder.cust_notes.setText(listData.getCustNotes());
        holder.order_time.setText(listData.getOrderTime());
        holder.price.setText(ct.getResources().getString(R.string.currency) +" "+ rupiah.replace(',', '.'));
        holder.order_type.setText(ct.getResources().getString(R.string.queue_no) + " " + listData.getQueueNo());
        holder.category.setText(" - "+  listData.getCategory());
        holder.table_name.setText(listData.getOrderStatus());
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView number, sale_no, customer, order_time, price, order_type, table_name, cust_notes, category;
        private LinearLayout lytWrapper;
        public ViewHolder(View itemView) {
            super(itemView);
            lytWrapper = itemView.findViewById(R.id.lyt_wrapper);
            number = itemView.findViewById(R.id.number);
            sale_no = itemView.findViewById(R.id.sale_no);
            customer = itemView.findViewById(R.id.customer);
            cust_notes = itemView.findViewById(R.id.cust_notes);
            order_time = itemView.findViewById(R.id.order_time);
            price = itemView.findViewById(R.id.price);
            order_type = itemView.findViewById(R.id.order_type);
            table_name = itemView.findViewById(R.id.table_name);
            category = itemView.findViewById(R.id.category);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                listener.onOrderSelected(order.get(getAdapterPosition()));
            });
        }
    }

    public interface DeleteOrderAdapterListener {
        void onOrderSelected(Order item);
    }


    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }

        return filter;
    }

    public class CustomFilter extends Filter{

        DeleteOrderAdapter adapter;
        List<Order> filterList;

        public CustomFilter(List<Order> filterList, DeleteOrderAdapter adapter)
        {
            this.adapter = adapter;
            this.filterList = filterList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length() > 0)
            {
                constraint=constraint.toString().toUpperCase();
                List<Order> filteredPlayers = new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getSaleNo().contains(constraint))
                    {
                        filteredPlayers.add(filterList.get(i));
                    }
                }

                results.count = filteredPlayers.size();
                results.values = filteredPlayers;
            }else
            {
                results.count = filterList.size();
                results.values = filterList;

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.order= (List<Order>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
