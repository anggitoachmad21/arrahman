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

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> implements Filterable {

    List<Member> customer, filterList;
    Context ct;
    ImageAdapterListener listener;
    CustomFilter filter;

    public MemberAdapter(List<Member> customer, Context ct, ImageAdapterListener listener) {
        this.customer = customer;
        this.filterList = customer;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Member listData = customer.get(position);
        String title_new = listData.getName().toLowerCase();
        String capitalize = capitalizeText(title_new);
        holder.number.setText((position + 1) +".");
        holder.name.setText(capitalize);
        holder.nomor_wa.setText(listData.getNomor_wa());
        holder.plat_no.setText(listData.getPlat_no());
        holder.type_member.setText(listData.getType_member());
        holder.saldo.setText(listData.getSaldo());
        holder.diskon.setText(listData.getDiskon());
        holder.tanggal_mulai.setText(listData.getTanggal_mulai());
        holder.tanggal_akhir.setText(listData.getTanggal_akhir());

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
        private final TextView number, name, nomor_wa, plat_no, type_member, saldo, diskon, tanggal_mulai, tanggal_akhir, edit;
        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            name = itemView.findViewById(R.id.name);
            nomor_wa = itemView.findViewById(R.id.nomor_wa);
            plat_no = itemView.findViewById(R.id.plat_no);
            type_member = itemView.findViewById(R.id.type_member);
            saldo = itemView.findViewById(R.id.saldo);
            diskon = itemView.findViewById(R.id.diskon);
            tanggal_mulai = itemView.findViewById(R.id.tanggal_mulai);
            tanggal_akhir = itemView.findViewById(R.id.tanggal_akhir);
            edit = itemView.findViewById(R.id.edit);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                //listener.onImageSelected(customer.get(getAdapterPosition()));
            });
        }
    }

    public interface ImageAdapterListener {
        void onItemSelected(Member item);
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

        MemberAdapter adapter;
        List<Member> filterList;

        public CustomFilter(List<Member> filterList, MemberAdapter adapter)
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
                List<Member> filteredPlayers = new ArrayList<>();

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

            adapter.customer= (List<Member>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
