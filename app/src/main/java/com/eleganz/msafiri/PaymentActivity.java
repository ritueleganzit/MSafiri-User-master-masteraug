package com.eleganz.msafiri;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.eleganz.msafiri.api.ApiClient;
import com.eleganz.msafiri.api.NotificationUtils;
import com.eleganz.msafiri.api.SharedPrefsUtil;
import com.eleganz.msafiri.api.Utils;
import com.eleganz.msafiri.api.model.AccessToken;
import com.eleganz.msafiri.api.model.STKPush;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eleganz.msafiri.api.AppConstants.BUSINESS_SHORT_CODE;
import static com.eleganz.msafiri.api.AppConstants.CALLBACKURL;
import static com.eleganz.msafiri.api.AppConstants.PARTYB;
import static com.eleganz.msafiri.api.AppConstants.PASSKEY;
import static com.eleganz.msafiri.api.AppConstants.PUSH_NOTIFICATION;
import static com.eleganz.msafiri.api.AppConstants.REGISTRATION_COMPLETE;
import static com.eleganz.msafiri.api.AppConstants.TOPIC_GLOBAL;
import static com.eleganz.msafiri.api.AppConstants.TRANSACTION_TYPE;
import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class PaymentActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id,trip_id,photoPath,joinid;
    CurrentTripSession currentTripSession;
    Button confirmbtn;
    private SharedPrefsUtil mSharedPrefsUtil;
    private String mFireBaseRegId;
    Dialog dialog;
    EditText mobile;
    String id;
    String mobiledata,amount,driver_id;
    private ProgressDialog mProgressDialog;
    private ApiClient mApiClient;
    String URLCONFIRM = "http://itechgaints.com/M-safiri-API/confirmTrip";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    ArrayList<String> mypassenger=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ImageView back=findViewById(R.id.back);
        mobile=findViewById(R.id.mobile);
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.
        mSharedPrefsUtil = new SharedPrefsUtil(this);

        getAccessToken();
        sessionManager=new SessionManager(PaymentActivity.this);
        mProgressDialog = new ProgressDialog(this);
        currentTripSession=new CurrentTripSession(PaymentActivity.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
        driver_id=tripData.get(CurrentTripSession.DRIVER_ID);

        photoPath=getIntent().getStringExtra("photoPath");
        joinid=getIntent().getStringExtra("joinid");
        amount=getIntent().getStringExtra("amount");
        sessionManager.checkLogin();
        mobiledata=sessionManager.getUserDetails().get(SessionManager.PHONE);
        mypassenger=getIntent().getStringArrayListExtra("mypassenger");
        mobile.setText(""+mobiledata);
        Log.d("PaymentScr",""+mypassenger);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<mypassenger.size();i++)
        {
            //Log.d("productsssssssss",etList.get(i)+"");
            if (i==mypassenger.size()-1)
            {
                sb.append(mypassenger.get(i)).append("");
            }
            else {
                sb.append(mypassenger.get(i)).append(",");

            }
        }

        Log.d("productsssssssss",sb+"");


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        confirmbtn=findViewById(R.id.confirmbtn);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(PaymentActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.alert);
                // Set dialog title
                dialog.setTitle("Custom Dialog");
                RobotoMediumTextView alerttext=dialog.findViewById(R.id.dataalert);
                alerttext.setText("You are going to pay KES " + amount+" with MPESA having phone number "+mobiledata);
                Button btncnf=dialog.findViewById(R.id.btncnf);
                btncnf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        performSTKPush("254743236784");

                    }
                });
                // set values for custom dialog components - text, image and button


                dialog.show();
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    NotificationUtils.createNotification(getApplicationContext(), message);
                    showResultDialog(message);
                }
            }
        };

        getFirebaseRegId(); mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    NotificationUtils.createNotification(getApplicationContext(), message);
                    showResultDialog(message);
                }
            }
        };

        getFirebaseRegId();
    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    private void performSTKPush(String mobiledata) {
        mProgressDialog.setMessage("Processing…");
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(mobiledata),
                PARTYB,
                Utils.sanitizePhoneNumber(mobiledata),
                CALLBACKURL + mFireBaseRegId,
                "test", //The account reference
                "test"  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {

                try {
                    if (response.isSuccessful()) {
                        dialog.dismiss();

                        final StringBuilder sb = new StringBuilder();
                        for(int i=0;i<mypassenger.size();i++)
                        {
                            //Log.d("productsssssssss",etList.get(i)+"");
                            if (i==mypassenger.size()-1)
                            {
                                sb.append(mypassenger.get(i)).append("");
                            }
                            else {
                                sb.append(mypassenger.get(i)).append(",");

                            }
                        }

                        confirmTrips(trip_id,sb);


                        Log.d("post to API. %s", response.body().getAmount()+" ");
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(PaymentActivity.this, "Payment Failed Try Again", Toast.LENGTH_SHORT).show();
                        Log.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    private void getFirebaseRegId() {
        mFireBaseRegId = mSharedPrefsUtil.getFirebaseRegistrationID();

        if (!TextUtils.isEmpty(mFireBaseRegId)) {
            mSharedPrefsUtil.saveFirebaseRegistrationID(mFireBaseRegId);
        }
    }

    public void showResultDialog(String result) {
        if (!mSharedPrefsUtil.getIsFirstTime()) {
            // run your one time code
            mSharedPrefsUtil.saveIsFirstTime(true);

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Payment Notification")
                    .setContentText("Payment made successfully")
                    .setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            mSharedPrefsUtil.saveIsFirstTime(false);
                        }
                    })
                    .show();
        }
    }

    private void confirmTrips(final String trip_id,StringBuilder sb)

    {


        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        TypedFile typedFile=new TypedFile("multipart/form-data",new File(""+photoPath.trim()));
        apiInterface.confirmTrip(user_id, trip_id, joinid, sb.toString(), "booked", typedFile, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                dialog.dismiss();
                mProgressDialog.dismiss();
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    Log.d("SuccessPaymentScr",""+stringBuilder);

                    if (stringBuilder != null || !(stringBuilder.toString().equalsIgnoreCase(""))) {

                       // Toast.makeText(PaymentActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            if (jsonObject.getString("message").equalsIgnoreCase("success"))

                            {
                                CurrentTripSession currentTripSession=new CurrentTripSession(PaymentActivity.this);
                                currentTripSession.createTripSession(trip_id,driver_id,true);

                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject child=jsonArray.getJSONObject(i);
                                    id=child.getString("id");
                                }

                                startActivity(new Intent(PaymentActivity.this,SuccessPayment.class)
                                        .putExtra("id",id)
                                        .putExtra("amount",amount)

                                        .putStringArrayListExtra("mypassenger",mypassenger));
                                finish();


                            }

                            else
                            {


                                //  Toast.makeText(PaymentActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                }catch (Exception e)
                {
                    dialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();

                Toast.makeText(PaymentActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();
            }
        });

    }



}
