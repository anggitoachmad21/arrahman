package id.latenight.creativepos.adapter.sampler;

public class PPN {
    private int id;
    private int percentage;
    public PPN(int id, int percentage) {
        this.id = id;
        this.percentage = percentage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
