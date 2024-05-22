package id.latenight.creativepos.adapter;

public class Order {
    private String id;
    private String sale_no;
    private String subtotal;
    private String discount;
    private String value_discount;
    private String total_payable;
    private String sale_date;
    private String order_time;
    private String order_type;
    private String customer_name;
    private String order_status;
    private String table_name;
    private String cust_notes;
    private String queue_no;
    private String category;
    private int status;

    public Order(String id, String sale_no, String total_payable, String sale_date, String order_time, String order_type, String customer_name, String order_status, String subtotal, String discount, String value_discount, String cust_notes, String queue_no, String category, int status) {
        this.id = id;
        this.sale_no = sale_no;
        this.total_payable = total_payable;
        this.sale_date = sale_date;
        this.order_time = order_time;
        this.order_type = order_type;
        this.customer_name = customer_name;
        this.order_status = order_status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.value_discount = value_discount;
        this.cust_notes = cust_notes;
        this.queue_no = queue_no;
        this.category = category;
        this.status = status;

    }

    public String getId() {
        return id;
    }
    public String getSaleNo() {
        return sale_no;
    }
    public String getTotalPayable() {
        return total_payable;
    }
    public String getSaleDate() {
        return sale_date;
    }
    public String getOrderTime() {
        return order_time;
    }
    public String getOrderType() {
        return order_type;
    }
    public String getCustomerName() {
        return customer_name;
    }
    public String getOrderStatus() {
        return order_status;
    }
    public String getTableName() {
        return table_name;
    }
    public String getSubtotal() {
        return subtotal;
    }
    public String getDiscount() {
        return discount;
    }
    public String getValueDiscount() {
        return value_discount;
    }
    public String getCustNotes() {
        return cust_notes;
    }
    public String getQueueNo() {
        return queue_no;
    }
    public String getCategory() {
        return category;
    }
    public int getStatus() {
        return status;
    }
}
