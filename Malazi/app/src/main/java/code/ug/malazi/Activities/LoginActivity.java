package code.ug.malazi.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.Helpers.Session;
import code.ug.malazi.MainActivity;
import code.ug.malazi.R;
import code.ug.malazi.Util.HttpCall;
import code.ug.malazi.Util.HttpRequest;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ProgressDialog progressDialog;
    EditText mEmail, mPass;
    private Session session;
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
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.d("MYTAG", token);
                mToken = token;
                if (sharedpreferences.contains(APPID)) {
                    mToken = (sharedpreferences.getString(APPID, ""));
                    Log.d("Toen: ", (sharedpreferences.getString(APPID, "")));
                } else {
                    mToken = "ebT4s5chCXg:APA91bHMmTrmigq-XXY5j8CCka_fNVIsi1iUbowIcYRnAfMa7zxGuc9msuoEcS6dJMnVGopjMf2_Yt8Z27E8Zt9qz7gpIHTr-8oqpajn3rWaQbhzkTbZN1XZLia68X-BhuNIedEhfZRy";
                }
            }
        });

        mEmail = findViewById(R.id.input_email);
        mPass = findViewById(R.id.input_password);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        session = new Session(LoginActivity.this);
        if (session.getLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        mSend = findViewById(R.id.reg);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().length() > 5 && mPass.getText().toString().length() >5){
                    loginUser(mEmail.getText().toString(), mPass.getText().toString(), mToken);
                }else if (mEmail.getText().toString().length() < 5){
                    Toasty.warning(LoginActivity.this, "Please Enter Correct Email", Toast.LENGTH_SHORT, true).show();

                } else if (mPass.getText().toString().length() < 5){
                    Toasty.warning(LoginActivity.this, "Password should be more than 6 characters", Toast.LENGTH_SHORT, true).show();

                } else {
                    Toasty.warning(LoginActivity.this, "Please Enter Email & Password", Toast.LENGTH_SHORT, true).show();
                }
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private void loginUser(final String email, final String password, final String tokenn) {
        progressDialog.setMessage("Logging you in...");
        showDialog();

        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        httpCallPost.setUrl("https://api.malazi.net/login");
        final HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("email",email);
        paramsPost.put("password",password);
        paramsPost.put("app_id", tokenn);
        httpCallPost.setParams(paramsPost);
        Log.d("Params ", String.valueOf(paramsPost));

        new HttpRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                Log.d(TAG, "Login Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");
                    Log.d("Geett", response);
                    if (!error) {
                        hideDialog();
                        String user = jObj.getString("accessToken");
                        sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(USERID, user);
                        editor.commit();

                        Toasty.success(LoginActivity.this, message, Toast.LENGTH_SHORT, true).show();
                        session.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(message)
                                .show();
                        hideDialog();
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Wrong Username Or Password")
                            .show();
                }

            }

        }.execute(httpCallPost);
    }


    public void reg (View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
