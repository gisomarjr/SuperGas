package br.com.gisomarkos.supergas;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


public class MapsActivity extends Activity implements OnMapReadyCallback {

    double latitude;
    double longitude;
    LatLng mapCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap map) {

        GPS gps = new GPS(this);

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // não pôde pegar a localização
            // GPS ou a Rede não está habilitada
            // Pergunta ao usuário para habilitar GPS/Rede em configurações
            gps.showSettingsAlert();
        }

        //seta a localização atual no mapa
        mapCenter = new LatLng(latitude, longitude);

        //movendo a camera para localização atual
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        final Marker makerMap = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icone_gas_pequeno))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .title("Fornecedor")
                .snippet("Nome: Gisomar")
                .position(new LatLng(-8.060483,-34.901429)));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_LONG).show();
            }
        });

        // Sets the map type to be "hybrid"
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMyLocationEnabled(true);

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(200)
                .build();

        // Animate the change in camera view over 3 seconds
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000, null);
    }
}
