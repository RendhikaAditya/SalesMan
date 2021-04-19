package com.example.salesman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesman.BuildConfig;
import com.example.salesman.R;
import com.example.salesman.model.KategoriModel;
import com.example.salesman.util.InterfaceKategori;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KategoriItemAdapter extends RecyclerView.Adapter<KategoriItemAdapter.ListViewHolder> {
    List<KategoriModel> kategori;
    Context context;
    String link = BuildConfig.BASE_IMG;
    InterfaceKategori interfaceKategori;

    public KategoriItemAdapter(List<KategoriModel> kategori, InterfaceKategori interfaceKategori) {
        this.kategori = kategori;
        this.interfaceKategori = interfaceKategori;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_item_row,parent,false);
        ListViewHolder holder = new KategoriItemAdapter.ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        KategoriModel data =  kategori.get(position);
        Picasso.get().load(link+data.getGambar_kategori()).into(holder.gambar);
        holder.kategori.setText(data.getNama_kategori());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceKategori.onIdKategori(data.getNama_kategori());
            }
        });

    }

    @Override
    public int getItemCount() {
        return kategori.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView gambar;
        TextView kategori;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            kategori = itemView.findViewById(R.id.namaKategori);
            gambar = itemView.findViewById(R.id.gambarKategori);

        }
    }
}
