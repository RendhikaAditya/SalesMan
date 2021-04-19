package com.example.salesman.model;

public class RiwayatModel{
    private int id;
    private String id_order;
    private int id_costumer;
    private int id_sales;
    private int total_harga;
    private String tgl_order;
    private String bentuk_pembayaran;
    private String nama_costumer;
    private String alamat_costumer;
    private String targer_harga_costumer;
    private String target_tercapai;
    private int jumlah;
    private int status;

    public RiwayatModel(int id, String id_order, int id_costumer, int id_sales, int total_harga, String tgl_order, String bentuk_pembayaran, String nama_costumer, String alamat_costumer, String targer_harga_costumer, String target_tercapai, int jumlah, int status) {
        this.id = id;
        this.id_order = id_order;
        this.id_costumer = id_costumer;
        this.id_sales = id_sales;
        this.total_harga = total_harga;
        this.tgl_order = tgl_order;
        this.bentuk_pembayaran = bentuk_pembayaran;
        this.nama_costumer = nama_costumer;
        this.alamat_costumer = alamat_costumer;
        this.targer_harga_costumer = targer_harga_costumer;
        this.target_tercapai = target_tercapai;
        this.jumlah = jumlah;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public int getId_costumer() {
        return id_costumer;
    }

    public void setId_costumer(int id_costumer) {
        this.id_costumer = id_costumer;
    }

    public int getId_sales() {
        return id_sales;
    }

    public void setId_sales(int id_sales) {
        this.id_sales = id_sales;
    }

    public int getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(int total_harga) {
        this.total_harga = total_harga;
    }

    public String getTgl_order() {
        return tgl_order;
    }

    public void setTgl_order(String tgl_order) {
        this.tgl_order = tgl_order;
    }

    public String getBentuk_pembayaran() {
        return bentuk_pembayaran;
    }

    public void setBentuk_pembayaran(String bentuk_pembayaran) {
        this.bentuk_pembayaran = bentuk_pembayaran;
    }

    public String getNama_costumer() {
        return nama_costumer;
    }

    public void setNama_costumer(String nama_costumer) {
        this.nama_costumer = nama_costumer;
    }

    public String getAlamat_costumer() {
        return alamat_costumer;
    }

    public void setAlamat_costumer(String alamat_costumer) {
        this.alamat_costumer = alamat_costumer;
    }

    public String getTarger_harga_costumer() {
        return targer_harga_costumer;
    }

    public void setTarger_harga_costumer(String targer_harga_costumer) {
        this.targer_harga_costumer = targer_harga_costumer;
    }

    public String getTarget_tercapai() {
        return target_tercapai;
    }

    public void setTarget_tercapai(String target_tercapai) {
        this.target_tercapai = target_tercapai;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
