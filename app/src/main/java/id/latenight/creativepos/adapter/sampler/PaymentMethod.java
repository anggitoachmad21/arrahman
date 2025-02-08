package id.latenight.creativepos.adapter.sampler;

public class PaymentMethod {
    private int id;
    private String name;
    private int payment_method_id;
    private String description;

    public PaymentMethod(int id, String name, int payment_method_id, String description) {
        this.id = id;
        this.name = name;
        this.payment_method_id = payment_method_id;
        this.description = description;
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

    public int getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(int payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
