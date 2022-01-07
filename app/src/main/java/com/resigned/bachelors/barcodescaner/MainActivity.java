package com.resigned.bachelors.barcodescaner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private String URL;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        URL = sharedPreferences.getString("url", "");
        Toast.makeText(getApplicationContext(), URL, Toast.LENGTH_SHORT).show();
        if(URL.equals("")){
            setURL();
            Toast.makeText(this, "url is = "+URL, Toast.LENGTH_SHORT).show();
        }else {
            scan_code();
        }


    }

    private void setURL() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setText("http://");
        alert.setMessage("Enter URL to save ");
//        alert.setTitle("Enter Your Title");

        alert.setView(edittext);
        alert.setPositiveButton("Save", (dialog, whichButton) -> {
            String sstr = edittext.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("url", sstr);
            myEdit.apply();
            scan_code();
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            dialog.dismiss();
        });
        alert.show();
    }

    public void scan_code() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning....");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents()!=null){
                sendRequestToServer(result.getContents());
            }else {
                Toast.makeText(this, "No Result Found...", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendRequestToServer(String contents) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =URL+"/?bar_code_data="+contents;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(this, response+" Added Successfully", Toast.LENGTH_LONG).show();
                    scan_code();
                }, error -> {
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                });
        queue.add(stringRequest);
    }


    public void btn_click_scan(View view) {
        scan_code();
    }

    public void btn_click_url_change(View view) {
        setURL();
    }
}