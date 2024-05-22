package id.latenight.creativepos.adapter;

public class Member {
    private int id;
    private String name;
    private String nomor_wa;
    private String plat_no;
    private String type_member;
    private String saldo;
    private String diskon;
    private String tanggal_mulai;
    private String tanggal_akhir;

    public Member(int id, String name, String nomor_wa, String plat_no, String type_member, String saldo, String diskon, String tanggal_mulai, String tanggal_akhir) {
        this.id = id;
        this.name = name;
        this.nomor_wa = nomor_wa;
        this.plat_no = plat_no;
        this.type_member = type_member;
        this.saldo = saldo;
        this.diskon = diskon;
        this.tanggal_mulai = tanggal_mulai;
        this.tanggal_akhir = tanggal_akhir;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setId(int name) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNomor_wa() {
        return nomor_wa;
    }

    public void setNomor_wa(String nomor_wa) {
        this.nomor_wa = nomor_wa;
    }

    public String getPlat_no() {
        return plat_no;
    }

    public void setPlat_no(String plat_no) {
        this.plat_no = plat_no;
    }

    public String getType_member() {
        return type_member;
    }

    public void setType_member(String type_member) {
        this.type_member = type_member;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getTanggal_mulai() {
        return tanggal_mulai;
    }

    public void setTanggal_mulai(String tanggal_mulai) {
        this.tanggal_mulai = tanggal_mulai;
    }

    public String getTanggal_akhir() {
        return tanggal_akhir;
    }

    public void setTanggal_akhir(String tanggal_akhir) {
        this.tanggal_akhir = tanggal_akhir;
    }
}

