package com.example.salesman.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.MainActivity;
import com.example.salesman.R;
import com.example.salesman.databinding.ActivityCariCostumerBinding;
import com.example.salesman.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CariCostumerActivity extends AppCompatActivity {

    private ActivityCariCostumerBinding binding;
    AlertDialog alertDialog;
    String link = BuildConfig.BASE_API;
    ArrayList<String> cost = new ArrayList<>();
    ArrayList<String> costLokasi = new ArrayList<>();
    ArrayAdapter<String> adapterLokasi;
    ArrayAdapter<String> adapter;
    String nama;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCariCostumerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);
        alertDialog = new SpotsDialog.Builder().setContext(this).setMessage("Sedang Mengambil Data ....").setCancelable(false).build();

        getCost();

        Intent i = getIntent();
        nama = i.getStringExtra("token");

        binding.namaCost.setText(nama);

        binding.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CariCostumerActivity.this,ScannerActivity.class);
                startActivity(i);
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setLoginStatus(false);
                prefManager.setLvl("");
                prefManager.setIdUser("");
                Intent intent = new Intent(CariCostumerActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void getCost() {
        Log.d("log ","Link : "+link+"costumer");
        alertDialog.show();
        Log.d("url ","code:"+link);
        AndroidNetworking.get(link+"costumer")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(CariCostumerActivity.this, "data dapat", Toast.LENGTH_SHORT).show();
                            Log.d("data", "cote : " + response + " | ");
                            JSONObject meta = response.getJSONObject("meta");
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONArray d = response.getJSONArray("data");
                                getCostLokasi();
                                Log.d("data", "yeay : ");
                                for (int i = 0; i < d.length(); i++) {
                                    JSONObject data = d.getJSONObject(i);
                                    cost.add(data.getString("nama_costumer"));
                                    Log.d("cost,", "item :" + cost.size());
                                }
                                setAdapter();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        alertDialog.hide();
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(CariCostumerActivity.this, "Pastikan Prangkat Terhubung Ke Internet", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void getCostLokasi() {
        AndroidNetworking.get(link+"costumer-regional?id_sales="+prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.hide();
                            JSONArray dataCost = response.getJSONArray("costumer");
                            Log.d("dataCOst", "data : "+dataCost);
                            if (dataCost.length()>0){
                                for (int i=0;i<dataCost.length();i++) {
                                    JSONObject data = dataCost.getJSONObject(i);
                                    costLokasi.add(data.getString("costumer"));
                                }
                                setAdapterLookasi();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        alertDialog.hide();
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(CariCostumerActivity.this, "Pastikan Prangkat Terhubung Ke Internet", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void setAdapterLookasi() {
        adapterLokasi = new ArrayAdapter<>(this,
                R.layout.row_item_1, costLokasi);
        binding.rvCost.setAdapter(adapterLokasi);


        Log.d("data", "hasil : " + costLokasi);
        binding.rvCost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CariCostumerActivity.this, MainActivity.class);
                intent.putExtra("NAMA", costLokasi.get(position));
                binding.namaCost.setText("");
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setAdapter() {
        adapter = new ArrayAdapter<>(this,
                R.layout.auto_complete_row, cost);
        binding.namaCost.setAdapter(adapter);
        binding.namaCost.setDropDownBackgroundResource(R.drawable.shape_ed);


        Log.d("data", "hasil : " + cost);
        binding.namaCost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CariCostumerActivity.this, MainActivity.class);
                intent.putExtra("NAMA", binding.namaCost.getText().toString());
                binding.namaCost.setText("");
                startActivity(intent);
            }
        });

    }
}