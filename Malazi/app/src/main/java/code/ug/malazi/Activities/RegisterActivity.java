package code.ug.malazi.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.Helpers.Session;
import code.ug.malazi.R;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    ProgressDialog progressDialog;
    EditText mEmail, mPass, mUsername;
    Button mSend;
    String mToken;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    public static final String APPID = "userid";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        mUsername = findViewById(R.id.input_name);
        mEmail = findViewById(R.id.input_email);
        mPass = findViewById(R.id.input_password);
        mSend = findViewById(R.id.reg);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.d("MYTAG", token);
                mToken = token;
                if (sharedpreferences.contains(APPID)) {
                    mToken = (sharedpreferences.getString(APPID, ""));
                    Log.d("Token: ", (sharedpreferences.getString(APPID, "")));
                } else {
                    mToken = "ebT4s5chCXg:APA91bHMmTrmigq-XXY5j8CCka_fNVIsi1iUbowIcYRnAfMa7zxGuc9msuoEcS6dJMnVGopjMf2_Yt8Z27E8Zt9qz7gpIHTr-8oqpajn3rWaQbhzkTbZN1XZLia68X-BhuNIedEhfZRy";
                }
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().length() > 5 && mPass.getText().toString().length() >5){
                    sendInfo(mUsername.getText().toString(), mEmail.getText().toString(), mPass.getText().toString(), mToken);
                } else if (mUsername.getText().toString().length() < 3){
                    Toasty.warning(RegisterActivity.this, "Password should be more than 6 characters", Toast.LENGTH_SHORT, true).show();

                } else if (mEmail.getText().toString().length() < 5){
                    Toasty.warning(RegisterActivity.this, "Please Enter Correct Email", Toast.LENGTH_SHORT, true).show();

                } else if (mPass.getText().toString().length() < 5){
                    Toasty.warning(RegisterActivity.this, "Password should be more than 6 characters", Toast.LENGTH_SHORT, true).show();

                } else {
                    Toasty.warning(RegisterActivity.this, "Please Enter Email & Password", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void sendInfo(final String userName, final String email, final String password, final String fireID){
        progressDialog.setMessage("Registering Account...");
        showDialog();

        RequestParams params = new RequestParams();
        params.put("username", userName);
        params.put("email", email);
        params.put("password", password);
        params.put("app_id", fireID);


        AsyncHttpClient client = new AsyncHttpClient();
        client.post("https://api.malazi.net/register", params, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("Clima", "Sucesss Json: " + response.toString());
                try {
                    //JSONObject jObj = new JSONObject(response);
                    boolean error = response.getBoolean("error");
                    String user = response.getString("user_id");
                    String msg = response.getString("message");

                    if (!error) {
                        sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(USERID, user);
                        editor.commit();
                        hideDialog();
                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Successful")
                                .setContentText(msg)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent intent = new Intent(RegisterActivity.this, CodeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        //Log.i("VOLLEY", response);
                        Log.i("MESSAGE", msg);
                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(msg)
                                .show();
                        Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Failed")
                        .setContentText(String.valueOf(response))
                        .show();

            }
        });

    }



    public void login (View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void code (View view) {
        Intent intent = new Intent(RegisterActivity.this, CodeActivity.class);
        startActivity(intent);
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
