package com.dosssik.andrey.myplane;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    static AnimatorPath path;
    final String TAG_TAG = "TAGTAGTAG";
    final LatLng startPosition = new LatLng(55.751624, 37.618715);
    final LatLng finalPosition = new LatLng(-22.911028, -43.209384);
    SupportMapFragment mapFragment;
    Button buttonMarker;
    Button buttonRoute;
    Button buttonGo;
    Marker markerPlane;
    Marker startPoint;
    Marker finalPoint;
    Polyline line;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        map = mapFragment.getMap(); // Should be Async or not..?
        if (map == null) {
            finish();
            return;
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        createPath();

    }

    private void createPath() {
        path = new AnimatorPath();
        float sX = (float) startPosition.latitude;
        float sY = (float) startPosition.longitude;

        float fX = (float) finalPosition.latitude;
        float fY = (float) finalPosition.longitude;

        path.moveTo((float) startPosition.latitude, (float) startPosition.longitude);
        path.curveTo(sX, fY, fX, sY, fX, fY);
    }

    private void initView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        buttonGo = (Button) findViewById(R.id.btn_go);
        buttonMarker = (Button) findViewById(R.id.btn_mark);
        buttonRoute = (Button) findViewById(R.id.btn_route);

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMarker(path);
            }
        });

        buttonMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startPoint == null & finalPoint == null & markerPlane == null) {

                    startPoint = map.addMarker(new MarkerOptions().position(startPosition)
                            .title("Msc").flat(true).icon(BitmapDescriptorFactory.defaultMarker()));
                    finalPoint = map.addMarker(new MarkerOptions().position(finalPosition)
                            .title("Rio").flat(false).anchor(1, 1).icon(BitmapDescriptorFactory.fromResource(R.drawable.ve)));
                    markerPlane = map.addMarker(new MarkerOptions().position(startPosition).draggable(true)
                            .title("Hi, i'm plane").flat(false).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.plane)));

                    buttonGo.setVisibility(View.VISIBLE);
                    buttonRoute.setVisibility(View.VISIBLE);
                    buttonMarker.setVisibility(View.GONE);
                }
            }
        });

        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (line == null) {
                    line = map.addPolyline(new PolylineOptions()
                            .add(startPosition, finalPosition)
                            .width(10)
                            .color(Color.GRAY)
                            .geodesic(true));
                }
            }
        });
    }

    void animateMarker(AnimatorPath path) {
        final ObjectAnimator anim = ObjectAnimator.ofObject(this, "markerMotion",
                new PathEvaluator(), path.getPoints().toArray());
        anim.setDuration(5000);
        anim.start();
    }

    public void setMarkerMotion(PathPoint newLoc) {

        LatLng nextFrame = new LatLng(newLoc.mX, newLoc.mY);
        markerPlane.setPosition(nextFrame);
    }

}
