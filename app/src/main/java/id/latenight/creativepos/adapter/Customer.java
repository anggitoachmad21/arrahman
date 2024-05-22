package id.latenight.creativepos.adapter;

public class Customer {
    private int id;
    private String name;
    private String type;
    private int counter;
    private int total_redeem;
    private int total_visit;
    private String last_visit;

    public Customer(int id, String name, String type, int counter,int total_redeem, int total_visit, String last_visit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.counter = counter;
        this.total_redeem = total_redeem;
        this.total_visit = total_visit;
        this.last_visit = last_visit;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public int getCounter() {
        return counter;
    }
    public int getTotalRedeem() {
        return total_redeem;
    }
    public int getTotalVisit() {
        return total_visit;
    }
    public String getLastVisit() {
        return last_visit;
    }

    public void setId(int name) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
