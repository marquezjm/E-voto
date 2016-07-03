package com.example.jose.e_voto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etcorreo;
    private EditText etpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etcorreo= (EditText) findViewById(R.id.etcorreo);
        etpass= (EditText) findViewById(R.id.etpass);
    }

    public void login(View view){
        String correo=etcorreo.getText().toString();
        String pass=etpass.getText().toString();
        new AsyncLogin().execute(correo,pass);

    }

    private class AsyncLogin extends AsyncTask<String,String,String>{
        ProgressDialog pdLoading=new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                url=new URL("http://e-voto.webcindario.com/index.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try{
                conn= (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder=new Uri.Builder()
                        .appendQueryParameter("correo",params[0])
                        .appendQueryParameter("password",params[1]);
                String query=builder.build().getEncodedQuery();

                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
            try{
                int response_code=conn.getResponseCode();
                if(response_code==HttpURLConnection.HTTP_OK){
                    InputStream input=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(input));
                    StringBuilder result=new StringBuilder();
                    String line=null;
                    line=reader.readLine();
                    //while((line=reader.readLine())!=null){
                        result.append(line);
                   // }
                    return (result.toString());
                }else{
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
//            super.onPostExecute(s);
            pdLoading.dismiss();
            String texto=result;
            String[]campos=texto.split("\\s+");
            if(campos[0].equalsIgnoreCase("1")){
                //Lanzar la segunda vista
                Toast.makeText(getApplicationContext(),"Inicio correcto, bienvenid@ "+campos[1],Toast.LENGTH_LONG).show();

            }else if(result.equalsIgnoreCase("")){
                etpass.setText("");
                etcorreo.setText("");
                Toast.makeText(getApplicationContext(),"Correo o contraseña invalido",Toast.LENGTH_LONG).show();
            }else if(result.equalsIgnoreCase("exception")||result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(getApplicationContext(),"OOPS! Algo salio mal. Problemas de conexion",Toast.LENGTH_LONG).show();
            }

        }
    }

    public void registro(View view){
        Intent i=new Intent(getApplicationContext(),Registro.class);
        startActivity(i);
    }
}
