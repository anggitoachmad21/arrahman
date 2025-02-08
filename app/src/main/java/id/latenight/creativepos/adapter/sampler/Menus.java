package id.latenight.creativepos.adapter.sampler;

public class Categories {
    private int id;
    private String name;
    private int categories_id;

    public Categories(int id, String name, int categories_id) {
        this.id = id;
        this.name = name;
        this.categories_id = categories_id;
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
}
