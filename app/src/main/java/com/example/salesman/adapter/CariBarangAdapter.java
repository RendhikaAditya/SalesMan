package com.example.salesman.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.salesman.BuildConfig;
import com.example.salesman.R;
import com.example.salesman.model.BarangModel;
import com.example.salesman.util.InterfaceBridge;
import com.example.salesman.util.InterfaceHarga;
import com.example.salesman.util.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class CariBarangAdapter extends RecyclerView.Adapter<CariBarangAdapter.ListViewHolder> {
    Context context;
    List<BarangModel> barang;
    String linkImg = BuildConfig.BASE_IMG;
    String link = BuildConfig.BASE_API;
    InterfaceHarga interfaceHarga;

    public CariBarangAdapter(List<BarangModel> barang, InterfaceHarga interfaceHarga) {
        this.barang = barang;
        this.interfaceHarga = interfaceHarga;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barang_row,parent,false);
        CariBarangAdapter.ListViewHolder holder = new CariBarangAdapter.ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final BarangModel model = barang.get(position);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.namaBarang.setText(model.getNama_barang());
        holder.hargaBarang.setText(formatRupiah.format((double)model.getHarga_barang()));
        Picasso.get().load(linkImg+model.getFoto_barang()).into(holder.fotoBarang);
        Log.d("barang", "foto : "+linkImg+model.getFoto_barang());
        Log.d("barang", "jml : "+model.getJml_barang());
        holder.cardFoto.setVisibility(View.VISIBLE);
        if(model.getFoto_barang().equals("null")){
            holder.cardFoto.setVisibility(View.GONE);
        }else {
            holder.cardFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImage(model);
                }
            });
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBarang(model, holder);
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusBarang(model, holder);
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusBarang(model, holder);
            }
        });
//        if (String.valueOf(model.getId_keranjang()).equalsIgnoreCase(null)){
//        }
        PrefManager prefManager = new PrefManager(context);
        if (!model.getJml_barang().equalsIgnoreCase("null")){
            Log.d("jalan", "hahax : "+model.getJml_barang()+" id "+model.getId_costumer()+" lvl "+prefManager.getLvl());

            if (model.getId_costumer().equalsIgnoreCase(prefManager.getLvl())){
                holder.jmlLayout.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                holder.jmlBarang.setText(model.getJml_barang());
            }

        }
    }

    private void minusBarang(BarangModel model, ListViewHolder holder) {
        PrefManager prefManager = new PrefManager(context);
        AndroidNetworking.post(link+"keranjang/update-kurang")
                .addBodyParameter("id_costumer", prefManager.getLvl())
                .addBodyParameter("id_barang", ""+model.getId_barang())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code")==200){
                                int jml = Integer.valueOf(holder.jmlBarang.getText().toString())-1;
                                if (jml==0){
                                    holder.jmlLayout.setVisibility(View.GONE);
                                    holder.add.setVisibility(View.VISIBLE);
                                }else {
                                    holder.jmlBarang.setText(jml+"");
                                }
                                interfaceHarga.onUpdateTotal(model.getHarga_barang());
                            }else {
                                Toast.makeText(context, "Sistem Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void plusBarang(BarangModel model, ListViewHolder holder) {
        PrefManager prefManager = new PrefManager(context);
        AndroidNetworking.post(link+"keranjang/update-tambah")
                .addBodyParameter("id_costumer", prefManager.getLvl())
                .addBodyParameter("id_barang", ""+model.getId_barang())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code")==200){
                                int jml = Integer.valueOf(holder.jmlBarang.getText().toString())+1;
                                holder.jmlBarang.setText(jml+"");
                                interfaceHarga.onUpdateTotal(model.getHarga_barang());
                            }else {
                                Toast.makeText(context, "Sistem Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addBarang(BarangModel model, ListViewHolder holder) {
        holder.loading.setVisibility(View.VISIBLE);
        PrefManager prefManager = new PrefManager(context);
        AndroidNetworking.post(link+"keranjang/add")
                .addBodyParameter("id_costumer", prefManager.getLvl())
                .addBodyParameter("id_sales", prefManager.getIdUser())
                .addBodyParameter("id_barang", ""+model.getId_barang())
                .addBodyParameter("jml_barang", "1")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            holder.loading.setVisibility(View.GONE);
                            if (response.getInt("code")==200){
                                holder.jmlLayout.setVisibility(View.VISIBLE);
                                holder.add.setVisibility(View.GONE);
                                interfaceHarga.onUpdateTotal(model.getHarga_barang());
                            }else {
                                holder.jmlLayout.setVisibility(View.GONE);
                                holder.add.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "Sistem Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                        holder.jmlLayout.setVisibility(View.GONE);
                        holder.add.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void showImage(BarangModel model) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(context);
        Picasso.get().load(linkImg+model.getFoto_barang()).into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    @Override
    public int getItemCount() {
        return barang.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoBarang;
        CardView cardFoto;
        TextView namaBarang, hargaBarang, jmlBarang;
        RelativeLayout plus, minus;
        LinearLayout add,jmlLayout;
        GifImageView loading;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            add = itemView.findViewById(R.id.button_add);
            plus = itemView.findViewById(R.id.button_plus);
            minus = itemView.findViewById(R.id.button_minus);
            jmlLayout = itemView.findViewById(R.id.layoutJml);
            jmlBarang = itemView.findViewById(R.id.jmlBarang);

            loading = itemView.findViewById(R.id.loadingAdd);

            cardFoto = itemView.findViewById(R.id.cardImg);
            fotoBarang = itemView.findViewById(R.id.img_barang);
            namaBarang = itemView.findViewById(R.id.namaBarang);
            hargaBarang = itemView.findViewById(R.id.hargaBarang);
        }
    }
}
