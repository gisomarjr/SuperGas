package br.com.gisomarkos.supergas;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import br.com.gisomarkos.supergas.R;

public class DetalheEscolhaFornecedor extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_escolha_fornecedor);

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        Double latitude = intent.getDoubleExtra("latitute",1);
        Double longitude = intent.getDoubleExtra("longitude",1);

        TextView nomeText = (TextView) findViewById(R.id.nome);
        nomeText.setText(nome);

        TextView latitudeText = (TextView) findViewById(R.id.latitude);
        latitudeText.setText("latitude:" +latitude);

        TextView longitudeText = (TextView) findViewById(R.id.longitude);
        longitudeText.setText("longitude" +longitude);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe_escolha_fornecedor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
