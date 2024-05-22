package id.latenight.creativepos.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import id.latenight.creativepos.R;
import id.latenight.creativepos.util.URI;

import static id.latenight.creativepos.util.CapitalizeText.capitalizeText;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    List<Product> product, filterList;
    Context ct;
    ImageAdapterListener listener;
    CustomFilter filter;

    public ProductAdapter(List<Product> product, Context ct, ImageAdapterListener listener) {
        this.product = product;
        this.filterList = product;
        this.ct = ct;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product listData = product.get(position);
        Glide
        .with(ct)
        .load(URI.PATH_IMAGE+listData.getImageurl())
        .centerCrop()
        .into(holder.img);

        String title_new = listData.getTitle().toLowerCase();
        String capitalize = capitalizeText(title_new);
        holder.title.setText(Html.fromHtml(capitalize));

    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title_view);

            itemView.setOnClickListener(view -> {
                // send selected berita in callback
                listener.onImageSelected(product.get(getAdapterPosition()));
            });
        }
    }

    public interface ImageAdapterListener {
        void onImageSelected(Product item);
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

        ProductAdapter adapter;
        List<Product> filterList;

        public CustomFilter(List<Product> filterList, ProductAdapter adapter)
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
                List<Product> filteredPlayers = new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getTitle().toUpperCase().contains(constraint))
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

            adapter.product= (List<Product>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}
