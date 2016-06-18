package com.androidsample.applevantamientoinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import com.androidsample.applevantamientoinventario.HttpClient.AsynResponse;
import com.androidsample.applevantamientoinventario.HttpClient.HttpClient;



public class FormulariosList extends Activity implements AsynResponse {

    private ListView formulariosList;
    private JSONArray formularios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formularios_list);

        formulariosList = (ListView) findViewById(android.R.id.list);

        HttpClient httpClient = new HttpClient(this, "http://192.168.0.6:81/practica/home/", "GetConfig", "get");

        httpClient.delegate = this;

        httpClient.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_formularios_list, menu);
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

    @Override
    public void processFinish(String output) {
        try {
            JSONObject formulario;
            JSONObject jsonObject = new JSONObject(output);
            formularios = jsonObject.getJSONArray("Result");

            ArrayList forms = new ArrayList();

            for (int i = 0; i < formularios.length(); i++){
                formulario = formularios.getJSONObject(i);
                String formName = formulario.getString("Title");
                forms.add(formName);
            }

            ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(FormulariosList.this, android.R.layout.simple_list_item_1, forms);

            formulariosList.setAdapter(listViewAdapter);

            formulariosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedForm = (String) (formulariosList.getItemAtPosition(position));

                    openForm(view, selectedForm);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(FormulariosList.this, output, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void openForm(View v, String formName){
        JSONObject formulario;

        for (int i = 0; i < formularios.length(); i++){
            try{
                formulario = formularios.getJSONObject(i);
                if(formulario.getString("Title").equals(formName)){
                    Intent intent = new Intent(FormulariosList.this, DinamicForm.class);
                    intent.putExtra("FormConfig", formulario.toString());

                    startActivity(intent);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }

    }
}
