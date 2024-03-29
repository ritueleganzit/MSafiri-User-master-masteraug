package com.eleganz.msafiri.lib;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.eleganz.msafiri.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by eleganz on 20/2/18.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custominfowindow, null);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);

        tvTitle.setText(marker.getTitle());
        tvSubTitle.setText(marker.getSnippet());

        return view;
    }
}
