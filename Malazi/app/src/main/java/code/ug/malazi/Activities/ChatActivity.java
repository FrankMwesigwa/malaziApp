package code.ug.malazi.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import code.ug.malazi.Adapters.ContactsAdapter;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.Helpers.MyDividerItemDecoration;
import code.ug.malazi.Models.Contact;
import code.ug.malazi.R;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    RecyclerView recyclerView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    public static final String MESSAGE = "msgIg";
    private List<Contact> contactList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));


        recyclerView = findViewById(R.id.recyclerView);
        contactList = new ArrayList<>();
        mAdapter = new ContactsAdapter(this, contactList, this);
        System.out.println(userID);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        JsonArrayRequest request = new JsonArrayRequest("https://api.malazi.net/user/"+ userID +"/get_message_list",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }else {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Contact items = new Contact();
                                    items.setName(obj.getJSONObject("reciver").getString("name"));
                                    items.setMsgId(obj.getString("message_id"));
                                    items.setImage(obj.getJSONObject("reciver").getString("image"));
                                    items.setrName(obj.getString("sender_name"));
                                    // adding contacts to contacts list
                                    contactList.add(items);
//                                    contactList.clear();
//                                    contactList.addAll((Collection<? extends Contact>) items);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // refreshing recycler view
                            mAdapter.notifyDataSetChanged();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toasty.error(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onContactSelected(Contact contact) {
        Intent intent = new Intent(getBaseContext(), MainChatActivity.class);
        intent.putExtra("MESSAGE", contact.getName());
        intent.putExtra("MESSAGE_ID", contact.getMsgId());
        intent.putExtra("MESSAGE_NAME", contact.getrName());
        startActivity(intent);
    }
}
