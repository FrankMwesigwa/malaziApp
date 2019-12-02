package code.ug.malazi.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.Adapters.FeaturedAdapter;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.Helpers.Session;
import code.ug.malazi.MainActivity;
import code.ug.malazi.Models.Property;
import code.ug.malazi.R;

public class ProfileActivity extends AppCompatActivity {

    TextView postsNum, favNum, mName, mEmail, mPhone, mType;
    ImageView proPic;
    RecyclerView mPosts, mLiked;
    RelativeLayout myPosts, myFav;
    private List<Property> proList;
    private FeaturedAdapter featuredAdapter;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    private ShimmerRecyclerView shimmerRecycler;
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new Session(ProfileActivity.this);

        postsNum = findViewById(R.id.postsNum);
        favNum = findViewById(R.id.favNum);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        proPic = findViewById(R.id.imageView);
        mType = findViewById(R.id.dataTyp);

        myPosts = findViewById(R.id.post_fav);
        myFav = findViewById(R.id.liked_fav);

        mPosts = findViewById(R.id.recycler_posts);
        mLiked = findViewById(R.id.recycler_best);
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);

        loadPosts();

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.malazi.net/user/" + userID,
                (String) null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String name = response.getString("full_name");
                    String email = response.getString("email");
                    String phone = response.getString("phone");
                    String prPic = response.getString("image");

                    mName.setText(name);
                    mEmail.setText("Email: "+ email);

                    Glide.with(ProfileActivity.this)
                            .load(prPic)
                            .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_launcher_round))
                            .into(proPic);


                    if (phone.equals("null")){
                        mPhone.setText("No Phone Number!");
                    }else {
                        mPhone.setText("Phone: "+ phone);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Try Again")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);


    }

    @SuppressLint("ResourceAsColor")
    public void loadPosts()  {
        myFav.setBackgroundColor(Color.TRANSPARENT);
        myPosts.setBackgroundColor(R.color.bags);
        mType.setText("My Posts");
        proList = new ArrayList<>();
        featuredAdapter = new FeaturedAdapter(this, proList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mPosts.setLayoutManager(mLayoutManager);
        mPosts.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        mPosts.setItemAnimator(new DefaultItemAnimator());
        mPosts.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(mClickListener);

        shimmerRecycler.setAdapter(featuredAdapter);
        shimmerRecycler.setVisibility(View.VISIBLE);
        shimmerRecycler.showShimmerAdapter();

        mPosts.setNestedScrollingEnabled(false);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        String userID = (sharedpreferences.getString(USERID, ""));

        JsonArrayRequest movieReq = new JsonArrayRequest("https://api.malazi.net/user/"+userID+"/my_properties",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("", response.toString());
                        if (response.toString().equals("[]")){
                            new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("No Properties")
                                    .show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Property movie = new Property();
                                    movie.setId(obj.getString("property_id"));
                                    movie.setName(obj.getString("title"));
                                    movie.setImage(obj.getString("image"));
                                    movie.setPrice(obj.getString("price"));
                                    //movie.setLocation(obj.getString("location"));
                                    movie.setCategory(obj.getString("category"));
                                    // adding movie to movies array
                                    proList.add(movie);
                                    shimmerRecycler.hideShimmerAdapter();
                                    shimmerRecycler.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            featuredAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(movieReq);
    }


    public void loadPosts(View view) {
        finish();
        startActivity(getIntent());
    }

    @SuppressLint("ResourceAsColor")
    public void liked(View view) {
        myFav.setBackgroundColor(R.color.bags);
        myPosts.setBackgroundColor(Color.TRANSPARENT);
        mType.setText("My Favourites");
        proList = new ArrayList<>();
        featuredAdapter = new FeaturedAdapter(this, proList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mLiked.setLayoutManager(layoutManager);
        mLiked.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        mLiked.setItemAnimator(new DefaultItemAnimator());
        mLiked.setAdapter(featuredAdapter);
        mPosts.setVisibility(View.GONE);
        mLiked.setVisibility(View.VISIBLE);

        featuredAdapter.setOnItemClickListener(clickListener);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));

        JsonArrayRequest movieReq = new JsonArrayRequest("https://api.malazi.net/user/"+userID+"/my_favourites",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("", response.toString());
                        if (response.equals("[]")){
                            new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("No Properties")
                                    .show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Property movie = new Property();
                                    movie.setId(obj.getString("property_id"));
                                    movie.setName(obj.getString("title"));
                                    movie.setImage(obj.getString("image"));
                                    movie.setPrice(obj.getString("price"));
                                    //movie.setLocation(obj.getString("location"));
                                    movie.setCategory(obj.getString("category"));
                                    // adding movie to movies array
                                    proList.add(movie);
                                    shimmerRecycler.hideShimmerAdapter();
                                    shimmerRecycler.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            featuredAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(movieReq);

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Property thisItem = proList.get(position);
            Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
            intent.putExtra("EXTRA_SESSION_ID", thisItem.getId());
            intent.putExtra("EXTRA_SESSION_NAME", thisItem.getName());
            intent.putExtra("EXTRA_SESSION", "myProperty");
            startActivity(intent);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Property thisItem = proList.get(position);
            Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
            intent.putExtra("EXTRA_SESSION_ID", thisItem.getId());
            intent.putExtra("EXTRA_SESSION_NAME", thisItem.getName());
            intent.putExtra("EXTRA_SESSION", "remove");
            startActivity(intent);

            //Toast.makeText(MainActivity.this, "You Clicked: " + thisItem.getId(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_out) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void logoutUser() {
        session.setLogin(false);
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
