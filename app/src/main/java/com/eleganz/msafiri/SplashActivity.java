package com.eleganz.msafiri;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import com.eleganz.msafiri.session.SessionManager;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(SplashActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            //fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
      /*  Animation pop_anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.pop_anim);
        final Animation flyin1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyin1);
        final Animation flyout1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyout1);

     */   logo=findViewById(R.id.logo);
     // logo.startAnimation(flyin1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//
               // logo.startAnimation(flyout1);

                if (sessionManager.isLoggedIn()) {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);

                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    finish();

                }
                else
                {
                    Intent i = new Intent(SplashActivity.this, MobileRegisterationActivity.class);

                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    finish();
                }

              /*  flyout1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
               *//* ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo_transition");*//*

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });*/

            }
        },3200);
    }
}
