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
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;

public class AddLandActivity extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener {

    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;
    private Button mSend, mImage;
    private ImageView ivImage1, ivImage2, ivImage3, ivImage4, ivImage5, ivImage6;
    LinearLayout mOne, mTwo;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String USERID = "appid";
    public int mSize;
    private static String subCategory;
    private static File[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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


        final FormElementTextSingleLine elementName = FormElementTextSingleLine.createInstance().setTitle("Title").setRequired(true);
        final FormElementTextSingleLine elementCity = FormElementTextSingleLine.createInstance().setTitle("City").setHint("Kampala").setRequired(true);
        final FormElementTextSingleLine elementLocation = FormElementTextSingleLine.createInstance().setTitle("Location").setHint("Mengo, Rubaga").setRequired(true);
        final FormElementTextSingleLine elementSize = FormElementTextSingleLine.createInstance().setTitle("Size").setRequired(true);
        final FormElementTextNumber elementPrice = FormElementTextNumber.createInstance().setTitle("Price").setHint("100,000 UGX").setRequired(true);
        final FormElementTextMultiLine elementDescription = FormElementTextMultiLine.createInstance().setTitle("Description").setRequired(true);

        List<String> category  = new ArrayList<>();
        category.add("Leasehold");
        category.add("Mailo land");
        category.add("Customary");
        category.add("Freehold");
        final FormElementPickerSingle elementType = FormElementPickerSingle.createInstance().setTitle("Select Category")
                .setTag(2149).setOptions(category).setPickerTitle("Select Category").setHint("Mailo land");

        List<BaseFormElement> formItems = new ArrayList<>();

        formItems.add(elementName);
        formItems.add(elementCity);
        formItems.add(elementLocation);
        formItems.add(elementSize);
        formItems.add(elementType);
        formItems.add(elementPrice);
        formItems.add(elementDescription);
        mFormBuilder.addFormElements(formItems);



        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFormBuilder.getFormElement(2149).getValue().equals("Leasehold")){
                    subCategory = "032a672da7933382bf10d355";
                } else if (mFormBuilder.getFormElement(2149).getValue().equals("Mailo land")){
                    subCategory = "38e2efc9b7a7a3b3154a4613";
                } else if (mFormBuilder.getFormElement(2149).getValue().equals("Customary")){
                    subCategory = "83bd88cc920371eebbf8e6a3";
                } else if (mFormBuilder.getFormElement(2149).getValue().equals("Freehold")){
                    subCategory = "ea62d0ce16aa646c6a99d0d5";
                }

                if (elementName.getValue().length() < 3 && elementCity.getValue().length() < 3 && elementLocation.getValue().length() < 3 &&
                        elementSize.getValue().length() < 2  && elementPrice.getValue().length() < 3
                && elementDescription.getValue().length() < 3 && mSize < 3) {

                    Toasty.warning(AddLandActivity.this, "Please Enter All Details", Toast.LENGTH_SHORT, true).show();
                }
                else if (images.length == 0){
                    Toasty.warning(AddLandActivity.this, "Please Select Images", Toast.LENGTH_SHORT, true).show();
                }
                else  {
                    sendInfo(elementName.getValue(), elementCity.getValue(), elementLocation.getValue(), elementSize.getValue(),
                            subCategory, elementPrice.getValue(), elementDescription.getValue());
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

    public void sendInfo (final String name, final String city, final String location, final String size,
                          final String type, final String price, final String desc){

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String userID = (sharedpreferences.getString(USERID, ""));
        progressDialog.setMessage("Uploading Property...");
        showDialog();

        RequestParams params = new RequestParams();
        params.put("title", name);
        params.put("description", desc);
        params.put("address", location);
        params.put("city", city);
        params.put("bed", location);
        params.put("subcategory_id", type);
        params.put("size", size);
        params.put("price", price);

        //RequestParams params = new RequestParams();
        try {
            params.put("images[]", images);
        } catch(FileNotFoundException e) {
            Log.d("Images Up", String.valueOf(e));
        }


        Log.d("Posting Info:", params.toString());

//        File images[] = new File[uploads.size()];
//        for (int i = 0; i < uploads.size(); i++){
//            params.put("images[" + i + "]", String.valueOf(uploads.get(i)));
//        }

        //Log.d("Posting Data", String.valueOf(uploads));




        AsyncHttpClient client = new AsyncHttpClient();
        client.post("https://api.malazi.net/user/" + userID+ "/upload_property", params, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("Clima", "Sucesss Json: " + response.toString());
                new SweetAlertDialog(AddLandActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Successful Uploaded")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Toasty.success(getApplicationContext(), "Successful Uploaded", Toast.LENGTH_SHORT, true).show();
                                Intent intent = new Intent(AddLandActivity.this, MainActivity.class);
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
                new SweetAlertDialog(AddLandActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(AddLandActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(AddLandActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Failed")
                        .setContentText(String.valueOf(response))
                        .show();

            }
        });


    }

//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
