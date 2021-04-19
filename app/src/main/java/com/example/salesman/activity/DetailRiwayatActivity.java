package com.example.salesman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.example.salesman.adapter.CariBarangAdapter;
import com.example.salesman.adapter.RiwayatBarangAdapter;
import com.example.salesman.databinding.ActivityDetailRiwayatBinding;
import com.example.salesman.model.BarangModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailRiwayatActivity extends AppCompatActivity {
    private ActivityDetailRiwayatBinding binding;
    String link = BuildConfig.BASE_API;
    List<BarangModel> barangs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRiwayatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);


        Intent intent = new Intent(getIntent());


        binding.rvRiwayatBarang.setHasFixedSize(true);
        binding.rvRiwayatBarang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvRiwayatBarang.setNestedScrollingEnabled(true);
        barangs = new ArrayList<>();


        binding.namaCost.setText(intent.getStringExtra("nama"));
        binding.alamatCost.setText(intent.getStringExtra("alamat"));
        binding.totalBelanja.setText(intent.getStringExtra("total"));

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBarang(intent);
    }

    private void getBarang(Intent intent) {
        Log.d("data", "cods ex : "+" isi : "+intent.getStringExtra("idOrd"));
        AndroidNetworking.get(link+"riwayatBarang?id="+intent.getStringExtra("idOrd"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONArray data = response.getJSONArray("data");
                                Log.d("data", "codse : " + data);
                                barangs.clear();
                                for (int i=0; i<data.length(); i++){
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangModel(
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            "",
                                            "",
                                            barang.getString("foto_barang"),
                                            barang.getString("harga")
                                    ));
                                }
                                RiwayatBarangAdapter cariAdapter = new RiwayatBarangAdapter(barangs);
                                binding.rvRiwayatBarang.setAdapter(cariAdapter);
                                cariAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "data = "+anError);
                        Toast.makeText(DetailRiwayatActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}