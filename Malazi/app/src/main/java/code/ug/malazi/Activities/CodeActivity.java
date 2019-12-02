package code.ug.malazi.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.Helpers.Session;
import code.ug.malazi.MainActivity;
import code.ug.malazi.R;
import code.ug.malazi.Util.HttpCall;
import code.ug.malazi.Util.HttpRequest;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class CodeActivity extends AppCompatActivity {

    private static final String TAG = "CodeActivity";
    ProgressDialog progressDialog;
    EditText mCode;
    Button mSend;
    private Session session;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    public static final String APPID = "userid";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        session = new Session(CodeActivity.this);

        mCode = findViewById(R.id.input_code);
        mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Activating Account...");
                showDialog();
                sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                String userID = (sharedpreferences.getString(USERID, ""));

                RequestParams params = new RequestParams();
                params.put("token", mCode.getText().toString());
                Log.d("Code Data", String.valueOf(params));

                AsyncHttpClient client = new AsyncHttpClient();
                client.post("https://api.malazi.net/activation_code/" + userID, params, new JsonHttpResponseHandler(){

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        Log.d("Fortune", "Sucesss Json: " + response.toString());
                        String res = response.toString();
                        Log.d("Fortune",  res);
                        try {

                            boolean error = response.getBoolean("error");
                            String message = response.getString("message");
                            if (!error) {
                                String user = response.getString("accessToken");
                                sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(USERID, user);
                                editor.commit();

                                Toasty.success(CodeActivity.this, message, Toast.LENGTH_SHORT, true).show();
                                session.setLogin(true);
                                Intent intent = new Intent(CodeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                new SweetAlertDialog(CodeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(message)
                                        .show();
                                String errorMsg = response.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            hideDialog();
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                        hideDialog();
                        new SweetAlertDialog(CodeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Wrong Code")
                                .show();
                    }

                });

            }
        });
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
