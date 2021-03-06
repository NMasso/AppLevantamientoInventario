package com.androidsample.applevantamientoinventario.HttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Nestor on 6/16/2016.
 */
public class HttpClient extends AsyncTask<String, Void, String > {

    private final Context context;
    private ProgressDialog progressDialog;
    private String progressDialogMessage = "Cargando";
    private String clientUrl;
    private String actionService;
    private String method;
    private HashMap<String, Object> hashMap = null;
    private String jsonString = null;
    public AsynResponse delegate = null;
    private String response;

    public HttpClient(Context context, String clientUrl, String actionService, String method) {
        this.context = context;
        this.clientUrl = clientUrl;
        this.actionService = actionService;
        this.method = method;
    }

    public void setGetData(HashMap<String, Object> hashMap){
        this.hashMap = hashMap;
    }

    public void setProgressDialogMessage(String progressDialogMessage) {
        this.progressDialogMessage = progressDialogMessage;
    }

    public void setJsonString(String jsonString){
        this.jsonString = jsonString;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection;

        //Se concatena la url y el nombre del servicio
        String stringUrl = clientUrl + actionService;

        if(method.equalsIgnoreCase("POST")){

            try {

                //Se declara la configuracion para la peticion POST
                url = new URL(stringUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(6000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Content-Lenght", String.valueOf(jsonString.length()));
                urlConnection.setFixedLengthStreamingMode(jsonString.getBytes().length);

                //Se prepara el output para enviar los datos al servicio
                OutputStream outputStream = urlConnection.getOutputStream();

                byte[] dataByte = jsonString.getBytes();

                outputStream.write(dataByte);
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();

                //Se valida si el servidor responde correctamente
                if(responseCode == HttpURLConnection.HTTP_OK){
                    //Se recibe la respuesta
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                    response = bufferedReader.readLine();
                } else{
                    response = "No se ha podido establecer la conexion al servicio";
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "No se ha podido establecer la conexion al servidor";
            }

        } else{

            try {
                if(hashMap!=null){
                    stringUrl = clientUrl+actionService+"?"+getGetData();
                }else{
                    stringUrl = clientUrl+actionService;
                }

                url = new URL(stringUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-type", "aplication/json");
                urlConnection.setConnectTimeout(6000);

                int responseCode = urlConnection.getResponseCode();

                //Se valida si el servidor responde correctamente
                if(responseCode == HttpURLConnection.HTTP_OK){
                    //Se recibe la respuesta
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                    response = bufferedReader.readLine();
                } else{
                    response = "No se ha podido establecer la conexion al servicio";
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "No se ha podido establecer la conexion al servidor";
            }

        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
        progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(this.context);
        progressDialog.setMessage(progressDialogMessage);
        progressDialog.show();
    }

    private String getGetData() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        if(hashMap != null){
            for(Map.Entry<String, Object> entry : this.hashMap.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                Object entryObj = entry.getValue();
                if(entryObj instanceof String){
                    result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } else {
                    result.append(entry.getValue());
                }
            }
            return result.toString();
        }

        return null;
    }
}
