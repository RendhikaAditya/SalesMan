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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesman.BuildConfig;
import com.example.salesman.R;
import com.example.salesman.model.BarangModel;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.util.InterfaceBridge;
import com.example.salesman.util.InterfaceHarga;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ListViewHolder> {
    Context context;
    List<BarangModel> barang;
    String linkImg = BuildConfig.BASE_IMG;
    InterfaceBridge interfaceBridge;

    public BarangAdapter(List<BarangModel> barang, InterfaceBridge interfaceBridge) {
        this.barang = barang;
        this.interfaceBridge = interfaceBridge;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barang_row,parent,false);
        BarangAdapter.ListViewHolder holder = new BarangAdapter.ListViewHolder(v);
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
                holder.jmlLayout.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                interfaceBridge.onUpdate(model.getHarga_barang());
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
        RelativeLayout add, plus, minus;
        LinearLayout jmlLayout;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            add = itemView.findViewById(R.id.button_add);
            plus = itemView.findViewById(R.id.button_plus);
            minus = itemView.findViewById(R.id.button_minus);
            jmlLayout = itemView.findViewById(R.id.layoutJml);
            jmlBarang = itemView.findViewById(R.id.jmlBarang);

            cardFoto = itemView.findViewById(R.id.cardImg);
            fotoBarang = itemView.findViewById(R.id.img_barang);
            namaBarang = itemView.findViewById(R.id.namaBarang);
            hargaBarang = itemView.findViewById(R.id.hargaBarang);
        }
    }
}
