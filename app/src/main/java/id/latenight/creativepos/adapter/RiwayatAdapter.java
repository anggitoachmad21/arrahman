package id.latenight.creativepos.adapter;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.latenight.creativepos.R;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> implements Filterable {

    List<Riwayat> customer, filterList;
    Context ct;
    ImageAdapterListener listener;
    CustomFilter filter;

    public RiwayatAdapter(List<Riwayat> customer, Context ct, ImageAdapterListener listener) {
        this.customer = customer;
        this.filterList = customer;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.riwayat_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Riwayat listData = customer.get(position);
        holder.number.setText((position + 1) +".");
        holder.sales_no.setText(listData.getSales_no());
        holder.sales_date.setText(listData.getSales_date());
        holder.payout.setText(listData.getName_type());
        holder.type_member.setText(listData.getType_member());
        holder.type.setText(listData.getType());
    }

    @Override
    public int getItemCount() {
        return customer.size();
    }

    @Override public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView number, sales_no, sales_date, payout, type_member, type;
        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            sales_date = itemView.findViewById(R.id.sales_date);
            sales_no = itemView.findViewById(R.id.sales_no);
            payout = itemView.findViewById(R.id.payout);
            type_member = itemView.findViewById(R.id.type_member);
            type = itemView.findViewById(R.id.type);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                //listener.onImageSelected(customer.get(getAdapterPosition()));
            });
        }
    }

    public interface ImageAdapterListener {
        void onItemSelected(Riwayat item);
        void onSelected(Riwayat item, String options);
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

        RiwayatAdapter adapter;
        List<Riwayat> filterList;

        public CustomFilter(List<Riwayat> filterList, RiwayatAdapter adapter)
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
                List<Riwayat> filteredPlayers = new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getSales_no().toUpperCase().contains(constraint))
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

            adapter.customer= (List<Riwayat>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
