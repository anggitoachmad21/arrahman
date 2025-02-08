package id.latenight.creativepos.adapter.sampler;

public class CustomerValues {
    private int id;
    private String name;
    private int is_member;
    private int user_id;

    public CustomerValues(int id, String name, int is_member, int user_id) {
        this.id = id;
        this.name = name;
        this.is_member = is_member;
        this.user_id = user_id;
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

    public int getIs_member() {
        return is_member;
    }

    public void setIs_member(int is_member) {
        this.is_member = is_member;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
