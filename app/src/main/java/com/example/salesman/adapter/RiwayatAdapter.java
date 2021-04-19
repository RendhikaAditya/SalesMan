package com.example.salesman.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesman.BuildConfig;
import com.example.salesman.R;
import com.example.salesman.activity.DetailRiwayatActivity;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.model.RiwayatModel;
import com.example.salesman.util.InterfaceKategori;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.salesman.R.color.error_color_material_dark;
import static com.example.salesman.R.color.second;
import static com.example.salesman.R.color.third;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ListViewHolder> {
    List<RiwayatModel> riwayats;
    Context context;
    String link = BuildConfig.BASE_IMG;

    public RiwayatAdapter(List<RiwayatModel> riwayats) {
        this.riwayats = riwayats;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_riwayat,parent,false);
        ListViewHolder holder = new RiwayatAdapter.ListViewHolder(v);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        RiwayatModel data =  riwayats.get(position);

        String[] strSplit = data.getTgl_order().split("-");
        String Y = strSplit[0];
        String M = strSplit[1];
        String D = strSplit[2];

        holder.idOrder.setText(data.getId_order());
        holder.namaCostumer.setText(data.getNama_costumer());
        holder.tglTransaksi.setText(D+"-"+M+"-"+Y);
        holder.jmlItem.setText(data.getJumlah()+" Item");

        if (data.getStatus()==1){
            holder.status.setText("Approve");
            holder.status.setTextColor(context.getResources().getColor(second));
        }else {
            holder.status.setText("On Going");
            holder.status.setTextColor(context.getResources().getColor(third));
        }

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.totalHarga.setText(formatRupiah.format(data.getTotal_harga()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailRiwayatActivity.class);
                intent.putExtra("nama", data.getNama_costumer());
                intent.putExtra("alamat", data.getAlamat_costumer());
                intent.putExtra("idOrd", data.getId_order());
                intent.putExtra("total", formatRupiah.format(data.getTotal_harga())+"");
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return riwayats.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView idOrder, tglTransaksi, jmlItem, namaCostumer, totalHarga, status;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            status = itemView.findViewById(R.id.status);
            idOrder = itemView.findViewById(R.id.idTransaksi);
            tglTransaksi = itemView.findViewById(R.id.tglTransaksi);
            jmlItem = itemView.findViewById(R.id.jmlItem);
            namaCostumer = itemView.findViewById(R.id.namaCostumer);
            totalHarga = itemView.findViewById(R.id.hargaTotal);

        }
    }
}
