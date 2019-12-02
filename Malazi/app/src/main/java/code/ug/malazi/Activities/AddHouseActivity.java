package code.ug.malazi.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import code.ug.malazi.MainActivity;
import code.ug.malazi.R;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementSwitch;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;

public class AddHouseActivity extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener {

    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;
    private Button mSend, mImage;
    private ImageView ivImage1, ivImage2, ivImage3, ivImage4, ivImage5, ivImage6;
    LinearLayout mOne, mTwo;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    private int mSize;
    private static String subCategory;
    private static File[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mRecyclerView = findViewById(R.id.recyclerView);
        mFormBuilder = new FormBuilder(this, mRecyclerView);

        mSend = findViewById(R.id.buttonSendInfo);
        mImage = findViewById(R.id.buttonImages);
        mOne = findViewById(R.id.img1);
        mTwo = findViewById(R.id.img2);

        ivImage1 = findViewById(R.id.iv_image1);
        ivImage2 = findViewById(R.id.iv_image2);
        ivImage3 = findViewById(R.id.iv_image3);
        ivImage4 = findViewById(R.id.iv_image4);
        ivImage5 = findViewById(R.id.iv_image5);
        ivImage6 = findViewById(R.id.iv_image6);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        FormHeader header1 = FormHeader.createInstance("Basic Info");
        final FormElementTextSingleLine elementName = FormElementTextSingleLine.createInstance().setTitle("Name");
        final FormElementTextSingleLine elementCity = FormElementTextSingleLine.createInstance().setTitle("City").setHint("Kampala");
        final FormElementTextSingleLine elementLocation = FormElementTextSingleLine.createInstance().setTitle("Location").setHint("Mengo, Rubaga");
        final FormElementSwitch elementFurn = FormElementSwitch.createInstance().setTitle("Furnished?").setSwitchTexts("Yes", "No").setValue("No");

        // multiple items picker input
        List<String> fruits = new ArrayList<>();
        fruits.add("Wifi");
        fruits.add("Parking");
        fruits.add("Power");
        fruits.add("Water");
        fruits.add("Security");
        final FormElementPickerMulti elementMutiple = FormElementPickerMulti.createInstance().setTitle("Select Services").setHint("Wifi, Packing, Power")
                .setOptions(fruits).setPickerTitle("Select Services").setTag(312);

        final FormElementTextNumber elementBed = FormElementTextNumber.createInstance().setTitle("Bedrooms").setHint("1");
        final FormElementTextNumber elementBath = FormElementTextNumber.createInstance().setTitle("Bathrooms").setHint("2");
        final FormElementTextNumber elementPrice = FormElementTextNumber.createInstance().setTitle("Price").setHint("100,000 UGX");
        final FormElementTextMultiLine elementDescription = FormElementTextMultiLine.createInstance().setTitle("Description");

        FormHeader header3 = FormHeader.createInstance("Images");

        List<String> categoryz = new ArrayList<>();
        categoryz.add("Farmhouse");
        categoryz.add("Full House");
        categoryz.add("Bungalow");
        categoryz.add("Mansion");
        categoryz.add("Apartments");
        final FormElementPickerSingle elementTy = FormElementPickerSingle.createInstance().setTag(672)
                .setTitle("Select Category").setOptions(categoryz).setPickerTitle("Select Category").setHint("Farmhouse");

        List<BaseFormElement> formItems = new ArrayList<>();
        formItems.add(header1);
        formItems.add(elementName);
        formItems.add(elementCity);
        formItems.add(elementLocation);
        formItems.add(elementBed);
        formItems.add(elementBath);
        formItems.add(elementTy);
        formItems.add(elementPrice);
        formItems.add(elementDescription);
        formItems.add(elementFurn);
        formItems.add(elementMutiple);

        formItems.add(header3);


        mFormBuilder.addFormElements(formItems);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFormBuilder.getFormElement(672).getValue().equals("Farmhouse")) {
                    subCategory = "b930247113330be38c3db237";
                } else if (mFormBuilder.getFormElement(672).getValue().equals("Full House")) {
                    subCategory = "bec43a2eabdf412d8748d4a7";
                } else if (mFormBuilder.getFormElement(672).getValue().equals("Bungalow")) {
                    subCategory = "d880b28301f8ce361a1241d9";
                } else if (mFormBuilder.getFormElement(672).getValue().equals("Mansion")) {
                    subCategory = "d981f593dc5cf83950080720";
                } else if (mFormBuilder.getFormElement(672).getValue().equals("Apartments")){
                    subCategory = "dfd9aebd719f35d5dfeb761f";
                }
                System.out.println(subCategory);

                if (elementName.getValue().length() < 3 && elementCity.getValue().length() < 3 && elementLocation.getValue().length() < 3 &&
                        elementBed.getValue().length() < 2 && elementBath.getValue().length() < 2 && elementPrice.getValue().length() < 3
                        && elementDescription.getValue().length() < 3 && mSize < 3) {
                    Toasty.warning(AddHouseActivity.this, "Please Enter All Details", Toast.LENGTH_SHORT, true).show();
                } else if (images.length == 0) {
                    Toasty.warning(AddHouseActivity.this, "Please Select Images", Toast.LENGTH_SHORT, true).show();
                } else {
                    sendInfo(elementName.getValue(),
                            elementCity.getValue(),
                            elementLocation.getValue(),
                            elementBed.getValue(),
                            elementBath.getValue(),
                            subCategory,
                            elementPrice.getValue(),
                            elementDescription.getValue(),
                            elementFurn.getValue(),
                            mFormBuilder.getFormElement(312).getValue());
                }
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("code.ug.malazi.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(3)
                        .setMaximumMultiSelectCount(6)
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        for (int i=0; i < uriList.size(); i++) {
            if (i >= 6) return;
            ImageView iv;
            switch (i) {
                case 0:
                    iv = ivImage1;
                    break;
                case 1:
                    iv = ivImage2;
                    break;
                case 2:
                    iv = ivImage3;
                    break;
                case 3:
                    iv = ivImage4;
                    break;
                case 4:
                    iv = ivImage5;
                    break;
                case 5:
                default:
                    iv = ivImage6;
            }

            mSize = uriList.size();
            System.out.println(uriList);
            System.out.println(mSize);

            images = new File[mSize];

            for (int k = 0; k < mSize; k++){
                images[k] = new File(uriList.get(k).getPath());
            }

            Glide.with(this)
                    .load(uriList.get(i))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .into(iv);
        }
    }

    public void sendInfo(final String name, final String city, final String location, final String bed, final String bath,
                         final String type, final String price, final String desc, final String furn, final String service) {


        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        progressDialog.setMessage("Uploading Property...");
        showDialog();

        RequestParams params = new RequestParams();
        params.put("title", name);
        params.put("description", desc);
        params.put("address", location);
        params.put("city", city);
        params.put("subcategory_id", type);
        params.put("bedrooms", bed);
        params.put("bathrooms", bath);
        params.put("furnished", furn);
        params.put("services", service);
        params.put("price", price);

        try {
            params.put("images[]", images);
        } catch (FileNotFoundException e) {
            Log.d("Images Up", String.valueOf(e));
        }
        Log.d("Posting Info:", params.toString());


        AsyncHttpClient client = new AsyncHttpClient();
        client.post("https://api.malazi.net/user/" + userID+ "/upload_property", params, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("Clima", "Sucesss Json: " + response.toString());
                new SweetAlertDialog(AddHouseActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Successful Uploaded")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Toasty.success(getApplicationContext(), "Successful Uploaded", Toast.LENGTH_SHORT, true).show();
                                Intent intent = new Intent(AddHouseActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Status 1", String.valueOf(statusCode));
                Log.d("Status 2", String.valueOf(headers));
                Log.d("Status 3", String.valueOf(throwable));
                Log.d("Status 4", String.valueOf(errorResponse));
                hideDialog();
                new SweetAlertDialog(AddHouseActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(AddHouseActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(AddHouseActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Failed")
                        .setContentText(String.valueOf(response))
                        .show();

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
