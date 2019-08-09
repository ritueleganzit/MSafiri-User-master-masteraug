package com.eleganz.msafiri;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.resources.TextAppearance;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPassenger extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id,trip_id,photoPath,joinid,seats,username,amount;
    CurrentTripSession currentTripSession;
    int Id=1;
    int Id2=1;
    ArrayList<EditText> etList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_passenger);

        etList  = new ArrayList<EditText>();
        LinearLayout tbl=findViewById(R.id.table);


        Log.d("etListetList",""+seats);
        sessionManager=new SessionManager(AddPassenger.this);
        currentTripSession=new CurrentTripSession(AddPassenger.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
        seats=getIntent().getStringExtra("seats");
        photoPath=getIntent().getStringExtra("photoPath");
        joinid=getIntent().getStringExtra("joinid");
        amount=getIntent().getStringExtra("amount");
        sessionManager.checkLogin();

        Log.d("etListetList",""+seats);
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
username=userData.get(SessionManager.FNAME);
findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onBackPressed();
    }
});
        if (seats!=null && !(seats.isEmpty()))
        {
            Log.d("etListetList-->",""+seats);

            int j= Integer.parseInt(seats);
            for(int i=1;i<=j;i++){
                Log.d("etListetList--->",""+i);

                LinearLayout.LayoutParams tableRowParams=
                        new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                tableRowParams.setMargins(0,10,0,10);
                CardView cardView=new CardView(AddPassenger.this);
                cardView.setCardElevation(5);
                cardView.setRadius(7);
                cardView.setLayoutParams(tableRowParams);
                cardView.setContentPadding(20,40,20,40);






                LinearLayout linearLayout=new LinearLayout(AddPassenger.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText et=new EditText(AddPassenger.this);
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#D0D0D0"));
                ViewCompat.setBackgroundTintList(et, colorStateList);
                TextView textView=new TextView(AddPassenger.this);

                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start,
                                               int end, Spanned dest, int dstart, int dend) {
//&&
//                                    !Character.toString(source.charAt(i)).equals("-")
                        for (int i = start;i < end;i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                    !Character.toString(source.charAt(i)).equals(",") )
                            {
                                return "";
                            }
                        }
                        return null;
                    }
                };

                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Light.ttf");
                Typeface face2 = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Medium.ttf");
                et.setTypeface(face);

textView.setTypeface(face2);
                et.setFilters(new InputFilter[] { filter });
                //  ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                et.setHint("Enter passenger name");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    et.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                } else {
                    et.setTextAppearance(android.R.style.TextAppearance_Medium);
                }
              //  et.setLayoutParams(layoutParams);
                et.setId(Id+i);//string+i
                textView.setId(Id2+i);
                int no=i+1;
                textView.setText("Passenger "+i);

                //add et to table row
                //add table row to table

                linearLayout.addView(textView);
                linearLayout.addView(et);

                linearLayout.requestLayout();

                cardView.addView(linearLayout);
                tbl.addView(cardView);
                //add edittext to list
                etList.add(et);
            }

            etList.get(0).setText(""+username);
            //etList.get(0).setEnabled(false);

        }


        findViewById(R.id.savdata).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList=new ArrayList();

                for (EditText et : etList) {
                    if (et.getText().toString().equalsIgnoreCase(""))
                    {

                    }else {
                        arrayList.add(et.getText().toString());
                    }
                }

                if (Integer.parseInt(seats)==arrayList.size())
                {
                    Log.d("seatsss",""+arrayList);
                    startActivity(new Intent(AddPassenger.this,PaymentActivity.class)
                            .putExtra("photoPath",photoPath)
                            .putExtra("joinid",joinid)
                            .putExtra("user_id",user_id)
                            .putExtra("trip_id",trip_id)
                            .putExtra("amount",amount)
                            .putStringArrayListExtra("mypassenger",arrayList)




                    );
                    finish();
                }
                else
                {
                    Toast.makeText(AddPassenger.this, "Please enter passenger details", Toast.LENGTH_SHORT).show();
                }

               /* Log.d("llllll",etList.size()+"");
                for (EditText et : etList) {

                    if (et.getText().toString().equalsIgnoreCase(""))
                    {


                    }




                }


                StringBuilder sb = new StringBuilder();
                for(int i=0;i<etList.size();i++)
                {


                    if (i==0)
                    {

                    }
                    else if (etList.get(i).getText().toString().equalsIgnoreCase(""))
                    {
                        Toast.makeText(AddPassenger.this, "Please Enter data", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        arrayList.add(etList.get(i).getText().toString());
                        Log.d("mmmmmm",""+arrayList);
                        startActivity(new Intent(AddPassenger.this,PaymentActivity.class)
                                .putExtra("photoPath",photoPath)
                                .putExtra("joinid",joinid)
                                .putExtra("user_id",user_id)
                                .putExtra("trip_id",trip_id)
                                .putStringArrayListExtra("mypassenger",arrayList)




                        );
                        finish();
                    }


                    Log.d("productsssssssss",etList.get(i).getText().toString()+""+i);
                    if (i==etList.size()-1)
                    {
                        sb.append(etList.get(i)).append("");
                    }
                    else {
                        sb.append(etList.get(i)).append(",");

                    }
                }

                Log.d("productsssssssss",sb+"");


*/
            }
        });
    }
}
