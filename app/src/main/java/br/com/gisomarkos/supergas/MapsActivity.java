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
import android.util.Log;
import android.widget.Toast;


public class MapsActivity extends Activity implements OnMapReadyCallback {

    double latitude;
    double longitude;
    LatLng meuLocal;
    LatLng outroLocal;
    private Marker marker;
    GoogleMap _map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void addMarker(LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

        marker = _map.addMarker(options);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        GPS gps = new GPS(this);

        this._map = map;

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
        meuLocal = new LatLng(latitude, longitude);
        outroLocal = new LatLng(-8.060483, -34.901429);
        //mapCenter = new LatLng(-8.060483, -34.901429);

        //movendo a camera para localização atual
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(meuLocal, 13));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.


        // EVENTS - mudou a posicao da camera? pega latitude e longitude
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i("Script", "setOnCameraChangeListener()");

                    LatLng latitudeCamera = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    if(distance(latitudeCamera,outroLocal) < 1000) {
                        addMarker(outroLocal, "Fornecedor", "Gisomar");
                        Toast.makeText(getApplicationContext(),"chegou no local",Toast.LENGTH_LONG).show();
                    }
					/*if(marker != null){
						marker.remove();
					}
					customAddMarker(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude), "1: Marcador Alterado", "O Marcador foi reposicionado");
					*/
            }
        });

           //se a distancia do local atual for menor  do que algum metro definido - adicionar o marcador no mapa
         /*   if(distance(meuLocal,outroLocal) <= 1000) {

                final Marker makerMap = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icone_gas_pequeno))
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .title("Fornecedor")
                        .snippet("Nome: Gisomar")
                        .position(outroLocal));//-8.060483, -34.901429
                Toast.makeText(this,"distancia: " + distance(meuLocal,outroLocal) + "metros",Toast.LENGTH_LONG).show();

            }*/

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
                .target(meuLocal)
                .zoom(13)
                .bearing(200)
                .build();

        // Animate the change in camera view over 3 seconds
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000, null);
    }

    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }
}
