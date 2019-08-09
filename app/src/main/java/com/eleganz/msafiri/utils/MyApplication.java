package com.eleganz.msafiri.utils;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by eleganz on 3/1/19.
 */

public class MyApplication  extends MultiDexApplication {

  //  private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

    }

   // public static synchronized MyApplication getInstance() {
      //  return mInstance;
 //   }


}