package com.example.salesman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.adapter.KategoriAdapter;
import com.example.salesman.databinding.ActivityMainBinding;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.util.InterfaceHarga;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements InterfaceHarga {
    private ActivityMainBinding binding;
    String link = BuildConfig.BASE_API;
    String idCost;
    private int mPreviousTotal = 0;

    List<KategoriModel> kategoris;
    boolean isLoading = false;
    int currentItem, totalItem, scrolOutItem;
    int harga = 0;
    LinearLayoutManager manager;
    KategoriAdapter adapter;
    String loadUrl;
    String namaCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent intent = new Intent(getIntent());
        namaCost = intent.getStringExtra("NAMA");


        binding.rvBarang.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvBarang.setLayoutManager(manager);
        binding.rvBarang.setNestedScrollingEnabled(true);
        initScrollListener();

        kategoris = new ArrayList<>();

        binding.koneksi.layoutKoneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataCost();
            }
        });

        getDataCost();
        binding.namaCost.setText(namaCost);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setLoading() {
//        binding.swBranda.setRefreshing(true);
        binding.layoutBranda.setVisibility(View.GONE);
        binding.shimmerBeranda.setVisibility(View.VISIBLE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerBeranda.startShimmer();
    }

    private void setSukses() {
//        binding.swBranda.setRefreshing(false);
        binding.layoutBranda.setVisibility(View.VISIBLE);
        binding.shimmerBeranda.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerBeranda.stopShimmer();
    }

    private void setGagal() {
//        binding.swBranda.setRefreshing(false);
        binding.layoutBranda.setVisibility(View.GONE);
        binding.shimmerBeranda.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.VISIBLE);
        binding.shimmerBeranda.startShimmer();
    }

    private void getDataCost() {
        setLoading();
        AndroidNetworking.post(link+"costumer/get")
                .addBodyParameter("nama", namaCost)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("data", "code : "+response);
                            JSONObject meta = response.getJSONObject("meta");
                            if (meta.getString("code").equalsIgnoreCase("200")){
                                JSONArray data = response.getJSONArray("data");
                                JSONObject cost = data.getJSONObject(0);
                                binding.alamatCost.setText(cost.getString("alamat_costumer"));
                                idCost =cost.getString("id_costumer");
                                getData();

                            }else {
                                setGagal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : "+anError);
                        setGagal();
                    }
                });
    }

    private void getData() {
        AndroidNetworking.get(link+"kategori")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "codse : "+response);
                            if (meta.getString("code").equalsIgnoreCase("200")){
                                setSukses();
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                kategoris.clear();
                                for (int i=0; i<data.length(); i++){
                                    JSONObject kat = data.getJSONObject(i);
                                    kategoris.add(new KategoriModel(
                                            kat.getInt("id_kategori"),
                                            kat.getString("nama_kategori")
                                    ));
                                }

                                    loadUrl = datas.getString("next_page_url");
                                    Log.d("new", "URL : "+loadUrl);
                                    getAdapter();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror kat First", "code : "+anError);
                        setGagal();
                    }
                });
    }

    private void getAdapter() {
        adapter = new KategoriAdapter(kategoris, this::onUpdateTotal);
        binding.rvBarang.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initScrollListener() {

        if (binding.layoutBranda != null) {
            binding.layoutBranda.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
//                    if (v.getChildAt(0).getBottom()<=(nestedScroll.getHeight()+scrollY)) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        Log.i("TAG", "BOTTOM SCROLL");
                        binding.loadMore.setVisibility(View.VISIBLE);
                        if (!isLoading){
                            loadMore();
                            isLoading = true;
                        }
                    }
                    if (scrollY < oldScrollY) {
                        Log.i("TAG", "Scroll UP");
                    }
                    if (scrollY > oldScrollY) {
                        Log.i("TAG", "Scroll DOWN");
                    }
                    if (scrollY == 0) {
                        getDataCost();
                        Log.i("TAG", "TOP SCROLL");
                    }
                }
            });
        }
    }

    private void loadMore() {
        AndroidNetworking.get(loadUrl)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "code x : "+response+" Link | "+loadUrl);
                            if (meta.getString("code").equalsIgnoreCase("200")){

                                binding.loadMore.setVisibility(View.GONE);
                                isLoading=false;
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                for (int i=0; i<data.length(); i++){
                                    JSONObject kat = data.getJSONObject(i);
                                    kategoris.add(new KategoriModel(
                                            kat.getInt("id_kategori"),
                                            kat.getString("nama_kategori")
                                    ));
                                }

                                    loadUrl = datas.getString("next_page_url");

                                adapter.notifyDataSetChanged();
                                isLoading = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror kat More", "code x : "+anError);
                        isLoading=false;
                        binding.loadMore.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Data Terakhir", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUpdateTotal(int tot) {
        harga = tot;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (tot>=1){
            binding.totalLayout.setVisibility(View.VISIBLE);
            binding.totalBelanja.setText(formatRupiah.format((double)tot));
        }else {
            binding.totalLayout.setVisibility(View.GONE);
        }

        Log.d("TAG", "tot : "+tot);
    }
}