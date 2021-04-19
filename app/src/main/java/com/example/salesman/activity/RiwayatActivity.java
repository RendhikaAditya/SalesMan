package com.example.salesman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.MainActivity;
import com.example.salesman.adapter.RiwayatAdapter;
import com.example.salesman.databinding.ActivityRiwayatBinding;
import com.example.salesman.model.RiwayatModel;
import com.example.salesman.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {
    private ActivityRiwayatBinding binding;
    PrefManager prefManager;
    List<RiwayatModel> riwayats;
    String link = BuildConfig.BASE_API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        binding.rvRiwayat.setHasFixedSize(true);
        binding.rvRiwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvRiwayat.setNestedScrollingEnabled(true);
        riwayats = new ArrayList<>();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiwayatActivity.this, CariCostumerActivity.class);
                startActivity(intent);
            }
        });

        getRiwayat();
        binding.swRiwayat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRiwayat();
            }
        });

    }

    private void getRiwayat() {
        setLoading();
        AndroidNetworking.post(link+"riwayat")
                .addBodyParameter("id", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject meta = null;
                        setSukses();
                        try {
                            meta = response.getJSONObject("meta");
                            if (meta.getString("code").equalsIgnoreCase("200")){
                                Gson gson = new Gson();
                                riwayats.clear();
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject d = data.getJSONObject(i);
                                    RiwayatModel Isi = gson.fromJson(d + "", RiwayatModel.class);
                                    riwayats.add(Isi);
                                }
                                RiwayatAdapter adapter = new RiwayatAdapter(riwayats);
                                binding.rvRiwayat.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else {
                                setGagal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        setGagal();
                    }
                });
    }

    private void setLoading() {
        binding.swRiwayat.setRefreshing(true);
        binding.shimmerRiwayat.setVisibility(View.VISIBLE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerRiwayat.startShimmer();
    }

    private void setSukses() {
        binding.swRiwayat.setRefreshing(false);
        binding.shimmerRiwayat.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerRiwayat.stopShimmer();
    }

    private void setGagal() {
        binding.swRiwayat.setRefreshing(false);
        binding.shimmerRiwayat.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.VISIBLE);
        binding.shimmerRiwayat.startShimmer();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RiwayatActivity.this, CariCostumerActivity.class);
        startActivity(intent);
    }
}