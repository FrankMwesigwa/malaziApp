package code.ug.malazi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import code.ug.malazi.Activities.AddHouseActivity;
import code.ug.malazi.Activities.AddLandActivity;
import code.ug.malazi.Activities.AddRentActivity;
import code.ug.malazi.Activities.CategoryActivity;
import code.ug.malazi.Activities.ChatActivity;
import code.ug.malazi.Activities.DetailsActivity;
import code.ug.malazi.Activities.ProfileActivity;
import code.ug.malazi.Activities.SearchActivity;
import code.ug.malazi.Adapters.CatergoryAdapter;
import code.ug.malazi.Adapters.FeaturedAdapter;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.Models.Catergory;
import code.ug.malazi.Models.Property;
import es.dmoral.toasty.Toasty;

import static code.ug.malazi.App.AppURLs.CATERGORY;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    RecyclerView mCatergory, mFeatured;
    private ShimmerRecyclerView shimmerRecycler, shimmerRecycler1;
    private List<Property> proList;
    private List<Catergory> catList;
    private CatergoryAdapter catergoryAdapter;
    private FeaturedAdapter featuredAdapter;

    FloatingActionButton actionButtonA, actionButtonB, actionButtonC;
    FloatingActionsMenu floatingActionsMenu;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    private static int k = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        floatingActionsMenu = findViewById(R.id.fab);
        actionButtonA = findViewById(R.id.action_a);
        actionButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddHouseActivity.class);
                startActivity(intent);
            }
        });
        actionButtonB = findViewById(R.id.action_b);
        actionButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRentActivity.class);
                startActivity(intent);
            }
        });
        actionButtonC = findViewById(R.id.action_c);
        actionButtonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddLandActivity.class);
                startActivity(intent);
            }
        });

        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler1 = findViewById(R.id.shimmer_recycler_vieww);

        //Catergory
        mCatergory = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCatergory.setLayoutManager(layoutManager);
        catList = new ArrayList<>();
        catergoryAdapter = new CatergoryAdapter(this, catList);
        mCatergory.setAdapter(catergoryAdapter);
        catergoryAdapter.setOnItemClickListener(onItemClickListener);

        shimmerRecycler.setAdapter(catergoryAdapter);
        shimmerRecycler.setVisibility(View.VISIBLE);
        shimmerRecycler.showShimmerAdapter();
        loadCatergory();


        //Featured
        mFeatured = findViewById(R.id.recycler_view);
        proList = new ArrayList<>();
        featuredAdapter = new FeaturedAdapter(this, proList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mFeatured.setLayoutManager(mLayoutManager);
        mFeatured.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        mFeatured.setItemAnimator(new DefaultItemAnimator());
        mFeatured.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(clickListener);

        shimmerRecycler1.setAdapter(featuredAdapter);
        shimmerRecycler1.setVisibility(View.VISIBLE);
        shimmerRecycler1.showShimmerAdapter();

        mFeatured.setNestedScrollingEnabled(false);

        loadFeatured();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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


    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Catergory thisItem = catList.get(position);
            Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
            intent.putExtra("SESSION_ID", thisItem.getId());
            intent.putExtra("EXTRA_SESSION_NAME", thisItem.getName());
            startActivity(intent);

            //Toast.makeText(MainActivity.this, "You Clicked: " + thisItem.getId(), Toast.LENGTH_SHORT).show();
        }
    };

    public void loadCatergory() {
        JsonArrayRequest movieReq = new JsonArrayRequest(CATERGORY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Catergory movie = new Catergory();
                                movie.setName(obj.getString("category_name"));
                                movie.setImage(obj.getString("image"));
                                movie.setId(obj.getString("category_id"));
                                // adding movie to movies array
                                catList.add(movie);
                                shimmerRecycler.hideShimmerAdapter();
                                shimmerRecycler.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        catergoryAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(movieReq);
    }

    public void loadFeatured() {
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String userID = (sharedpreferences.getString(USERID, ""));

        JsonArrayRequest movieReq = new JsonArrayRequest("https://api.malazi.net/user/"+userID+"/get_properties/featured",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Use: "+userID, response.toString());
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
                                shimmerRecycler1.hideShimmerAdapter();
                                shimmerRecycler1.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        featuredAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(floatingActionsMenu.isExpanded()){
            floatingActionsMenu.collapse();
            finish();
            startActivity(getIntent());
        }

    }

    public void closeApp() {
        ++k;
        if(k==1){
            //do whatever you want to do on first click for example:
            Toasty.info(this, "Press back one more time to exit", Toast.LENGTH_LONG).show();
        }else{
            //do whatever you want to do on the click after the first for example:
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
            intent.putExtra("SESSION_ID", "5e5edc8456cbd3a9391114b1");
            intent.putExtra("EXTRA_SESSION_NAME", "House 4 Rent");
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
            intent.putExtra("SESSION_ID", "22305ba8aeda9da5453bb4ee");
            intent.putExtra("EXTRA_SESSION_NAME", "House 4 sale");
            startActivity(intent);
        } else if (id == R.id.nav_tools) {
            Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
            intent.putExtra("SESSION_ID", "13c7d26f000896941dc0f6f0");
            intent.putExtra("EXTRA_SESSION_NAME", "Land");
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private android.support.v7.widget.SearchView searchView;

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
        //performNewSearch(query);
        searchView.clearFocus();
        System.out.println(query);
        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
        intent.putExtra("SEARCH", query);
        startActivity(intent);

        return true;
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }

//    private void performNewSearch(String query) {
//        System.out.println(query);
//        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
//        intent.putExtra("EXTRA_SESSION_SEARCH", query);
//        startActivity(intent);
//    }

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
