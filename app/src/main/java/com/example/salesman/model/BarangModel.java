package com.example.salesman.model;


public class BarangModel {

    private int id_barang;
    private String nama_barang;
    private int harga_barang;
    private int id_kategori;
    private String jml_barang;
    private String id_costumer;
    private String foto_barang;
    private String hargaSementara;

    public BarangModel(int id_barang, String nama_barang, int harga_barang, int id_kategori, String jml_barang, String id_costumer, String foto_barang, String hargaSementara) {
        this.id_barang = id_barang;
        this.nama_barang = nama_barang;
        this.harga_barang = harga_barang;
        this.id_kategori = id_kategori;
        this.jml_barang = jml_barang;
        this.id_costumer = id_costumer;
        this.foto_barang = foto_barang;
        this.hargaSementara = hargaSementara;
    }

    public int getId_barang() {
        return id_barang;
    }

    public void setId_barang(int id_barang) {
        this.id_barang = id_barang;
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

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getJml_barang() {
        return jml_barang;
    }

    public void setJml_barang(String jml_barang) {
        this.jml_barang = jml_barang;
    }

    public String getId_costumer() {
        return id_costumer;
    }

    public void setId_costumer(String id_costumer) {
        this.id_costumer = id_costumer;
    }

    public String getFoto_barang() {
        return foto_barang;
    }

    public void setFoto_barang(String foto_barang) {
        this.foto_barang = foto_barang;
    }

    public String getHargaSementara() {
        return hargaSementara;
    }

    public void setHargaSementara(String hargaSementara) {
        this.hargaSementara = hargaSementara;
    }
}

