package com.example.jose.e_voto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jose.e_voto.models.presidenteModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

public class Voto extends AppCompatActivity  {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ListView lv1;
    int seleccion=10;
    String matricula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voto);
        Intent i=getIntent();
        matricula=i.getStringExtra("matricula");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);

        lv1= (ListView) findViewById(R.id.lv1);
        new AsyncVotoP().execute();
    }

    public void votar(View view){
        if(seleccion==10){
            Toast.makeText(this,"selecciona a alguien para continuar",Toast.LENGTH_SHORT).show();
        }else{
            new AsyncVotacion().execute(matricula, String.valueOf(seleccion+1));
        }
    }


    private class AsyncVotacion extends AsyncTask<String,String,String>{
        ProgressDialog pdLoading=new ProgressDialog(Voto.this);
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
                url=new URL("http://e-voto.webcindario.com/votaciones.php");
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
                        .appendQueryParameter("id_cand",params[1]);
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
                Toast.makeText(Voto.this,"Gracias por realizar su voto",Toast.LENGTH_LONG).show();
                Intent i=new Intent(Voto.this,MainActivity.class);
                startActivity(i);
                finish();
            }else if(result.equalsIgnoreCase("")){

                Toast.makeText(getApplicationContext(),"Ha ocurrido un error intentelo nuevamente",Toast.LENGTH_LONG).show();
            }else if(result.equalsIgnoreCase("exception")||result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(getApplicationContext(),"OOPS! Algo salio mal. Problemas de conexion",Toast.LENGTH_LONG).show();
            }

        }
    }
    private class AsyncUpdate extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }



    private class AsyncVotoP extends AsyncTask<String,String,List<presidenteModel>>{
        ProgressDialog pdLoading=new ProgressDialog(Voto.this);
        HttpURLConnection conn;
        URL url=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected List<presidenteModel> doInBackground(String... params) {
            String voto = "voto";

            try {
                url = new URL("http://e-voto.webcindario.com/pruebajson.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("peticion", voto);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line = null;
                    line = reader.readLine();
                    result.append(line);
                    String finalJSON = result.toString();
                    JSONObject parentObject = new JSONObject(finalJSON);
                    JSONArray presidentes = parentObject.getJSONArray("presidentes");

                    List<presidenteModel> presidenteModelList=new ArrayList<>();
                    for(int i=0;i<presidentes.length();i++) {

                        JSONObject candidato = presidentes.getJSONObject(i);
                        presidenteModel presidenteModel=new presidenteModel();
                        presidenteModel.setNombre(candidato.getString("nombre"));
                        presidenteModel.setPartido(candidato.getString("partido"));
                        presidenteModel.setFoto(candidato.getString("foto"));
                        presidenteModelList.add(presidenteModel);
                    }
                    return presidenteModelList;
                    //return (result.toString());
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(List<presidenteModel> result) {
            super.onPostExecute(result);
            presidentesAdapter adapter=new presidentesAdapter(getApplicationContext(),R.layout.row,result);
            lv1.setAdapter(adapter);
            lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    seleccion=i;
                    try{
                        for (int ctr=0;ctr<=lv1.getChildCount();ctr++){
                            if(i==ctr){
                                lv1.getChildAt(ctr).setBackgroundColor(Color.CYAN);
                            }else{
                                lv1.getChildAt(ctr).setBackgroundColor(Color.TRANSPARENT);
                            }
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            pdLoading.dismiss();



        }

    }

    public class presidentesAdapter extends ArrayAdapter{
        public  List<presidenteModel> presidenteModelList;
        private int resources;
        private LayoutInflater inflater;

        public presidentesAdapter(Context context, int resource, List<presidenteModel> objects) {
            super(context, resource, objects);
            presidenteModelList=objects;
            this.resources=resource;
            inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.row,null);
                holder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
                holder.tv1= (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2= (TextView) convertView.findViewById(R.id.tv2);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }




            final ProgressBar progressBar= (ProgressBar) convertView.findViewById(R.id.progressBar);



            ImageLoader.getInstance().displayImage(presidenteModelList.get(position).getFoto(), holder.imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            holder.tv1.setText(presidenteModelList.get(position).getNombre());
            holder.tv2.setText(presidenteModelList.get(position).getPartido());

        return convertView;
        }
        class ViewHolder{
            private ImageView imageView;
            private TextView tv1;
            private TextView tv2;

        }
    }
}
