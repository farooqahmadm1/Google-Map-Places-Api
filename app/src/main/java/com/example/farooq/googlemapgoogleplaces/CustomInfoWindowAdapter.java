package com.example.farooq.googlemapgoogleplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View view;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        view = LayoutInflater.from(this.context).inflate(R.layout.info_window_layout,null);
    }
    private void renderWindow(Marker marker,View view){
        String title = marker.getTitle();
        TextView ttitle = (TextView) view.findViewById(R.id.title);
        if (!title.equals("")){
            ttitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tsnippet = (TextView) view.findViewById(R.id.snippet);
        if (!title.equals("")){
            tsnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker,view);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker,view);
        return view;
    }
}
