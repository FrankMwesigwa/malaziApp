package code.ug.malazi.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.App.MyApplication;
import code.ug.malazi.MainActivity;
import code.ug.malazi.R;
import code.ug.malazi.Util.HttpCall;
import code.ug.malazi.Util.HttpRequest;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class DetailsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView mTitle, mLocation, mCategory, mPrice, mDescription, mPostaName, mPosterContact;
    ImageView mPic;
    Button mChat, mRFav, mBook;
    CarouselView carouselView;
    LinearLayout mRemove;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    public static final String CHATID = "chatid";
    String[] sampleNetworkImageURLs = {
    };
    private int mYear, mMonth, mDay;
    String dateS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        String sessionName = getIntent().getStringExtra("EXTRA_SESSION_NAME");
        String xtehb = getIntent().getStringExtra("EXTRA_SESSION");
        setTitle(sessionName);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mTitle = findViewById(R.id.name);
        mLocation = findViewById(R.id.location);
        mCategory = findViewById(R.id.catergory);
        mPrice = findViewById(R.id.price);
        mDescription = findViewById(R.id.desc);
        mPostaName = findViewById(R.id.username);
        mPosterContact = findViewById(R.id.email);
        mChat = findViewById(R.id.btn_follow);
        carouselView =  findViewById(R.id.carouselView);
        mPic = findViewById(R.id.image_profile);
        //mBook = findViewById(R.id.booking);
        mRFav = findViewById(R.id.removeFav);
        mRemove = findViewById(R.id.beats);

        if(xtehb.equals("myProperty")){
            mRemove.setVisibility(View.GONE);
            mChat.setVisibility(View.INVISIBLE);
        } else if (xtehb.equals("")){
            mRFav.setVisibility(View.GONE);
        } else if (xtehb.equals("remove")){
            mRemove.setVisibility(View.GONE);
        }

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });

        carouselView.setSlideInterval(4000);
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                //Toast.makeText(CarouselActivity.this, "Clicked item: "+ position, Toast.LENGTH_SHORT).show();
            }
        });

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.malazi.net/user/"+ userID +"/get_property/" + sessionId,
                (String) null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String name = response.getString("title");
                    String desc = response.getString("description");
                    String price = response.getString("price");
                    String city = response.getString("city");
                    String address = response.getString("address");
                    String cat = response.getString("category");
                    String subCat = response.getString("sub_category");
                    String imgz = response.getString("images");

                    String pPic = response.getJSONObject("posted_by").getString("image");
                    String pName = response.getJSONObject("posted_by").getString("name");
                    String pEmail = response.getJSONObject("posted_by").getString("email");

                    mTitle.setText(name);
                    mLocation.setText("Location: "+ address+ ", "+ city);
                    mCategory.setText("Category: "+ cat +"/ "+ subCat);
                    mPrice.setText(price + " UGX");
                    mDescription.setText(desc);
                    Log.d("Test 1", imgz);

                    JSONArray jsonArray = new JSONArray(imgz);
                    String[] strArr = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        strArr[i] = jsonArray.getString(i);
                        sampleNetworkImageURLs = strArr;
                        System.out.println("hg :"+Arrays.toString(sampleNetworkImageURLs));
                        loadArray();
                    }

                    mPostaName.setText(pName);
                    mPosterContact.setText(pEmail);
                    Glide.with(DetailsActivity.this)
                            .load(pPic)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mPic);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Try Again")
                        .show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void loadArray(){
        carouselView.setPageCount(sampleNetworkImageURLs.length);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.with(getApplicationContext())
                    .load(sampleNetworkImageURLs[position])
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit().centerCrop()
                    .into(imageView);
        }
    };


    public void sendMsg(){
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.malazi.net/user/"+ userID +"/send_message/" + sessionId,
                (String) null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Tesdt", response.toString());
                try {
                    //JSONObject jObj = new JSONObject(response);
                    boolean error = response.getBoolean("error");
                    final String msg = response.getString("message_id");
                    final String chaterName = response.getString("sender_name");

                    if (!error) {
                        hideDialog();
                        Toasty.success(DetailsActivity.this, "Chat Initiated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), MainChatActivity.class);
                        intent.putExtra("MESSAGE", mPostaName.getText().toString());
                        intent.putExtra("MESSAGE_ID", msg);
                        intent.putExtra("MESSAGE_NAME", chaterName);
                        startActivity(intent);
                    } else {
                        //Log.i("VOLLEY", response);
                        Log.i("MESSAGE", msg);
                        new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(msg)
                                .setContentText("Chat not failed")
                                .show();
                        Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
                    new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Failed")
                            .setContentText("Chat not failed")
                            .show();
                    Log.d("Failure", response.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Try Again")
                        .show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void remove (View view){
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        //api.malazi.net/user/{user_id}/book_property
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.malazi.net/user/"+ userID +"/remove_favourite/" + sessionId,
                (String) null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Successfully")
                        .setContentText("Removed From Favourites")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Intent intent = new Intent(DetailsActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Try Again")
                        .show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void favour (View view){

        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        //api.malazi.net/user/{user_id}/book_property
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.malazi.net/user/"+ userID +"/add_favourite/" + sessionId,
                (String) null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Successfully")
                        .setContentText("Added to Favourites")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .show();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Try Again")
                        .show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);


    }

    @SuppressLint("StaticFieldLeak")
    public void book (View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.book,null);
        final EditText txtun = dialog_layout.findViewById(R.id.txtPeriod);
        final EditText txtDate = dialog_layout.findViewById(R.id.txtPeriod1);

        builder.setView(dialog_layout)

                .setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
                        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                        String userID = (sharedpreferences.getString(USERID, ""));
                        progressDialog.setMessage("Booking Property...");
                        showDialog();
                        RequestParams params = new RequestParams();
                        params.put("property_id", sessionId);
                        params.put("start_date", txtDate.getText().toString());
                        params.put("period", txtun.getText().toString());
                        Log.d("Params ", String.valueOf(params));
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post("https://api.malazi.net/user/"+ userID+ "/book_property", params, new JsonHttpResponseHandler(){

                            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                                Log.d("Clima", "Sucesss Json: " + response.toString());
                                try {
                                    //JSONObject jObj = new JSONObject(response);
                                    boolean error = response.getBoolean("error");
                                    String msg = response.getString("message");

                                    if (!error) {

                                        Toasty.success(DetailsActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                                        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(msg)
                                                .show();
                                        hideDialog();
                                        String errorMsg = response.getString("error_msg");
                                        Toast.makeText(getApplicationContext(),
                                                errorMsg, Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    hideDialog();
                                    new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Something Went Wrong Trying Again")
                                            .show();
                                    Log.d("Failure", response.toString());
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                Log.d("Status 1", String.valueOf(statusCode));
                                Log.d("Status 2", String.valueOf(headers));
                                Log.d("Status 3", String.valueOf(throwable));
                                Log.d("Status 4", String.valueOf(errorResponse));
                                hideDialog();
                                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed")
                                        .setContentText(String.valueOf(errorResponse))
                                        .show();

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Log.d("Fail 1", String.valueOf(statusCode));
                                Log.d("Fail 2", String.valueOf(headers));
                                Log.d("Fail 3", String.valueOf(throwable));
                                Log.d("Fail 4", responseString);
                                hideDialog();
                                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed")
                                        .setContentText(responseString)
                                        .show();
                            }

                            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                                super.onFailure(statusCode, headers, e, response);
                                Log.d("Hell 1", String.valueOf(statusCode));
                                Log.d("Hell 2", String.valueOf(headers));
                                Log.d("Hell 3", String.valueOf(e));
                                Log.d("Hell 4", String.valueOf(response));
                                hideDialog();
                                new SweetAlertDialog(DetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed")
                                        .setContentText(String.valueOf(response))
                                        .show();

                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();





    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
