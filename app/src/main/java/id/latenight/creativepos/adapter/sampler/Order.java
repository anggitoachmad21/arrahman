package id.latenight.creativepos.adapter.sampler;

public class Order {
    private int id;
    private String sale_no;
    private int order_id;
    private String order_details;
    private int status;

    public Order(int id, String sale_no ,int order_id, int status, String order_details) {
        this.id = id;
        this.sale_no = sale_no;
        this.order_id = order_id;
        this.status = status;
        this.order_details = order_details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_details() {
        return order_details;
    }

    public void setOrder_details(String order_details) {
        this.order_details = order_details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSale_no() {
        return sale_no;
    }

    public void setSale_no(String sale_no) {
        this.sale_no = sale_no;
    }
}
