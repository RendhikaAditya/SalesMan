
package com.example.salesman.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.databinding.ActivityLoginBinding;
import com.example.salesman.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    PrefManager prefManager;
    String link = BuildConfig.BASE_API;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);
        alertDialog = new SpotsDialog.Builder().setContext(this).setMessage("Sedang Mencoba Masuk ....").setCancelable(false).build();

        if (prefManager.getLoginStatus()){
            Intent intent = new Intent(LoginActivity.this, CariCostumerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogin();
            }
        });

    }

    private void getLogin() {
        alertDialog.show();
        AndroidNetworking.post(link+"sales/login")
                .addBodyParameter("username", binding.etUsername.getText().toString())
                .addBodyParameter("password", binding.etPassword.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        alertDialog.hide();
                        Log.d("code","data : "+response);
                        try {
                            if (response.getString("code").equalsIgnoreCase("200")){
                                JSONObject data = response.getJSONObject("data");
                                prefManager.setIdUser(data.getString("id_sales"));
                                prefManager.setNamaUser(data.getString("nama_sales"));
                                prefManager.setLoginStatus(true);
                                Intent intent = new Intent(LoginActivity.this, CariCostumerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        alertDialog.hide();
                        Log.d("code","eror : "+anError);
                        Toast.makeText(LoginActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}