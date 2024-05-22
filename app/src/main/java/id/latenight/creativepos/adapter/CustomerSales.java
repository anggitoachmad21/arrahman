package id.latenight.creativepos.adapter;

public class CustomerSales {
    private int id;
    private String sale_no;
    private String sale_date;
    private int total_payable;
    private String washer_1;
    private String washer_2;

    public CustomerSales(int id, String sale_no, String sale_date, int total_payable, String washer_1, String washer_2) {
        this.id = id;
        this.sale_no = sale_no;
        this.sale_date = sale_date;
        this.total_payable = total_payable;
        this.washer_1 = washer_1;
        this.washer_2 = washer_2;
    }

    public int getId() {
        return id;
    }
    public String getSaleNo() {
        return sale_no;
    }
    public String getSaleDate() {
        return sale_date;
    }
    public int getTotalPayable() {
        return total_payable;
    }
    public String getWasher1() {
        return washer_1;
    }
    public String getWasher2() {
        return washer_2;
    }
}
