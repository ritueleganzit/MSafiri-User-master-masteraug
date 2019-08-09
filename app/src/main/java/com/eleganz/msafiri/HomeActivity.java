package com.eleganz.msafiri;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.fragment.MapFragment;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.Home;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.MyFirebaseMessagingService;
/*
import com.facebook.login.LoginManager;
*/
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivityLog";
    CircleImageView profile_image;
    SessionManager sessionManager;
    String user_id, photo, user,lname;
    RobotoMediumTextView user_name;
    SharedPreferences sh_imagePreference;
    SharedPreferences.Editor imagePreference;
    CurrentTripSession currentTripSession;
  //  private GoogleApiClient mGoogleApiClient;
    private String user_trip_status,login_type;

    String noti_message="",type="",ntrip_id="";
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
       /* ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setAlpha(255);
        colorDrawable.setColor((Color.parseColor("#64000000")));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);*/
        sessionManager = new SessionManager(HomeActivity.this);
        currentTripSession = new CurrentTripSession(HomeActivity.this);

        sessionManager.checkLogin();

        sh_imagePreference = getSharedPreferences("imagepref", MODE_PRIVATE);
        imagePreference = sh_imagePreference.edit();
        HashMap<String, String> userData = sessionManager.getUserDetails();
        user_id = userData.get(SessionManager.USER_ID);
        user = userData.get(SessionManager.FNAME);
        lname = userData.get(SessionManager.LNAME);
        login_type = userData.get(SessionManager.LOGIN_TYPE);


        Log.d("Namwww",""+user+" "+lname);

noti_message=getIntent().getStringExtra("content");
        type=getIntent().getStringExtra("type");



        if(noti_message != null && !noti_message.isEmpty())
        {
            if(type != null && !type.isEmpty()) {
            if(type.equalsIgnoreCase("complete_trip"))
            {

            }
            else
            {
                openDialog(noti_message);

            }

        }



        }
       /* if (currentTripSession.hasTrip()) {

            Toast.makeText(this, "dfgdg", Toast.LENGTH_SHORT).show();
           getSingleTripData();
        }*/

/*
        toolbar.bringToFront();
*/
        final AdvanceDrawerLayout drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 15);
        drawer.setViewElevation(Gravity.START, 20);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_ham, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, EditProfile.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(HomeActivity.this,
                                profile_image,
                                ViewCompat.getTransitionName(profile_image));
                startActivity(intent, options.toBundle());


            }
        });
        profile_image = headerview.findViewById(R.id.profile_image);

        user_name = headerview.findViewById(R.id.user_name);
        Log.d(TAG, "" + photo);
        Log.d(TAG, "m" + user);

        if (user != null && !user.isEmpty()) {
            // doSomething
            if (!user.equalsIgnoreCase("null"))
            {
                user_name.setText(user);
            }


        }
       /* navigationView.setVerticalFadingEdgeEnabled(true);
        for (int i = 0; i < navigationView.getChildCount(); i++) {
            navigationView.getChildAt(i).setOverScrollMode(View.OVER_SCROLL_NEVER);
        }*/
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();*/
        super.onStart();



           /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();*/

    }
   /* private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }*/
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(HomeActivity.this)
                .unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            // read any data you might need from intent and do your action here

            String data=intent.getStringExtra("complete");
            ntrip_id=intent.getStringExtra("trip_id");
            if(data != null && !data.isEmpty())
            {
                startActivity(new Intent(HomeActivity.this, ReviewActivity.class).putExtra("trip_id",ntrip_id));

            }

        }
    };
    public void openDialog(String message)
    {

        AlertDialog alertDialog=  new AlertDialog.Builder(HomeActivity.this)
                .setMessage(""+message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        if (type!=null && !type.isEmpty())
                        {

                        }
                    }
                })

                .setCancelable(false)
                .show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        textView.setTypeface(face);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MapFragment map_one = new MapFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.container, map_one);
        ft.commit();
        HashMap<String, String> userData = sessionManager.getUserDetails();

        photo = userData.get(SessionManager.PHOTO);
        Log.d(TAG, "---" + photo);
      //  Glide.with(getApplicationContext()).load(photo).apply(RequestOptions.circleCropTransform()).into(profile_image);
        LocalBroadcastManager.getInstance(HomeActivity.this)
                .registerReceiver(mBroadcastReceiver, MyFirebaseMessagingService.BROADCAST_INTENT_FILTER);
        if (photo != null && !photo.isEmpty()) {
            // doSomething
            Glide.with(getApplicationContext()).load(photo).apply(RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.pr)).into(profile_image);

        }
        else
        {

        }
        getUserData();

    }

    @Override
    public void onBackPressed() {
        AdvanceDrawerLayout drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private int getStatusBarHeight() {
        int height;

        Resources myResources = getResources();
        int idStatusBarHeight = myResources.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = getResources().getDimensionPixelSize(idStatusBarHeight);
            Toast.makeText(this,
                    "Status Bar Height = " + height,
                    Toast.LENGTH_LONG).show();
        } else {
            height = 0;
            Toast.makeText(this,
                    "Resources NOT found",
                    Toast.LENGTH_LONG).show();
        }

        return height;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trips) {
            // Handle the camera action
            startActivity(new Intent(HomeActivity.this, YourTripsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        } else if (id == R.id.nav_help) {
            startActivity(new Intent(HomeActivity.this, HelpActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        }
        else if (id==R.id.nav_cr_trips)
        {
            startActivity(new Intent(HomeActivity.this, TrackingScreen.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        /*else if (id == R.id.nav_rating) {
           *//* startActivity(new Intent(HomeActivity.this,ReviewActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
*//*
        }*/
        else if (id == R.id.nav_logout) {
            // sessionManager.logoutUser();


            AlertDialog alertDialog=     new AlertDialog.Builder(this).setMessage("Are you sure you want to logout?").setCancelable(false)

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            logoutclearToken(user_id);
                            //signOut();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
            Typeface face= Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
            textView.setTypeface(face);
        } else if (id == R.id.nav_drive) {



        } else if (id == R.id.nav_legal) {
            startActivity(new Intent(HomeActivity.this, LegalActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


         /*   String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", 23.0350, 72.5293, "Home Sweet Home", 22.9962, 72.5235, "Where the party is at");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);*/


        }

        /*AdvanceDrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    private void getUserData() {

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getUserData(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
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


                                if (jsonObject1.getString("fname")!=null && !(jsonObject1.getString("fname").isEmpty()))
                                {

                                        // doSomething
                                        if (!jsonObject1.getString("fname").equalsIgnoreCase("null")) {
                                            user_name.setText(jsonObject1.getString("fname"));


                                    }
                                }
                                else
                                {
                                    if (!user.equalsIgnoreCase("null")) {
                                        user_name.setText(""+user);

                                    }

                                }

                                imagePreference.putString("photo", jsonObject1.getString("photo"));
                                imagePreference.commit();
                                if (login_type.equalsIgnoreCase("manual")) {

                                    Glide.with(HomeActivity.this).load(jsonObject1.getString("photo")).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.pr)).into(profile_image);
sessionManager.updateImage(jsonObject1.getString("photo"));
                                }
                            }

                            Log.d(TAG, "Success " + stringBuilder + "");

                        }


                        Log.d(TAG, "Success " + stringBuilder + "");


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(HomeActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

            }
        });

    }


    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        HashMap<String,String> hashMap=currentTripSession.getTripDetails();
        apiInterface.getSingleTripData(hashMap.get(CurrentTripSession.TRIP_ID), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d(TAG,""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject childObjct = jsonArray.getJSONObject(i);
                            user_trip_status=""+childObjct.getString("user_trip_status");

                            Log.d("jyity8u",""+user_trip_status);

                            if (user_trip_status.equalsIgnoreCase("onboard")) {

    Log.d("jyity8u",""+user_trip_status);

    startActivity(new Intent(HomeActivity.this, CurrentTrip.class));
}
    if (user_trip_status.equalsIgnoreCase("booked")) {

    Log.d("jyity8u",""+user_trip_status);

    startActivity(new Intent(HomeActivity.this, CurrentTrip.class));
}
if (user_trip_status.equalsIgnoreCase("completed")) {

    Log.d("jyity8u",""+user_trip_status);

}



                        }

                    }
                    else
                    {

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(HomeActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

            }
        });


    }
    public void logoutclearToken(String user_id)
    {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        apiInterface.logoutclearToken("", user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d(TAG, "->" + stringBuilder);

                    sessionManager.logoutUser();
                  /*  LoginManager.getInstance().logOut();

                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                }
                            });
      */          }catch (Exception e)
                {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(HomeActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

            }
        });
    }

}
