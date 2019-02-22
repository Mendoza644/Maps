package com.example.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback {
    ArrayList<Place> places;
    Spinner spinnerMapType;
    SeekBar seekBarZoom;
    LatLng defaultLatLng = new LatLng(13.714966, -89.155755);
    FollowPosition followPosition;
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                places = (ArrayList<Place>) bundle.getSerializable(FetchPlacesServices.RESULT);
                if (places != null && places.size() > 0) {
                    if (mMap != null) {
                        for (Place tmp : places) {
                            LatLng tmpLatLng = new LatLng(tmp.getLatitude(), tmp.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(tmpLatLng).title(tmp.getNamePlace()));
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    Intent intent1 = new Intent(context, DetailsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("details", places);
                                    context.startActivity(intent1);
                                    return true;
                                }
                            });
                        }
                    }
                }
            }
            //FIN DEL CÓDIGO INTERIOR CREADO POR USTED
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when
        //the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        seekBarZoom = (SeekBar) findViewById(R.id.seekBarZoom);
        //HAGA USO DEL ASISTENTE PARA CREAR setOnSeekBarChangeListener
        //El único método que modificará es onProgressChanged
        seekBarZoom.setOnSeekBarChangeListener(new
                                                       SeekBar.OnSeekBarChangeListener() {
                                                           @Override
                                                           public void onProgressChanged(SeekBar seekBar, int progress, boolean
                                                                   fromUser) {
                                                               //CODIGO INTERIOR CREADO POR USTED
                                                               chooseMoveCamera(mMap, defaultLatLng, progress);
                                                           }

                                                           @Override
                                                           public void onStartTrackingTouch(SeekBar seekBar) {
                                                           }

                                                           @Override
                                                           public void onStopTrackingTouch(SeekBar seekBar) {
                                                           }
                                                       });
        spinnerMapType = (Spinner) findViewById(R.id.spinnerMapType);
        //HAGA USO DEL ASISTENTE PARA CREAR setOnItemSelectedListener
        spinnerMapType.setOnItemSelectedListener(new
                                                         AdapterView.OnItemSelectedListener() {
                                                             @Override
                                                             public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                                                        long id) {
                                                                 //CODIGO INTERIOR CREADO POR USTED
                                                                 String mapType = spinnerMapType.getSelectedItem().toString();
                                                                 if (mMap == null) return;
                                                                 if (mapType.equals("MAP_TYPE_NORMAL")) {
                                                                     mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                                 } else if (mapType.equals("MAP_TYPE_SATELLITE")) {
                                                                     mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                                                 } else if (mapType.equals("MAP_TYPE_HYBRID")) {
                                                                     mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                                                 }
                                                                 //FIN DE CODIGO INTERIOR CREADO POR USTED
                                                             }

                                                             @Override
                                                             public void onNothingSelected(AdapterView<?> parent) {
                                                             }
                                                         });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new
                IntentFilter(FetchPlacesServices.NOTIFICATION));
        /**/
        Intent intent = new Intent(this, FetchPlacesServices.class);
        startService(intent);
        if (followPosition != null) {
            followPosition.register(MapsActivity.this);
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        if (followPosition != null)
            followPosition.unRegister(MapsActivity.this);
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this
     * case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install
     * it inside the SupportMapFragment. This method will only be triggered once the user
     * has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        followPosition = new FollowPosition(this.mMap, MapsActivity.this);
        followPosition.register(MapsActivity.this);
        //Moveremos la cámara a la Universidad Don Bosco
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
        chooseMoveCamera(mMap, defaultLatLng, 10);
        drawShapes();
    }

    //El siguiente método permitirá movernos de manera animada
    // a una posición del mapa
    private void chooseMoveCamera(GoogleMap googleMap, LatLng tmpLatLng, int
            zoom) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder().zoom(zoom).target(tmpLatLng).build();
        googleMap.animateCamera
                (CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //El siguiente método custom permite agregar diferentes figuras
    private void drawShapes() {
        ShapesMap shapesMap = new ShapesMap(this.mMap);
        //PolyLines
        ArrayList<LatLng> lines = new ArrayList<>();
        lines.add(new LatLng(13.715777, -89.152472));
        lines.add(new LatLng(13.715342, -89.152437));
        lines.add(new LatLng(13.715389, -89.151542));
        lines.add(new LatLng(13.715768, -89.151579));
        lines.add(new LatLng(13.715777, -89.152472));
        //Llamado al método custom drawLine de shapesMap
        shapesMap.drawLine(lines, 5, Color.BLUE);
        ArrayList<LatLng> linesD = new ArrayList<>();
        ArrayList<LatLng> poligon = new ArrayList<>();
        poligon.add(new LatLng(13.715389, -89.151542));
        poligon.add(new LatLng(13.715407, -89.148059));
        poligon.add(new LatLng(13.717434, -89.148355));
        poligon.add(new LatLng(13.717336, -89.151456));
        poligon.add(new LatLng(13.716741, -89.151447));
        poligon.add(new LatLng(13.716692, -89.152468));
        poligon.add(new LatLng(13.715841, -89.152558));
        poligon.add(new LatLng(13.715768, -89.151579));
        poligon.add(new LatLng(13.715389, -89.151542));
        //Transparencia
        //Valor Hexadecimal, transparencia + color
        //0x: Valor hexadecimal
        //2F: Trasparencia
        //00FF00: Color Hexadecimal
        shapesMap.drawPoligon(poligon, 5, Color.RED, 0x2F00FF00);
        //Agregando Circulo
        LatLng circlePoint = new LatLng(13.714966, -89.155755);
        shapesMap.drawCircle(circlePoint, 50, Color.GREEN, 2, Color.TRANSPARENT);
    }
}