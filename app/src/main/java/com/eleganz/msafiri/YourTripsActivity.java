package com.eleganz.msafiri;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eleganz.msafiri.adapter.MyTripAdapter;
import com.eleganz.msafiri.fragment.MySampleFabFragment;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.Home;
import com.eleganz.msafiri.model.TripData;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.HistoryData;
import com.eleganz.msafiri.utils.MyFirebaseMessagingService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

public class YourTripsActivity extends AppCompatActivity  {
    SessionManager sessionManager;
    String user_id,tripbooked;
    ProgressDialog dialog;
    LinearLayout tv_no_data;
    RecyclerView list;
    String noti_message="",type="",ntrip_id="";

    ArrayList<HistoryData> arrayList=new ArrayList<>();
    private static final String TAG = "YourTripsActivityLog";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_trips);
        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(YourTripsActivity.this);

        if(type != null && !type.isEmpty()) {

        }
      //  sessionManager.checkLogin();


        Log.d(TAG,"oncreate");

        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        list=findViewById(R.id.list);
        tv_no_data=findViewById(R.id.tv_no_data_lin);
        dialog = new ProgressDialog(YourTripsActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait");

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(YourTripsActivity.this,LinearLayoutManager.VERTICAL,false);
        list.setLayoutManager(layoutManager);


        userTrips();
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(YourTripsActivity.this)
                .unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(YourTripsActivity.this)
                .registerReceiver(mBroadcastReceiver, MyFirebaseMessagingService.BROADCAST_INTENT_FILTER);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            // read any data you might need from intent and do your action here

            String data=intent.getStringExtra("complete");
            type=intent.getStringExtra("type");
            ntrip_id=intent.getStringExtra("trip_id");
            if(data != null && !data.isEmpty())
            {
                startActivity(new Intent(YourTripsActivity.this, ReviewActivity.class).putExtra("trip_id",ntrip_id));

            }

        }
    };    private void userTrips() {
        dialog.show();
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.userTrips(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {


                dialog.dismiss();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    if (jsonObject.getString("status").equalsIgnoreCase("1"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        Log.d("YourTripstatus",""+stringBuilder);

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String user_trip_status=jsonObject1.getString("user_trip_status");
                            if(user_trip_status != null && !user_trip_status.isEmpty())
                            {
                                if (((user_trip_status.equalsIgnoreCase("0"))) ||(user_trip_status.equalsIgnoreCase("onboard")) || (user_trip_status.equalsIgnoreCase("confirm")))
                                {
                                    Log.d("YourTripstatus","if"+user_trip_status);
                                }
                                else {
                                    Log.d("YourTripstatus","else-->"+user_trip_status);

                                    HistoryData historyData=new HistoryData(jsonObject1.getString("driver_id"),
                                            jsonObject1.getString("trip_id"),
                                            jsonObject1.getString("rating"),
                                            jsonObject1.getString("comments"),
                                            jsonObject1.getString("user_trip_status"),
                                            jsonObject1.getString("status"),
                                            jsonObject1.getString("from_title"),
                                            jsonObject1.getString("from_lat"),
                                            jsonObject1.getString("from_lng"),
                                            jsonObject1.getString("from_address"),
                                            jsonObject1.getString("to_title"),
                                            jsonObject1.getString("to_lat"),
                                            jsonObject1.getString("to_lng"),
                                            jsonObject1.getString("to_address"),
                                            jsonObject1.getString("end_datetime"),
                                            jsonObject1.getString("last_lat"),
                                            jsonObject1.getString("last_lng"),
                                            jsonObject1.getString("fullname"),
                                            jsonObject1.getString("photo"),
                                            jsonObject1.getString("vehicle_name"),
                                            jsonObject1.getString("trip_price"),
                                            jsonObject1.getString("calculate_time"),
                                            jsonObject1.getString("trip_screenshot")
                                    );
            historyData.setCancelledby(jsonObject1.getString("cancel_by"));
            historyData.setCancel_reason(jsonObject1.getString("cancel_reason"));
                                    historyData.setBook_id(jsonObject1.getString("book_id"));
                                    historyData.setDate(jsonObject1.getString("datetime"));
                                    arrayList.add(historyData);
                                    Collections.reverse(arrayList);
                                }
                            }
                            else {
                                Log.d("YourTripstatus","empty");
                            }


                        }
                        if ((arrayList.isEmpty()) || (arrayList.size()==0) || (arrayList==null))
                    {
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                    else {
                            tv_no_data.setVisibility(View.GONE);
                            MyTripAdapter myTripAdapter=new MyTripAdapter(arrayList,YourTripsActivity.this);
                            list.setAdapter(myTripAdapter);
                            Log.d("YourTripstatus",""+stringBuilder);
                            Log.d("YourTripstatus",""+arrayList.get(0).getUser_trip_status());
                        }



                    }
                    else {
                        dialog.dismiss();
                        tv_no_data.setVisibility(View.VISIBLE);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(YourTripsActivity.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

                dialog.dismiss();

             //   Toast.makeText(YourTripsActivity.this, "yjyjiy"+error.getMessage(), Toast.LENGTH_SHORT).show();
                tv_no_data.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
    /*public class MyTripAdapter extends BaseAdapter implements OnMapReadyCallback
    {

        ArrayList<TripData> arrayList;
        Context context;
        MapView mapView;

        public MyTripAdapter(ArrayList<TripData> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            view=layoutInflater.inflate(R.layout.yourtriprow,null);
            TripData tripData=arrayList.get(i);

            mapView= (MapView) view.findViewById(R.id.map);

            mapView.getMapAsync(this);

            return view;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {


        }
    }*/
}
