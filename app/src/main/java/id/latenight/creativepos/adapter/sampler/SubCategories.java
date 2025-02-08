package id.latenight.creativepos.adapter.sampler;

public class SubCategories {
    private int id;
    private String name;
    private int categories_id;
    private int sub_categories_id;

    public SubCategories(int id, String name, int categories_id, int sub_categories_id) {
        this.id = id;
        this.name = name;
        this.categories_id = categories_id;
        this.sub_categories_id = sub_categories_id;
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

    public int getCategories_id() {
        return categories_id;
    }

    public void setCategories_id(int categories_id) {
        this.categories_id = categories_id;
    }

    public int getSub_categories_id() {
        return sub_categories_id;
    }

    public void setSub_categories_id(int sub_categories_id) {
        this.sub_categories_id = sub_categories_id;
    }
}
