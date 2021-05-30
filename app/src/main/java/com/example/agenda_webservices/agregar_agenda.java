package com.example.agenda_webservices;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class agregar_agenda extends Activity {
    String accion = "nuevo";
    String id = "";
    String rev = "";
    JSONObject datosJSON;
    String resp;

    @Override
    protected void onCreate(@Nullable Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.agregar_agenda);

        Bundle parametros = getIntent().getExtras();
        accion = parametros.getString("accion");

        if (accion.equals("modificar")) {
            try {
                datosJSON = new JSONObject(parametros.getString("valores"));

                TextView temp = (TextView) findViewById(R.id.edtCodigo);
                temp.setText(datosJSON.getString("codigo"));

                temp = (TextView) findViewById(R.id.edtNombre);
                temp.setText(datosJSON.getString("nombre"));

                temp = (TextView) findViewById(R.id.edtDireccion);
                temp.setText(datosJSON.getString("direccion"));

                temp = (TextView) findViewById(R.id.edtTelefono);
                temp.setText(datosJSON.getString("telefono"));

                temp = (TextView) findViewById(R.id.edtDui);
                temp.setText(datosJSON.getString("dui"));

                id = datosJSON.getString("id");
                rev = datosJSON.getString("rev");
            } catch (Exception ex) {
                Toast.makeText(agregar_agenda.this, "Error al recuperar datos: " + ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }


        Button btn = (Button) findViewById(R.id.btnGuardar);
        btn.setOnClickListener((v) -> {

            TextView temp = (TextView) findViewById(R.id.edtCodigo);
            String codigo = temp.getText().toString();

            temp = (TextView) findViewById(R.id.edtNombre);
            String nombre = temp.getText().toString();

            temp = (TextView) findViewById(R.id.edtDireccion);
            String direccion = temp.getText().toString();

            temp = (TextView) findViewById(R.id.edtTelefono);
            String telefono = temp.getText().toString();

            temp = (TextView) findViewById(R.id.edtDui);
            String dui = temp.getText().toString();

            JSONObject miData = new JSONObject();
            try {

                if (accion.equals("modificar")) {
                    miData.put("_id", id);
                    miData.put("_rev", rev);
                }
                miData.put("codigo", codigo);
                miData.put("nombre", nombre);
                miData.put("direccion", direccion);
                miData.put("telefono", telefono);
                miData.put("dui", dui);

                enviarDatos objEnviar = new enviarDatos();
                objEnviar.execute(miData.toString());

            } catch (Exception ex) {
                Toast.makeText(agregar_agenda.this, "Error al guardar: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }

        });
        FloatingActionButton btnRegAmigos = (FloatingActionButton) findViewById(R.id.btnAgregar);
        btnRegAmigos.setOnClickListener((v) -> {
            Intent regresar = new Intent(agregar_agenda.this, MainActivity.class);
            startActivity(regresar);

        });
    }

    private class enviarDatos extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            String JsonResponse = null;
            String JsonDATA = params[0];
            BufferedReader reader = null;

            try {
                URL url = new URL("https//10.0.2.2:5984/db_agenda/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "aplicatiion/json");
                urlConnection.setRequestProperty("Accept", "aplication/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");

                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i(TAG, JsonResponse);

                return JsonResponse;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("ok")) {
                    Toast.makeText(agregar_agenda.this, "Regristro almacenadpo con exito.", Toast.LENGTH_LONG).show();
                    Intent regresar = new Intent(agregar_agenda.this, MainActivity.class);
                    startActivity(regresar);
                } else {
                    Toast.makeText(agregar_agenda.this, "Error al intentar almacenar el registro...", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                Toast.makeText(agregar_agenda.this, "Error el enviar a la red: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
