package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.DirectionsJSONParser;
import com.eleganz.msafiri.utils.HistoryData;
import com.eleganz.msafiri.utils.SensorService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class TripBooked extends AppCompatActivity implements OnMapReadyCallback {
    SessionManager sessionManager;
    String user_id,book_id,tripbooked;
    ProgressDialog spotsDialog;
    RelativeLayout dummyrel;
    ProgressBar progrss;
     EditText editreason;
     TextView reason;
    boolean isVisible=true;
     ArrayList<PassengerData> arrayList;
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    MapView mapView;
    private String TAG="TripBooked";
    HistoryData historyData;
    CircleImageView driver_fab;
    ArrayList<String> userslist;

    RecyclerView passengers;
    RelativeLayout  cnfrel;
    RobotoMediumTextView pickuploc,pickuplocaddress,destloc,destlocaddress,comment,trip_rate;
    RatingBar ratingBar;
    RobotoMediumTextView driver_txt1,vehicle_tx1,calculate_time;
    GoogleMap map;
    Runnable runnable;
    CurrentTripSession currentTripSession;
StringBuilder stringBuilder;
    SharedPreferences trip_pref;
    SharedPreferences.Editor trip_editor;
    private int nCounter = 0;
    private String idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_booked);
        ImageView back=findViewById(R.id.back);
        mapView= (MapView) findViewById(R.id.map);
        sessionManager=new SessionManager(TripBooked.this);
        currentTripSession=new CurrentTripSession(TripBooked.this);
        sessionManager.checkLogin();
        tripbooked=getIntent().getStringExtra("from");
        trip_pref=getSharedPreferences("current_trip_pref",MODE_PRIVATE);
        trip_editor=trip_pref.edit();
        idd=trip_pref.getString("current_trip_id","");
        Log.d(TAG,"oncreate");
        cnfrel=findViewById(R.id.cnfrel);
        dummyrel=findViewById(R.id.temp);
        final HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        ratingBar=findViewById(R.id.ratingBar);
        pickuploc=findViewById(R.id.pickuploc);
        pickuplocaddress=findViewById(R.id.pickuplocaddress);
        destlocaddress=findViewById(R.id.destlocaddress);
        destloc=findViewById(R.id.destloc);
        driver_fab=findViewById(R.id.driver_fab);
        driver_txt1=findViewById(R.id.driver_txt1);
        vehicle_tx1=findViewById(R.id.vehicle_tx1);
        calculate_time=findViewById(R.id.calculate_time);
        spotsDialog=new ProgressDialog(TripBooked.this);
        spotsDialog.setCanceledOnTouchOutside(false);
        spotsDialog.setCancelable(false);
        spotsDialog.setMessage("Please Wait");

        trip_rate=findViewById(R.id.trip_rate);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapView.getMapAsync(this);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        historyData= (HistoryData) getIntent().getSerializableExtra("historyData");
        book_id=historyData.getBook_id();

        if (historyData.getRating().equalsIgnoreCase("")) {

            Log.d("kkklll", "--" + historyData.getFrom_lng());

        }
        else {
            ratingBar.setRating(Float.parseFloat(historyData.getRating()));
        }

        if (historyData.getUser_trip_status().equalsIgnoreCase("booked"))
        {

            // Toast.makeText(this, ""+historyData.getUser_trip_status(), Toast.LENGTH_SHORT).show();
            // doTimerTask();
            startService(new Intent(this, SensorService.class));
        }

        else {
            // Toast.makeText(this, ""+historyData.getUser_trip_status(), Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, SensorService.class));
            //stopTask();
        }


            /*Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
             runnable=new Runnable() {
                @Override
                public void run() {
                    if (historyData.getUser_trip_status().equalsIgnoreCase("booked"))

                    {
                        Log.d(TAG + "mmmmm", "if" + System.currentTimeMillis());

                        getSingleTripData();
                        handler1.postDelayed(this, 3000);
                    } else {
                        handler1.removeCallbacks(this);
                        handler1.removeCallbacks(runnable);

                        handler1.removeCallbacksAndMessages(runnable);
                        handler1.removeMessages(0);
                        Log.d(TAG + "mmmmm", "" + System.currentTimeMillis());


                    }
                }
            };
             handler1.postDelayed(runnable,3000);*/




        //
        // pickuploc.setText(""+historyData.getFrom_title());

        pickuplocaddress.setText(""+historyData.getFrom_address());
        //destloc.setText(""+historyData.getTo_title());
        destlocaddress.setText(""+historyData.getTo_address());
        driver_txt1.setText(""+historyData.getFullname());
        vehicle_tx1.setText(""+historyData.getVehicle_name());
        calculate_time.setText(""+historyData.getCalculate_time());
spotsDialog.show();
        getPassanger();
        findViewById(R.id.tellbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripBooked.this, TellYourDriverActivity.class)
                        .putExtra("tripbookedid",historyData.getTrip_id())
                        .putExtra("tripdriverid",historyData.getDriver_id()));

            }
        });
        findViewById(R.id.cancelride).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* final EditText input = new EditText(TripBooked.this);
                input.setHint("Reason");*/
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.activity_cancel_ride, null);

                 editreason=alertLayout.findViewById(R.id.editreason);
                 reason=alertLayout.findViewById(R.id.reason);
                passengers=alertLayout.findViewById(R.id.passengers);
                final AlertDialog alert = new AlertDialog.Builder(TripBooked.this)
                        .setView(alertLayout)
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("No",null)
                        .setCancelable(false)
                        .create();

                alert.show();
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(TripBooked.this, LinearLayoutManager.VERTICAL,false);
                passengers.setLayoutManager(layoutManager);
                userslist=new ArrayList<>();
                if (userslist.size()==arrayList.size()) {
                editreason.setVisibility(View.VISIBLE);
                    reason.setVisibility(View.VISIBLE);

                }
                else {
                    editreason.setVisibility(View.GONE);
                    reason.setVisibility(View.GONE);

                }

                    passengers.setAdapter(new PassengerAdapter(arrayList,TripBooked.this));





                Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (userslist.size()==arrayList.size())
        {
            Log.d("mmmmm",""+userslist);

            if(editreason.getText().toString().isEmpty())
            {
                new AlertDialog.Builder(TripBooked.this)
                        .setMessage("Please give the reason")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
            else
            {
                Log.d("mmmmm","Equal"+userslist);
                //  spotsDialog.show();
                // startActivity(new Intent(TripBooked.this,CancelRide.class));

                cancelTrip(historyData.getTrip_id(),""+editreason.getText().toString());
            }

            // Toast.makeText(TripBooked.this, "Equal", Toast.LENGTH_SHORT).show();
        }
        else
        {

            if (userslist.size()!=0)
            {
                stringBuilder=new StringBuilder();
                // Toast.makeText(TripBooked.this, "has data", Toast.LENGTH_SHORT).show();
                for(int i=0;i<userslist.size();i++)
                {
                    //Log.d("productsssssssss",etList.get(i)+"");
                    if (i==userslist.size()-1)
                    {
                        stringBuilder.append(userslist.get(i)).append("");
                    }
                    else {
                        stringBuilder.append(userslist.get(i)).append(",");

                    }
                }
                spotsDialog.show();
                cancelPassenger(stringBuilder);

            }
            else {
                Log.d("mmmmm","asafs");
                new AlertDialog.Builder(TripBooked.this)
                        .setMessage("Please Select Passenger")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }

        }
    }
});








            }
        });
        if (historyData.getTrip_price()!=null && !historyData.getTrip_price().isEmpty())

        {
            trip_rate.setText("$ "+historyData.getTrip_price());


        }
        if (historyData.getTrip_price().equalsIgnoreCase("null"))
        {
            trip_rate.setText("$ 0" );

        }



        Glide.with(TripBooked.this).load(""+historyData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.male)).into(driver_fab);
        // trip_rate.setText(""+historyData.getTrip_price());
    }

    private void cancelPassenger(StringBuilder stringBuilder) {

        Log.d("cancelPassenger",""+stringBuilder);
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.cancelPassanger("" + stringBuilder, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                spotsDialog.dismiss();
                final StringBuilder stringBuilder = new StringBuilder();
try {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);

    }
    Log.d("csc",""+stringBuilder);
    if (tripbooked!=null  && !(tripbooked.isEmpty()))
    {
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
    else {
        startActivity(new Intent(TripBooked.this,YourTripsActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
    /*startActivity(new Intent(TripBooked.this,YourTripsActivity.class));
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    finish();*/
}
catch (Exception e)
{

}
            }

            @Override
            public void failure(RetrofitError error) {
                spotsDialog.dismiss();
                Toast.makeText(TripBooked.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void cancelTrip(String trip_id,String reason) {
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.cancelTrips(trip_id, user_id,"cancel",reason,book_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    final StringBuilder stringBuilder=new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("cancelTrip",""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))

                    {
                        currentTripSession.clearTripData();

                        spotsDialog.dismiss();
                        startActivity(new Intent(TripBooked.this, CancelRideActivity.class));
                        finish();
                    }

                    else
                    {
                        spotsDialog.dismiss();

                        Toast.makeText(TripBooked.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        Log.d("cancelTrip",""+jsonObject.getString("message"));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(TripBooked.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

                spotsDialog.dismiss();
                Log.d("cancelTrip",""+error.getMessage());
            }
        });

    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();


            Double currentLatitude = intent.getDoubleExtra("latitude", 0);

            Double currentLongitude = intent.getDoubleExtra("longitude", 0);

            //  ... react to local broadcast message

        }

    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        spotsDialog.show();
        map = googleMap;
        MapsInitializer.initialize(getApplicationContext());
        map.getUiSettings().setAllGesturesEnabled(false);

        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("MainAct", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");
        Log.d("drawRoute", "--" + historyData.getFrom_lng());
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                drawRoute(historyData.getFrom_lat(), historyData.getFrom_lng(), historyData.getTo_lat(), historyData.getTo_lng());
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if(isVisible)
                {
                    isVisible=false;
                    dummyrel.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideOutDown).duration(500).repeat(0).playOn(cnfrel);

                }
                else
                {
                    isVisible=true;


                    YoYo.with(Techniques.SlideInUp).duration(100).repeat(0).playOn(cnfrel);
                    dummyrel.setVisibility(View.VISIBLE);

                }
                Log.d("OnMapClick","clicked");


            }
        });




    }
    private class DownloadTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TripBooked.ParserTask parserTask = new TripBooked.ParserTask();


            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points  = new ArrayList();
            SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(TripBooked.this);

            SharedPreferences.Editor collection = db.edit();
            Gson gson = new Gson();
            String arrayList1 = gson.toJson(result);

            collection.putString("mylatlon", arrayList1);
            collection.commit();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {



                List<HashMap<String,String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#4885ed"));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            spotsDialog.dismiss();
        }
    }


    private void drawRoute(String from_lat, String from_lng, String to_lat, String to_lng) {

        LatLng origin = new LatLng(Double.parseDouble(from_lat), Double.parseDouble(from_lng));
        LatLng destination = new LatLng(Double.parseDouble(to_lat), Double.parseDouble(to_lng));
        ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(origin);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.location_green);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap firstMarker = Bitmap.createScaledBitmap(b, 70   , 70, false);


        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.mipmap.location_red);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap firstMarker2 = Bitmap.createScaledBitmap(b2, 70   , 70, false);



        Marker amarker1=    map.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(firstMarker)));
        MarkerOptions options2 = new MarkerOptions();

        // Setting the position of the marker
        options2.position(destination);
        Marker amarker2=    map.addMarker(options2.icon(BitmapDescriptorFactory.fromBitmap(firstMarker2)));

        mMarkerArray.add(amarker1);
        mMarkerArray.add(amarker2);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerArray) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
        map.animateCamera(cu);
        String url = getDirectionsUrl(origin, destination);
        TripBooked.DownloadTask downloadTask = new TripBooked.DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyAlfxVsErR9EwJwhLaEmEJnm2kZMhMrovU";

        Log.d("theurl",url);
        return url;
    }



    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Exceptiondata", data);

            JSONObject jsonObject=new JSONObject(""+data);
            JSONArray jsonArray=jsonObject.getJSONArray("routes");

            for (int i=0;i<jsonArray.length();i++)
            {

                JSONObject childObject=jsonArray.getJSONObject(i);

                JSONArray jsonArray1=childObject.getJSONArray("legs");
                for (int j=0;j<jsonArray1.length();j++)
                {
                    JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("distance").getString("text"));

                    // durationtxt=jsonObject1.getJSONObject("duration").getString("text");


                }

            }


            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getSingleTripData(historyData.getTrip_id(), new Callback<Response>() {
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
                Toast.makeText(TripBooked.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();

            }
        });


    }
    public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.MyViewHolder>{
        ArrayList<PassengerData> arrayList;
        Context context;
        boolean isSelectedAll;
        boolean isChecked=false;


        public PassengerAdapter(ArrayList<PassengerData> arrayList, Context context)
        {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.row_passenger,parent,false);

            MyViewHolder myViewHolder=new PassengerAdapter.MyViewHolder(v);

            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final PassengerData passengerData=arrayList.get(position);


            holder.p_name.setText(passengerData.getUsername());
            holder.select_radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        userslist.add(passengerData.getUser_id());
                        if (userslist.size()==arrayList.size())
                        {
                            editreason.setVisibility(View.VISIBLE);
                            reason.setVisibility(View.VISIBLE);
                        }
                        else {
                            editreason.setVisibility(View.GONE);
                            reason.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        userslist.remove(passengerData.getUser_id());
                        if (userslist.size()==arrayList.size())
                        {
                            editreason.setVisibility(View.VISIBLE);
                            reason.setVisibility(View.VISIBLE);
                        }
                        else {
                            editreason.setVisibility(View.GONE);
                            reason.setVisibility(View.GONE);
                        }



                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout radio;
            CircleImageView p_photo;
            TextView p_name;
            CheckBox select_radioButton;

            public MyViewHolder(View itemView) {
                super(itemView);
                radio=itemView.findViewById(R.id.radio);
                p_photo=itemView.findViewById(R.id.p_photo);
                p_name=itemView.findViewById(R.id.p_name);
                select_radioButton=itemView.findViewById(R.id.select_radioButton);

            }
        }

    }
    public void getPassanger()
    {
      arrayList   = new ArrayList<>();
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
       apiInterface.getPassanger(historyData.getBook_id(), new Callback<Response>() {
           @Override
           public void success(Response response, Response response2) {
               spotsDialog.dismiss();
               try {
                   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                   String line;
                   while ((line = bufferedReader.readLine()) != null) {
                       stringBuilder.append(line);
                   }


                   JSONObject jsonObject = new JSONObject("" + stringBuilder);

                   if (jsonObject.getString("status").equalsIgnoreCase("1")) {


                       JSONArray jsonArray=jsonObject.getJSONArray("data");
                       for (int i=0;i<jsonArray.length();i++)
                       {
                           JSONObject childobject=jsonArray.getJSONObject(i);
                           if (childobject.getString("status").equalsIgnoreCase("booked")){
                               PassengerData passengerData=new PassengerData(childobject.getString("passanger_id"),childobject.getString("passanger_name"),"");
                               arrayList.add(passengerData);
                           }


                       }
                   }
               }catch (Exception e)
               {

               }


           }

           @Override
           public void failure(RetrofitError error) {
               spotsDialog.dismiss();
               Toast.makeText(TripBooked.this, "Network or server error, please try again later.", Toast.LENGTH_LONG).show();


           }
       });


    }
}

