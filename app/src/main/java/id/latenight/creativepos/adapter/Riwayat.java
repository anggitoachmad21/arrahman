package id.latenight.creativepos.adapter;

public class Riwayat {
    private int id;
    private String sales_no;
    private String sales_date;
    private String name_type;
    private String type_member;
    private String type;

    public Riwayat(int id, String sales_no, String sales_date, String name_type, String type_member, String type) {
        this.id = id;
        this.sales_no = sales_no;
        this.sales_date = sales_date;
        this.name_type = name_type;
        this.type_member = type_member;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSales_no() {
        return sales_no;
    }

    public void setSales_no(String sales_no) {
        this.sales_no = sales_no;
    }

    public String getSales_date() {
        return sales_date;
    }

    public void setSales_date(String sales_date) {
        this.sales_date = sales_date;
    }

    public String getName_type() {
        return name_type;
    }

    public void setName_type(String name_type) {
        this.name_type = name_type;
    }

    public String getType_member() {
        return type_member;
    }

    public void setType_member(String type_member) {
        this.type_member = type_member;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

