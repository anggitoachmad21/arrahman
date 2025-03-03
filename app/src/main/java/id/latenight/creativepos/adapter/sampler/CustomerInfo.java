package id.latenight.creativepos.adapter.sampler;

public class CustomerInfo {
    private int id;
    private String name;
    private String customer_info;
    public CustomerInfo(int id, String name, String customer_info) {
        this.id = id;
        this.name = name;
        this.customer_info = customer_info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomer_info() {
        return customer_info;
    }

    public void setCustomer_info(String customer_info) {
        this.customer_info = customer_info;
    }
}
