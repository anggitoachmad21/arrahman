package id.latenight.creativepos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.R;

public class CustomerSalesAdapter extends RecyclerView.Adapter<CustomerSalesAdapter.ViewHolder> implements Filterable {

    List<CustomerSales> customer, filterList;
    Context ct;
    ImageAdapterListener listener;
    CustomFilter filter;

    public CustomerSalesAdapter(List<CustomerSales> customer, Context ct, ImageAdapterListener listener) {
        this.customer = customer;
        this.filterList = customer;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_sales_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CustomerSales listData = customer.get(position);

        NumberFormat formatRupiah = NumberFormat.getInstance();
        String rupiah = formatRupiah.format(listData.getTotalPayable());

        holder.number.setText((position + 1) +".");
        holder.sale_no.setText("INV"+listData.getSaleNo());
        holder.sale_date.setText(listData.getSaleDate());
        holder.total_payable.setText(ct.getResources().getString(R.string.currency) +" "+ rupiah.replace(',', '.'));
        holder.washer1.setText(String.valueOf(listData.getWasher1()));
        holder.washer2.setText(listData.getWasher2());
    }

    @Override
    public int getItemCount() {
        return customer.size();
    }

    @Override public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView number, sale_no, sale_date, total_payable, washer1, washer2;
        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            sale_no = itemView.findViewById(R.id.sale_no);
            sale_date = itemView.findViewById(R.id.sale_date);
            total_payable = itemView.findViewById(R.id.total_payable);
            washer1 = itemView.findViewById(R.id.washer1);
            washer2 = itemView.findViewById(R.id.washer2);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                //listener.onImageSelected(customer.get(getAdapterPosition()));
            });
        }
    }

    public interface ImageAdapterListener {
        void onItemSelected(CustomerSales item);
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

        CustomerSalesAdapter adapter;
        List<CustomerSales> filterList;

        public CustomFilter(List<CustomerSales> filterList, CustomerSalesAdapter adapter)
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
                List<CustomerSales> filteredPlayers = new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getSaleNo().toUpperCase().contains(constraint))
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

            adapter.customer= (List<CustomerSales>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
