package com.androidsample.applevantamientoinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DinamicForm extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinamic_form);

        JSONObject formConfig = null;
        try {
            formConfig = new JSONObject(getIntent().getStringExtra("FormConfig"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        createForm(formConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dinamic_form, menu);
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

    private void createForm(JSONObject formConfig){
        final LinearLayout mlinearLayout = (LinearLayout) findViewById(R.id.mlinearLayout);

        TextView textView;
        View fieldView;

        try {
            JSONArray fieldsConfig = formConfig.getJSONArray("Fields");

            for (int i = 0; i < fieldsConfig.length(); i++){

                JSONObject field = fieldsConfig.getJSONObject(i);
                String fieldName = field.getString("FieldName");

                textView = new TextView(this);
                textView.setText(fieldName);
                textView.setTextSize(18);

                if(field.getBoolean("Domain")){
                    JSONArray domains = field.getJSONArray("Domains");

                    fieldView = createField(null, fieldName, true, domains);

                    mlinearLayout.addView(textView);
                    mlinearLayout.addView(fieldView);

                } else {
                    fieldView = createField(field.getString("FieldType"), fieldName, false, null);

                    mlinearLayout.addView(textView);
                    mlinearLayout.addView(fieldView);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            showAlert();
        }

    }

    private View createField(String fieldType, String fielName, Boolean hasDomains, JSONArray domains){
        LinearLayout.LayoutParams mRparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View view;
        Spinner spinner;
        EditText edit;

        if(hasDomains){
            spinner = new Spinner(this);
            //spinner.setId(i);
            spinner.setTag(fielName);
            spinner.setLayoutParams(mRparams);

            List<CodeValue> codeValueList = createCodeValueList(domains);

            ArrayAdapter<CodeValue> spinnerArrayAdapter = new ArrayAdapter<CodeValue>(this, android.R.layout.simple_spinner_item, codeValueList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CodeValue cv = (CodeValue) parent.getItemAtPosition(position);
                    cv.getCode();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            view = spinner;
        } else {
            edit = new EditText(this);

            edit.setTag(fielName);
            edit.setLayoutParams(mRparams);

            if(fieldType.equals("Int")){
                edit.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else{
                edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }

            view = edit;

        }

        return view;
    }

    private List<CodeValue> createCodeValueList(JSONArray domains){

        JSONObject domain;
        List<CodeValue> codeValueList = new ArrayList<CodeValue>();
        CodeValue codeValue;

        for (int i = 0; i < domains.length(); i++){

            try {
                domain = domains.getJSONObject(i);
                codeValue = new CodeValue(domain.getInt("Code"), domain.getString("Value"));
                codeValueList.add(codeValue);
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert();
            }

        }

        return codeValueList;

    }

    private AlertDialog.Builder showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DinamicForm.this);
        builder.setMessage("Ha ocurrido un error al generar el formulario");
        builder.setCancelable(false);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DinamicForm.this.finish();
            }
        });

        return builder;
    }
}
