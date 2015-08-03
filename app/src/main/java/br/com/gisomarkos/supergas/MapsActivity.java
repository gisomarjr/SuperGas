package br.com.gisomarkos.supergas;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;


public class MapsActivity extends Activity implements OnMapReadyCallback {

    double latitudeAtual;
    double longitudeAtual;
    LatLng meuLocal;
    Marker marker;
    ArrayList<LatLng> outroLocal = new ArrayList<>();
    GoogleMap _map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0, new Intent("ALARME_DISPARADO"), PendingIntent.FLAG_NO_CREATE) == null);

        if(alarmeAtivo){
            Log.i("Script", "Novo alarme");

            Intent intent = new Intent("ALARME_DISPARADO");
            PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 5000, p);
        }
        else{
            Log.i("Script", "Alarme ja ativo");
        }

    }

    @Override
    public void onDestroy(){
        Intent intent = new Intent("ALARME_DISPARADO");
		PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

		AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarme.cancel(p);
        super.onDestroy();
    }



    public void addMarker(LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

        marker = _map.addMarker(options);
    }


    @Override
    public void onMapReady(final GoogleMap map) {

        GPS gps = new GPS(this);

        this._map = map;

        if(gps.canGetLocation()){
            latitudeAtual = gps.getLatitude();
            longitudeAtual = gps.getLongitude();
        }else{
            // não pôde pegar a localização
            // GPS ou a Rede não está habilitada
            // Pergunta ao usuário para habilitar GPS/Rede em configurações
            gps.showSettingsAlert();
        }

        //seta a localização atual no mapa
        meuLocal = new LatLng(latitudeAtual, longitudeAtual);

        outroLocal.add(new LatLng(-8.060483, -34.901429));
        outroLocal.add(new LatLng(-8.1207278,-34.8931794));
        outroLocal.add(new LatLng(-8.1116051,-34.9327198));
        outroLocal.add(new LatLng(-8.1265263,-34.9232858));
        outroLocal.add(new LatLng(-8.1203119,-34.9542641));
        outroLocal.add(new LatLng(-8.1228291,-34.9548113));
        outroLocal.add(new LatLng(-8.1234132,-34.9549615));

        //movendo a camera para localização atual
        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(meuLocal, 13));

        // EVENTS - mudou a posicao da camera? pega latitude e longitude
        _map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i("Script", "setOnCameraChangeListener()");

                    LatLng latitudeCamera = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

                    if(cameraPosition.zoom > 17) {

                        for (int i = 0; i < outroLocal.size(); i++) {

                            if (distance(latitudeCamera, outroLocal.get(i)) < 1000) {
                                addMarker(outroLocal.get(i), "Fornecedor" + i, "Gisomar");
                            }

                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"É necessário aproximar do mapa para verificar fornecedores...",Toast.LENGTH_SHORT).show();
                    }

            }
        });


        _map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(getApplicationContext(), DetalheEscolhaFornecedor.class);
                intent.putExtra("nome", marker.getTitle());
                intent.putExtra("latitute", marker.getPosition().latitude);
                intent.putExtra("longitude", marker.getPosition().longitude);
                startActivity(intent);
            }
        });

        // Sets the map type to be "hybrid"
        _map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        _map.setMyLocationEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(meuLocal).zoom(18).bearing(0).tilt(0).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        _map.animateCamera(update, 3000, new GoogleMap.CancelableCallback(){
            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }
        });

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
