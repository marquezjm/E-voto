package com.example.jose.e_voto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Registro extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    EditText mat;
    EditText pass;
    EditText pass1;
    EditText correo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mat= (EditText) findViewById(R.id.matricula);
        pass= (EditText) findViewById(R.id.pass);
        pass1= (EditText) findViewById(R.id.pass1);
        correo= (EditText) findViewById(R.id.correo);

    }

    public void enviar(View view){
        String matricula=mat.getText().toString();
        String password=pass.getText().toString();
        String password1=pass1.getText().toString();
        String mail=correo.getText().toString();

        if(password.equals(password1)){
            new AsyncReg().execute(matricula,password,mail);
        }else{
            Toast.makeText(getApplicationContext(),"Las contraseñas ingresadas no son iguales",Toast.LENGTH_SHORT ).show();
        }

    }

    private class AsyncReg extends AsyncTask<String,String,String>{
        ProgressDialog pdLoading=new ProgressDialog(Registro.this);
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
            String registro="registro";
            try{
                url=new URL("http://e-voto.webcindario.com/metodos.php");
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
                        .appendQueryParameter("matricula",params[0])
                        .appendQueryParameter("password",params[1])
                        .appendQueryParameter("correo",params[2])
                        .appendQueryParameter("peticion",registro);
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
            //super.onPostExecute(s);
            pdLoading.dismiss();
            String texto=result;
            String[]campos=texto.split("\\s+");
            if(campos[0].equalsIgnoreCase("1")){
                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"El usuario fue creado correctamente ",Toast.LENGTH_LONG).show();
            }else if(campos[0].equalsIgnoreCase("0")){
                mat.setText("");
                pass.setText("");
                pass1.setText("");
                correo.setText("");
                Toast.makeText(getApplicationContext(),"matricula invalida",Toast.LENGTH_LONG).show();
            }else if(result.equalsIgnoreCase("exception")||result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(getApplicationContext(),"OOPS! Algo salio mal. Problemas de conexion",Toast.LENGTH_LONG).show();
            }
        }
    }
}
