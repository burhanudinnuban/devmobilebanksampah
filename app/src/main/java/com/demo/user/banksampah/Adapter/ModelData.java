package com.demo.user.banksampah.Adapter;

public class ModelData {
    private String id_member;
    private String nama_member;
    private String point;


    public ModelData(String id_member, String nama_member, String point) {
        this.id_member = id_member;
        this.nama_member = nama_member;
        this.point = point;


    }

    String getId_member() {
        return id_member;
    }

    String getNama_member() {
        return nama_member;
    }

    String getPoint() {
        return point;
    }
}
