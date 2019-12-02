package code.ug.malazi.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import code.ug.malazi.Models.Catergory;
import code.ug.malazi.R;

public class CatergoryAdapter extends RecyclerView.Adapter<CatergoryAdapter.MyViewHolder> {

    private Context context;
    private List<Catergory> catList;
    private View.OnClickListener mOnItemClickListener;

    public CatergoryAdapter(Context context, List<Catergory> mData) {
        this.context = context;
        this.catList = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.explore_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.featuredName.setText(catList.get(position).getName());
        holder.featuredId.setText(catList.get(position).getId());

        Glide.with(context)
                .load(catList.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(holder.featuredImage);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView featuredName, featuredId;
        ImageView featuredImage;

        public MyViewHolder(View mView) {
            super(mView);

            featuredName = mView.findViewById(R.id.book_title_id);
            featuredId = mView.findViewById(R.id.iddd);

            featuredImage = mView.findViewById(R.id.book_img_id);

            mView.setTag(this);
            mView.setOnClickListener(mOnItemClickListener);

        }
    }

}
