package com.eleganz.msafiri;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class MobileRegisterationActivity extends AppCompatActivity {
    private static final String TAG = "MobileRegisterationActivity";
    EditText mobile;
    ProgressDialog progressDialog;
    private String Token="";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_mobile_registeration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            //fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        progressDialog=new ProgressDialog(MobileRegisterationActivity.this);
        sessionManager = new SessionManager(MobileRegisterationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        mobile=findViewById(R.id.mobile);
        mobile.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void afterTextChanged(Editable s) {
                String str = mobile.getText().toString();
                if(str.length()==3&& len <str.length()){//len check for backspace
                    mobile.append("-");
                }

                if (str.length()==10)
                {
                    if (Token!=null) {
                        progressDialog.show();
                        getUserLogin();
                    }
                    else
                    {
                        Thread t=new Thread(new Runnable() {
                            @Override
                            public void run() {

                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MobileRegisterationActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        Token = instanceIdResult.getToken();
                                        Log.e("get token",Token);
                                    }
                                });
                                if (Token!=null)
                                {

                                    Log.d("thisismytoken", Token);

                                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                                    StrictMode.setThreadPolicy(threadPolicy);
                                    // getGoogleLogin(str_email,fname,lname,idtoken);

                                    getUserLogin();

                                }
                                else
                                {
                                    Log.d("thisismytoken", "No token"+Token);

                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });t.start();
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = mobile.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        });
    }



    private void getUserLogin() {

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-Safiri/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        final String strNew = mobile.getText().toString().replaceAll("-", ""); // strNew is 'DCBA123'
        Log.d("sdfsfs",""+strNew);
        Log.d("sdfsfs",""+Token);

        apiInterface.regUserMobile(strNew, "userdata","android",Token, new Callback<Response>() {
            @SuppressLint("LongLogTag")
            @Override
            public void success(Response response, Response response2) {

                progressDialog.dismiss();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject("" + stringBuilder);
                    if (jsonObject != null) {
                        if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                sessionManager.createLoginSession("manual",jsonObject1.getString("user_email"),jsonObject1.getString("lname"),jsonObject1.getString("user_id"), jsonObject1.getString("fname"), "", jsonObject1.getString("photo"));



                                Intent intent=new Intent(MobileRegisterationActivity.this,MobileOTPActivity.class);
                                intent.putExtra("usertype",jsonObject1.getString("user_type"));
                                intent.putExtra("id",jsonObject1.getString("user_id"));
                                intent.putExtra("number","254"+strNew);

                                startActivity(intent);


                                if (jsonObject1.getString("user_type").equalsIgnoreCase("newuser"))
                                {
                                    Toast.makeText(MobileRegisterationActivity.this, "new", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(MobileRegisterationActivity.this, "existing", Toast.LENGTH_SHORT).show();
                                }


                            }

                        } else {
                            Toast.makeText(MobileRegisterationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            //     logo.startAnimation(flyout1);


                            progressDialog.dismiss();
                            //   progressBar.startAnimation(flyout2);

                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @SuppressLint("LongLogTag")
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure " + error.getMessage() + "");
                // logo.startAnimation(flyout1);

                Toast.makeText(MobileRegisterationActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

                                        /*progress.startAnimation(flyout1);
                                        animationDrawable.stop();*/
                progressDialog.dismiss();
                //  progressBar.startAnimation(flyout2);
               /* Intent i = new Intent(MobileRegisterationActivity.this, MobileRegisterationActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();*/
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MobileRegisterationActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Token = instanceIdResult.getToken();
                        Log.e("get token",Token);
                    }
                });
                if (Token!=null)
                {

                    Log.d("thisismytoken", Token);

                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                    StrictMode.setThreadPolicy(threadPolicy);
                    // getGoogleLogin(str_email,fname,lname,idtoken);


                }
                else
                {
                    Log.d("thisismytoken", "No token"+Token);

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t.start();
    }
}
