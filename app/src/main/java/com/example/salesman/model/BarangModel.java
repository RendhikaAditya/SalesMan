package com.example.salesman.model;

import java.io.Serializable;

public class BarangModel{

    private int id_barang;
    private int id_kategori;
    private String nama_barang;
    private int harga_barang;
    private Object foto_barang;
    private String nama_kategori;

    public BarangModel(int id_barang, int id_kategori, String nama_barang, int harga_barang, Object foto_barang, String nama_kategori) {
        this.id_barang = id_barang;
        this.id_kategori = id_kategori;
        this.nama_barang = nama_barang;
        this.harga_barang = harga_barang;
        this.foto_barang = foto_barang;
        this.nama_kategori = nama_kategori;
    }

    public int getId_barang() {
        return id_barang;
    }

    public void setId_barang(int id_barang) {
        this.id_barang = id_barang;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public int getHarga_barang() {
        return harga_barang;
    }

    public void setHarga_barang(int harga_barang) {
        this.harga_barang = harga_barang;
    }

    public Object getFoto_barang() {
        return foto_barang;
    }

    public void setFoto_barang(Object foto_barang) {
        this.foto_barang = foto_barang;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public void setNama_kategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }
}

