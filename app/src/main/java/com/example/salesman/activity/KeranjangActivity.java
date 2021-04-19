package com.example.salesman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.eminayar.panter.DialogType;
import com.eminayar.panter.PanterDialog;
import com.eminayar.panter.interfaces.OnSingleCallbackConfirmListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.MainActivity;
import com.example.salesman.R;
import com.example.salesman.adapter.CariBarangAdapter;
import com.example.salesman.adapter.KeranjangBarangAdapter;
import com.example.salesman.databinding.ActivityKeranjangBinding;
import com.example.salesman.model.BarangKeranjangModel;
import com.example.salesman.model.BarangModel;
import com.example.salesman.util.InterfaceHarga;
import com.example.salesman.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class KeranjangActivity extends AppCompatActivity implements InterfaceHarga {
    private ActivityKeranjangBinding binding;

    PrefManager prefManager;
    LinearLayoutManager manager;
    String link = BuildConfig.BASE_API;
    KeranjangBarangAdapter cariAdapter;
    List<BarangKeranjangModel> barangs;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKeranjangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);
        alertDialog = new SpotsDialog.Builder().setContext(this).setMessage("Sedang Mengirim Data ....").setCancelable(false).build();

        binding.rvBarangCost.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvBarangCost.setLayoutManager(manager);
        binding.rvBarangCost.setNestedScrollingEnabled(true);
        barangs = new ArrayList<>();

        getData();
        getTotal();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayList<String> filterKat = new ArrayList<>();
        getTipeBayar(filterKat);

        binding.konfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new PanterDialog(KeranjangActivity.this)
                        .setHeaderBackground(R.drawable.bot_shape_2)
                        .setTitle("Bentuk Bayar", 18, Color.WHITE)
                        .setDialogType(DialogType.SINGLECHOICE)
                        .isCancelable(true)
                        .items(filterKat, new OnSingleCallbackConfirmListener() {
                            @Override
                            public void onSingleCallbackConfirmed(PanterDialog dialog, int pos, String text) {
                                setKOnfirmasi(text);
                            }
                        })
                        .show();
            }
        });

    }

    private void getTipeBayar(ArrayList<String> filterKat) {
        AndroidNetworking.get(link+"bentukBayar")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "meta : "+response);
                            if (meta.getString("code").equalsIgnoreCase("200")){
                                JSONArray data = response.getJSONArray("data");
                                for (int i=0; i<data.length(); i++){
                                    JSONObject d = data.getJSONObject(i);
                                    filterKat.add(d.getString("bentuk_pembayaran"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("data", "eror : "+anError);
                    }
                });
    }

    private void setKOnfirmasi(String jenisBayar) {
        alertDialog.show();
        AndroidNetworking.post(link+"transaksi")
                .addBodyParameter("id_costumer", prefManager.getLvl())
                .addBodyParameter("id_sales", prefManager.getIdUser())
                .addBodyParameter("jenis_bayar", jenisBayar)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.hide();
                            if (response.getString("code").equalsIgnoreCase("200")){
                                Toast.makeText(KeranjangActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(KeranjangActivity.this, RiwayatActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(KeranjangActivity.this, "Proses Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        alertDialog.hide();
                        Toast.makeText(KeranjangActivity.this, "Periksa Koneksi Anda", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getData() {
        AndroidNetworking.get(link+"keranjang/cost?id="+prefManager.getLvl()+"&ids="+prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("data", "cods ex : " + response+" isi : "+prefManager.getLvl());
                            if (response.getString("code").equalsIgnoreCase("200")) {
                                JSONArray data = response.getJSONArray("data");
                                Log.d("data", "codxse : "+data);
                                barangs.clear();
                                for (int i=0; i<data.length(); i++){
                                    Log.d("dats","los"+i);
                                    JSONObject barang = data.getJSONObject(i);
                                    barangs.add(new BarangKeranjangModel(
                                            barang.getInt("id_keranjang"),
                                            barang.getInt("id_barang"),
                                            barang.getString("nama_barang"),
                                            barang.getString("keterangan"),
                                            barang.getInt("harga_barang"),
                                            barang.getInt("id_kategori"),
                                            barang.getString("jml_barang"),
                                            barang.getString("id_costumer"),
                                            barang.getString("foto_barang"),
                                            barang.getString("harga")
                                    ));
                                    Log.d("barang", "id barang : "+barang.getInt("id_barang"));
                                    getCariAdapter(barangs);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("data", "eror : "+anError);
                    }
                });
    }

    private void getTotal() {
        AndroidNetworking.get(link+"keranjang/total-belanja?id_costumer="+prefManager.getLvl()+"&id_sales="+prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("total", "data: "+response);
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                            binding.totalLayout.setVisibility(View.GONE);
                            if(response.getInt("code")==200){
                                int total = response.getInt("data");
                                if (total>=1){
                                    binding.totalLayout.setVisibility(View.VISIBLE);
                                    binding.totalBelanja.setText(formatRupiah.format((double)total));
                                }else {
                                    binding.totalLayout.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("total", "eror : "+anError);
                    }
                });
    }

    private void getCariAdapter(List<BarangKeranjangModel> barangs) {
        cariAdapter = new KeranjangBarangAdapter(barangs, this::onUpdateTotal);
        binding.rvBarangCost.setAdapter(cariAdapter);
        cariAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateTotal(int tot) {
        getTotal();
    }
}