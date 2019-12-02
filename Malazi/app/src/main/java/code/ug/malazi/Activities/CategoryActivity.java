package code.ug.malazi.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import code.ug.malazi.Adapters.CatAdapter;
import code.ug.malazi.Adapters.CatergoryAdapter;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.Models.Catergory;
import code.ug.malazi.R;

import static code.ug.malazi.App.AppURLs.CATERGORY;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Catergory> catList;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    private CatAdapter catAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        String sessionId = getIntent().getStringExtra("SESSION_ID");
        String sessionName = getIntent().getStringExtra("EXTRA_SESSION_NAME");
        setTitle(sessionName);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));

        catList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        catList = new ArrayList<>();
        catAdapter = new CatAdapter(this, catList);
        recyclerView.setAdapter(catAdapter);
        catAdapter.setOnItemClickListener(onItemClickListener);

        JsonArrayRequest movieReq = new JsonArrayRequest("https://api.malazi.net/admin/get_subcategories/" + sessionId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Catergory movie = new Catergory();
                                movie.setName(obj.getString("subcategory"));
                                movie.setId(obj.getString("subcategory_id"));
                                // adding movie to movies array
                                catList.add(movie);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        catAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(movieReq);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Catergory thisItem = catList.get(position);
            Intent intent = new Intent(getBaseContext(), AllActivity.class);
            intent.putExtra("CAT_ID", thisItem.getId());
            intent.putExtra("CAT_NAME", thisItem.getName());
            startActivity(intent);

            //Toast.makeText(MainActivity.this, "You Clicked: " + thisItem.getId(), Toast.LENGTH_SHORT).show();
        }
    };

}
