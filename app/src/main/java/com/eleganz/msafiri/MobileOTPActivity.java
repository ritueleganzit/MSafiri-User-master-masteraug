package com.eleganz.msafiri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.philio.pinentry.PinEntryView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MobileOTPActivity extends AppCompatActivity {
PinEntryView vr_code;
LinearLayout submit;
TextView resendotp;
    SessionManager sessionManager;

    String pinentered="",usertype="",id="",number="";
    ProgressDialog progressDialog;
    private String TAG="MobileOTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mobile_otp);
        vr_code=findViewById(R.id.vr_code);
        sessionManager = new SessionManager(MobileOTPActivity.this);
        resendotp=findViewById(R.id.resendotp);
        usertype=getIntent().getStringExtra("usertype");
        number=getIntent().getStringExtra("number");
        id=getIntent().getStringExtra("id");
        progressDialog = new ProgressDialog(MobileOTPActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        submit=findViewById(R.id.submit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            //fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        vr_code.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
            @Override
            public void onPinEntered(String pin) {
                pinentered=pin;

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinentered.equalsIgnoreCase("")) {

                    Toast.makeText(MobileOTPActivity.this, "Please Enter Pin", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.show();
                    matchOtp();
                }
            }
        });
    }

    private void matchOtp() {
        Log.d(TAG,""+id);
        Log.d(TAG,""+number);

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-Safiri/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
apiInterface.sentcode(pinentered, id, "userdata", new Callback<Response>() {
    @Override
    public void success(Response response, Response response2) {
        progressDialog.dismiss();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }


            Log.d(TAG,""+stringBuilder+"--"+usertype);
            JSONObject jsonObject = new JSONObject("" + stringBuilder);
            if (jsonObject != null) {
                if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                       if (usertype.equalsIgnoreCase("newuser"))
                        {
                                Intent intent = new Intent(MobileOTPActivity.this, RegistrationContinue.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                        }
                        else
                        {
                                Intent intent = new Intent(MobileOTPActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);







                    }



                }
                else
                {
                    Toast.makeText(MobileOTPActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }


                }
            /*JSONObject jsonObject = new JSONObject("" + stringBuilder);
            if (jsonObject != null) {
                if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("user_type").equalsIgnoreCase("newuser"))
                        {
                            Toast.makeText(MobileOTPActivity.this, "new", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MobileOTPActivity.this, "existing", Toast.LENGTH_SHORT).show();
                        }


                    }

                } else {
                    Toast.makeText(MobileOTPActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    //     logo.startAnimation(flyout1);


                    progressDialog.dismiss();
                    //   progressBar.startAnimation(flyout2);

                }
            }*/


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failure(RetrofitError error) {
        progressDialog.dismiss();

    }
});
    }
}
