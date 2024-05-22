package id.latenight.creativepos.adapter;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.R;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> implements Filterable {

    List<Customer> customer, filterList;
    Context ct;
    ImageAdapterListener listener;
    CustomFilter filter;

    public CustomerAdapter(List<Customer> customer, Context ct, ImageAdapterListener listener) {
        this.customer = customer;
        this.filterList = customer;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Customer listData = customer.get(position);
        String title_new = listData.getName().toLowerCase();
        String capitalize = capitalizeText(title_new);
        holder.number.setText((position + 1) +".");
        holder.name.setText(capitalize);
        holder.type.setText(listData.getType());
        holder.counter.setText(listData.getCounter()+"x");
        holder.total_redeem.setText(listData.getTotalRedeem()+"x");
        holder.total_visit.setText(listData.getTotalVisit()+"x");
        holder.last_visit.setText(listData.getLastVisit());

        holder.edit.setOnClickListener(view -> {
            listener.onItemSelected(listData);
        });
    }

    @Override
    public int getItemCount() {
        return customer.size();
    }

    @Override public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView number, name, type, counter, total_redeem, total_visit, last_visit, edit;
        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            counter = itemView.findViewById(R.id.counter);
            total_redeem = itemView.findViewById(R.id.total_redeem);
            total_visit = itemView.findViewById(R.id.total_visit);
            last_visit = itemView.findViewById(R.id.last_visit);
            edit = itemView.findViewById(R.id.edit);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                //listener.onImageSelected(customer.get(getAdapterPosition()));
            });
        }
    }

    public interface ImageAdapterListener {
        void onItemSelected(Customer item);
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

        CustomerAdapter adapter;
        List<Customer> filterList;

        public CustomFilter(List<Customer> filterList, CustomerAdapter adapter)
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
                List<Customer> filteredPlayers = new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getName().toUpperCase().contains(constraint))
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

            adapter.customer= (List<Customer>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
