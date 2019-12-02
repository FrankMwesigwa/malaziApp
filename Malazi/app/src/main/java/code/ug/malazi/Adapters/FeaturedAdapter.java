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

import code.ug.malazi.Models.Property;
import code.ug.malazi.R;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.MyViewHolder> {

    private Context context;
    private List<Property> mFeatured;
    private View.OnClickListener mOnItemClickListener;

    public FeaturedAdapter(Context context, List<Property> mData) {
        this.context = context;
        this.mFeatured = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.featured_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.featuredName.setText(mFeatured.get(position).getName());
        holder.featuredCat.setText(mFeatured.get(position).getCategory());
        holder.featuredPrice.setText(mFeatured.get(position).getPrice() + " UGX");
        //holder.featuredLocation.setText(mFeatured.get(position).getLocation());
        holder.featuredId.setText(mFeatured.get(position).getId());
        Glide.with(context)
                .load(mFeatured.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(holder.featuredImage);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mFeatured.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView featuredName, featuredCat, featuredPrice, featuredId, featuredLocation;
        ImageView featuredImage;

        public MyViewHolder(@NonNull View mView) {
            super(mView);

            featuredName = mView.findViewById(R.id.title);
            featuredCat = mView.findViewById(R.id.catergory);
            featuredPrice = mView.findViewById(R.id.price);
            featuredId = mView.findViewById(R.id.iddd);
            featuredLocation = mView.findViewById(R.id.location);

            featuredImage = mView.findViewById(R.id.imageMain);

            mView.setTag(this);
            mView.setOnClickListener(mOnItemClickListener);

        }
    }

}
