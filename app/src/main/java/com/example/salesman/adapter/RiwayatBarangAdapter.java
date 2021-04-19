package com.example.salesman.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
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
import com.example.salesman.model.BarangKeranjangModel;
import com.example.salesman.model.BarangModel;
import com.example.salesman.util.InterfaceHarga;
import com.example.salesman.util.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

import static com.example.salesman.R.id;
import static com.example.salesman.R.layout;

public class RiwayatBarangAdapter extends RecyclerView.Adapter<RiwayatBarangAdapter.ListViewHolder> {
    Context context;
    List<BarangModel> barang;
    String linkImg = BuildConfig.BASE_IMG;
    String link = BuildConfig.BASE_API;

    public RiwayatBarangAdapter(List<BarangModel> barang) {
        this.barang = barang;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout.riwayat_barang_row,parent,false);
        RiwayatBarangAdapter.ListViewHolder holder = new RiwayatBarangAdapter.ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final BarangModel model = barang.get(position);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.namaBarang.setText(model.getNama_barang());
        holder.keterangan.setText(Html.fromHtml(model.getKeterangan()));
        if (model.getKeterangan().equals("null")){
            holder.keterangan.setVisibility(View.GONE);
        }else{
            holder.keterangan.setVisibility(View.VISIBLE);
        }
        holder.hargaBarang.setText(formatRupiah.format((double)Integer.valueOf(model.getHargaSementara())));
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
        holder.jmlBarang.setText(model.getJml_barang());


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
        TextView namaBarang, hargaBarang, jmlBarang, keterangan;

        GifImageView loading;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            jmlBarang = itemView.findViewById(id.jmlBarang);
            loading = itemView.findViewById(id.loadingAdd);

            cardFoto = itemView.findViewById(id.cardImg);
            fotoBarang = itemView.findViewById(id.img_barang);
            namaBarang = itemView.findViewById(id.namaBarang);
            keterangan = itemView.findViewById(id.txtKeterangan);
            hargaBarang = itemView.findViewById(id.hargaBarang);
        }
    }
}
