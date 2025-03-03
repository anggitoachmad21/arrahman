package id.latenight.creativepos.adapter.sampler;

public class Washer {
    private int id;
    private String name;
    private int washer_id;
    private String type;
    public Washer(int id, String name, int washer_id, String type) {
        this.id = id;
        this.name = name;
        this.washer_id = washer_id;
        this.type = type;
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

    public int getWasher_id() {
        return washer_id;
    }

    public void setWasher_id(int washer_id) {
        this.washer_id = washer_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
