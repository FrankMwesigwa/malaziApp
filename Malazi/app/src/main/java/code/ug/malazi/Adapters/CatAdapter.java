package code.ug.malazi.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import code.ug.malazi.Models.Catergory;
import code.ug.malazi.R;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.MyViewHolder> {

    private Context context;
    private List<Catergory> mCat;
    private View.OnClickListener mOnItemClickListener;

    public CatAdapter(Context context, List<Catergory> mData) {
        this.context = context;
        this.mCat = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.cat_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.featuredName.setText(mCat.get(position).getName());
        holder.featuredId.setText(mCat.get(position).getId());

        String firstLetter = String.valueOf(holder.featuredName.getText()).substring(0,1);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(mCat.get(position));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter.toUpperCase(), color);
        holder.featuredImage.setImageDrawable(drawable);

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mCat.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView featuredName, featuredId;
        ImageView featuredImage;

        public MyViewHolder(@NonNull View mView) {
            super(mView);

            featuredName = mView.findViewById(R.id.name);
            featuredId = mView.findViewById(R.id.idd);
            featuredImage = mView.findViewById(R.id.thumbnail);

            mView.setTag(this);
            mView.setOnClickListener(mOnItemClickListener);

        }
    }

}
