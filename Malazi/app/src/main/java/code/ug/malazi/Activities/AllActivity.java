package code.ug.malazi.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.Adapters.FeaturedAdapter;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.MainActivity;
import code.ug.malazi.Models.Property;
import code.ug.malazi.R;

public class AllActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    RecyclerView mRecyelerView;
    private List<Property> proList;
    private FeaturedAdapter featuredAdapter;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    private ShimmerRecyclerView shimmerRecycler;
    private android.support.v7.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        String catId = getIntent().getStringExtra("CAT_ID");
        String name  = getIntent().getStringExtra("CAT_NAME");
        setTitle(name + "Properties");

        mRecyelerView = findViewById(R.id.recyclerView);
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);



        proList = new ArrayList<>();
        featuredAdapter = new FeaturedAdapter(this, proList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyelerView.setLayoutManager(mLayoutManager);
        mRecyelerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        mRecyelerView.setItemAnimator(new DefaultItemAnimator());
        mRecyelerView.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(clickListener);

        shimmerRecycler.setAdapter(featuredAdapter);
        shimmerRecycler.setVisibility(View.VISIBLE);
        shimmerRecycler.showShimmerAdapter();

        mRecyelerView.setNestedScrollingEnabled(false);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));

        JsonArrayRequest movieReq = new JsonArrayRequest("https://api.malazi.net/user/"+userID+"/get_properties/" + catId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("", response.toString());
                        if (response.toString().equals("[]")){
                            new SweetAlertDialog(AllActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("No Properties found")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Intent intent = new Intent(AllActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }else {
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

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Property thisItem = proList.get(position);
            Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
            intent.putExtra("EXTRA_SESSION_ID", thisItem.getId());
            intent.putExtra("EXTRA_SESSION_NAME", thisItem.getName());
            intent.putExtra("EXTRA_SESSION", "");
            startActivity(intent);

            //Toast.makeText(MainActivity.this, "You Clicked: " + thisItem.getId(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search)
                .getActionView(); // set the reference to the searchView
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search....");
        searchView.clearFocus();
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        performNewSearch(query);
        searchView.clearFocus();
        System.out.println(query);
        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
        intent.putExtra("EXTRA_SESSION_SEARCH", query);
        startActivity(intent);

        return true;
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void performNewSearch(String query) {
        System.out.println(query);
        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
        intent.putExtra("EXTRA_SESSION_SEARCH", query);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
