package com.example.salesman.adapter;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.R;
import com.example.salesman.model.BarangModel;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.util.InterfaceBridge;
import com.example.salesman.util.InterfaceHarga;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> implements InterfaceBridge {
    Context context;
    List<KategoriModel> dataKategori;
    String link = BuildConfig.BASE_API;
    InterfaceHarga interfaceHarga;

    public KategoriAdapter(List<KategoriModel> dataKategori, InterfaceHarga interfaceHarga) {
        this.dataKategori = dataKategori;
        this.interfaceHarga = interfaceHarga;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_row,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriAdapter.ViewHolder holder, int position) {
        KategoriModel data =  dataKategori.get(position);
        holder.txt_kategori.setText(data.getNama_kategori());
        List<BarangModel>barangModels = new ArrayList<>();
        holder.kosong.setVisibility(View.GONE);
        holder.rv_barang.setNestedScrollingEnabled(true);
        getBarang(holder.rv_barang, data.getId_kategori(), holder.kosong, barangModels);

    }

    private void getBarang(RecyclerView rv_barang, int id_kategori, TextView kosong, List<BarangModel> barangModels) {
        AndroidNetworking.post(link+"barang")
                .addBodyParameter("kategori", ""+id_kategori)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            Log.d("data", "cods ex : " + response);
                            if (meta.getString("code").equalsIgnoreCase("200")) {
                                JSONObject datas = response.getJSONObject("data");
                                JSONArray data = datas.getJSONArray("data");
                                Log.d("data", "codse : " + data);
                                barangModels.clear();
                                for (int i=0; i<data.length(); i++){
                                    JSONObject barang = data.getJSONObject(i);
                                    barangModels.add(new BarangModel(
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
                                    adapter(barangModels, rv_barang);
                                }
                                if (barangModels.isEmpty()){
                                    kosong.setVisibility(View.VISIBLE);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                        @Override
                    public void onError(ANError anError) {

                    }
                });


    }

    private void adapter(List<BarangModel> barangModels, RecyclerView rv_barang) {
        BarangAdapter adapter = new BarangAdapter(barangModels, this::onUpdate);
        rv_barang.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataKategori.size();
    }

    @Override
    public void onUpdate(int tot) {
        interfaceHarga.onUpdateTotal(tot);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_kategori, kosong;
        RecyclerView rv_barang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            txt_kategori =itemView.findViewById(R.id.kategoriTxt);
            kosong = itemView.findViewById(R.id.kosong);
            rv_barang = itemView.findViewById(R.id.rvBarang);
            rv_barang.setHasFixedSize(true);
            rv_barang.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }





}
