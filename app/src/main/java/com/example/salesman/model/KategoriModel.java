package com.example.salesman.model;

public class KategoriModel {

    private int id_kategori;
    private String nama_kategori;
    private String gambar_kategori;

    public KategoriModel(int id_kategori, String nama_kategori, String gambar_kategori) {
        this.id_kategori = id_kategori;
        this.nama_kategori = nama_kategori;
        this.gambar_kategori = gambar_kategori;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public void setNama_kategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }

    public String getGambar_kategori() {
        return gambar_kategori;
    }

    public void setGambar_kategori(String gambar_kategori) {
        this.gambar_kategori = gambar_kategori;
    }
}
