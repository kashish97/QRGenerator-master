package androidmads.example.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidmads.example.R;
import androidmads.example.Utils.Upload;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Upload> orderList;


    public TrackAdapter(Context mCtx, List<Upload> orderList) {
        this.mCtx = mCtx;
        this.orderList = orderList;
        setHasStableIds(true);

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_track, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        final Upload cart = orderList.get(position);
        holder.qty.setText("Quantity "+cart.getName());
        holder.dept.setText("Department "+cart.getType());
        holder.date.setText("Date Time Updated "+cart.getDescription());

    }

    @Override
    public int getItemCount () {
        return orderList.size();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView qty, date, dept;

        public ProductViewHolder(View itemView) {
            super(itemView);
            qty = itemView.findViewById(R.id.textView2);
            dept = itemView.findViewById(R.id.textView4);
            date = itemView.findViewById(R.id.textView5);
        }
    }
}
