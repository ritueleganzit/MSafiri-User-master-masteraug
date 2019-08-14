package com.eleganz.msafiri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.PaymentActivity.SHARED_PREFERENCES;
import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class MobileRegisterationActivity extends AppCompatActivity {
    private static final String TAG = "MobileRegisterationActivity";
    EditText mobile;
    ProgressDialog progressDialog;
    private String Token="";
    SessionManager sessionManager;
    SharedPreferences devicetokenpref;
    SharedPreferences.Editor devicetokenprefeditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_mobile_registeration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            //fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        devicetokenpref=getSharedPreferences("devicetoken",MODE_PRIVATE);
        devicetokenprefeditor=devicetokenpref.edit();
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
                        devicetokenprefeditor.putString("device",""+Token);
                        devicetokenprefeditor.commit();
                        devicetokenprefeditor.apply();
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
                                    devicetokenprefeditor.putString("device",""+Token);
                                    devicetokenprefeditor.commit();
                                    devicetokenprefeditor.apply();
                                    Log.d("thisismytoken", Token);
                                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("InstanceID", Token);
                                    editor.commit();
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
        checkPermission();

    }



    private void getUserLogin() {

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-Safiri/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        final String strNew = mobile.getText().toString().replaceAll("-", ""); // strNew is 'DCBA123'
        Log.d("sdfsfs",""+strNew);
        Log.d("sdfsfs",""+Token);

        apiInterface.regUserMobile("254"+strNew, "userdata","android",Token, new Callback<Response>() {
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

                    Log.d("MOBILEREG",""+stringBuilder);
                    JSONObject jsonObject = new JSONObject("" + stringBuilder);
                    if (jsonObject != null) {
                        if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);



                                Intent intent=new Intent(MobileRegisterationActivity.this,MobileOTPActivity.class);
                                intent.putExtra("usertype",jsonObject1.getString("user_type"));
                                intent.putExtra("id",jsonObject1.getString("user_id"));
                                intent.putExtra("number","254"+strNew);
                                intent.putExtra("user_email",jsonObject1.getString("user_email"));
                                intent.putExtra("photo",jsonObject1.getString("photo"));
                                intent.putExtra("fname",jsonObject1.getString("fname"));
                                intent.putExtra("lname",jsonObject1.getString("lname"));

                                startActivity(intent);
                                finish();


                                if (jsonObject1.getString("user_type").equalsIgnoreCase("newuser"))
                                {
                                   // Toast.makeText(MobileRegisterationActivity.this, "new", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                   // Toast.makeText(MobileRegisterationActivity.this, "existing", Toast.LENGTH_SHORT).show();
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
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("InstanceID", Token);
                    editor.commit();
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

    public void checkPermission()
    {
        Dexter.withActivity(MobileRegisterationActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                        if (report.getDeniedPermissionResponses().size()>0)
                        {
                           // Toast.makeText(MobileRegisterationActivity.this, ""+report.getDeniedPermissionResponses().get(0).getPermissionName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        //token.continuePermissionRequest();

                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(MobileRegisterationActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(MobileRegisterationActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


}
