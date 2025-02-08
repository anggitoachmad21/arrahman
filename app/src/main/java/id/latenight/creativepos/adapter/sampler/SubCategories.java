package id.latenight.creativepos.adapter;

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
}
