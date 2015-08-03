package br.com.gisomarkos.supergas.Controller;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.gisomarkos.supergas.MapsActivity;
import br.com.gisomarkos.supergas.Model.Fornecedor;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by gisomarsilva on 03/08/15.
 */
public class CFornecedor  extends AsyncTask<String, Void, Fornecedor> {

    Fornecedor fornecedor;

    @Override
    protected Fornecedor doInBackground(String... url) {

        OkHttpClient client = new OkHttpClient();

        try {

            Request request = new Request.Builder()
                    .url(java.net.URLEncoder.encode(url[0].toString(), "ISO-8859-1"))
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Gson gson = new Gson();
            fornecedor = gson.fromJson(json, Fornecedor.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fornecedor;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Fornecedor fornecedor) {
        super.onPostExecute(fornecedor);

    }

}
