package com.example.salesman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.eminayar.panter.DialogType;
import com.eminayar.panter.PanterDialog;
import com.eminayar.panter.interfaces.OnSingleCallbackConfirmListener;
import com.example.salesman.activity.KeranjangActivity;
import com.example.salesman.activity.RiwayatActivity;
import com.example.salesman.adapter.BarangAdapter;
import com.example.salesman.adapter.CariBarangAdapter;
import com.example.salesman.adapter.KategoriAdapter;
import com.example.salesman.adapter.KategoriItemAdapter;
import com.example.salesman.adapter.KeranjangBarangAdapter;
import com.example.salesman.databinding.ActivityMainBinding;
import com.example.salesman.model.BarangKeranjangModel;
import com.example.salesman.model.BarangModel;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.util.InterfaceHarga;
import com.example.salesman.util.InterfaceKategori;
import com.example.salesman.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements InterfaceHarga, InterfaceKategori {
    private ActivityMainBinding binding;
    String idCost;
    private int mPreviousTotal = 0;

    ArrayList<String> filterKat = new ArrayList<>();
    List<KategoriModel> kategoris;
    boolean isLoading = false;
    int currentItem, totalItem, scrolOutItem;
    int harga = 0;
    LinearLayoutManager manager;
    BarangAdapter adapter;
    CariBarangAdapter cariAdapter;
    String loadUrl;
    String namaCost;

    Boolean isFitered = false;

    PrefManager prefManager;
    String link = BuildConfig.BASE_API;
    List<BarangModel> barangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);


        Intent intent = new Intent(getIntent());
        namaCost = intent.getStringExtra("NAMA");

        binding.nextOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, KeranjangActivity.class);
                startActivity(intent1);
            }
        });


        binding.btnRiwayatOrderCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHistori();
            }
        });


        binding.rvKategori.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvKategori.setLayoutManager(manager);
        binding.rvKategori.setNestedScrollingEnabled(true);
        initScrollListener();

        binding.rvCariBarang.setHasFixedSize(true);
        binding.rvCariBarang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvCariBarang.setNestedScrollingEnabled(true);

        kategoris = new ArrayList<>();
        barangs = new ArrayList<>();

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
                prefManager.setLvl("");
            }
        });
        binding.searchView.setQueryHint(Html.fromHtml("<font color = #BEBEBE>" + "Cari Nama Barang Disini" + "</font>"));


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    Log.d("kosong", "cari Aktif");
                    getDataCari(newText);
                }
                return false;
            }
        });
        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getDataCost();
                return false;
            }
        });
        binding.swBranda.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataCost();
            }
        });

        binding.btnHistori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, RiwayatActivity.class);
                startActivity(intent1);
            }
        });

        binding.closeKat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.layoutKatBarang.setVisibility(View.VISIBLE);
//                binding.rvCariBarang.setVisibility(View.GONE);
//                binding.btnFilter.setVisibility(View.VISIBLE);
//                binding.btnFilterClose.setVisibility(View.GONE);
                binding.kategoriTxt.setText("Kategori");
                binding.closeKat.setVisibility(View.GONE);
                binding.rvKategori.setVisibility(View.VISIBLE);
                getData();
            }
        });
    }

    private void getHistori() {
        binding.loadSearch.setVisibility(View.VISIBLE);
        AndroidNetworking.get(link + "transaksi/histori?id=" + prefManager.getLvl())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equalsIgnoreCase("200")) {
                                binding.loadSearch.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(MainActivity.this, KeranjangActivity.class);
                                startActivity(intent1);
                            } else {
                                Toast.makeText(MainActivity.this, "Data Tidak Tersedia", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MainActivity.this, "Check Koneksi Anda", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        prefManager.setLvl("");
    }

    private void getDataFilter(String text) {
        binding.kategoriTxt.setText(text);
        binding.closeKat.setVisibility(View.VISIBLE);
        binding.rvKategori.setVisibility(View.GONE);
        binding.loadSearch.setVisibility(View.VISIBLE);
        AndroidNetworking.post(link + "barang")
                .addBodyParameter("filterKategori", text)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            binding.loadMore.setVisibility(View.GONE);
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "cods ex : " + response + " isi : " + text);
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                Log.d("data", "codse : " + data);
                                barangs.clear();
                                isFitered = true;

                                binding.loadSearch.setVisibility(View.GONE);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangModel(
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            barang.getString("id_costumer"),
                                            barang.getString("id_sales"),
                                            barang.getString("foto_barang"),
                                            barang.getString("hargaSementara")
                                    ));
//                                    Log.d("barang", "id kranjang"+barang.getInt("id_keranjang"));
                                    getCariAdapter(barangs);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.loadSearch.setVisibility(View.GONE);
                    }
                });
    }

    private void getKategori() {
        AndroidNetworking.get(link + "kategoriAll")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "codse : " + response);
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                setSukses();
                                JSONArray data = response.getJSONArray("data");
                                kategoris.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject kat = data.getJSONObject(i);
                                    filterKat.add(kat.getString("nama_kategori"));
                                    kategoris.add(new KategoriModel(
                                            kat.getInt("id_kategori"),
                                            kat.getString("nama_kategori"),
                                            kat.getString("gambar")
                                    ));
                                }
                                adpterKategroi();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror kat First", "code : " + anError);
                        setGagal();
                    }
                });
    }

    private void adpterKategroi() {
        KategoriItemAdapter kategoriItemAdapter = new KategoriItemAdapter(kategoris, this::onIdKategori);
        binding.rvKategori.setAdapter(kategoriItemAdapter);
        kategoriItemAdapter.notifyDataSetChanged();
    }

    private void getDataCari(String query) {
        binding.loadSearch.setVisibility(View.VISIBLE);
        binding.kategoriTxt.setText(query);
        binding.closeKat.setVisibility(View.VISIBLE);
        AndroidNetworking.post(link + "barang")
                .addBodyParameter("nama", query)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "cods ex : " + response + " isi : " + query);
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                Log.d("data", "codse : " + data);
                                barangs.clear();
                                binding.loadSearch.setVisibility(View.GONE);
                                binding.loadMore.setVisibility(View.GONE);
                                binding.rvKategori.setVisibility(View.GONE);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangModel(
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            barang.getString("id_costumer"),
                                            barang.getString("id_sales"),
                                            barang.getString("foto_barang"),
                                            barang.getString("hargaSementara")
                                    ));
//                                    Log.d("barang", "id kranjang"+barang.getInt("id_keranjang"));
                                    getCariAdapter(barangs);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.loadSearch.setVisibility(View.GONE);
                    }
                });

    }

    private void getCariAdapter(List<BarangModel> barangs) {
        cariAdapter = new CariBarangAdapter(barangs, this::onUpdateTotal);
        binding.rvCariBarang.setAdapter(cariAdapter);
        cariAdapter.notifyDataSetChanged();
    }

    private void setLoading() {
        binding.swBranda.setRefreshing(true);
        binding.bg.setVisibility(View.GONE);
        binding.layoutBranda.setVisibility(View.GONE);
        binding.shimmerBeranda.setVisibility(View.VISIBLE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerBeranda.startShimmer();
    }

    private void setSukses() {
        binding.bg.setVisibility(View.VISIBLE);
        binding.swBranda.setRefreshing(false);
        binding.layoutBranda.setVisibility(View.VISIBLE);
        binding.shimmerBeranda.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.GONE);
        binding.shimmerBeranda.stopShimmer();
    }

    private void setGagal() {
        binding.bg.setVisibility(View.VISIBLE);
        binding.swBranda.setRefreshing(false);
        binding.layoutBranda.setVisibility(View.GONE);
        binding.shimmerBeranda.setVisibility(View.GONE);
        binding.koneksi.layoutKoneksi.setVisibility(View.VISIBLE);
        binding.shimmerBeranda.startShimmer();
    }

    private void getDataCost() {
        setLoading();
        AndroidNetworking.post(link + "costumer/get")
                .addBodyParameter("nama", namaCost)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("data", "code : " + response);
                            JSONObject meta = response.getJSONObject("meta");
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject cost = data.getJSONObject(0);
                                binding.alamatCost.setText(cost.getString("alamat_costumer"));
                                idCost = cost.getString("id_costumer");

                                binding.targetJual.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            getDialog(cost.getString("targer_harga_costumer"), cost.getString("target_tercapai"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                prefManager.setLvl(idCost);
                                getData();
                                getKategori();
                            } else {
                                setGagal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : " + anError);
                        setGagal();
                    }
                });
    }

    private void getDialog(String target_harga, String target_tercapai) {

        Dialog builder = new Dialog(this);

        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(R.layout.dialog_target);
        builder.setCancelable(true);

        TextView targetHarga = builder.findViewById(R.id.targetJual);
        TextView targetTercapai = builder.findViewById(R.id.targetTercapai);
        TextView sisaTarget = builder.findViewById(R.id.sisaTarget);
        TextView simpan = builder.findViewById(R.id.simpan);

        int sisa = Integer.valueOf(target_harga)-Integer.valueOf(target_tercapai);

        targetHarga.setText(target_harga);
        targetTercapai.setText(target_tercapai);
        sisaTarget.setText(sisa+"");

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.hide();
            }
        });

        builder.show();


    }

    private void getData() {
        AndroidNetworking.post(link + "barang")
                .addBodyParameter("limit", "5")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "codse : " + response);
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                setSukses();
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                barangs.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangModel(
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            barang.getString("id_costumer"),
                                            barang.getString("id_sales"),
                                            barang.getString("foto_barang"),
                                            barang.getString("hargaSementara")
                                    ));
                                }
                                loadUrl = datas.getString("next_page_url");
                                Log.d("new", "URL : " + loadUrl);
                                getAdapter();
                                getTotal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror kat First", "code : " + anError);
                        setGagal();
                    }
                });
    }

    private void getAdapter() {
        adapter = new BarangAdapter(barangs, this::onUpdateTotal);
        binding.rvCariBarang.setAdapter(adapter);
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
                        if (!isLoading) {
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
                        Log.i("TAG", "TOP SCROLL");
                    }
                }
            });
        }
    }

    private void loadMore() {
        AndroidNetworking.post(loadUrl)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "code x : " + response + " Link | " + loadUrl);
                            if (meta.getString("code").equalsIgnoreCase("200")) {

                                binding.loadMore.setVisibility(View.GONE);
                                isLoading = false;
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangModel(
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            barang.getString("id_costumer"),
                                            barang.getString("id_sales"),
                                            barang.getString("foto_barang"),
                                            barang.getString("hargaSementara")
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
                        Log.d("eror kat More", "code x : " + anError);
                        isLoading = false;
                        binding.loadMore.setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this, "Data Terakhir", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUpdateTotal(int tot) {
        getTotal();
        Log.d("TAG", "tot : " + tot);
    }

    private void getTotal() {
        AndroidNetworking.get(link + "keranjang/total-belanja?id_costumer=" + idCost+"&id_sales="+prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("total", "data: " + response);
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                            binding.totalLayout.setVisibility(View.GONE);
                            if (response.getInt("code") == 200) {
                                int total = response.getInt("data");
                                if (total >= 1) {
                                    binding.totalLayout.setVisibility(View.VISIBLE);
                                    binding.totalBelanja.setText(formatRupiah.format((double) total));
                                } else {
                                    binding.totalLayout.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("total", "eror : " + anError);
                    }
                });
    }


    @Override
    public void onIdKategori(String kat) {
        getDataFilter(kat);

    }
}